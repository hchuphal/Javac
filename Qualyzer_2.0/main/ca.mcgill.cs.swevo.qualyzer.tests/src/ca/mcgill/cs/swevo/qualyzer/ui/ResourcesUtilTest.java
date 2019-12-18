/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperInvestigator;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperMemo;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperTranscript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ResourcesUtilTest
{
	private static final String PROJECT_NAME = "ResourcesProject";
	private static final String INVEST_NAME = "InvestNickName";
	
	private Project fProject;
	
	@Before
	public void setUp()
	{
		fProject = Facade.getInstance().createProject(PROJECT_NAME, INVEST_NAME, "", "");
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(true);
	}

	/**
	 * Test the getProject() method.
	 */
	@Test
	public void getProjectTest()
	{
		
		Investigator i = Facade.getInstance().createInvestigator("bob", "", "", fProject, true);
		
		Participant p = Facade.getInstance().createParticipant("pBob", "", fProject);
		
		Memo m = Facade.getInstance().createMemo("mmeo", "", i, new ArrayList<Participant>(), fProject, null, null);
		
		ArrayList<Participant> parts = new ArrayList<Participant>();
		parts.add(p);
		Transcript t = Facade.getInstance().createTranscript("trans", "", "", parts, fProject);
		
		Code c = Facade.getInstance().createCode("code", "", fProject);
		
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getName());
		
		WrapperInvestigator wI = new WrapperInvestigator(fProject);
		WrapperParticipant wP = new WrapperParticipant(fProject);
		WrapperMemo wM = new WrapperMemo(fProject);
		WrapperCode wC = new WrapperCode(fProject);
		WrapperTranscript wT = new WrapperTranscript(fProject);
		
		assertEquals(ResourcesUtil.getProject(i), fProject);
		assertEquals(ResourcesUtil.getProject(p), fProject);
		assertEquals(ResourcesUtil.getProject(m), fProject);
		assertEquals(ResourcesUtil.getProject(t), fProject);
		assertEquals(ResourcesUtil.getProject(c), fProject);
		assertEquals(ResourcesUtil.getProject(wI), fProject);
		assertEquals(ResourcesUtil.getProject(wP), fProject);
		assertEquals(ResourcesUtil.getProject(wM), fProject);
		assertEquals(ResourcesUtil.getProject(wT), fProject);
		assertEquals(ResourcesUtil.getProject(wC), fProject);
		assertEquals(ResourcesUtil.getProject(wProject), fProject);

	}
	
	/**
	 * test open investigator editor.
	 */
	@Test
	public void openInvestigatorEditorTest()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Investigator i = Facade.getInstance().createInvestigator("Jon", "Jonathan Faubert", "McGill University", fProject, true);
		
		ResourcesUtil.openEditor(page, i);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			if(editor.getName().equals(fProject.getName() + ".editor.investigator."+i.getNickName()))
			{
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
	
	/**
	 * test open participant editor.
	 */
	@Test
	public void openParticipantEditorTest()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Participant p = Facade.getInstance().createParticipant("jon", "Jonathan Faubert", fProject);
		
		ResourcesUtil.openEditor(page, p);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			if(editor.getName().equals(fProject.getName() + ".editor.participant."+p.getParticipantId()))
			{
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
	
	@Test
	public void openCodeEditorTest()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		WrapperCode codes = new WrapperCode(fProject);
		
		ResourcesUtil.openEditor(page, codes);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			if(editor.getName().equals("editor.code."+fProject.getName()))
			{
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
	
	@Test
	public void openFragmentViewerTest()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		Code code = Facade.getInstance().createCode("codeID", "desc", fProject);
		
		ResourcesUtil.openEditor(page, code);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			if(editor.getName().equals(fProject.getName() + ".fragmentViewer."+code.getCodeName()))
			{
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
	
	@Test
	public void openMemoEditorTest()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		Memo memo = Facade.getInstance().createMemo("memo", "", fProject.getInvestigators().get(0), null, fProject, null, null);
		FileUtil.setupMemoFiles(memo.getName(), fProject.getName(), "");
		
		ResourcesUtil.openEditor(page, memo);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			try
			{
				String one = editor.getEditorInput().getName();
				String two = fProject.getName() + "." + memo.getClass().getSimpleName()+"."+memo.getFileName();
				if(one.equals(two))
				{
					found = true;
					break;
				}
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
				fail();
			}
		}
		
		assertTrue(found);
	}
	
	@Test
	public void openDocumentEditorTest()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		Memo memo = Facade.getInstance().createMemo("memo", "", fProject.getInvestigators().get(0), null, fProject, null, null);
		FileUtil.setupMemoFiles(memo.getName(), fProject.getName(), "");
		
		Participant p = Facade.getInstance().createParticipant("participant", "", fProject);
		ArrayList<Participant> parts = new ArrayList<Participant>();
		parts.add(p);
		
		Transcript transcript = Facade.getInstance().createTranscript("transcript", "", "", parts, fProject);
		FileUtil.setupTranscriptFiles(transcript.getName(), fProject.getName(), "", "");
		
		IAnnotatedDocument document = memo;
		ResourcesUtil.openEditor(page, document);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			try
			{
				if(editor.getEditorInput().getName().equals(fProject.getName()+"."+memo.getClass().getSimpleName()+"."+memo.getFileName()))
				{
					found = true;
					break;
				}
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
				fail();
			}
		}
		
		assertTrue(found);
		
		document = transcript;
		ResourcesUtil.openEditor(page, document);
		
		found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			try
			{
				if(editor.getEditorInput().getName().equals(fProject.getName()+"."+Transcript.class.getSimpleName()+"."+transcript.getFileName()))
				{
					found = true;
					break;
				}
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
				fail();
			}
		}
		
		assertTrue(found);
	}
}
