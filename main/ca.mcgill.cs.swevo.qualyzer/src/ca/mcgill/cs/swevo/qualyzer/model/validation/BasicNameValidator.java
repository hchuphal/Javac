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

import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Implements name validation behavior. Basically verifies that:
 * - A name is not empty
 * - A name is not already in use (except if it's oldName) (but the function to verify this is 
 * a do nothing function that in most cases should be overriden)
 * - A name in alphanumerical+ format.
 * - A name is shorter than 256 characters long.
 */
/**
 * @author martin
 *
 */
public class BasicNameValidator extends AbstractValidator
{
	private static final String BLANK = " "; //$NON-NLS-1$
	private static final int MAX_LENGTH = 255;
	
	protected final Project fProject;
	protected final String fName;
	protected final String fOldName;
	protected final String fLabel;
	
	/**
	 * Constructs a new BasicNameValidator.
	 * @param pLabel The label that appears before the error message (e.g., "Investigator ID").
	 * @param pName The name chosen for the new code.
	 * @param pOldName The current name of the code (null if this is a new code).
	 * @param pProject The Project in which the code is to be created.
	 */
	protected BasicNameValidator(String pLabel, String pName, String pOldName, Project pProject)
	{
		fLabel = pLabel;
		fName = pName;
		fOldName = pOldName;
		fProject = PersistenceManager.getInstance().getProject(pProject.getName());
	}
	
	/**
	 * Constructs a new BasicNameValidator with a null old name.
	 * @param pLabel The label that appears before the error message (e.g., "Investigator ID").
	 * @param pName The name chosen for the new code.
	 * @param pProject The Project in which the code is to be created.
	 */
	public BasicNameValidator(String pLabel, String pName, Project pProject)
	{
		this(pLabel, pName, null, pProject);
	}

	@Override
	public boolean isValid() 
	{
		boolean lReturn = true;
		
		if(fName.length() == 0)
		{
			lReturn = false;
			fMessage = fLabel + BLANK + MessagesClient.getString("model.validation.BasicNameValidator.empty", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"); //$NON-NLS-1$
		}
		else if(fName.length() > MAX_LENGTH)
		{
			lReturn = false;
			fMessage = fLabel + BLANK + MessagesClient.getString("model.validation.BasicNameValidator.tooLong", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"); //$NON-NLS-1$
		}
		else if(!validateName(fName))
		{
			lReturn = false;
			fMessage = fLabel + BLANK + MessagesClient.getString("model.validation.BasicNameValidator.invalid", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"); //$NON-NLS-1$
		}
		else if(nameInUse())
		{
			if((fOldName == null) || (!fName.equals(fOldName)))
			{
				lReturn = false;
				fMessage = fLabel + BLANK + 
					MessagesClient.getString("model.validation.BasicNameValidator.taken", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"); //$NON-NLS-1$
			}
		}
		return lReturn;
	}
	
	
	/**
	 * Step method of the template method design pattern. Defines the 
	 * exact format for names. Override to change the format.
	 * @param pName The name to validate
	 * @return True if the name is valid
	 */
	protected boolean validateName(String pName)
	{
		return ValidationUtils.verifyID(fName);
	}
	
	/**
	 * @return True if fName is already in use.
	 */
	protected boolean nameInUse()
	{
		return false;
	}
}