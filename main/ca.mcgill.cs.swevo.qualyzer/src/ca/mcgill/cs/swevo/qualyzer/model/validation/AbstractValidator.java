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

/**
 * Common behavior for all validators: manages the error message to be reported.
 */
public abstract class AbstractValidator implements IValidator
{
	protected String fMessage;

	/**
	 * Only subclasses can instantiate.
	 */
	protected AbstractValidator() 
	{
	}

	@Override
	public String getErrorMessage() 
	{
		return fMessage;
	}
}