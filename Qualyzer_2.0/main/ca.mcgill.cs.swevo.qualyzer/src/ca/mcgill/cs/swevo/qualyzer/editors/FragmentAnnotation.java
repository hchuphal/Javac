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
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.ui.texteditor.AnnotationPreference;

import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 *	An annotation that marks a text fragment.
 */
public class FragmentAnnotation extends Annotation
{
	private Fragment fFragment;
	
	/**
	 * Constructor.
	 * @param colour 
	 */
	public FragmentAnnotation(Fragment fragment, String colour)
	{
		super(colour, false, buildCodeString(fragment));
		fFragment = fragment;
	}
	
	/*
	public FragmentAnnotation(Fragment fragment, String colour)
	{
		super(RTFConstants.COLOUR_TYPES[0], false, buildCodeString(fragment));
		fFragment = fragment;
	}
	*/
	
	public FragmentAnnotation(Fragment fragment)
	{
		super(RTFConstants.FRAGMENT_TYPE, false, buildCodeString(fragment));
		fFragment = fragment;
	}
	
	/**
	 * Returns the text containing all the code names.
	 * @return
	 */
	private static String buildCodeString(Fragment fragment)
	{
		String toReturn = ""; //$NON-NLS-1$
		for(CodeEntry entry : fragment.getCodeEntries())
		{
			toReturn += entry.getCode().getCodeName() + ", "; //$NON-NLS-1$
		}
		
		return toReturn.isEmpty() ? null : toReturn.substring(0, toReturn.length() - 2);
	}

	/**
	 * Get the text fragment associated with this annotation.
	 * @return
	 */
	public Fragment getFragment()
	{
		return fFragment;
	}

	/**
	 * Update the fragment that is associated with this Annotation.
	 * @param fragment
	 */
	public void setFragment(Fragment fragment)
	{
		fFragment = fragment;
		setText(buildCodeString(fragment));
	}
	
	/**
	 * Updates the text field to match the Code Entries in the Fragment.
	 */
	public void updateText()
	{
		setText(buildCodeString(fFragment));
	}
	
}
