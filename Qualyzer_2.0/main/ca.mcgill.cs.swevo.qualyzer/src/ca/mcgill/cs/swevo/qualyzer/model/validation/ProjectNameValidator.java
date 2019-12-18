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
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class ProjectNameValidator extends BasicNameValidator
{

	/**
	 * @param pName
	 * @param pOldName
	 * @param pProject
	 */
	public ProjectNameValidator(String pName, String pOldName, Project pProject)
	{
		super(MessagesClient.getString("model.validation.ProjectNameValidator.projectName", "ca.mcgill.cs.swevo.qualyzer.model.validation.messages"), //$NON-NLS-1$
				pName, pOldName, pProject); 
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.validation.BasicNameValidator#nameInUse()
	 */
	@Override
	protected boolean nameInUse()
	{
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(fName.replace(' ', '_'));
		
		if(wProject.exists())
		{
			return true;
		}
		else
		{
			for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
			{
				if(project.getName().equalsIgnoreCase(wProject.getName()))
				{
					return !fName.equalsIgnoreCase(fOldName); 
				}
			}
				
			return false;
		}
	}
	
}
