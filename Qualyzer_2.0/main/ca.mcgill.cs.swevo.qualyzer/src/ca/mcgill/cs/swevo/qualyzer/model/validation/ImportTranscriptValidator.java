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

/**
 * Validates the business rules when a transcript is imported.
 * Additional rules can be validated with the TranscriptValidator.
 * - The transcript name is empty
 * - The transcript name refers to a file that does not exist.
 */
public class ImportTranscriptValidator extends AbstractValidator
{
	private final String fTranscriptFileName;
	
	/**
	 * Constructs a new ImportTranscriptValidator.
	 * @param pTranscriptFileName The name of the file to import.
	 */
	public ImportTranscriptValidator(String pTranscriptFileName)
	{
		fTranscriptFileName = pTranscriptFileName;
	}
	
	@Override
	public boolean isValid() 
	{
		boolean lReturn = true;
		
		if(fTranscriptFileName.length() == 0)
		{
			fMessage = MessagesClient.getString("model.validation.ImportTranscriptValidator.chooseFile", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"); //$NON-NLS-1$
			lReturn = false;
		}
		else if(!(new File(fTranscriptFileName).exists()))
		{
			fMessage = MessagesClient.getString("model.validation.ImportTranscriptValidator.chooseFile", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"); //$NON-NLS-1$
			lReturn = false;
		}
	
		return lReturn;
	}
}
