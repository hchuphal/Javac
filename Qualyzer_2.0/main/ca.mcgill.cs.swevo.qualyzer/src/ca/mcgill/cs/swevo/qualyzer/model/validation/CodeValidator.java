/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Robillard
 *     Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates the business rules when a new code is created.
 */
public class CodeValidator extends RelaxedNameValidator
{
	/**
	 * Constructs a new CodeValidator.
	 * @param pName The name chosen for the new code.
	 * @param pOldName The current name of the code (null if this is a new code).
	 * @param pProject The Project in which the code is to be created.
	 */
	public CodeValidator(String pName, String pOldName, Project pProject)
	{
		super(MessagesClient.getString("model.validation.CodeValidator.label", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"), pName, pOldName, pProject); //$NON-NLS-1$
	}
	
	/**
	 * Constructs a new CodeValidator with a null old name.
	 * @param pName The name chosen for the new code.
	 * @param pProject The Project in which the code is to be created.
	 */
	public CodeValidator(String pName, Project pProject)
	{
		this(pName, null, pProject);
	}
	
	@Override
	protected boolean nameInUse()
	{
		for(Code code : fProject.getCodes())
		{
			if(code.getCodeName().equals(fName))
			{
				return true;
			}
		}
		return false;
	}
}
