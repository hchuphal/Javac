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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeFragmentViewerInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.CodeFragmentViewerPage;
import ca.mcgill.cs.swevo.qualyzer.model.Code;

/**
 * Handles the Query that shows all the fragments that contain a given code.
 */
public class CodeFragmentViewer extends FormEditor
{

	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.codeFragmentViewer"; //$NON-NLS-1$
	private Code fCode;
	private CodeFragmentViewerPage fPage;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages()
	{
		fCode = ((CodeFragmentViewerInput) getEditorInput()).getCode();
		setPartName(fCode.getCodeName() + MessagesClient.getString("editors.CodeFragmentViewer.fragments", "ca.mcgill.cs.swevo.qualyzer.editors.Messages")); //$NON-NLS-1$
		fPage = new CodeFragmentViewerPage(this, fCode);
		try
		{
			addPage(fPage);
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		//read only
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
		//read only
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
	@Override
	public boolean isDirty()
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
