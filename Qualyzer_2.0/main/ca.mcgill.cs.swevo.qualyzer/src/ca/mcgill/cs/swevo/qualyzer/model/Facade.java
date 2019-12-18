/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * 
 */
public final class Facade
{
	private static Facade gFacade = null;

	private ListenerManager fListenerManager;

	private final Logger fLogger = LoggerFactory.getLogger(Facade.class);

	private Facade()
	{
		fListenerManager = new ListenerManager();
	}

	/**
	 * Get the Facade.
	 * 
	 * @return
	 */
	public static Facade getInstance()
	{
		if (gFacade == null)
		{
			gFacade = new Facade();
		}

		return gFacade;
	}

	/**
	 * Create a new Project with the given name and investigator information.
	 * 
	 * @param name
	 * @param nickname
	 * @param fullName
	 * @param institution
	 * @return
	 * @throws QualyzerException
	 */
	public Project createProject(String name, String nickname, String fullName, String institution)
			throws QualyzerException
	{
		String folderName = name.replace(' ', '_');
		IProject wProject = FileUtil.makeProjectFileSystem(folderName);

		Project project = new Project();
		project.setName(name);
		project.setFolderName(folderName);

		createInvestigator(nickname, fullName, institution, project, false);

		PersistenceManager.getInstance().initDB(wProject);
		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(folderName);
		HibernateUtil.quietSave(manager, project);
		
		try
		{
			FileUtil.setProjectProperty(wProject, FileUtil.ACTIVE_INV, nickname);
			FileUtil.setProjectProperty(wProject, FileUtil.PROJECT_VERSION, QualyzerActivator.CURRENT_VERSION);
		}
		catch (CoreException e)
		{
			fLogger.error("Could not set Active Investigator", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString("model.Facade.activeInvestigatorError", "ca.mcgill.cs.swevo.qualyzer.model.messages"), e); //$NON-NLS-1$
		}
		
		fListenerManager.notifyProjectListeners(ChangeType.ADD, project, this);

		return project;
	}

	/**
	 * Create a new code.
	 * 
	 * @param codeName
	 * @param codeDescription
	 * @param project
	 * @return
	 */
	public Code createCode(String codeName, String codeDescription, Project project)
	{
		Code code = new Code();
		code.setCodeName(codeName);
		code.setDescription(codeDescription);
		code.setProject(project);
		project.getCodes().add(code);

		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getFolderName());
		HibernateUtil.quietSave(manager, project);

		fListenerManager.notifyCodeListeners(ChangeType.ADD, new Code[] { code }, this);

		return code;
	}


	/**
	 * Create an investigator from the given information.
	 * @param nickname
	 * @param fullName
	 * @param institution
	 * @param project
	 * @param save
	 * @return
	 * @throws QualyzerException
	 */
	public Investigator createInvestigator(String nickname, String fullName, String institution, Project project,
			boolean save) throws QualyzerException
	{
		Investigator investigator = new Investigator();
		investigator.setNickName(nickname);
		investigator.setFullName(fullName);
		investigator.setInstitution(institution);
		investigator.setProject(project);

		project.getInvestigators().add(investigator);

		if (save)
		{
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getFolderName());
			HibernateUtil.quietSave(manager, project);
		}

		fListenerManager.notifyInvestigatorListeners(ChangeType.ADD, new Investigator[] { investigator }, this);

		return investigator;
	}

	/**
	 * Create a new participant from the given information.
	 * @param participantId
	 * @param fullname
	 * @param fProject
	 * @return
	 */
	public Participant createParticipant(String participantId, String fullName, Project project)
			throws QualyzerException
	{
		Participant participant = new Participant();

		participant.setParticipantId(participantId);
		participant.setFullName(fullName);
		participant.setProject(project);
		project.getParticipants().add(participant);

		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getFolderName());
		HibernateUtil.quietSave(manager, project);

		fListenerManager.notifyParticipantListeners(ChangeType.ADD, new Participant[] { participant }, this);

		return participant;
	}

	/**
	 * Create a new transcript.
	 * @param name
	 * @param date
	 * @param audioFilePath
	 * @param participants
	 * @param project
	 * @return
	 * @throws QualyzerException
	 */
	public Transcript createTranscript(String name, String date, String audioFilePath, List<Participant> participants,
			Project project) throws QualyzerException
	{
		Transcript transcript = new Transcript();
		transcript.setName(name);
		String fileName = name.replace(' ', '_') + ".rtf"; //$NON-NLS-1$
		transcript.setFileName(fileName); 
		transcript.setDate(date);
		transcript.setParticipants(participants);

		String fileExt = ""; //$NON-NLS-1$
		if (!audioFilePath.isEmpty())
		{
			fileExt = audioFilePath.substring(audioFilePath.lastIndexOf('.'));
		}

		transcript.setAudioFile(createAudioFile(name, fileExt));

		project.getTranscripts().add(transcript);
		transcript.setProject(project);

		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				project.getFolderName());
		HibernateUtil.quietSave(manager, project);

		fListenerManager.notifyTranscriptListeners(ChangeType.ADD, new Transcript[] { transcript }, this);

		return transcript;
	}

	/**
	 * Build the audio file for the specified transcript.
	 * 
	 * @param transcriptName
	 * @param fileExt
	 * @return
	 */
	public AudioFile createAudioFile(String transcriptName, String fileExt)
	{
		if (fileExt.isEmpty())
		{
			return null;
		}

		AudioFile audioFile = new AudioFile();
		audioFile.setRelativePath(File.separator + "audio" + File.separator +  //$NON-NLS-1$
				transcriptName.replace(' ', '_') + fileExt); //$NON-NLS-1$

		return audioFile;
	}

	/**
	 * Create a new Fragment. Must be called with a properly loaded document.
	 * 
	 * @param document
	 * @param offset
	 * @param length
	 * @return
	 */
	public Fragment createFragment(IAnnotatedDocument document, int offset, int length)
	{
		Fragment fragment = new Fragment();

		fragment.setOffset(offset);
		fragment.setLength(length);
		try
		{
			fragment.setDocument(document);
			document.getFragments().put(fragment.getOffset(), fragment);
		}
		catch (HibernateException he)
		{
			String key = "model.Facade.Fragment.cannotCreate"; //$NON-NLS-1$
			String errorMessage = MessagesClient.getString(key, "ca.mcgill.cs.swevo.qualyzer.model.messages");
			fLogger.error(key, he);
			throw new QualyzerException(errorMessage, he);
		}
		
		if(document instanceof Transcript)
		{
			fListenerManager.notifyTranscriptListeners(ChangeType.MODIFY,
					new Transcript[] { (Transcript) document }, this);
		}
		else if(document instanceof Memo)
		{
			fListenerManager.notifyMemoListeners(ChangeType.MODIFY, new Memo[]{(Memo) document}, this);
		}

		return fragment;
	}
	
	/**
	 * Create a new Timestamp. Must be called with a properly loaded transcript.
	 * 
	 * @param transcript
	 * @param lineNumber
	 * @param seconds
	 * @return
	 */
	public Timestamp createTimestamp(Transcript transcript, int lineNumber, int seconds)
	{
		Timestamp timestamp = new Timestamp();

		timestamp.setLineNumber(lineNumber);
		timestamp.setSeconds(seconds);
		try
		{
			timestamp.setTranscript(transcript);
			transcript.getTimestamps().put(lineNumber, timestamp);
		}
		catch (HibernateException he)
		{
			String key = "model.Facade.Timestamp.cannotCreate"; //$NON-NLS-1$
			String errorMessage = MessagesClient.getString(key, "ca.mcgill.cs.swevo.qualyzer.model.messages");
			fLogger.error(key, he);
			throw new QualyzerException(errorMessage, he);
		}
		
			
		fListenerManager.notifyTranscriptListeners(ChangeType.MODIFY, new Transcript[] { transcript}, this);
		return timestamp;
	}

	/**
	 * Try to delete a project.
	 * 
	 * @param project
	 */
	public void deleteProject(Project project)
	{
		fListenerManager.notifyProjectListeners(ChangeType.DELETE, project, this);
		
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getFolderName());

		try
		{
			HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
					project.getFolderName());

			QualyzerActivator.getDefault().getHibernateDBManagers().remove(project.getFolderName());
			//manager.shutdownDBServer();
			manager.close();

			wProject.delete(true, true, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			String key = "model.Facade.Project.cannotDelete"; //$NON-NLS-1$
			String errorMessage = MessagesClient.getString(key, "ca.mcgill.cs.swevo.qualyzer.model.messages");
			fLogger.error(key, e);
			throw new QualyzerException(errorMessage, e);
		}
	}

	/**
	 * Try to delete a participant.
	 * 
	 * @param participant
	 */
	public void deleteParticipant(Participant participant)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				participant.getProject().getFolderName());
		Session session = null;
		Transaction t = null;

		try
		{
			session = manager.openSession();
			t = session.beginTransaction();

			/*
			 * The following is ALL required in order to delete the object from the database. Don't ask me why, I don't
			 * really understand it myself -JF.
			 */
			project = session.get(Project.class, participant.getProject().getPersistenceId());
			Object part = session.get(Participant.class, participant.getPersistenceId());

			((Project) project).getParticipants().remove(part);

			session.delete(part);
			session.saveOrUpdate(project);
			session.flush();
			t.commit();

			fListenerManager.notifyParticipantListeners(ChangeType.DELETE, new Participant[] { participant }, this);
		}
		catch (HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String key = "model.Facade.Participant.cannotDelete"; //$NON-NLS-1$
			String errorMessage = MessagesClient.getString(key, "ca.mcgill.cs.swevo.qualyzer.model.messages");
			fLogger.error(key, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}

	/**
	 * Try to delete the investigator.
	 * 
	 * @param investigator
	 */
	public void deleteInvestigator(Investigator investigator)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				investigator.getProject().getFolderName());
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();

			/*
			 * The following is ALL required in order to delete the object from the database. Don't ask me why, I don't
			 * really understand it myself -JF.
			 */
			project = session.get(Project.class, investigator.getProject().getPersistenceId());
			Object inv = session.get(Investigator.class, investigator.getPersistenceId());

			((Project) project).getInvestigators().remove(inv);

			session.delete(inv);
			session.saveOrUpdate(project);
			session.flush();
			t.commit();
			fListenerManager.notifyInvestigatorListeners(ChangeType.DELETE, new Investigator[] { investigator }, this);
		}
		catch (HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String key = "model.Facade.Investigator.cannotDelete"; //$NON-NLS-1$
			String errorMessage = MessagesClient.getString(key, "ca.mcgill.cs.swevo.qualyzer.model.messages");
			fLogger.error(key, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}

	/**
	 * Try to delete the transcript.
	 * 
	 * @param transcript
	 */
	public void deleteTranscript(Transcript transcript)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				transcript.getProject().getFolderName());
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();

			/*
			 * The following is ALL required in order to delete the object from the database. Don't ask me why, I don't
			 * really understand it myself -JF.
			 */
			project = session.get(Project.class, transcript.getProject().getPersistenceId());
			Transcript trans = (Transcript) session.get(Transcript.class, transcript.getPersistenceId());

			((Project) project).getTranscripts().remove(trans);

			session.delete(trans);
			session.saveOrUpdate(project);
			session.flush();
			t.commit();
			fListenerManager.notifyTranscriptListeners(ChangeType.DELETE, new Transcript[] { transcript }, this);
		}
		catch (HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String key = "model.Facade.Transcript.cannotDelete"; //$NON-NLS-1$
			String errorMessage = MessagesClient.getString(key, "ca.mcgill.cs.swevo.qualyzer.model.messages");
			fLogger.error(key, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}
	
	/**
	 * Try to delete the memo.
	 * 
	 * @param memo
	 */
	public void deleteMemo(Memo memo)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				memo.getProject().getFolderName());
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();

			/*
			 * The following is ALL required in order to delete the object from the database. Don't ask me why, I don't
			 * really understand it myself -JF.
			 */
			project = session.get(Project.class, memo.getProject().getPersistenceId());
			Memo lMemo = (Memo) session.get(Memo.class, memo.getPersistenceId());

			((Project) project).getMemos().remove(lMemo);

			session.delete(lMemo);
			session.saveOrUpdate(project);
			session.flush();
			t.commit();
			fListenerManager.notifyMemoListeners(ChangeType.DELETE, new Memo[] { memo }, this);
		}
		catch (HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String key = "model.Facade.Memo.cannotDelete"; //$NON-NLS-1$
			String errorMessage = MessagesClient.getString(key, "ca.mcgill.cs.swevo.qualyzer.model.messages");
			fLogger.error(key, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}

	/**
	 * Force a Transcript to load all its fields.
	 * 
	 * @param transcript
	 * @return
	 */
	public Transcript forceTranscriptLoad(Transcript transcript)
	{
		Transcript toReturn = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				transcript.getProject().getFolderName());
		Session s = manager.openSession();

		try
		{
			Object object = s.get(Transcript.class, transcript.getPersistenceId());
			toReturn = (Transcript) object;

			Hibernate.initialize(toReturn.getParticipants());
			Hibernate.initialize(toReturn.getFragments());
			Hibernate.initialize(toReturn.getTimestamps());
		}
		finally
		{
			HibernateUtil.quietClose(s);
		}

		return toReturn;
	}

	/**
	 * Force a Memo to load all of its fields.
	 * 
	 * @param memo
	 * @return
	 */
	public Memo forceMemoLoad(Memo memo)
	{
		Memo toReturn = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				memo.getProject().getFolderName());
		Session s = manager.openSession();
		try
		{
			Object object = s.get(Memo.class, memo.getPersistenceId());
			toReturn = (Memo) object;
			Hibernate.initialize(toReturn.getParticipants());
			Hibernate.initialize(toReturn.getFragments());
		}
		finally
		{
			HibernateUtil.quietClose(s);
		}
		return toReturn;
	}

	/**
	 * Get the Listener Manager.
	 * 
	 * @return
	 */
	public ListenerManager getListenerManager()
	{
		return fListenerManager;
	}


	/**
	 * Save an investigator.
	 * 
	 * @param investigator
	 */
	public void saveInvestigator(Investigator investigator)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				investigator.getProject().getFolderName());
		HibernateUtil.quietSave(manager, investigator);

		fListenerManager.notifyInvestigatorListeners(ChangeType.MODIFY, new Investigator[] { investigator }, this);
	}

	/**
	 * Save a Participant.
	 * 
	 * @param participant
	 */
	public void saveParticipant(Participant participant)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				participant.getProject().getFolderName());
		HibernateUtil.quietSave(manager, participant);

		fListenerManager.notifyParticipantListeners(ChangeType.MODIFY, new Participant[] { participant }, this);
	}

	/**
	 * Save a Transcript.
	 * 
	 * @param transcript
	 */
	public void saveTranscript(Transcript transcript)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				transcript.getProject().getFolderName());
		HibernateUtil.quietSave(manager, transcript);

		fListenerManager.notifyTranscriptListeners(ChangeType.MODIFY, new Transcript[] { transcript }, this);
	}

	/**
	 * @param modifiedCodes
	 */
	public void saveCodes(Code[] modifiedCodes)
	{
		if (modifiedCodes.length > 0)
		{
			HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
					modifiedCodes[0].getProject().getFolderName());
			HibernateUtil.quietSave(manager, modifiedCodes);

			fListenerManager.notifyCodeListeners(ChangeType.MODIFY, modifiedCodes, this);
		}

	}

	/**
	 * Delete a code.
	 * @param code
	 */
	public void deleteCode(Code code)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				code.getProject().getFolderName());
		Session session = null;
		Transaction t = null;

		try
		{
			session = manager.openSession();
			t = session.beginTransaction();

			/*
			 * The following is ALL required in order to delete the object from the database. Don't ask me why, I don't
			 * really understand it myself -JF.
			 */
			project = session.get(Project.class, code.getProject().getPersistenceId());
			Object lCode = session.get(Code.class, code.getPersistenceId());

			((Project) project).getCodes().remove(lCode);

			session.delete(lCode);
			session.saveOrUpdate(project);
			session.flush();
			t.commit();

			fListenerManager.notifyCodeListeners(ChangeType.DELETE, new Code[] { (Code) lCode }, this);
		}
		catch (HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String errorMessage = MessagesClient.getString("model.Facade.code.cannotDelete", "ca.mcgill.cs.swevo.qualyzer.model.messages"); //$NON-NLS-1$
			fLogger.error(errorMessage, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}

	}

	/**
	 * Delete a fragment.
	 * @param fragment
	 */
	public void deleteFragment(Fragment fragment)
	{
		IAnnotatedDocument document = fragment.getDocument();
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				document.getProject().getFolderName());
		Session session = null;
		Transaction t = null;

		try
		{
			session = manager.openSession();
			t = session.beginTransaction();

			/*
			 * The following is ALL required in order to delete the object from the database. Don't ask me why, I don't
			 * really understand it myself -JF.
			 */

			document.getFragments().remove(fragment.getOffset());
			session.delete(fragment);
			session.saveOrUpdate(document);
			session.flush();
			t.commit();

			if (document instanceof Transcript)
			{

				fListenerManager.notifyTranscriptListeners(ChangeType.MODIFY,
						new Transcript[] { (Transcript) document }, this);
			}
			else
			{
				fListenerManager.notifyMemoListeners(ChangeType.MODIFY, new Memo[] { (Memo) document }, this);
			}
		}
		catch (HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String errorMessage = MessagesClient.getString("model.Facade.fragment.cannotDelete", "ca.mcgill.cs.swevo.qualyzer.model.messages"); //$NON-NLS-1$
			fLogger.error(errorMessage, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}
	
	/**
	 * Delete a timestamp.
	 * @param timestamp
	 */
	public void deleteTimestamp(Timestamp timestamp)
	{
		Transcript transcript = timestamp.getTranscript();
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				transcript.getProject().getFolderName());
		Session session = null;
		Transaction t = null;

		try
		{
			session = manager.openSession();
			t = session.beginTransaction();

			/*
			 * The following is ALL required in order to delete the object from the database. Don't ask me why, I don't
			 * really understand it myself -JF.
			 */
			transcript.getTimestamps().remove(timestamp.getLineNumber());
			session.delete(timestamp);
			session.saveOrUpdate(transcript);
			session.flush();
			t.commit();

			fListenerManager.notifyTranscriptListeners(ChangeType.MODIFY, new Transcript[] { transcript }, this);
		}
		catch (HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String errorMessage = MessagesClient.getString("model.Facade.Timestamp.cannotDelete", "ca.mcgill.cs.swevo.qualyzer.model.messages"); //$NON-NLS-1$
			fLogger.error(errorMessage, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}

	/**
	 * Try to save the document.
	 * Convenience method, delegates to saveTranscript(Transcript) and saveMemo(Memo)
	 * 
	 * @param document
	 */
	public void saveDocument(IAnnotatedDocument document)
	{
		if (document instanceof Transcript)
		{
			saveTranscript((Transcript) document);
		}
		else if(document instanceof Memo)
		{
			saveMemo((Memo) document);
		}
	}

	/**
	 * Force a document to load.
	 * Convenience method, delegates to saveTranscript(Transcript) and saveMemo(Memo)
	 * 
	 * @param document
	 * @return
	 */
	public IAnnotatedDocument forceDocumentLoad(IAnnotatedDocument document)
	{
		if (document instanceof Transcript)
		{
			return forceTranscriptLoad((Transcript) document);
		}
		else if (document instanceof Memo)
		{
			return forceMemoLoad((Memo) document);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Create a new memo from the given information.
	 * 
	 * @param memoName
	 * @param date
	 * @param author
	 * @param participants
	 * @param project
	 * @param code
	 * @param transcript
	 * @return
	 */
	public Memo createMemo(String memoName, String date, Investigator author, List<Participant> participants,
			Project project, Code code, Transcript transcript)
	{
		Memo memo = new Memo();
		memo.setName(memoName);
		String fileName = memoName.replace(' ', '_') + ".rtf"; //$NON-NLS-1$
		memo.setFileName(fileName); 
		memo.setDate(date);
		memo.setAuthor(author);
		memo.setParticipants(participants);
		memo.setCode(code);
		memo.setTranscript(transcript);

		project.getMemos().add(memo);
		memo.setProject(project);

		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				project.getFolderName());
		HibernateUtil.quietSave(manager, project);

		fListenerManager.notifyMemoListeners(ChangeType.ADD, new Memo[] { memo }, this);

		return memo;
	}

	/**
	 * Save a memo.
	 * @param memo
	 */
	public void saveMemo(Memo memo)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				memo.getProject().getFolderName());
		HibernateUtil.quietSave(manager, memo);

		fListenerManager.notifyMemoListeners(ChangeType.MODIFY, new Memo[] { memo }, this);
		
	}

	/**
	 * Rename a project. This only affects the Qualyzer project in the database to 
	 * rename the files on disk use FileUtil.renameProject(String oldname, String newName)
	 * @param project
	 * @param newName
	 */
	public void renameProject(Project project, String newName)
	{
		//Close related editors
		fListenerManager.notifyProjectListeners(ChangeType.RENAME, project, this);
		
		String oldName = project.getName();
		String oldFolderName = project.getFolderName();
		
		Project refreshedProject = PersistenceManager.getInstance().getProject(oldName);
		
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(oldFolderName);
		refreshedProject.setName(newName);
		refreshedProject.setFolderName(newName.replace(' ', '_'));
		HibernateUtil.quietSave(manager, refreshedProject);
		
		fListenerManager.handleProjectNameChange(oldName, refreshedProject);
	}
	
	/**
	 * Update a project database when the project's version does not match the current version.
	 * @param project
	 */
	public void updateProject(IProject project) throws CoreException, QualyzerException
	{
		PersistenceManager.getInstance().updateDB(project);
		FileUtil.setProjectProperty(project, FileUtil.PROJECT_VERSION, QualyzerActivator.CURRENT_VERSION);
	}
}
