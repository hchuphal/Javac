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

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class MemoValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	private static final String TEST_PARTICIPANT_NAME = "Jaffy";
	private static final String TEST_PARTICIPANT_ID = "P01";
	private static final String TEST_MEMO_NAME = "Memo1";

	private Facade fFacade;
	private Project fProject;
	private Investigator fInvestigator;
	
	@Before
	public void setUp()
	{
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		fInvestigator = fProject.getInvestigators().get(0);
		Participant lBob = fFacade.createParticipant(TEST_PARTICIPANT_ID, TEST_PARTICIPANT_NAME, fProject);
		List<Participant> lParticipants = new ArrayList<Participant>();
		lParticipants.add(lBob);
		fFacade.createMemo(TEST_MEMO_NAME, "", fInvestigator, lParticipants, fProject, null, null);
	}

	@After
	public void tearDown()
	{
		fFacade.deleteProject(fProject);
	}
	
	/**
	 * Verifies that the memo name is not empty.
	 */
	@Test
	public void testEmptyMemoName()
	{
		MemoValidator lValidator = new MemoValidator("", fInvestigator, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Memo name " + MessagesClient.getString("model.validation.BasicNameValidator.empty", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the memo name follows alphanumerical+
	 */
	@Test
	public void testMemoNameFormat()
	{
		MemoValidator lValidator = new MemoValidator("Bing! Crosby", fInvestigator, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Memo name " + MessagesClient.getString("model.validation.BasicNameValidator.invalid", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the memo does not already exist
	 */
	@Test
	public void testMemotUnique()
	{
		MemoValidator lValidator = new MemoValidator(TEST_MEMO_NAME, fInvestigator, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Memo name " + MessagesClient.getString("model.validation.BasicNameValidator.taken", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the memo has an investigator attached
	 */
	@Test
	public void testAttachedInvestigator()
	{
		MemoValidator lValidator = new MemoValidator("NewMemo", null, fProject);
		assertFalse(lValidator.isValid());
		assertEquals(MessagesClient.getString("model.validation.MemoValidator.chooseAuthor", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid memo is indeed validated
	 */
	@Test
	public void testValidMemo()
	{
		MemoValidator lValidator = new MemoValidator("NewMemo", fInvestigator, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null, lValidator.getErrorMessage());
	}
}
