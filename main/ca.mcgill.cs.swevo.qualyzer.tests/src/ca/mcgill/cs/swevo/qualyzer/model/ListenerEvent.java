/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class ListenerEvent
{

	private ChangeType fChangeType;

	private Object fObject;

	/**
	 * 
	 * @param fChangeType
	 * @param fObject
	 */
	public ListenerEvent(ChangeType changeType, Object object)
	{
		super();
		this.fChangeType = changeType;
		this.fObject = object;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public ChangeType getChangeType()
	{
		return fChangeType;
	}

	/**
	 * 
	 * Does something.
	 * 
	 * @return
	 */
	public Object getObject()
	{
		return fObject;
	}

}
