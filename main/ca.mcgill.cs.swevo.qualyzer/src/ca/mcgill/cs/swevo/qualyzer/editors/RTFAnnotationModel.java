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

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 * The annotation model for our RTF Editor. It handles removing Fragments from the DB when
 * they are removed from the model. Otherwise it delegates all tasks to the ResourceMarkerAnnotationModel.
 */
public class RTFAnnotationModel extends ResourceMarkerAnnotationModel
{
	
	/**
	 * Constructor.
	 * @param element
	 */
	public RTFAnnotationModel(RTFEditorInput element)
	{
		super(element.getFile());
	}
	
	/**
	 * Remove a (fragment) annotation from the AnnotationModel without removing it from the DB.
	 * Skips the overridden version of removeAnnotation and goes straight to its super.
	 */
	public void removeAnnotationOnly(Annotation annotation)
	{
		super.removeAnnotation(annotation, true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.AnnotationModel#removeAnnotation(
	 * org.eclipse.jface.text.source.Annotation, boolean)
	 */
	@Override
	protected void removeAnnotation(Annotation annotation, boolean fireModelChanged)
	{
		if(annotation instanceof FragmentAnnotation)
		{
			Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
			Facade.getInstance().deleteFragment(fragment);
		}
		CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		
		super.removeAnnotation(annotation, fireModelChanged);
		view.getCommonViewer().refresh();
	}
}
