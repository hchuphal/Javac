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
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 *
 */
public class CodeChooserValidatorTest
{

	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	
	private static final String TEST_CODE = "Code1";
	private static final String LONG_NAME = "AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
											"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
											"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
											"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
											"AAAAAAAAAABBBBBB";
	
	private Facade fFacade;
	
	private Project fProject;
	
	/**
	 * 
	 */
	@Before
	public void setUp()
	{
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		fFacade.createCode(TEST_CODE, "", fProject);
	}

	/**
	 * 
	 */
	@After
	public void tearDown()
	{
		fFacade.deleteProject(fProject);
	}
	
	/**
	 * Verifies that the name is not empty.
	 */
	@Test
	public void testEmpty()
	{
		CodeChooserValidator lValidator = new CodeChooserValidator("", fProject, null);
		assertFalse(lValidator.isValid());
		assertEquals("Code name " + MessagesClient.getString("model.validation.BasicNameValidator.empty", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name is not tooLong.
	 */
	@Test
	public void testTooLong()
	{
		CodeChooserValidator lValidator = new CodeChooserValidator(LONG_NAME, fProject, null);
		assertFalse(lValidator.isValid());
		assertEquals("Code name " + MessagesClient.getString("model.validation.BasicNameValidator.tooLong", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name follows alphanumerical+
	 */
	@Test
	public void testFormat()
	{
		CodeChooserValidator lValidator = new CodeChooserValidator("Moon \\Indigo", fProject, null);
		assertFalse(lValidator.isValid());
		assertEquals("Code name " + MessagesClient.getString("model.validation.BasicNameValidator.invalid", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name does not already exist
	 */
	@Test
	public void testUniqueName()
	{
		CodeChooserValidator lValidator = new CodeChooserValidator(TEST_CODE, fProject, null);
		assertFalse(lValidator.isValid());
		assertEquals("Code name " + MessagesClient.getString("model.validation.BasicNameValidator.taken", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid code is indeed validated
	 */
	@Test
	public void testValid()
	{
		CodeChooserValidator lValidator = new CodeChooserValidator("NewCode", fProject, null);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a code that is taken by a fragment will be refused.
	 */
	@Test
	public void testTaken()
	{
		Participant p = Facade.getInstance().createParticipant("id", "name", fProject);
		List<Participant> list = new ArrayList<Participant>();
		list.add(p);
		Transcript t = Facade.getInstance().createTranscript("tran", "01/01/1989", "", list, fProject);
		Fragment fragment = Facade.getInstance().createFragment(t, 0, 1);
		CodeEntry entry = new CodeEntry();
		entry.setCode(fProject.getCodes().get(0));
		fragment.getCodeEntries().add(entry);
		
		CodeChooserValidator lValidator = new CodeChooserValidator(TEST_CODE, fProject, fragment);
		
		assertFalse(lValidator.isValid());
		assertEquals(lValidator.getErrorMessage(), MessagesClient.getString("model.validator.CodeChooserValidator.codeAttached", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"));
	}
	
	/**
	 * Tests that a code that is not taken by a fragment will be accepted.
	 */
	@Test
	public void testTakenValid()
	{
		Participant p = Facade.getInstance().createParticipant("id", "name", fProject);
		List<Participant> list = new ArrayList<Participant>();
		list.add(p);
		Transcript t = Facade.getInstance().createTranscript("tran", "01/01/1989", "", list, fProject);
		Fragment fragment = Facade.getInstance().createFragment(t, 0, 1);
		CodeEntry entry = new CodeEntry();
		entry.setCode(fProject.getCodes().get(0));
		fragment.getCodeEntries().add(entry);
		
		CodeChooserValidator lValidator = new CodeChooserValidator("NewCode", fProject, fragment);
		
		assertTrue(lValidator.isValid());
		assertEquals(lValidator.getErrorMessage(), null);
	}
}
