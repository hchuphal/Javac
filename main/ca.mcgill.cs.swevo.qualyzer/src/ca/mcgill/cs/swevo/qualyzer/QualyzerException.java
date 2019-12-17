/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     -
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

/**
 * Unspecified issue in the Qualyzer code.
 */
public class QualyzerException extends RuntimeException
{
	private static final long serialVersionUID = 2105440594348845310L;

	/**
	 * Creates a default exception.
	 */
	public QualyzerException()
	{
	}

	/**
	 * Creates an exception.
	 * @param message
	 */
	public QualyzerException(String message)
	{
		super(message);
	}

	/**
	 * Creates an exception.
	 * @param cause
	 */
	public QualyzerException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Creates an exception.
	 * @param message
	 * @param cause
	 */
	public QualyzerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
