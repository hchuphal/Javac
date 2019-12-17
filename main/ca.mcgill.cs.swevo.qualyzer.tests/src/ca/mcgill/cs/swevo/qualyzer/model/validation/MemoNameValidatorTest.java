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
public class MemoNameValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	private static final String TEST_PARTICIPANT_NAME = "Jaffy";
	private static final String TEST_PARTICIPANT_ID = "P01";
	private static final String TEST_MEMO_NAME = "Transcript1";
	private static final String TEST_MEMO_NAME2 = "Transcript2";
	
	private static final String LONG_NAME = "AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBB";
	
	private Facade fFacade;
	private Project fProject;
	private Investigator fInvestigator;
	
	@Before
	public void setUp()
	{
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		Participant lBob = fFacade.createParticipant(TEST_PARTICIPANT_ID, TEST_PARTICIPANT_NAME, fProject);
		List<Participant> lParticipants = new ArrayList<Participant>();
		lParticipants.add(lBob);
		fFacade.createMemo(TEST_MEMO_NAME, "", fInvestigator, lParticipants, fProject, null, null);
		fFacade.createMemo(TEST_MEMO_NAME2, "", fInvestigator, lParticipants, fProject, null, null);
	}

	@After
	public void tearDown()
	{
		fFacade.deleteProject(fProject);
	}
	
	/**
	 * Verifies that the name is not empty.
	 */
	@Test
	public void testEmptyName()
	{
		MemoNameValidator lValidator = new MemoNameValidator("", TEST_MEMO_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Memo name " + MessagesClient.getString("model.validation.BasicNameValidator.empty", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name follows alphanumerical+
	 */
	@Test
	public void testNameFormat()
	{
		MemoNameValidator lValidator = new MemoNameValidator("Mickey Mouse!!", TEST_MEMO_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Memo name " + MessagesClient.getString("model.validation.BasicNameValidator.invalid", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name can be the current name
	 */
	@Test
	public void testMemoUniqueName1()
	{
		MemoNameValidator lValidator = new MemoNameValidator(TEST_MEMO_NAME, TEST_MEMO_NAME, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name cannot be of an existing memo.
	 */
	@Test
	public void testMemoUniqueName2()
	{
		MemoNameValidator lValidator = new MemoNameValidator(TEST_MEMO_NAME2, TEST_MEMO_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Memo name " + MessagesClient.getString("model.validation.BasicNameValidator.taken", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid memo is indeed valid
	 */
	@Test
	public void testValid()
	{
		MemoNameValidator lValidator = new MemoNameValidator("NewMemo", TEST_MEMO_NAME, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name is not tooLong.
	 */
	@Test
	public void testTooLong()
	{
		MemoNameValidator lValidator = new MemoNameValidator(LONG_NAME, null, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Memo name " + MessagesClient.getString("model.validation.BasicNameValidator.tooLong", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
}
