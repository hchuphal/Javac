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


package ca.mcgill.cs.swevo.qualyzer.projectExplorer;

import java.lang.reflect.Field;
import java.util.Properties;

import org.eclipse.ui.internal.navigator.extensions.NavigatorViewerDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For use only by the ProjectExplorerView.
 */
@SuppressWarnings("restriction")
final class CommonViewerUtil
{
	private static final String ERROR_MSG = "Error hidding buttons in Project Explorer"; //$NON-NLS-1$
	private static Logger gLogger = LoggerFactory.getLogger(CommonViewerUtil.class);
	
	private CommonViewerUtil(){};
	
	/**
	 * Set the property of the CommonViewer to hide the link button and the customise actions.
	 * Uses reflection to get around access restrictions, leave this code alone.
	 * @param desc The NavigatorViewerDescriptor.
	 * @param prop The property you want to change.
	 * @param val The value to change it to.
	 */
	static void setProperty(NavigatorViewerDescriptor desc, String prop, String val)
	{
		try
		{
			Field[] fields = desc.getClass().getDeclaredFields();
			
			for(Field field : fields)
			{
				if(field.getName().equals("properties")) //$NON-NLS-1$
				{
					field.setAccessible(true);
					Object object = field.get(desc);
					Properties properties = (Properties) object;
					properties.setProperty(prop, val);
					field.setAccessible(false);
				}
			}
		}
		catch (SecurityException e)
		{
			gLogger.error(ERROR_MSG, e);
		}
		catch (IllegalArgumentException e)
		{
			gLogger.error(ERROR_MSG, e);
		}
		catch (IllegalAccessException e)
		{
			gLogger.error(ERROR_MSG, e);
		}
	}
}
