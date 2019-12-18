/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Martin Robillard
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates memo names.
 */
public class MemoNameValidator extends BasicNameValidator
{
	/**
	 * Constructs a new Validator.
	 * @param pNewName The name chosen for the new transcript.
	 * @param pOldName The current name of the transcript.
	 * @param pProject The Project in which the transcript exists.
	 */
	public MemoNameValidator(String pNewName, String pOldName, Project pProject)
	{
		super(MessagesClient.getString("model.validation.MemoNameValidator.label", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"), //$NON-NLS-1$
				pNewName, pOldName, pProject); 
	}
	
	@Override
	protected boolean nameInUse()
	{
		for(Memo memo : fProject.getMemos())
		{
			if(memo.getName().equalsIgnoreCase(fName))
			{
				return true;
			}
		}
		return false;
	}

}
