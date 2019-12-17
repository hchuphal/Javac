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
package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

/**
 * Initialises all the Preference values for the Qualyzer Preference Page.
 */
@SuppressWarnings("restriction")
public class QualyzerPreferenceInitializer extends AbstractPreferenceInitializer
{

	private static final String DEFAULT_COLOR = "191,191,191"; //$NON-NLS-1$
	private static final int DEFAULT_SEEK_TIME = 3;

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(QualyzerActivator.PLUGIN_ID);
		node.put(IQualyzerPreferenceConstants.DEFAULT_INVESTIGATOR, System.getProperty("user.name")); //$NON-NLS-1$
		node.put(IQualyzerPreferenceConstants.FRAGMENT_COLOR, DEFAULT_COLOR); 
		node.putInt(IQualyzerPreferenceConstants.SEEK_TIME, DEFAULT_SEEK_TIME);
		
		String value = EditorsPlugin.getDefault().getPreferenceStore().getDefaultString(JFaceResources.TEXT_FONT);
		node.put(IQualyzerPreferenceConstants.FONT, value);
	}

}
