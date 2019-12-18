/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *	Keeps track of all the listeners to various change events in the model.
 *
 */
public class ListenerManager
{
	/**
	 * Defines the various reasons listeners can be notified.
	 */
	public enum ChangeType
	{ADD, DELETE, MODIFY, RENAME}
	
	private HashMap<Project, ArrayList<ProjectListener>> fProjectListeners;
	private HashMap<Project, ArrayList<CodeListener>> fCodeListeners;
	private HashMap<Project, ArrayList<InvestigatorListener>> fInvestigatorListeners;
	private HashMap<Project, ArrayList<ParticipantListener>> fParticipantListeners;
	private HashMap<Project, ArrayList<TranscriptListener>> fTranscriptListeners;
	private HashMap<Project, ArrayList<MemoListener>> fMemoListeners;
	
	/**
	 * Constructor.
	 */
	public ListenerManager()
	{
		fProjectListeners = new HashMap<Project, ArrayList<ProjectListener>>();
		fCodeListeners = new HashMap<Project, ArrayList<CodeListener>>();
		fInvestigatorListeners = new HashMap<Project, ArrayList<InvestigatorListener>>();
		fParticipantListeners = new HashMap<Project, ArrayList<ParticipantListener>>();
		fTranscriptListeners = new HashMap<Project, ArrayList<TranscriptListener>>();
		fMemoListeners = new HashMap<Project, ArrayList<MemoListener>>();
	}
	
	/**
	 * Notify the registered ProjectListeners that the given Project has changed.
	 * Note that this method takes a single project, not an array.
	 * @param cType
	 * @param project
	 * @param facade
	 */
	public void notifyProjectListeners(ChangeType cType, Project project, Facade facade)
	{

		ArrayList<ProjectListener> list = fProjectListeners.get(project);
		ArrayList<ProjectListener> listCopy = new ArrayList<ProjectListener>();
		
		if(list != null)
		{
			for(ProjectListener listener : list)
			{
				listCopy.add(listener);
			}
			
			for(ProjectListener listener : listCopy)
			{
				listener.projectChanged(cType, project, facade);
			}
		}
	}
	
	/**
	 * Notify all the CodeListeners that some Codes have changed.
	 * Assumes that all the codes belong to the same project.
	 * @param cType
	 * @param project
	 * @param facade
	 */
	public void notifyCodeListeners(ChangeType cType, Code[] codes, Facade facade)
	{
		if(codes.length > 0)
		{
			ArrayList<CodeListener> list = fCodeListeners.get(codes[0].getProject());
			ArrayList<CodeListener> listCopy = new ArrayList<CodeListener>();
			
			if(list != null)
			{
				for(CodeListener listener : list)
				{
					listCopy.add(listener);
				}
				
				for(CodeListener listener : listCopy)
				{
					listener.codeChanged(cType, codes, facade);
				}
			}
		}
	}
	
	/**
	 * Notify the InvestigatorListeners that some Investigators have changed.
	 * Assumes that all the Investigators belong to the same project.
	 * @param cType
	 * @param investigator
	 * @param facade
	 */
	public void notifyInvestigatorListeners(ChangeType cType, Investigator[] investigators, Facade facade)
	{
		if(investigators.length > 0)
		{
			ArrayList<InvestigatorListener> list = fInvestigatorListeners.get(investigators[0].getProject());
			ArrayList<InvestigatorListener> listCopy = new ArrayList<InvestigatorListener>();
			if(list != null)
			{
				for(InvestigatorListener listener : list)
				{
					listCopy.add(listener);
				}
				
				for(InvestigatorListener listener : listCopy)
				{
					listener.investigatorChanged(cType, investigators, facade);
				}
			}
		}
	}
	
	/**
	 * Notify the ParticipantListeners that some Participants have changed.
	 * Assumes that all the Participants belong to the same projects.
	 * @param cType
	 * @param participant
	 * @param facade
	 */
	public void notifyParticipantListeners(ChangeType cType, Participant[] participants, Facade facade)
	{
		if(participants.length > 0)
		{
			ArrayList<ParticipantListener> list = fParticipantListeners.get(participants[0].getProject());
			ArrayList<ParticipantListener> listCopy = new ArrayList<ParticipantListener>();
			
			if(list != null)
			{
				for(ParticipantListener listener : list)
				{
					listCopy.add(listener);
				}
				
				for(ParticipantListener listener : listCopy)
				{
					listener.participantChanged(cType, participants, facade);
				}
			}
		}
	}
	
	/**
	 * Notify the TranscriptListeners that some Transcripts have changed.
	 * Assumes that all the Transcripts belong to the same project.
	 * @param cType
	 * @param transcript
	 * @param facade
	 */
	public void notifyTranscriptListeners(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		if(transcripts.length > 0)
		{
			ArrayList<TranscriptListener> list = fTranscriptListeners.get(transcripts[0].getProject());
			ArrayList<TranscriptListener> listCopy = new ArrayList<TranscriptListener>();
			
			if(list != null)
			{
				for(TranscriptListener listener : list)
				{
					listCopy.add(listener);
				}
				
				for(TranscriptListener listener : listCopy)
				{
					listener.transcriptChanged(cType, transcripts, facade);
				}
			}
		}
	}
	
	/**
	 * Register a CodeListener with the specified Project.
	 * @param project
	 * @param listener
	 */
	public void registerCodeListener(Project project, CodeListener listener)
	{
		ArrayList<CodeListener> listenerList = fCodeListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<CodeListener>();
		}
		
		listenerList.add(listener);
		
		fCodeListeners.put(project, listenerList);
	}
	
	/**
	 * Register an InvestigatorListener with the given Project.
	 * @param project
	 * @param listener
	 */
	public void registerInvestigatorListener(Project project, InvestigatorListener listener)
	{
		ArrayList<InvestigatorListener> listenerList = fInvestigatorListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<InvestigatorListener>();
		}
		
		listenerList.add(listener);
		fInvestigatorListeners.put(project, listenerList);
	}
	
	/**
	 * Register a ParticipantListener with the given Project.
	 * @param project
	 * @param listener
	 */
	public void registerParticipantListener(Project project, ParticipantListener listener)
	{
		ArrayList<ParticipantListener> listenerList = fParticipantListeners.get(project);
	
		if(listenerList == null)
		{
			listenerList = new ArrayList<ParticipantListener>();
		}
		
		listenerList.add(listener);
		fParticipantListeners.put(project, listenerList);
	}
	
	/**
	 * Register a ProjectListener with a particular project.
	 * @param project
	 * @param listener
	 */
	public void registerProjectListener(Project project, ProjectListener listener)
	{
		ArrayList<ProjectListener> listenerList = fProjectListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<ProjectListener>();
		}
		
		listenerList.add(listener);
		fProjectListeners.put(project, listenerList);
	}
	
	/**
	 * Register a TranscriptListener with the given Project.
	 * @param project
	 * @param listener
	 */
	public void registerTranscriptListener(Project project, TranscriptListener listener)
	{
		ArrayList<TranscriptListener> listenerList = fTranscriptListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<TranscriptListener>();
		}
		
		listenerList.add(listener);
		fTranscriptListeners.put(project, listenerList);
	}
	
	/**
	 * Unregister a CodeListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterCodeListener(Project project, CodeListener listener)
	{
		ArrayList<CodeListener> listenerList = fCodeListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister an InvestigatorListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterInvestigatorListener(Project project, InvestigatorListener listener)
	{
		ArrayList<InvestigatorListener> listenerList = fInvestigatorListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister a ParticipantListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterParticipantListener(Project project, ParticipantListener listener)
	{
		ArrayList<ParticipantListener> listenerList = fParticipantListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister a ProjectListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterProjectListener(Project project, ProjectListener listener)
	{
		ArrayList<ProjectListener> listenerList = fProjectListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister a TranscriptListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterTranscriptListener(Project project, TranscriptListener listener)
	{
		ArrayList<TranscriptListener> listenerList = fTranscriptListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}

	/**
	 * @param add
	 * @param memos
	 * @param facade
	 */
	public void notifyMemoListeners(ChangeType cType, Memo[] memos, Facade facade)
	{
		if(memos.length > 0)
		{
			ArrayList<MemoListener> list = fMemoListeners.get(memos[0].getProject());
			ArrayList<MemoListener> listCopy = new ArrayList<MemoListener>();
			if(list != null)
			{
				for(MemoListener listener : list)
				{
					listCopy.add(listener);
				}
				
				for(MemoListener listener : listCopy)
				{
					listener.memoChanged(cType, memos, facade);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param project
	 * @param listener
	 */
	public void registerMemoListener(Project project, MemoListener listener)
	{
		ArrayList<MemoListener> list = fMemoListeners.get(project);
		
		if(list == null)
		{
			list = new ArrayList<MemoListener>();
		}
		
		list.add(listener);
		fMemoListeners.put(project, list);
	}
	
	/**
	 * 
	 * @param project
	 * @param listener
	 */
	public void unregisterMemoListener(Project project, MemoListener listener)
	{
		ArrayList<MemoListener> list = fMemoListeners.get(project);
		
		if(list != null)
		{
			list.remove(listener);
		}
	}
	
	/**
	 * Updates the listener Maps with the new project.
	 * @param oldName
	 * @param newProject
	 */
	public void handleProjectNameChange(String oldName, Project newProject)
	{
		updateMap(oldName, newProject, fProjectListeners);
		
		updateMap(oldName, newProject, fCodeListeners);
		
		updateMap(oldName, newProject, fInvestigatorListeners);
		
		updateMap(oldName, newProject, fParticipantListeners);
		
		updateMap(oldName, newProject, fTranscriptListeners);
		
		updateMap(oldName, newProject, fMemoListeners);
	}

	/**
	 * @param <T>
	 * @param oldName
	 * @param newProject
	 */
	private <T> void updateMap(String oldName, Project newProject, HashMap<Project, ArrayList<T>> map)
	{
		for(Project project : map.keySet())
		{
			if(project.getName().equals(oldName))
			{
				ArrayList<T> list = map.remove(project);
				map.put(newProject, list);
				break;
			}
		}
	}
}
