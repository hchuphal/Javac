/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *     Martin Robillard 
 *     Jonathan Faubert 
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Default perspective of Qualyzer. Actual setup of the perspective is done
 * through a perspective extension in plugin.xml.
 */
public class Perspective implements IPerspectiveFactory
{
	@Override
	public void createInitialLayout(IPageLayout layout)
	{
	}
}
