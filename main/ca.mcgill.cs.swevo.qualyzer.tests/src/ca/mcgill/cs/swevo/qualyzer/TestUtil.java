/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Name - Initial Contribution
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public final class TestUtil
{
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	private TestUtil()
	{

	}

	public static final Project createProject(String projectName, String investigatorName, String participantId,
			String transcriptName)
	{
		Facade facade = Facade.getInstance();
		Project project = facade.createProject(projectName, investigatorName, investigatorName, "");
		Participant participant = facade.createParticipant(participantId, "", project);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		FileUtil.setupTranscriptFiles(transcriptName, projectName, "", ""); //$NON-NLS-1$
		Transcript transcript = facade.createTranscript(transcriptName, "01/01/2001", "", participants, project);
		fillFile(project.getName(), transcript.getFileName());
		return project;
	}
	
	public static final Project createProject(String projectName, String investigatorName, String memoName)
	{
		Facade facade = Facade.getInstance();
		Project project = facade.createProject(projectName, investigatorName, investigatorName, "");
		
		FileUtil.setupMemoFiles(memoName, projectName, "");
		facade.createMemo(memoName, "01/01/1989", project.getInvestigators().get(0), new ArrayList<Participant>(), 
				project, null, null);
		
		return project;
	}

	private static final void fillFile(String projectName, String fileName)
	{
		try
		{
			IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			proj.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			IFile file = proj.getFile("transcripts" + File.separator + fileName);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
			bw
					.write("{\\rtf1\\ansi\\deff0\n\n\n"
							+ "{\\*\\generator Msftedit 5.41.21.2500;}\\viewkind4\\uc1\\pard\\f0\\fs24 I want to test manually writing an rtf doc. If this works then I will be very happy. This is also written in MSWord so it is going to have lots of extra tags that will probably confuse things a little bit.\\par\n"
							+ "\\par\n"
							+ "\\tab Sadly, I don't think that the tab will show up without me adding it in myself.\\par\n"
							+ "\\par\n"
							+ "Let's see if the space between these two lines shows up without me having to do anything fancy.\\par\n"
							+ "\\par\n" + "\\b This should be bold\\b0 .\\par\n" + "\\par\n"
							+ "\\i This should be in italics.\\par\n" + "\\i0\\par\n"
							+ "\\ul This should be underlined.\\par\n" + "\\par\n"
							+ "\\b This should be underlined and bold.\\b0\\par\n" + "\\par\n"
							+ "\\i This should be underlined and in italics.\\ulnone\\i0\\par\n" + "\\par\n"
							+ "\\b\\i This should be bold and in italics.\\b0\\i0\\par\n" + "\\f1\\fs20\\par\n"
							+ "}\n\0\n");
			bw.close();
			file.setContents(new ByteArrayInputStream(outputStream.toByteArray()), true, false,
					new NullProgressMonitor());
		}
		catch (Exception e)
		{
			// Ok because in testing :-)
			e.printStackTrace();
		}
	}

	public static final boolean codeExists(Project project, String codeName) {
		boolean exists = false;
		for (Code code: project.getCodes())
		{
			if (code.getCodeName().equals(codeName))
			{
				exists = true;
				break;
			}
		}
		return exists;
	}
	
	public static final void setProjectExplorerSelection(Object element)
	{
		CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		view.getCommonViewer().refresh();
		view.getCommonViewer().expandAll();
		IStructuredSelection selection = new StructuredSelection(element);
		view.selectReveal(selection);
	}
	
	public static final void setProjectExplorerSelection(Object[] elements)
	{
	CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
	view.getCommonViewer().refresh();
	view.getCommonViewer().expandAll();
	IStructuredSelection selection = new StructuredSelection(elements);
	view.selectReveal(selection);
	}
	
	public static final String readFile(File file)
	{
		BufferedInputStream bin = null;
		StringBuilder builder = new StringBuilder();

		try
		{
			// create FileInputStream object
			FileInputStream fin = new FileInputStream(file);

			// create object of BufferedInputStream
			bin = new BufferedInputStream(fin);

			// create a byte array
			byte[] contents = new byte[1024];

			int bytesRead = 0;
			while ((bytesRead = bin.read(contents)) != -1)
			{

				builder.append(new String(contents, 0, bytesRead));
			}

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			// close the BufferedInputStream using close method
			try
			{
				if (bin != null)
					bin.close();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}

		}
		
		return builder.toString();
		
	}
}
