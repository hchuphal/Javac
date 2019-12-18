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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 *
 */
public class ProjectPropertiesDialog extends TitleAreaDialog
{
	private static Logger gLogger = LoggerFactory.getLogger(ProjectPropertiesDialog.class);
	
	private Project fProject;
	private Combo fInvestigator;
	
	private String fCurrentName;
	private String fInvestigatorName;
	
	/**
	 * Constructor.
	 * @param shell
	 */
	public ProjectPropertiesDialog(Shell shell, Project project)
	{
		super(shell);
		fProject = project;
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getFolderName());
		try
		{
			fCurrentName = FileUtil.getProjectProperty(wProject, FileUtil.ACTIVE_INV);
		}
		catch (CoreException e)
		{
			gLogger.error("Unable to read active investigator", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString(
					"dialogs.ProjectPropertiesDialog.errorMessage", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"), e); //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialogs.ProjectPropertiesDialog.projectProperties", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		setMessage(MessagesClient.getString(
				"dialogs.ProjectPropertiesDialog.propertiesForProject", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages") + fProject.getName()); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText(MessagesClient.getString("dialogs.ProjectPropertiesDialog.projectPath", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		String path = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getFolderName())
			.getLocation().toString();
		label = new Label(composite, SWT.BORDER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		label.setText(path);
		
		label = new Label(composite, SWT.NULL);
		label.setText(MessagesClient.getString("dialogs.ProjectPropertiesDialog.activeInvestigator", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		fInvestigator = new Combo(composite, SWT.READ_ONLY);
		int position = 0;
		for(Investigator investigator : fProject.getInvestigators())
		{
			fInvestigator.add(investigator.getNickName());
			if(fCurrentName.equals(investigator.getNickName()))
			{
				position = fInvestigator.getItemCount() - 1;
			}
		}
		fInvestigator.select(position);
		fInvestigator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{
		fInvestigatorName = fInvestigator.getText();
		super.okPressed();
	}
	
	/**
	 * Get the investigator name that was chosen.
	 * @return
	 */
	public String getInvestigator()
	{
		return fInvestigatorName;
	}
	
	/**
	 * 
	 * @return
	 */
	public Combo getInvestigatorCombo()
	{
		return fInvestigator;
	}
}
