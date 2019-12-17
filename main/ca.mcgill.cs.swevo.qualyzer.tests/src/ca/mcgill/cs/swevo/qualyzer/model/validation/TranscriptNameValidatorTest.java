/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Robillard
 *******************************************************************************/
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
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

public class TranscriptNameValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	private static final String TEST_PARTICIPANT_NAME = "Jaffy";
	private static final String TEST_PARTICIPANT_ID = "P01";
	private static final String TEST_AUDIO_FILE_NAME1 = "Test.mp3";
	private static final String TEST_TRANSCRIPT_NAME = "Transcript1";
	private static final String TEST_TRANSCRIPT_NAME2 = "Transcript2";
	
	private static final String LONG_NAME = "AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBB";
	
	private Facade fFacade;
	private Project fProject;
	
	@Before
	public void setUp()
	{
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		Participant lBob = fFacade.createParticipant(TEST_PARTICIPANT_ID, TEST_PARTICIPANT_NAME, fProject);
		List<Participant> lParticipants = new ArrayList<Participant>();
		lParticipants.add(lBob);
		fFacade.createTranscript(TEST_TRANSCRIPT_NAME, "", TEST_AUDIO_FILE_NAME1, lParticipants, fProject);
		fFacade.createTranscript(TEST_TRANSCRIPT_NAME2, "", TEST_AUDIO_FILE_NAME1, lParticipants, fProject);
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
		TranscriptNameValidator lValidator = new TranscriptNameValidator("", TEST_TRANSCRIPT_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Transcript name " + MessagesClient.getString("model.validation.BasicNameValidator.empty", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name follows alphanumerical+
	 */
	@Test
	public void testNameFormat()
	{
		TranscriptNameValidator lValidator = new TranscriptNameValidator("Mickey Mouse!!", TEST_TRANSCRIPT_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Transcript name " + MessagesClient.getString("model.validation.BasicNameValidator.invalid", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name can be the current name
	 */
	@Test
	public void testInvestigatorUniqueName1()
	{
		TranscriptNameValidator lValidator = new TranscriptNameValidator(TEST_TRANSCRIPT_NAME, TEST_TRANSCRIPT_NAME, fProject);
		assertTrue(lValidator.isValid());
	}
	
	/**
	 * Verifies that the name cannot be of an existing transcript.
	 */
	@Test
	public void testInvestigatorUniqueName2()
	{
		TranscriptNameValidator lValidator = new TranscriptNameValidator(TEST_TRANSCRIPT_NAME2, TEST_TRANSCRIPT_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Transcript name " + MessagesClient.getString("model.validation.BasicNameValidator.taken", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests with a null old name.
	 */
	@Test
	public void testInvestigatorUniqueName3()
	{
		TranscriptNameValidator lValidator = new TranscriptNameValidator("Ziggy", null, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid transcript is indeed valid
	 */
	@Test
	public void testValid()
	{
		TranscriptNameValidator lValidator = new TranscriptNameValidator("NewTranscript", TEST_TRANSCRIPT_NAME, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name is not tooLong.
	 */
	@Test
	public void testTooLong()
	{
		TranscriptNameValidator lValidator = new TranscriptNameValidator(LONG_NAME, null, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Transcript name " + MessagesClient.getString("model.validation.BasicNameValidator.tooLong", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
}
