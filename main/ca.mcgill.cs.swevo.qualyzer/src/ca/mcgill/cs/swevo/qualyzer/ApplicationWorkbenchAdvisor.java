/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
	private static final String PERSPECTIVE_ID = "ca.mcgill.cs.swevo.qualyzer.perspective"; //$NON-NLS-1$
	private final Logger fLogger = LoggerFactory.getLogger(ApplicationWorkbenchAdvisor.class);

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId()
	{
		return PERSPECTIVE_ID;
	}

	@Override
	public IAdaptable getDefaultPageInput()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public void preStartup()
	{
		super.preStartup();

		IDE.registerAdapters();
	}
	
	// This method is necessary to prevent the platform from
    // issuing a warning that resources are not saved, after each session.
    // Impact on performance to be assessed.
    @Override
    public boolean preShutdown()
    {
        try
        {
            ResourcesPlugin.getWorkspace().save(true, null);
        }
        catch(final CoreException lException)
        {
            fLogger.error("Problem saving workspace state", lException); //$NON-NLS-1$
        }
        return super.preShutdown();
    } 
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer)
	{
		configurer.declareImage(IDE.SharedImages.IMG_OBJ_PROJECT, QualyzerActivator
				.getImageDescriptor("icons/prj_obj.gif"), true); //$NON-NLS-1$
		super.initialize(configurer);
	}
}
