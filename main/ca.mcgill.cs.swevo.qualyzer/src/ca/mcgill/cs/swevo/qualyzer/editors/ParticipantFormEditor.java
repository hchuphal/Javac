/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Martin Robillard
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
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.ParticipantEditorPage;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;

/**
 * A form editor for Participant objects.
 */
public class ParticipantFormEditor extends FormEditor
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor"; //$NON-NLS-1$
	private static Logger gLogger = LoggerFactory.getLogger(ParticipantFormEditor.class);

	private Participant fParticipant;
	private ParticipantEditorPage fPage;

	@Override
	protected void addPages()
	{
		IEditorInput input = getEditorInput();
		if(input instanceof ParticipantEditorInput)
		{
			ParticipantEditorInput partInput = (ParticipantEditorInput) input;
			fParticipant = partInput.getParticipant();
			try
			{
				fPage = new ParticipantEditorPage(this, fParticipant);
				addPage(fPage);
				this.setPartName(fParticipant.getParticipantId());
			}
			catch(PartInitException e)
			{
				gLogger.error("Could not open Participant editor", e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		fParticipant.setParticipantId(fPage.getId());
		fParticipant.setFullName(fPage.getFullname());
		
		Facade.getInstance().saveParticipant(fParticipant);
		
		CommonNavigator view;
		view = (CommonNavigator) getSite().getPage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		view.getCommonViewer().refresh();
		
		setPartName(fParticipant.getParticipantId());
		fPage.notDirty();
	}
	
	/** 
	 * Does nothing because participant forms cannot be saved with a different name.
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
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
	 * Allows the page to update the editor.
	 * @param participant
	 */
	public void setParticipant(Participant participant)
	{
		fParticipant = participant;
	}
}
