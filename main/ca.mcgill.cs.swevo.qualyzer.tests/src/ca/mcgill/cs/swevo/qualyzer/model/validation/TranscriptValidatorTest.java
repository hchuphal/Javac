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

public class TranscriptValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";
	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	private static final String TEST_PARTICIPANT_NAME = "Jaffy";
	private static final String TEST_PARTICIPANT_ID = "P01";
	private static final String TEST_AUDIO_FILE_NAME1 = "Test.mp3";
	private static final String TEST_AUDIO_FILE_NAME2 = "Test2.mp3";
	private static final String TEST_TRANSCRIPT_NAME = "Transcript1";

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
	}

	@After
	public void tearDown()
	{
		fFacade.deleteProject(fProject);
	}
	
	/**
	 * Verifies that the transcript name is not empty.
	 */
	@Test
	public void testEmptyTranscriptName()
	{
		TranscriptValidator lValidator = new TranscriptValidator("", fProject,1,TEST_AUDIO_FILE_NAME2);
		assertFalse(lValidator.isValid());
		assertEquals("Transcript name " + MessagesClient.getString("model.validation.BasicNameValidator.empty", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the transcript name follows alphanumerical+
	 */
	@Test
	public void testTranscriptNameFormat()
	{
		TranscriptValidator lValidator = new TranscriptValidator("Bing! Crosby", fProject,1,TEST_AUDIO_FILE_NAME2);
		assertFalse(lValidator.isValid());
		assertEquals("Transcript name " + MessagesClient.getString("model.validation.BasicNameValidator.invalid", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the transcript does not already exist
	 */
	@Test
	public void testTranscriptUnique()
	{
		TranscriptValidator lValidator = new TranscriptValidator(TEST_TRANSCRIPT_NAME, fProject,1,TEST_AUDIO_FILE_NAME2);
		assertFalse(lValidator.isValid());
		assertEquals("Transcript name " + MessagesClient.getString("model.validation.BasicNameValidator.taken", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the transcript has at least one participant attached
	 */
	@Test
	public void testAttachedParticipants()
	{
		TranscriptValidator lValidator = new TranscriptValidator("NewTranscript", fProject,0,TEST_AUDIO_FILE_NAME2);
		assertFalse(lValidator.isValid());
		assertEquals(MessagesClient.getString("model.validation.TranscriptValidator.selectOne", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the transcript can be created with an empty audio file.
	 */
	@Test
	public void testEmptyAudioFile()
	{
		TranscriptValidator lValidator = new TranscriptValidator("NewTranscript", fProject,1,"");
		assertTrue(lValidator.isValid());
	}
	
	/**
	 * Verifies that the transcript cannot be created with an audio file name that does not exist.
	 */
	@Test
	public void testBogusAudioFile()
	{
		TranscriptValidator lValidator = new TranscriptValidator("NewTranscript", fProject,1,"Bogus.mp3");
		assertFalse(lValidator.isValid());
		assertEquals(MessagesClient.getString("model.validation.TranscriptValidator.enterAudioName", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid transcript is indeed validated with an audio file
	 */
	@Test
	public void testValidTranscript()
	{
		TranscriptValidator lValidator = new TranscriptValidator("NewTranscript", fProject,1,TEST_AUDIO_FILE_NAME1);
		assertTrue(lValidator.isValid());
	}
}
