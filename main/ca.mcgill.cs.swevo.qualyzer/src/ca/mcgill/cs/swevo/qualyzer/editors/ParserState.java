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

/**
 * Represents a snapshot of the state of the parsers. Is necessary to allow proper handling of groups.
 * Maintains the formatting state (bold, italic, underline).
 */
public class ParserState
{
	private boolean fBold;
	private boolean fItalic;
	private boolean fUnderline;
	
	/**
	 * Constructor.
	 * @param bold
	 * @param italic
	 * @param underline
	 */
	public ParserState(boolean bold, boolean italic, boolean underline)
	{
		fBold = bold;
		fItalic = italic;
		fUnderline = underline;
	}
	
	/**
	 * Is the state bold.
	 * @return
	 */
	public boolean isBold()
	{
		return fBold;
	}
	
	/**
	 * Is the state italic.
	 * @return
	 */
	public boolean isItalic()
	{
		return fItalic;
	}
	
	/**
	 * Is the state underline.
	 * @return
	 */
	public boolean isUnderline()
	{
		return fUnderline;
	}
}
