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

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates the creation of a new memo.
 */
public class MemoValidator extends MemoNameValidator
{
	private Investigator fAuthor;
	
	/**
	 * Creates a new validator.
	 * @param pName The name chosen for the new transcript.
	 * @param investigator The author of the memo.
	 * @param project The Project in which the transcript is to be created.
	 */
	public MemoValidator(String pName, Investigator investigator, Project project)
	{
		super(pName, null, project);
		fAuthor = investigator;
	}

	@Override
	public boolean isValid()
	{
		boolean lReturn = super.isValid();
		
		// Additional conditions tested only if the name passed all validation.
		if(lReturn)
		{
			if(fAuthor == null)
			{
				lReturn = false;
				fMessage = MessagesClient.getString("model.validation.MemoValidator.chooseAuthor", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages");  //$NON-NLS-1$
			}
		}
		return lReturn;
	}

}
