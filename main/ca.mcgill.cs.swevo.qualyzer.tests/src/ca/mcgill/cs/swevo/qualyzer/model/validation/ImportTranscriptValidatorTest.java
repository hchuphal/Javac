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

import org.junit.Test;

public class ImportTranscriptValidatorTest
{
	/**
	 * Verifies that the transcript file name is not empty.
	 */
	@Test
	public void testEmptyTranscriptFileName()
	{
		ImportTranscriptValidator lValidator = new ImportTranscriptValidator("");
		assertFalse(lValidator.isValid());
		assertEquals(MessagesClient.getString("model.validation.ImportTranscriptValidator.chooseFile", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the transcript file name is not bogus.
	 */
	@Test
	public void testBogusTranscriptFileName()
	{
		ImportTranscriptValidator lValidator = new ImportTranscriptValidator("bogus.txt");
		assertFalse(lValidator.isValid());
		assertEquals(MessagesClient.getString("model.validation.ImportTranscriptValidator.chooseFile", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that a valid transcript file name gets validated.
	 */
	@Test
	public void testValidTranscriptFileName()
	{
		ImportTranscriptValidator lValidator = new ImportTranscriptValidator("Transcript.txt");
		assertTrue(lValidator.isValid());
	}
}
