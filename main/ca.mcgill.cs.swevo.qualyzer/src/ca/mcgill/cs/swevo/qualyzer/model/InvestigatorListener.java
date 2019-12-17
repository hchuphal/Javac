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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model;

import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 *
 */
public interface InvestigatorListener
{

	/**
	 * Receive a notification of a change to an investigator.
	 * @param cType
	 * @param investigator
	 * @param facade
	 */
	void investigatorChanged(ChangeType cType, Investigator[] investigators, Facade facade);
}
