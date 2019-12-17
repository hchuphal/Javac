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

import java.io.File;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates the business rules when a new transcript is created:
 * - The transcript name is not already in use
 * - The transcript name is not empty
 * - The transcript name is in alphanumerical+ format.
 * - At least one participant is associated with the transcript
 * - The name of the audio file is not empty but does not refer to an existing file
 */
public class TranscriptValidator extends TranscriptNameValidator
{
	private final int fNumberOfParticipants;
	private final String fAudioFileName;
	
	/**
	 * Constructs a new TranscriptValidator.
	 * @param pName The name chosen for the new transcript.
	 * @param pProject The Project in which the transcript is to be created.
	 */
	public TranscriptValidator(String pName, Project pProject, int pNumberOfParticipants, String pAudioFileName)
	{
		super(pName, null, pProject);
		fNumberOfParticipants = pNumberOfParticipants;
		fAudioFileName = pAudioFileName;
	}
	
	@Override
	public boolean isValid() 
	{
		boolean lReturn = super.isValid();
		
		// Additional conditions tested only if the name passed all validation.
		if(lReturn)
		{
			if(fNumberOfParticipants <= 0)
			{
				fMessage = MessagesClient.getString("model.validation.TranscriptValidator.selectOne", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages");  //$NON-NLS-1$
				lReturn = false;
			}
			else
			{
				File file = new File(fAudioFileName);
				if((fAudioFileName.length() != 0) && !file.exists())
				{
					fMessage = MessagesClient.getString("model.validation.TranscriptValidator.enterAudioName", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages");  //$NON-NLS-1$
					lReturn = false;
				}
			}
		}
		return lReturn;
	}
}
