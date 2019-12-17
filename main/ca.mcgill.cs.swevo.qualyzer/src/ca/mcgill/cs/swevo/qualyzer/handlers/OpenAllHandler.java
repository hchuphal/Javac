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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The handler for the open all command.
 * Opens an editor for each selected item.
 */
public class OpenAllHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
	
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = view.getCommonViewer().getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection structSelection = (IStructuredSelection) selection;
			
			for(Object element : structSelection.toArray())
			{
				if(element instanceof Participant)
				{
					ResourcesUtil.openEditor(page, (Participant) element);
				}
				else if(element instanceof Investigator)
				{
					ResourcesUtil.openEditor(page, (Investigator) element);
				}
				else if(element instanceof Transcript)
				{
					ResourcesUtil.openEditor(page, (Transcript) element);
				}
				else if(element instanceof WrapperCode)
				{
					ResourcesUtil.openEditor(page, (WrapperCode) element);
				}
				else if(element instanceof Memo)
				{
					ResourcesUtil.openEditor(page, (Memo) element);
				}
			}
		}
		
		return null;
	}

}
