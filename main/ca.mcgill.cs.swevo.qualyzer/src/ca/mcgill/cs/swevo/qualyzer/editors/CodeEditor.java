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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.CodeEditorPage;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * The code editor.
 *
 */
public class CodeEditor extends FormEditor
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.codeEditor"; //$NON-NLS-1$
	
	private static final int MAX_LENGTH = 15;
	private static final String DOTS = "..."; //$NON-NLS-1$

	private static Logger gLogger = LoggerFactory.getLogger(CodeEditor.class);
	
	private CodeEditorPage fPage;

	@Override
	protected void addPages()
	{
		IEditorInput input = getEditorInput();
		if(input instanceof CodeEditorInput)
		{
			Project project = ((CodeEditorInput) input).getProject();
			
			String name = MessagesClient.getString("editors.pages.CodeEditor.codeEditor", "ca.mcgill.cs.swevo.qualyzer.editors.Messages") + project.getName(); //$NON-NLS-1$
			if(name.length() > MAX_LENGTH)
			{
				name = name.substring(0, MAX_LENGTH) + DOTS;
			}
			setPartName(name); 
			
			fPage = new CodeEditorPage(this, project);
			try
			{
				addPage(fPage);
			}
			catch (PartInitException e)
			{
				gLogger.error("Failed to open code editor", e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		Code[] codes = fPage.getCodes();
		Facade.getInstance().saveCodes(codes);
		fPage.notDirty();
		fPage.doSave(monitor);
		
		CommonNavigator view = (CommonNavigator) getSite().getPage().findView(
				QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		view.getCommonViewer().refresh();
	}

	@Override
	public void doSaveAs()
	{

	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	// This override is to eliminate the single tab at the bottom of the editor.
	@Override
	protected void createPages() 
	{
		super.createPages();
	    if(getPageCount() == 1 && getContainer() instanceof CTabFolder) 
	    {
	    	((CTabFolder) getContainer()).setTabHeight(0);
	    }
	}

}
