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
 * Contains the various RTF Tags that the parser needs.
 */
public final class RTFTags
{
	public static final String HEADER = "{\\rtf1\\ansi\\deff0\n"; //$NON-NLS-1$
	public static final String FOOTER = "\n}\n\0"; //$NON-NLS-1$

	public static final String BOLD_START = "b"; //$NON-NLS-1$
	public static final String BOLD_END = "b0"; //$NON-NLS-1$
	public static final String ITALIC_START = "i"; //$NON-NLS-1$
	public static final String ITALIC_END = "i0"; //$NON-NLS-1$
	public static final String UNDERLINE_START = "ul"; //$NON-NLS-1$
	public static final String UNDERLINE_END = "ulnone"; //$NON-NLS-1$
	public static final String NEW_LINE = "par"; //$NON-NLS-1$
	public static final String TAB = "tab"; //$NON-NLS-1$

	public static final String PAR_DEFAULT = "pard"; //$NON-NLS-1$
	public static final String PLAIN = "plain"; //$NON-NLS-1$

	public static final String UNICODE_COUNT_FULL = "uc"; //$NON-NLS-1$
	public static final String SPACE_CHAR = " "; //$NON-NLS-1$
	public static final String NEW_LINE_CHAR = "\n"; //$NON-NLS-1$
	public static final String TAB_CHAR = "\t"; //$NON-NLS-1$
	public static final String CARRIAGE_CHAR = "\r"; //$NON-NLS-1$
	public static final String NULL_CHAR_STR = "\0";  //$NON-NLS-1$
	public static final char NULL_CHAR = '\0'; //$NON-NLS-1$
	public static final char MINUS = '-'; //$NON-NLS-1$
	public static final char ESCAPE_8BIT = '\''; //$NON-NLS-1$
	public static final char BACKSLASH = '\\'; //$NON-NLS-1$
	public static final char LEFT_BRACE = '{'; //$NON-NLS-1$
	public static final char RIGHT_BRACE = '}'; //$NON-NLS-1$
	public static final char QUOTE = '"'; //$NON-NLS-1$
	public static final char UNICODE_COUNT = 'c'; //$NON-NLS-1$
	public static final char UNICODE = 'u'; //$NON-NLS-1$
	public static final int BULLET_POINT = 149;

	public static final String BOLD_START_TAG = "\\b"; //$NON-NLS-1$
	public static final String BOLD_END_TAG = "\\b0"; //$NON-NLS-1$
	public static final String ITALIC_START_TAG = "\\i"; //$NON-NLS-1$
	public static final String ITALIC_END_TAG = "\\i0"; //$NON-NLS-1$
	public static final String UNDERLINE_START_TAG = "\\ul"; //$NON-NLS-1$
	public static final String UNDERLINE_END_TAG = "\\ulnone"; //$NON-NLS-1$
	public static final String NEW_LINE_TAG = "\\par \n"; //$NON-NLS-1$
	public static final String TAB_TAG = "\\tab "; //$NON-NLS-1$
	public static final String UNICODE_START_TAG = "\\u"; //$NON-NLS-1$
	public static final String UNICODE_END_TAG = "\\'3f"; //$NON-NLS-1$

	public static final String FONT_TABLE = "fonttbl"; //$NON-NLS-1$
	public static final String STYLESHEET = "stylesheet"; //$NON-NLS-1$
	public static final String INFO = "info"; //$NON-NLS-1$
	public static final String IGNORE = "*"; //$NON-NLS-1$
	public static final String COLOR_TABLE = "colortbl;"; //$NON-NLS-1$
	public static final String COLOR_TABLE2 = "colortbl"; //$NON-NLS-1$
	public static final String PICTURE = "pict"; //$NON-NLS-1$

	public static final String[] IGNORE_GROUPS = { FONT_TABLE, STYLESHEET, INFO, IGNORE, COLOR_TABLE, COLOR_TABLE2,
		PICTURE };

	public static final String[] ESCAPE_CONTROLS = { String.valueOf(LEFT_BRACE), String.valueOf(RIGHT_BRACE),
			String.valueOf(BACKSLASH), String.valueOf(QUOTE) };

	public static final String[] SPACES = { SPACE_CHAR, NEW_LINE_CHAR, CARRIAGE_CHAR, NULL_CHAR_STR};

	public static final String[] RESET = { PAR_DEFAULT, PLAIN };

	private RTFTags()
	{

	}

}
