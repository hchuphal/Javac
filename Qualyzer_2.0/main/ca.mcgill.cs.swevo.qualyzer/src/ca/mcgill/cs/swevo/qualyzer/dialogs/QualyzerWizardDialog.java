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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

/**
 * For Testing, exposes the finishPressed() method.
 */
public class QualyzerWizardDialog extends WizardDialog
{
	/**
	 * @param parentShell
	 * @param newWizard
	 */
	public QualyzerWizardDialog(IWizard newWizard)
	{
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), newWizard);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardDialog#finishPressed()
	 */
	@Override
	public void finishPressed()
	{
		super.finishPressed();
	}
}
