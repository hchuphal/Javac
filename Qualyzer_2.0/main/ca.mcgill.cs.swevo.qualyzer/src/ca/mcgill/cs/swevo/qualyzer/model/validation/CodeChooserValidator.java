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

import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validator for the code chooser attached to fragments.
 */
public class CodeChooserValidator extends CodeValidator
{
	private Fragment fFragment;

	/**
	 * Constructor.
	 * @param pName The name for the code.
	 * @param pProject The project.
	 * @param pFragment The fragment to attach the code to.
	 */
	public CodeChooserValidator(String pName, Project pProject, Fragment pFragment)
	{
		super(pName, pProject);
		fFragment = pFragment;
	}
	
	@Override
	public boolean isValid()
	{
		boolean valid = super.isValid();
		
		// Must test even if valid because an already used fragment could be 
		// already set in the fragment.
		if(fFragment != null && codeInUse(fName))
		{
			fMessage = MessagesClient.getString("model.validator.CodeChooserValidator.codeAttached", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages");  //$NON-NLS-1$
			valid = false;
		}
				
		return valid;
	}

	private boolean codeInUse(String name)
	{
		for(CodeEntry entry : fFragment.getCodeEntries())
		{
			if(entry.getCode().getCodeName().equals(name))
			{
				return true;
			}
		}
		return false;
	}
}
