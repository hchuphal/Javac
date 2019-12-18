/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 */
public class ValidationUtilsTest
{
	/**
	 * Verify Ids.
	 */
	@Test
	public void verifyIDTest()
	{
		assertTrue(ValidationUtils.verifyID("aA1_-"));
		assertTrue(ValidationUtils.verifyID("aasdl_sSDFA-3425"));
		
		assertFalse(ValidationUtils.verifyID(""));
		assertFalse(ValidationUtils.verifyID("\'"));
		assertFalse(ValidationUtils.verifyID("!"));
		assertFalse(ValidationUtils.verifyID("^"));
		assertFalse(ValidationUtils.verifyID("ffffsadfsadfieurASDFSDF84375987 ,"));
	}
}
