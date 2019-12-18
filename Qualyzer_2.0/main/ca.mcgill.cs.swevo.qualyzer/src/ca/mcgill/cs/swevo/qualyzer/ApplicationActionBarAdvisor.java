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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
	private IWorkbenchAction fSaveAction;

	/**
	  * Actions - important to allocate these only in makeActions, and then use
	  * them in the fill methods. This ensures that the actions aren't recreated
	  * when fillActionBars is called with FILL_PROXY.
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
	{
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window)
	{
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		// exitAction = ActionFactory.QUIT.create(window);
		// register(exitAction);
		fSaveAction = ActionFactory.SAVE.create(window);
		register(fSaveAction);
		
		// This code is for displaying the welcome page from a menu
		if (window.getWorkbench().getIntroManager().hasIntro()) 
		{
	        register(ActionFactory.INTRO.create(window));
		}
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar)
	{
		// MenuManager fileMenu = new MenuManager("&File",
		// IWorkbenchActionConstants.M_FILE);
		// menuBar.add(fileMenu);
		// fileMenu.add(exitAction);
	}

}
