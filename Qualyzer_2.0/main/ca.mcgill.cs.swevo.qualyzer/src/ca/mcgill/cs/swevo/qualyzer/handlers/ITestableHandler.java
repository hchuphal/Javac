/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/

package ca.mcgill.cs.swevo.qualyzer.handlers;

import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;

/**
 *
 */
public interface ITestableHandler
{

	/**
	 *
	 * @return
	 */
	boolean isTesting();

	/**
	 *
	 * @param isTesting
	 */
	void setTesting(boolean isTesting);

	/**
	 * @return the tester
	 */
	IDialogTester getTester();

	/**
	 * @param tester the tester to set
	 */
	void setTester(IDialogTester tester);
}
