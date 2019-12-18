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

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates the business rules when a new investigator is created.
 */
public class InvestigatorValidator extends BasicNameValidator
{
	/**
	 * Constructs a new InvestigatorValidator.
	 * @param pName The ID chosen for the new investigator.
	 * @param pOldName The current ID of the investigator (if applicable). Null if there are none.
	 * @param pProject The Project in which the investigator is to be created.
	 */
	public InvestigatorValidator(String pName, String pOldName, Project pProject)
	{
		super(MessagesClient.getString("model.validation.InvestigatorValidator.label", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"), //$NON-NLS-1$
				pName, pOldName, pProject); 
	}
	
	/**
	 * Constructs a new InvestigatorValidator with a null old name.
	 * @param pName The ID chosen for the new investigator.
	 * @param pProject The Project in which the investigator is to be created.
	 */
	public InvestigatorValidator(String pName, Project pProject)
	{
		this(pName, null, pProject);
	}
	
	@Override
	protected boolean nameInUse()
	{
		for(Investigator investigator : fProject.getInvestigators())
		{
			if(investigator.getNickName().equals(fName))
			{
				return true;
			}
		}
		return false;
	}
}
