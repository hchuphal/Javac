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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.InvestigatorEditorPage;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;

/**
 * An editor for Investigator Objects.
 */
public class InvestigatorFormEditor extends FormEditor
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.investigatorFormEditor"; //$NON-NLS-1$
	private static Logger gLogger = LoggerFactory.getLogger(InvestigatorFormEditor.class);
	
	private InvestigatorEditorPage fPage;
	private Investigator fInvestigator;

	@Override
	protected void addPages()
	{
		fInvestigator = ((InvestigatorEditorInput)getEditorInput()).getInvestigator();
		setPartName(fInvestigator.getNickName());
		fPage = new InvestigatorEditorPage(this, fInvestigator);
		try
		{
			addPage(fPage);
		}
		catch (PartInitException e)
		{
			gLogger.error("Could not open Investigator editor.", e); //$NON-NLS-1$
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		fInvestigator.setFullName(fPage.getFullname());
		fInvestigator.setNickName(fPage.getNickname());
		fInvestigator.setInstitution(fPage.getInstitution());
		
		Facade.getInstance().saveInvestigator(fInvestigator);
		
		CommonNavigator view;
		view = (CommonNavigator) getSite().getPage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		view.getCommonViewer().refresh();
		
		setPartName(fInvestigator.getNickName());
		fPage.notDirty();
	}

	@Override
	public void doSaveAs(){}

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
	
	/**
	 * This allows the page to update the editor.
	 * @param investigator
	 */
	public void setInvestigator(Investigator investigator)
	{
		fInvestigator = investigator;
	}

}
