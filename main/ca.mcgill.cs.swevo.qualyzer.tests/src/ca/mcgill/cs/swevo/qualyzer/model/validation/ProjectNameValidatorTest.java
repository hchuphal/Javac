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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class ProjectNameValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_PROJECT_NAME2 = "TestProject2";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	
	private static final String LONG_NAME = "AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBB";
	
	private Facade fFacade;
	private Project fProject;
	private Project fProject2;
	
	@Before
	public void setUp()
	{
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		fProject2 = fFacade.createProject(TEST_PROJECT_NAME2, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
	}

	@After
	public void tearDown()
	{
		fFacade.deleteProject(fProject);
		fFacade.deleteProject(fProject2);
	}
	
	/**
	 * Verifies that the name is not empty.
	 */
	@Test
	public void testEmptyName()
	{
		ProjectNameValidator lValidator = new ProjectNameValidator("", TEST_PROJECT_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Project name " + MessagesClient.getString("model.validation.BasicNameValidator.empty", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name follows alphanumerical+
	 */
	@Test
	public void testNameFormat()
	{
		ProjectNameValidator lValidator = new ProjectNameValidator("Mickey Mouse!!", TEST_PROJECT_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Project name " + MessagesClient.getString("model.validation.BasicNameValidator.invalid", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name can be the current name
	 */
	@Test
	public void testProjectUniqueName1()
	{
		ProjectNameValidator lValidator = new ProjectNameValidator(TEST_PROJECT_NAME, TEST_PROJECT_NAME, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name cannot be of an existing memo.
	 */
	@Test
	public void testProjectUniqueName2()
	{
		ProjectNameValidator lValidator = new ProjectNameValidator(TEST_PROJECT_NAME2, TEST_PROJECT_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Project name " + MessagesClient.getString("model.validation.BasicNameValidator.taken", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid project is indeed valid
	 */
	@Test
	public void testValid()
	{
		ProjectNameValidator lValidator = new ProjectNameValidator("NewProject", TEST_PROJECT_NAME, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name is not tooLong.
	 */
	@Test
	public void testTooLong()
	{
		ProjectNameValidator lValidator = new ProjectNameValidator(LONG_NAME, null, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Project name " + MessagesClient.getString("model.validation.BasicNameValidator.tooLong", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
}
