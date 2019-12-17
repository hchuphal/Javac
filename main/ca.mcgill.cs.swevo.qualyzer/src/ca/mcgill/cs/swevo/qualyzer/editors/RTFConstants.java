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

/**
 * Stores all the ID's for the various annotations and actions.
 * *_TYPE denotes an annotation type string.
 * *_ACTION_ID denotes the action ID used by the RTFEditor.
 */
public final class RTFConstants
{
	public static final String BOLD_TYPE = "RTFEditor.annotation.bold"; //$NON-NLS-1$
	public static final String ITALIC_TYPE = "RTFEditor.annotation.italic"; //$NON-NLS-1$
	public static final String UNDERLINE_TYPE = "RTFEditor.annotation.underline"; //$NON-NLS-1$
	public static final String BOLD_ITALIC_TYPE = "RTFEditor.annotation.boldItalic"; //$NON-NLS-1$
	public static final String BOLD_UNDERLINE_TYPE = "RTFEditor.annotation.boldUnderline"; //$NON-NLS-1$
	public static final String ITALIC_UNDERLINE_TYPE = "RTFEditor.annotation.italicUnderline"; //$NON-NLS-1$
	public static final String BOLD_ITALIC_UNDERLINE_TYPE = "RTFEditor.annotation.boldItalicUnderline"; //$NON-NLS-1$
	
	public static final String FRAGMENT_TYPE = "RTFEditor.annotation.fragment"; //$NON-NLS-1$

	public static final String TIMESTAMP_TYPE = "ca.mcgill.cs.swevo.qualyzer.annotation.timestamp"; //$NON-NLS-1$
	
	public static final String BOLD_ACTION_ID = "action.bold"; //$NON-NLS-1$
	public static final String ITALIC_ACTION_ID = "action.italic"; //$NON-NLS-1$
	public static final String UNDERLINE_ACTION_ID = "action.underline"; //$NON-NLS-1$
	public static final String FRAGMENT_ACTION_ID = "action.markFragment"; //$NON-NLS-1$
	public static final String REMOVE_ALL_CODES_ACTION_ID = "action.removeAllCodes"; //$NON-NLS-1$
	public static final String REMOVE_CODE_ACTION_ID = "action.removeCode"; //$NON-NLS-1$
	public static final String VIEW_FRAGMENTS_ACTION_ID = "action.viewFragments"; //$NON-NLS-1$
	public static final String ADD_TIMESTAMP_ACTION_ID = "action.addTimeStamp"; //$NON-NLS-1$
	public static final String REMOVE_TIMESTAMP_ACTION_ID = "action.removeTimeStamp"; //$NON-NLS-1$
	
	public static final String TIMESTAMP_MARKER_ID = "ca.mcgill.cs.swevo.qualyzer.marker.timestamp"; //$NON-NLS-1$
		
	private RTFConstants(){}
}
