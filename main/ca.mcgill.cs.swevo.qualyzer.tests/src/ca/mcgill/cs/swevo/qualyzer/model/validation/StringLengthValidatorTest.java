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

public class StringLengthValidatorTest
{
	private static final String LONG_NAME = "AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBB";
	
	/**
	 * Empty String
	 */
	@Test
	public void testEmpty()
	{
		StringLengthValidator lValidator = new StringLengthValidator("Empty", "");
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Normal String
	 */
	@Test
	public void testNormal()
	{
		StringLengthValidator lValidator = new StringLengthValidator("Normal", "Normal");
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
	
	/**
	 * Too long string.
	 */
	@Test
	public void testTooLong()
	{
		StringLengthValidator lValidator = new StringLengthValidator("Too long", LONG_NAME);
		assertFalse(lValidator.isValid());
		assertEquals("Too long must be shorter than 256 characters.",lValidator.getErrorMessage());
	}
}
