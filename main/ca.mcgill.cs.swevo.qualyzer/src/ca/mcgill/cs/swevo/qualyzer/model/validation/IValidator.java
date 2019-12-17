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
 * Behavior for all objects in charge of validating input data. The behavior for
 * validators follows a two-method protocols. First, a call to isValid() returns true
 * if the input is valid. If not, a call to getErrorMessage() returns the error message
 * associated with the <I>last</I> isValid() call, or null if the input was valid.
 * <P>
 * The best practice for using <CODE>IValidator</CODE> objects is as such:
 * <P>
 * <CODE>
 * IValidator v = ...
 * String error = null;
 * if(!v.isValid()) error = v.getErrorMessage();
 * </CODE>
 */
public interface IValidator 
{
	/**
	 * @return True is this object represents valid input.
	 */
	boolean isValid();
	
	/**
	 * @return The error message describing why the input is not valid according to the
	 * last isValid call, or null if isValid was never called or was called and returned true.
	 */
	String getErrorMessage();
}
