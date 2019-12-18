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

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates the business rules when a new investigator is created.
 */
public class ParticipantValidator extends RelaxedNameValidator
{
	/**
	 * Constructs a new ParticipantValidator.
	 * @param pName The ID chosen for the new participant.
	 * @param pOldName The ID of the current participant (null if there are none).
	 * @param pProject The Project in which the participant is to be created.
	 */
	public ParticipantValidator(String pName, String pOldName, Project pProject)
	{
		super(MessagesClient.getString("model.validation.ParticipantValidator.label", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"), //$NON-NLS-1$
				pName, pOldName, pProject); 
	}
	
	/**
	 * Constructs a new ParticipantValidator with a null old name.
	 * @param pName The ID chosen for the new participant.
	 * @param pProject The Project in which the participant is to be created.
	 */
	public ParticipantValidator(String pName, Project pProject)
	{
		this(pName, null, pProject);
	}
	
	@Override
	protected boolean nameInUse()
	{
		for(Participant participant : fProject.getParticipants())
		{
			if(participant.getParticipantId().equals(fName))
			{
				return true;
			}
		}
		return false;
	}
}
