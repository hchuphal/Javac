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
package ca.mcgill.cs.swevo.qualyzer.providers;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Label provider for our navigator content.
 */
public class NavigatorLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider
{
	private static final String FOLDER_IMG = "FOLDER_IMG"; //$NON-NLS-1$
	private static final String PROJECT_IMG = "PROJECT_IMG"; //$NON-NLS-1$
	private static final String CPROJECT_IMG = "CPROJECT_IMG"; //$NON-NLS-1$
	private static final String PARTICIPANT_IMG = "PARTICIPANT_IMG"; //$NON-NLS-1$
	private static final String FILE_IMG = "FILE_IMG"; //$NON-NLS-1$
	private static final String CODE_IMG = "CODE_IMG"; //$NON-NLS-1$
	private static final String INVESTIGATOR_IMG = "INVESTIGATOR_IMG"; //$NON-NLS-1$
	private static final String MEMO_IMG = "MEMO_IMG"; //$NON-NLS-1$
	
	private final ImageRegistry fRegistry;
	
	/**
	 * Creates a new label provider for the project navigator.
	 */
	public NavigatorLabelProvider()
	{
		fRegistry = QualyzerActivator.getDefault().getImageRegistry();
		addImage(FOLDER_IMG, QualyzerActivator.PLUGIN_ID, "icons/fldr_obj.gif"); //$NON-NLS-1$
		addImage(PROJECT_IMG, QualyzerActivator.PLUGIN_ID, "icons/prj_obj.gif"); //$NON-NLS-1$
		addImage(PARTICIPANT_IMG, QualyzerActivator.PLUGIN_ID, "icons/participant.png"); //$NON-NLS-1$
		addImage(FILE_IMG, QualyzerActivator.PLUGIN_ID, "icons/file_obj.gif"); //$NON-NLS-1$
		addImage(CPROJECT_IMG, QualyzerActivator.PLUGIN_ID, "icons/cprj_obj.gif"); //$NON-NLS-1$
		addImage(CODE_IMG, QualyzerActivator.PLUGIN_ID, "icons/code_obj.png"); //$NON-NLS-1$
		addImage(INVESTIGATOR_IMG, QualyzerActivator.PLUGIN_ID, "icons/investigator_obj.png"); //$NON-NLS-1$
		addImage(MEMO_IMG, QualyzerActivator.PLUGIN_ID, "icons/memo_obj.png"); //$NON-NLS-1$
	}
	
	/**
	 * Does nothing.
	 * @param aConfig
	 */
	public void init(ICommonContentExtensionSite aConfig)
	{
	}
	
	private void addImage(String key, String pluginID, String path)
	{
		String fullKey = computeKey(key, pluginID);
		ImageDescriptor descriptor = fRegistry.getDescriptor(fullKey);
		if(descriptor == null)
		{
			fRegistry.put(fullKey, AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path));
		}
	}
	
	private String computeKey(String key, String pluginID)
	{
		return pluginID + "_" + key; //$NON-NLS-1$
	}
	
	private Image getImage(String key, String pluginID)
	{
		return fRegistry.get(computeKey(key, pluginID));
	}
	
	@Override
	public Image getImage(Object element)
	{
		Image image = null;
		if(element instanceof IProject)
		{
			if(((IProject) element).isOpen())
			{
				image = getImage(PROJECT_IMG, QualyzerActivator.PLUGIN_ID);
			}
			else
			{
				image = getImage(CPROJECT_IMG, QualyzerActivator.PLUGIN_ID);
			}
		}
		else if(element instanceof ProjectWrapper && !(element instanceof WrapperCode))
		{
			image = getImage(FOLDER_IMG, QualyzerActivator.PLUGIN_ID);
		}
		else if(element instanceof Transcript)
		{
			image = getImage(FILE_IMG, QualyzerActivator.PLUGIN_ID);
		}
		else if(element instanceof Investigator)
		{
			image = getImage(INVESTIGATOR_IMG, QualyzerActivator.PLUGIN_ID);
		}
		else if(element instanceof Memo)
		{
			image = getImage(MEMO_IMG, QualyzerActivator.PLUGIN_ID);
		}
		else if(element instanceof Participant)
		{
			image = getImage(PARTICIPANT_IMG, QualyzerActivator.PLUGIN_ID);
		}
		else if(element instanceof WrapperCode)
		{
			image = getImage(CODE_IMG, QualyzerActivator.PLUGIN_ID);
		}
		
		return image;
	}

	//Only displayed in the status bar (which we don't have)
	@Override
	public String getDescription(Object anElement)
	{
		return null;
	}
	
	@Override
	public String getText(Object anElement)
	{
		String output = null;

		if (anElement instanceof IProject)
		{
			Project project = PersistenceManager.getInstance().getProject(((IProject) anElement).getName());
			if(project != null)
			{
				output = project.getName();
			}
		}
		else if(anElement instanceof Project)
		{
			output = ((Project) anElement).getName();
		}
		else if(anElement instanceof ProjectWrapper)
		{
			output = ((ProjectWrapper) anElement).getResource();
		}
		else if(anElement instanceof Transcript)
		{
			output = ((Transcript) anElement).getName();
		}
		else if(anElement instanceof Investigator)
		{
			output = ((Investigator) anElement).getNickName();
		}
		else if(anElement instanceof Memo)
		{
			output = ((Memo) anElement).getName();
		}
		else if(anElement instanceof Participant)
		{
			output = ((Participant) anElement).getParticipantId();
		}
		
		return output;
	}
	
	/**
	 * @param aMemento
	 */
	public void restoreState(IMemento aMemento)
	{

	}

	/**
	 * @param aMemento
	 */
	public void saveState(IMemento aMemento)
	{
	}

}
