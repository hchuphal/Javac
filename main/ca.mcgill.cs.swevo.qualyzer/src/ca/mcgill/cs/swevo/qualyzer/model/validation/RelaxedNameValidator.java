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

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Implements name validation behavior that is more relaxed than
 * The basic name validator. Specifically, verifies that:
 * - A name is not empty
 * - A name is not already in use (except if it's oldName) (but the function to verify this is 
 * a do nothing function that in most cases should be overriden)
 * - A name only contains letter characters, digits, spaces, or underscores
 * - A name is shorter than 256 characters long.
 */
public class RelaxedNameValidator extends BasicNameValidator
{
	/**
	 * Constructs a new RelaxedNameValidator.
	 * @param pLabel The label that appears before the error message (e.g., "Investigator ID").
	 * @param pName The name chosen for the new code.
	 * @param pOldName The current name of the code (null if this is a new code).
	 * @param pProject The Project in which the code is to be created.
	 */
	protected RelaxedNameValidator(String pLabel, String pName, String pOldName, Project pProject)
	{
		super(pLabel, pName, pOldName, pProject);
	}
	
	/**
	 * Constructs a new RelaxedNameValidator with a null old name.
	 * @param pLabel The label that appears before the error message (e.g., "Investigator ID").
	 * @param pName The name chosen for the new code.
	 * @param pProject The Project in which the code is to be created.
	 */
	public RelaxedNameValidator(String pLabel, String pName, Project pProject)
	{
		this(pLabel, pName, null, pProject);
	}

	/**
	 * Step method of the template method design pattern. Defines the 
	 * exact format for names. Override to change the format.
	 * @param pName The name to validate
	 * @return True if the name is valid
	 */
	protected boolean validateName(String pName)
	{
		return ValidationUtils.verifyIDRelaxed(fName);
	}
}