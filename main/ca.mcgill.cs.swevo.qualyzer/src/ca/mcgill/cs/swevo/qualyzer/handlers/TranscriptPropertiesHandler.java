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
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.TranscriptPropertiesDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;
import ca.mcgill.cs.swevo.qualyzer.model.AudioFile;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * Opens the transcript properties dialog and then saves any changes that are made.
 */
public class TranscriptPropertiesHandler extends AbstractHandler implements ITestableHandler
{

	private boolean fTesting = false;
	private IDialogTester fTester = new NullTester();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = view.getCommonViewer().getSelection();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			TranscriptPropertiesDialog dialog = new TranscriptPropertiesDialog(shell, (Transcript) element);
			
			dialog.setBlockOnOpen(!fTesting);
			dialog.open();
			fTester.execute(dialog);
			if(dialog.getReturnCode() == Window.OK)
			{
				Transcript transcript = (Transcript) element;
				String projectName = transcript.getProject().getFolderName();
				String newDate = dialog.getDate();
				String audioFile = dialog.getAudioFile();
				
				String oldAudio = ""; //$NON-NLS-1$
				if(transcript.getAudioFile() != null)
				{
					String projectPath = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName).getLocation() + ""; //$NON-NLS-1$
					
					oldAudio = projectPath + transcript.getAudioFile().getRelativePath();
				}
				
				copyNewAudioFile(transcript, audioFile, oldAudio);
								
				transcript.setDate(newDate);
				transcript.setParticipants(dialog.getParticipants());
								
				Facade.getInstance().saveTranscript(transcript);
				view.getCommonViewer().refresh();
			}
		}
		
		return null;
	}

	/**
	 * @param transcript
	 * @param audioFile
	 * @param oldAudio
	 */
	private void copyNewAudioFile(Transcript transcript, String audioFile, String oldAudio)
	{
		if(!oldAudio.equals(audioFile))
		{
			if(!audioFile.isEmpty())
			{
				AudioFile aFile = new AudioFile();
				String relativePath = File.separator+"audio"+File.separator; //$NON-NLS-1$
				relativePath += transcript.getName().replace(' ', '_') + 
					audioFile.substring(audioFile.lastIndexOf('.'));
				aFile.setRelativePath(relativePath);
				transcript.setAudioFile(aFile);
						
				String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation()+File.separator;
				File input = new File(audioFile);
				String dest = workspacePath + File.separator + transcript.getProject().getFolderName();
				dest = dest + relativePath;
				File output = new File(dest);
				try
				{
					FileUtil.copyFile(input, output);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				transcript.setAudioFile(null);
			}
		}
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
