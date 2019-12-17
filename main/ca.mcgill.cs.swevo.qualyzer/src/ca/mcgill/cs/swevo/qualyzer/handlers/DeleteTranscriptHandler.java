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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.TranscriptDeleteDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Hander for the delete transcript command.
 *
 */
public class DeleteTranscriptHandler extends AbstractHandler implements ITestableHandler
{
	private static final String TRANSCRIPT = File.separator + "transcripts" + File.separator; //$NON-NLS-1$

	private final Logger fLogger = LoggerFactory.getLogger(DeleteTranscriptHandler.class);

	private IDialogTester fTester = new NullTester();

	private boolean fTesting = false;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = view.getCommonViewer().getSelection();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if(selection != null && selection instanceof IStructuredSelection)
		{
			List<Transcript> toDelete = new ArrayList<Transcript>();
			List<Project> projects = new ArrayList<Project>();
			List<String> conflicts = new ArrayList<String>();
			for(Object element : ((IStructuredSelection) selection).toArray())
			{
				if(element instanceof Transcript)
				{
					Transcript transcript = (Transcript) element;
					
					if(!projects.contains(transcript.getProject()))
					{
						projects.add(transcript.getProject());
					}
					
					String conflict = checkForConflicts(transcript);
					if(conflict != null)
					{
						conflicts.add(conflict);
					}
					toDelete.add(transcript);	
				}
			}
			if(projects.size() > 1)
			{
				String warningMessage = MessagesClient.getString(
						"handlers.DeleteTranscriptHandler.multipleProjects", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
				fLogger.warn(warningMessage);
				MessageDialog.openError(shell, MessagesClient.getString(
						"handlers.DeleteTranscriptHandler.deleteFailed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), warningMessage); //$NON-NLS-1$
			}
			else if(!conflicts.isEmpty())
			{
				String message = buildString(conflicts);
				MessageDialog.openError(shell, MessagesClient.getString(
						"handlers.DeleteTranscriptHandler.unableToDelete", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), message); //$NON-NLS-1$
			}
			else
			{
				proceedWithDeletion(page, shell, toDelete);
			}
		}
		return null;
	}
	
	/**
	 * @param conflicts
	 * @return
	 */
	private String buildString(List<String> conflicts)
	{
		String message = MessagesClient.getString("handlers.DeleteTranscriptHandler.conflicts", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
		
		for(String string : conflicts)
		{
			message += string;
		}
		
		return message;
	}

	/**
	 * Checks that no Memos reference the transcript.
	 * @param transcript
	 * @return
	 */
	private String checkForConflicts(Transcript transcript)
	{
		String conflict = ""; //$NON-NLS-1$
		
		for(Memo memo : transcript.getProject().getMemos())
		{
			if(transcript.equals(memo.getTranscript()))
			{
				if(!conflict.isEmpty())
				{
					conflict += "\n"; //$NON-NLS-1$
				}
				conflict += MessagesClient.getString(
						"handlers.DeleteTranscriptHandler.transcript", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")+transcript.getName(); //$NON-NLS-1$
				conflict += MessagesClient.getString("handlers.DeleteTranscriptHandler.memo", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + memo.getName(); //$NON-NLS-1$
			}
		}
		
		return conflict.isEmpty() ? null : conflict;
	}

	/**
	 * @param page
	 * @param shell
	 * @param toDelete
	 */
	private void proceedWithDeletion(IWorkbenchPage page, Shell shell, List<Transcript> toDelete)
	{	
		TranscriptDeleteDialog dialog = new TranscriptDeleteDialog(shell, toDelete.size() > 1);
		dialog.create();
		dialog.setBlockOnOpen(!fTesting);
		dialog.open();
		
		fTester.execute(dialog);
		
		if(dialog.getReturnCode() == Window.OK)
		{	
			for(Transcript transcript : toDelete)
			{
				delete(transcript, dialog.getDeleteCodes(), 
						dialog.getDeleteParticipants(), shell);
									
				CommonNavigator view;
				view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
				view.getCommonViewer().refresh();
			}
		}
		
	}

	/**
	 * @param transcript
	 * @param deleteAudio
	 * @param deleteCodes
	 * @param deleteParticipants
	 */
	private void delete(Transcript transcript, boolean deleteCodes, boolean deleteParticipants,
			Shell shell)
	{
		Project project = transcript.getProject();
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getFolderName());
		ArrayList<Participant> participants = null;
		ArrayList<Code> codes = null;
		
		if(deleteParticipants)
		{
			participants = deleteParticipants(transcript);
		}
		
		if(deleteCodes)
		{
			codes = deleteCodes(transcript);
		}
		
		File audioFile = null;
		if(transcript.getAudioFile() != null)
		{
			audioFile = new File(wProject.getLocation() + transcript.getAudioFile().getRelativePath());
		}
		
		File file = new File(wProject.getLocation() + TRANSCRIPT + transcript.getFileName());
		if(!file.delete())
		{
			String warningMessage = MessagesClient.getString(
					"handlers.DeleteTranscriptHandler.transcriptDeleteFailed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
			fLogger.warn(warningMessage);
			MessageDialog.openWarning(shell, MessagesClient.getString(
					"handlers.DeleteTranscriptHandler.fileAccess", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), warningMessage); //$NON-NLS-1$
		}
		
		Facade.getInstance().deleteTranscript(transcript);
		deleteCodesAndParticipants(codes, participants);
		
		if(audioFile != null && !audioFile.delete())
		{
			String warningMessage = MessagesClient.getString(
					"handlers.DeleteTranscriptHandler.audioDeleteFailed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
			fLogger.warn(warningMessage);
			MessageDialog.openWarning(shell, MessagesClient.getString(
					"handlers.DeleteTranscriptHandler.fileAccess", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), warningMessage); //$NON-NLS-1$
		}
	}

	/**
	 * @param codes
	 * @param participants
	 */
	private void deleteCodesAndParticipants(ArrayList<Code> codes, ArrayList<Participant> participants)
	{
		if(participants != null)
		{
			for(Participant p : participants)
			{
				Facade.getInstance().deleteParticipant(p);
			}
		}
		
		if(codes != null)
		{
			for(Code code : codes)
			{
				Facade.getInstance().deleteCode(code);
			}
		}
		
	}

	/**
	 * @param transcript
	 * @return
	 */
	private ArrayList<Code> deleteCodes(Transcript transcript)
	{
		ArrayList<Code> codes = new ArrayList<Code>();
		Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
		Project project = lTranscript.getProject();
		for(Fragment fragment : lTranscript.getFragments().values())
		{
			for(CodeEntry entry : fragment.getCodeEntries())
			{
				Code code = entry.getCode();
				if(!codes.contains(code))
				{
					codes.add(code);
				}
			}
		}
		for(Transcript pTranscript : project.getTranscripts())
		{
			if(!pTranscript.equals(transcript))
			{
				Transcript lTrans = Facade.getInstance().forceTranscriptLoad(pTranscript);
				for(Fragment fragment : lTrans.getFragments().values())
				{
					for(CodeEntry entry : fragment.getCodeEntries())
					{
						Code code = entry.getCode();
						if(codes.contains(code))
						{
							codes.remove(code);
						}
					}
				}
			}
		}
		for(Memo memo : project.getMemos())
		{
			Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
			for(Fragment fragment : lMemo.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					Code code = entry.getCode();
					if(codes.contains(code))
					{
						codes.remove(code);
					}
				}
			}
		}
		return codes;
	}

	/**
	 * @param transcript
	 * @param project
	 * @param manager 
	 */
	private ArrayList<Participant> deleteParticipants(Transcript transcript)
	{
		ArrayList<Participant> toDelete = new ArrayList<Participant>();
				
		Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
		Project project = lTranscript.getProject();
		for(Participant participant : lTranscript.getParticipants())
		{
			boolean found = false;
			for(Transcript otherTranscript : project.getTranscripts())
			{
				if(!otherTranscript.equals(transcript))
				{
					Transcript lOtherTranscript = Facade.getInstance().forceTranscriptLoad(otherTranscript);
					
					for(Participant otherParticipant : lOtherTranscript.getParticipants())
					{
						if(otherParticipant.equals(participant))
						{
							found = true;
							break;
						}
					}
				}
			}
			if(!found)
			{
				toDelete.add(participant);
			}
		}
				
		return toDelete;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#getTester()
	 */
	@Override
	public IDialogTester getTester()
	{
		return fTester;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#isWindowsBlock()
	 */
	@Override
	public boolean isTesting()
	{
		return fTesting;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setTester(
	 * ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester)
	 */
	@Override
	public void setTester(IDialogTester tester)
	{
		fTester = tester;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setWindowsBlock(boolean)
	 */
	@Override
	public void setTesting(boolean windowsBlock)
	{
		fTesting = windowsBlock;
	}

}