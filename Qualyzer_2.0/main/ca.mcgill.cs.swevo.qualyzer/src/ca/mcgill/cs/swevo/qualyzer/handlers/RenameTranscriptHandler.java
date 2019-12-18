/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     -Barthelemy Dagenais (bart@cs.mcgill.ca)
 *     -Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;
import ca.mcgill.cs.swevo.qualyzer.model.AudioFile;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * Qualyzer handler for rename (F2).
 * The transcript version. Renames the transcript file, transcript object, and audio file/object.
 *
 */
public class RenameTranscriptHandler extends AbstractHandler implements ITestableHandler
{
	private static final String DOT = "."; //$NON-NLS-1$
	private static final String EXT = ".rtf"; //$NON-NLS-1$
	private static final String TRANSCRIPT = File.separator+"transcripts"+File.separator; //$NON-NLS-1$
	private static final String AUDIO = File.separator+"audio"+File.separator; //$NON-NLS-1$
	
	private boolean fClosed = false;
	private IDialogTester fTester = new NullTester();
	private boolean fTesting = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = view.getCommonViewer().getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			
			if(element instanceof Transcript)
			{
				Project project = ResourcesUtil.getProject(element);
				
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				RenameDialog dialog = new RenameDialog(shell, project);

				dialog.create();
				dialog.setCurrentName(((Transcript) element).getName());
				
				if(trancriptNoLongerExists((Transcript) element))
				{
					MessageDialog.openError(HandlerUtil.getActiveShell(event), 
							MessagesClient.getString("handlers.RenameHandler.fileError", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"),  //$NON-NLS-1$
							MessagesClient.getString("handlers.RenameHandler.transcriptRenamed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")); //$NON-NLS-1$
					return null;
				}
				
				dialog.setBlockOnOpen(!fTesting);
				dialog.open();
				fTester.execute(dialog);
				
				if(dialog.getReturnCode() == Window.OK)
				{
					if(element instanceof Transcript)
					{
						rename((Transcript) element, dialog.getName());
						
						Facade.getInstance().saveTranscript((Transcript) element);	
					}
					view.getCommonViewer().refresh();
					
					if(fClosed)
					{
						ResourcesUtil.openEditor(page, (Transcript) element);
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param element
	 * @return
	 */
	private boolean trancriptNoLongerExists(Transcript element)
	{
		String projectName = element.getProject().getFolderName();
		String projectPath = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getLocation().toString();
		
		File file = new File(projectPath+TRANSCRIPT+element.getFileName());
		
		return !file.exists();
	}

	/**
	 * Carries out the actual renaming operations.
	 * @param transcript
	 * @param name
	 */
	private void rename(Transcript transcript, String name)
	{	
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = activePage.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			String editorName = transcript.getProject().getName() + DOT + Transcript.class.getSimpleName() + DOT;
			if(editor.getName().equals(editorName + transcript.getFileName()))
			{
				activePage.closeEditor(editor.getEditor(true), true);
				fClosed = true;
			}
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(transcript.getProject().getFolderName());
		String projectPath = project.getLocation().toString();
		File origFile = new File(projectPath + TRANSCRIPT + transcript.getFileName());
		File newFile = new File(projectPath + TRANSCRIPT + name.replace(' ', '_') + EXT);
		
		origFile.renameTo(newFile);
		
		AudioFile audio = transcript.getAudioFile();
		if(audio != null)
		{
			origFile = new File(projectPath + audio.getRelativePath());
			
			if(!origFile.exists())
			{
				origFile = getNewAudioFile(projectPath);
			}
			
			String audioExt = audio.getRelativePath().substring(audio.getRelativePath().lastIndexOf('.'));
			newFile = new File(projectPath + AUDIO + name.replace(' ', '_') + audioExt);
			
			origFile.renameTo(newFile);
			
			audio.setRelativePath(AUDIO + name.replace(' ', '_') + audioExt);
		}
		
		transcript.setName(name);
		transcript.setFileName(name.replace(' ', '_')+EXT);
	}

	/**
	 * If the audio file has disapeared asks for a new one.
	 * @param projectPath
	 * @return
	 */
	private File getNewAudioFile(String projectPath)
	{
		File origFile;
		MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				MessagesClient.getString("handlers.RenameHandler.fileError", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), //$NON-NLS-1$
				MessagesClient.getString("handlers.RenameHandler.audioFileGone", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")); //$NON-NLS-1$ 
		
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.setFilterPath(projectPath+AUDIO);
		dialog.setFilterExtensions(new String[]{"*.mp3;*.wav"}); //$NON-NLS-1$
		dialog.setFilterNames(new String[]{MessagesClient.getString(
				"handlers.ImportAudioFileHandler.audioExt", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")}); //$NON-NLS-1$
		
		String fileName = dialog.open();
		origFile = new File(fileName);
		return origFile;
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
