/*******************************************************************************
 * Copyright (c) 2011 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors;

import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BACKSLASH;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BOLD_END;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BOLD_END_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BOLD_START;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BOLD_START_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BULLET_POINT;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ESCAPE_8BIT;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ESCAPE_CONTROLS;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.FOOTER;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.HEADER;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.IGNORE_GROUPS;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ITALIC_END;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ITALIC_END_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ITALIC_START;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ITALIC_START_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.LEFT_BRACE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.MINUS;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.NEW_LINE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.NEW_LINE_CHAR;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.NEW_LINE_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.NULL_CHAR;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.PLAIN;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.RESET;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.RIGHT_BRACE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.SPACES;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.SPACE_CHAR;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.TAB;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.TAB_CHAR;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.TAB_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNDERLINE_END;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNDERLINE_END_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNDERLINE_START;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNDERLINE_START_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE_COUNT;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE_COUNT_FULL;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE_END_TAG;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE_START_TAG;
import static ca.mcgill.cs.swevo.qualyzer.util.ParserUtil.equal;
import static ca.mcgill.cs.swevo.qualyzer.util.ParserUtil.getDefault;
import static ca.mcgill.cs.swevo.qualyzer.util.ParserUtil.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.CharUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;

/**
 * The DocumentProvider for our editor. If you need to parse a document for some other task (without opening the editor)
 * do the following.
 * 
 * RTFDocumentProvider provider = new RTFDocumentProvider(); RTFEditorInput input = new RTFEditorInput(file, document);
 * IDocument parsedDocument = provider.getCreatedDocument(input);
 * 
 * String paresedText = parsedDocument.getText();
 * 
 */
public class RTFDocumentProvider2 extends FileDocumentProvider
{

	private static Logger gLogger = LoggerFactory.getLogger(RTFDocumentProvider2.class);

	private static final String EMPTY = ""; //$NON-NLS-1$

	private static final int HEX_RADIX = 16;
	
	private static final String SPACE = " "; //$NON-NLS-1$
	
	private static final String UNICOUNT = "UNICOUNT";

	/**
	 * Exists only for use by things that need parsed documents, but that don't want to open the editor.
	 * 
	 * @param element
	 *            The editor input that will be used to create the document.
	 * @return The parsed document.
	 */
	public IDocument getCreatedDocument(Object element)
	{
		try
		{
			return createDocument(element);
		}
		catch (CoreException e)
		{
			gLogger.error("DocumentProvider: Failed to create document.", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Given the RTFEditorInput creates the document and then attaches all the fragments to the document.
	 */
	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{
		RTFDocument doc = (RTFDocument) super.createDocument(element);

		IAnnotatedDocument document = ((RTFEditorInput) element).getDocument();

		for (Fragment fragment : document.getFragments().values())
		{
			Position position = new Position(fragment.getOffset(), fragment.getLength());
			doc.addAnnotation(position, new FragmentAnnotation(fragment));
		}

		return doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createEmptyDocument()
	 */
	@Override
	protected IDocument createEmptyDocument()
	{
		return new RTFDocument();
	}

	/**
	 * This is the main loop of the RTF parser.
	 */
	//CSOFF:
	@Override
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding)
			throws CoreException
	{
		RTFDocument rtfDocument = (RTFDocument) document;
		StringBuilder text = new StringBuilder();
		Map<String, Integer> currentTags = new HashMap<String, Integer>();
		currentTags.put(UNICOUNT, 1);
		Stack<Map<String, Integer>> state = new Stack<Map<String, Integer>>();
		state.push(currentTags);

		try
		{
			int c = contentStream.read();
			while (c != -1)
			{
				char ch = (char) c;
				if (ch == BACKSLASH)
				{
					ParserPair pair = handleControl(contentStream, safeState(state));
					handleControlCommand(pair.fString, text, safeState(state), rtfDocument);
					c = pair.fChar;
				}
				else if (ch == LEFT_BRACE)
				{
					ParserPair pair = handleGroup(contentStream, state, text, rtfDocument);
					c = pair.fChar;
				}
				else if (ch == RIGHT_BRACE)
				{
					handleEndGroup(contentStream, state, text, rtfDocument);
					c = contentStream.read();
				}
				else
				{
					if (!in(ch, SPACES))
					{
						text.append(ch);
					}
					else if (equal(ch, SPACE_CHAR))
					{
						text.append(ch);
					}
					c = contentStream.read();
				}

			}

		}
		catch (Exception e)
		{
			gLogger.error("Error while parsing a rtf file.", e); //$NON-NLS-1$
		}

		rtfDocument.set(text.toString());
	}
	//CSON:

	private void handleEndGroup(InputStream contentStream, Stack<Map<String, Integer>> state, StringBuilder text,
			RTFDocument document)
	{
		Map<String, Integer> oldState = safeState(state, true);
		reset(oldState, text, document);
		Map<String, Integer> currentState = safeState(state);
		resetNew(text, currentState, document);
	}

	private ParserPair handleGroup(InputStream contentStream, Stack<Map<String, Integer>> state, StringBuilder text,
			RTFDocument document) throws IOException
	{
		int c = contentStream.read();
		char ch = NULL_CHAR;
		ParserPair pair = null;
		String groupName = null;
		while (c != -1)
		{
			ch = (char) c;
			if (!Character.isWhitespace(ch))
			{
				break;
			}
			else
			{
				c = contentStream.read();
			}
		}

		if (ch == BACKSLASH)
		{
			pair = handleControl(contentStream, safeState(state));
			c = pair.fChar;
			groupName = pair.fString;
		}
		else
		{
			groupName = " ";
		}

		if (in(groupName, IGNORE_GROUPS))
		{
			c = skipGroup(contentStream, c);
		}
		else
		{
			Map<String, Integer> oldState = safeState(state);
			reset(oldState, text, document);
			Map<String, Integer> newState = new HashMap<String, Integer>(oldState);
			newState.put(UNICOUNT, oldState.get(UNICOUNT));
			state.push(newState);
			resetNew(text, newState, document);
			
			handleControlCommand(groupName, text, newState, document);
		}
		return new ParserPair(c, groupName);
	}

	private void resetNew(StringBuilder text, Map<String, Integer> state, RTFDocument document)
	{
		for (String command : state.keySet())
		{
			if (command.equals(UNICOUNT))
			{
				continue;
			}
			state.remove(command);
			handleControlCommand(command, text, state, document);
		}

	}

	private void reset(Map<String, Integer> state, StringBuilder text, RTFDocument document)
	{
		handleControlCommand(PLAIN, text, state, document);
	}

	private int skipGroup(InputStream contentStream, int inputC) throws IOException
	{
		int c = inputC;
		int count = 1;
		while (c != -1 && count > 0)
		{
			char ch = (char) c;
			if (ch == LEFT_BRACE)
			{
				count++;
			}
			else if (ch == RIGHT_BRACE)
			{
				count--;
			}
			c = contentStream.read();
		}
		return c;
	}

	//CSOFF:
	private void handleControlCommand(String control, StringBuilder text, Map<String, Integer> state,
			RTFDocument document)
	{
		if (control.equals(BOLD_START) && !state.containsKey(BOLD_START))
		{
			startBold(document, text, state);
		}
		else if (control.equals(BOLD_END) && state.containsKey(BOLD_START))
		{
			endBold(document, text, state);
		}
		else if (control.equals(ITALIC_START) && !state.containsKey(ITALIC_START))
		{
			startItalic(document, text, state);
		}
		else if (control.equals(ITALIC_END) && state.containsKey(ITALIC_START))
		{
			endItalic(document, text, state);
		}
		else if (control.equals(UNDERLINE_START) && !state.containsKey(UNDERLINE_START))
		{
			startUnderline(document, text, state);
		}
		else if (control.equals(UNDERLINE_END) && state.containsKey(UNDERLINE_START))
		{
			endUnderline(document, text, state);
		}
		else if (control.equals(NEW_LINE))
		{
			text.append(NEW_LINE_CHAR);
		}
		else if (control.equals(TAB))
		{
			text.append(TAB_CHAR);
		}
		else if (in(control, RESET))
		{
			handleControlCommand(BOLD_END, text, state, document);
			handleControlCommand(ITALIC_END, text, state, document);
			handleControlCommand(UNDERLINE_END, text, state, document);
		}
		else if (in(control, ESCAPE_CONTROLS))
		{
			text.append(control);
		}
		else if (control.charAt(0) == ESCAPE_8BIT)
		{
			text.append(get8bit(control.substring(1)));
		}
		else if (isUnicodeCount(control))
		{
			int number = Integer.parseInt(control.substring(2));
			state.put(UNICOUNT, number);
		}
		else if (isUnicode(control))
		{
			ParserPair unicode = parseUnicode(control.substring(1));
			int unicodeNumber = Integer.parseInt(unicode.fString);
			char unicodeChar = (char) unicodeNumber;
			text.append(unicodeChar);
		}
	}
	//CSON:

	private boolean isUnicodeCount(String control)
	{
		return control.length() > 2 && control.startsWith(UNICODE_COUNT_FULL) && Character.isDigit(control.charAt(2));
	}

	private ParserPair parseUnicode(String unicodeStr)
	{
		StringBuilder number = new StringBuilder();
		int size = unicodeStr.length();
		for (int i = 0; i < size; i++)
		{
			char ch = unicodeStr.charAt(i);
			if (Character.isDigit(ch))
			{
				number.append(ch);
			}
			else
			{
				// For now, we don't care about the replacement...
				break;
			}
		}

		return new ParserPair(-1, number.toString());
	}

	private boolean isUnicode(String control)
	{
		return control.length() > 1 && control.charAt(0) == UNICODE && Character.isDigit(control.charAt(1));
	}

	private char get8bit(String numberStr)
	{
		int c = Integer.parseInt(numberStr, HEX_RADIX);
		// Replace weird bullet point by dashes.
		if (c == BULLET_POINT)
		{
			c = '-';
		}
		return (char) c;
	}

	private ParserPair handleControl(InputStream contentStream, Map<String, Integer> state) throws IOException
	{
		int c = contentStream.read();
		return handleControl(contentStream, c, EMPTY, state);
	}

	//CSOFF:
	private ParserPair handleControl(InputStream contentStream, int startChar, String startControl, Map<String, Integer> state) throws IOException
	{
		StringBuilder controlWord = new StringBuilder(startControl);
		int c = startChar;
		char ch;

		while (c != -1)
		{
			ch = (char) c;
			if (ch == UNICODE && isEmpty(controlWord))
			{
				// This is potentially an unicode char
				ParserPair pair = getUnicode(contentStream, state);
				c = pair.fChar;
				controlWord = new StringBuilder(pair.fString);
				break;
			}
			else if (Character.isLetter(ch))
			{
				// Start of a control word
				controlWord.append(ch);
			}
			else if (ch == ESCAPE_8BIT && isEmpty(controlWord))
			{
				// This is an escaped 8bit char
				ParserPair pair = get8Bit(contentStream);
				c = pair.fChar;
				controlWord = new StringBuilder(pair.fString);
				break;
			}
			else if (Character.isDigit(ch))
			{
				// Unit of control word
				controlWord.append(ch);
			}
			else if (ch == MINUS)
			{
				controlWord.append(ch);
			}
			else
			{
				if (isEmpty(controlWord))
				{
					controlWord.append(ch);
					c = contentStream.read();
				}
				else if (Character.isWhitespace(ch))
				{
					// This is a delimiter. Skip it
					c = contentStream.read();
				}
				break;
			}
			c = contentStream.read();
		}

		return new ParserPair(c, controlWord.toString());
	}
	//CSON:
	
	private ParserPair get8Bit(InputStream contentStream) throws IOException
	{
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(ESCAPE_8BIT);
		sBuilder.append((char) contentStream.read());
		sBuilder.append((char) contentStream.read());
		int c = contentStream.read();
		return new ParserPair(c, sBuilder.toString());
	}

	
	private ParserPair getUnicode(InputStream contentStream, Map<String, Integer> state) throws IOException
	{
		
		StringBuilder control = new StringBuilder();
		int c = contentStream.read();
		if (c != -1)
		{
			char ch = (char) c;
			if (ch == UNICODE_COUNT)
			{
				ParserPair number = getNumber(contentStream);
				control.append(UNICODE_COUNT_FULL);
				control.append(number.fString);
				c = number.fChar;
				// This is a control so a space is a delimiter.
				if (Character.isWhitespace((char) c))
				{
					c = contentStream.read();
				}
			}
			else if (!Character.isDigit(ch))
			{
				ParserPair result = handleControl(contentStream, c, String.valueOf(UNICODE), state);
				c = result.fChar;
				control = new StringBuilder(result.fString);
			}
			else
			{
				ParserPair number = getNumber(contentStream, Integer.parseInt(String.valueOf(ch)));
				int replacement = number.fChar;
				ParserPair replPair = getUnicodeReplacement(contentStream, replacement, state);
				c = replPair.fChar;
				
				control.append(UNICODE);
				control.append(number.fString);
				control.append(replPair.fString);
			}
		}

		return new ParserPair(c, control.toString());
	}
	
	private ParserPair getUnicodeReplacement(InputStream contentStream, int replC, Map<String, Integer> state)
	throws IOException
	{
		int count = state.get(UNICOUNT);
		int currentCount = 0;
		int c = replC;
		StringBuilder replString = new StringBuilder();
		while (currentCount < count)
		{
			String replch = String.valueOf((char) c);
			if (equal(BACKSLASH, replch))
			{
				// This is a 8 bit
				ParserPair repl8bit = handleControl(contentStream, state);
				replString.append(repl8bit.fString);
				c = repl8bit.fChar;
			}
			else
			{
				// This was a 7 bit character
				replString.append(replch);
				c = contentStream.read();
			}
			currentCount++;
		}
		return new ParserPair(c, replString.toString());
	}

	private ParserPair getNumber(InputStream contentStream) throws IOException
	{
		return getNumber(contentStream, -1);
	}
	
	private ParserPair getNumber(InputStream contentStream, int initial) throws IOException
	{
		StringBuilder number = new StringBuilder();
		if (initial > -1)
		{
			number.append(initial);
		}
		int c = contentStream.read();
		while (c != -1)
		{
			char ch = (char) c;
			if (Character.isDigit(ch))
			{
				number.append(ch);
				c = contentStream.read();
			}
			else
			{
				break;
			}
		}
		return new ParserPair(c, number.toString());
	}

	private boolean isEmpty(StringBuilder builder)
	{
		return builder.toString().isEmpty();
	}

	private Map<String, Integer> safeState(Stack<Map<String, Integer>> state)
	{
		return safeState(state, false);
	}

	/**
	 * Returns the set of tags at the top of the stack. Return an empty set if the stack is empty. This should never
	 * occur, but some badly-formatted RTF documents seem to lead to this situation.
	 * 
	 * @param state
	 * @param pop
	 * @return
	 */
	private Map<String, Integer> safeState(Stack<Map<String, Integer>> state, boolean pop)
	{
		if (!state.isEmpty())
		{
			if (pop)
			{
				return state.pop();
			}
			else
			{
				return state.peek();
			}
		}
		else
		{
			gLogger.error("State was empty.");
			return new HashMap<String, Integer>();
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startBold(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (state.containsKey(BOLD_START))
		{
			return;
		}

		int boldPos = currentText.length();
		int italicPos = getDefault(state, ITALIC_START, -1);
		int underlinePos = getDefault(state, UNDERLINE_START, -1);

		if (italicPos != -1 && underlinePos != -1 && italicPos != boldPos)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(italicPos, boldPos - italicPos);

			document.addAnnotation(position, annotation);
			italicPos = boldPos;
			underlinePos = boldPos;
		}
		else if (italicPos != -1 && italicPos != boldPos)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			Position position = new Position(italicPos, boldPos - italicPos);

			document.addAnnotation(position, annotation);
			italicPos = boldPos;
		}
		else if (underlinePos != -1 && underlinePos != boldPos)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(underlinePos, boldPos - underlinePos);

			document.addAnnotation(position, annotation);
			underlinePos = boldPos;
		}

		// Save state
		state.put(BOLD_START, boldPos);
		if (italicPos != -1)
		{
			state.put(ITALIC_START, italicPos);
		}
		if (underlinePos != -1)
		{
			state.put(UNDERLINE_START, underlinePos);
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endBold(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (!state.containsKey(BOLD_START))
		{
			return;
		}

		int boldPos = state.get(BOLD_START);
		int italicPos = getDefault(state, ITALIC_START, -1);
		int underlinePos = getDefault(state, UNDERLINE_START, -1);

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(boldPos, curPos - boldPos);

		if (italicPos != -1 && underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			italicPos = curPos;
			underlinePos = curPos;
			state.put(ITALIC_START, italicPos);
			state.put(UNDERLINE_START, underlinePos);
		}
		else if (italicPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			italicPos = curPos;
			state.put(ITALIC_START, italicPos);
		}
		else if (underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			underlinePos = curPos;
			state.put(UNDERLINE_START, underlinePos);
		}
		else
		{
			annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		state.remove(BOLD_START);
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endUnderline(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (!state.containsKey(UNDERLINE_START))
		{
			return;
		}

		int underlinePos = state.get(UNDERLINE_START);
		int italicPos = getDefault(state, ITALIC_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(underlinePos, curPos - underlinePos);

		if (boldPos != -1 && italicPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			boldPos = curPos;
			italicPos = curPos;
			state.put(BOLD_START, boldPos);
			state.put(ITALIC_START, italicPos);
		}
		else if (boldPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			boldPos = curPos;
			state.put(BOLD_START, boldPos);
		}
		else if (italicPos != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			italicPos = curPos;
			state.put(ITALIC_START, italicPos);
		}
		else
		{
			annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		state.remove(UNDERLINE_START);
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startUnderline(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (state.containsKey(UNDERLINE_START))
		{
			return;
		}

		int underlinePos = currentText.length();
		int italicPos = getDefault(state, ITALIC_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		if (boldPos != -1 && italicPos != -1 && boldPos != underlinePos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			Position position = new Position(boldPos, underlinePos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = underlinePos;
			italicPos = underlinePos;
		}
		else if (boldPos != -1 && boldPos != underlinePos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(boldPos, underlinePos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = underlinePos;
		}
		else if (italicPos != -1 && italicPos != underlinePos)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			Position position = new Position(italicPos, underlinePos - italicPos);

			document.addAnnotation(position, annotation);
			italicPos = underlinePos;
		}

		// Save state
		state.put(UNDERLINE_START, underlinePos);
		if (italicPos != -1)
		{
			state.put(ITALIC_START, italicPos);
		}
		if (boldPos != -1)
		{
			state.put(BOLD_START, boldPos);
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endItalic(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (!state.containsKey(ITALIC_START))
		{
			return;
		}

		int italicPos = state.get(ITALIC_START);
		int underlinePos = getDefault(state, UNDERLINE_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(italicPos, curPos - italicPos);

		if (boldPos != -1 && underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			boldPos = curPos;
			underlinePos = curPos;
			state.put(BOLD_START, boldPos);
			state.put(UNDERLINE_START, underlinePos);
		}
		else if (boldPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			boldPos = curPos;
			state.put(BOLD_START, boldPos);
		}
		else if (underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			underlinePos = curPos;
			state.put(UNDERLINE_START, underlinePos);
		}
		else
		{
			annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		state.remove(ITALIC_START);
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startItalic(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (state.containsKey(ITALIC_START))
		{
			return;
		}

		int italicPos = currentText.length();
		int underlinePos = getDefault(state, UNDERLINE_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		if (boldPos != -1 && underlinePos != -1 && boldPos != italicPos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(boldPos, italicPos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = italicPos;
			underlinePos = italicPos;
		}
		else if (boldPos != -1 && boldPos != italicPos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(boldPos, italicPos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = italicPos;
		}
		else if (underlinePos != -1 && underlinePos != italicPos)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(underlinePos, italicPos - underlinePos);

			document.addAnnotation(position, annotation);
			underlinePos = italicPos;
		}

		// Save state
		state.put(ITALIC_START, italicPos);
		if (underlinePos != -1)
		{
			state.put(UNDERLINE_START, underlinePos);
		}
		if (boldPos != -1)
		{
			state.put(BOLD_START, boldPos);
		}
	}
	
	/**
	 * Converts the contents of the document back into rtf so that it can be saved to disk.
	 * 
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException
	{
		FileEditorInput input = (FileEditorInput) element;
		IAnnotationModel model = getAnnotationModel(element);

		StringBuilder contents = new StringBuilder(document.get());
		StringBuilder toWrite = new StringBuilder(EMPTY);

		toWrite = buildRTFString(contents, model);

		InputStream stream = new ByteArrayInputStream(toWrite.toString().getBytes());
		try
		{
			input.getFile().setContents(stream, IResource.FORCE, new NullProgressMonitor());
		}
		catch (CoreException e)
		{

		}

		// This seems to be necessary for the timestamp markers to persist across saves.
		FileInfo info = (FileInfo) getElementInfo(element);
		if (info != null)
		{
			RTFAnnotationModel rtfModel = (RTFAnnotationModel) info.fModel;
			rtfModel.updateMarkers(info.fDocument);
		}

		// Updates all of the fragment positions, and removes any annotations that have length 0.
		IAnnotatedDocument rtfDoc = ((RTFEditorInput) element).getDocument();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		while (iter.hasNext())
		{
			Annotation annotation = iter.next();
			if (annotation instanceof FragmentAnnotation)
			{
				updateFragment(model, rtfDoc, annotation);
			}
			else
			{
				if (model.getPosition(annotation).length == 0)
				{
					model.removeAnnotation(annotation);
				}
			}
		}

		Facade.getInstance().saveDocument(rtfDoc);
	}
	
	/**
	 * Updates the given annotation's fragment to match it's new offset and length. Then updates the map key so that it
	 * matches the new offset. If the fragment has a length of 0 it gets removed from the model (and the DB).
	 * 
	 * @param model
	 * @param rtfDoc
	 * @param annotation
	 */
	private void updateFragment(IAnnotationModel model, IAnnotatedDocument rtfDoc, Annotation annotation)
	{
		Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
		Position position = model.getPosition(annotation);
		if (position.length == 0)
		{
			model.removeAnnotation(annotation);
		}
		else
		{
			int oldOffset = fragment.getOffset();
			fragment.setOffset(position.offset);
			fragment.setLength(position.length);
			rtfDoc.getFragments().remove(oldOffset);
			rtfDoc.getFragments().put(position.offset, fragment);
		}
	}

	/**
	 * Goes through the editor text and all the annotations to build the string that will be written to the disk.
	 * Converts any special characters to their RTF tags as well.
	 * 
	 * @param contents
	 * @param model
	 * @return
	 */
	private StringBuilder buildRTFString(StringBuilder contents, IAnnotationModel model)
	{
		StringBuilder output = new StringBuilder(HEADER);
		ArrayList<Position> positions = new ArrayList<Position>();
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		prepareAnnotationLists(model, positions, annotations);

		Position position = null;
		Annotation annotation = null;

		for (int i = 0; i < contents.length(); i++)
		{
			if (position == null)
			{
				for (int j = 0; j < positions.size(); j++)
				{
					if (positions.get(j).offset == i)
					{
						position = positions.remove(j);
						annotation = annotations.remove(j);
						break;
					}
					else if (positions.get(j).offset > i)
					{
						break;
					}
				}
				if (position != null)
				{
					output.append(getStartTagFromAnnotation(annotation));
				}
			}

			char c = contents.charAt(i);
			output.append(getMiddleChar(c));

			if (position != null && i == position.offset + position.length - 1)
			{
				output.append(getEndTagFromAnnotation(annotation));

				position = null;
				annotation = null;
			}

			output.append(getEndChar(c));
		}

		return output.append(FOOTER);
	}
	
	/**
	 * Gets the annotations and their positions from the model and sorts them by position. Sets them into the two
	 * provided arraylists.
	 * 
	 * @param model
	 * @param positions
	 * @param annotations
	 */
	@SuppressWarnings("unchecked")
	private void prepareAnnotationLists(IAnnotationModel model, ArrayList<Position> positions,
			ArrayList<Annotation> annotations)
	{
		Iterator<Annotation> iter = model.getAnnotationIterator();
		while (iter.hasNext())
		{
			Annotation annotation = iter.next();
			String type = annotation.getType();
			if (!(annotation instanceof FragmentAnnotation) && !type.equals(RTFConstants.TIMESTAMP_TYPE))
			{
				if (positions.isEmpty())
				{
					annotations.add(annotation);
					positions.add(model.getPosition(annotation));
				}
				else
				{
					Position position = model.getPosition(annotation);
					int i;
					for (i = 0; i < positions.size(); i++)
					{
						Position curPos = positions.get(i);
						if (position.offset < curPos.offset)
						{
							annotations.add(i, annotation);
							positions.add(i, position);
							break;
						}
					}

					if (i >= positions.size())
					{
						annotations.add(annotation);
						positions.add(position);
					}
				}

			}
		}
	}

	/**
	 * Gets the rtf representations of newline and tab if the current character is one of those.
	 * 
	 * @param c
	 * @return
	 */
	private String getEndChar(char c)
	{
		StringBuilder output = new StringBuilder(EMPTY);
		if (c == '\n')
		{
			output.append(NEW_LINE_TAG);
		}
		else if (c == '\t')
		{
			output.append(TAB_TAG);
		}
		return output.toString();
	}

	/**
	 * Stops newlines tabs and EOF from being written, adds an escape to brackets and backslash, converts non-ascii
	 * characters to their RTF tag and lets all other characters through.
	 * 
	 * @param c
	 * @return
	 */
	private String getMiddleChar(char c)
	{
		StringBuilder output = new StringBuilder(EMPTY);
		if (c != '\n' && c != '\t' && c != '\0')
		{
			if (c == '{' || c == '}' || c == '\\')
			{
				output.append(BACKSLASH);
			}

			if (CharUtils.isAscii(c))
			{
				output.append(c);
			}
			else
			{
				int unicode = (int) c;
				output = new StringBuilder(UNICODE_START_TAG + unicode + UNICODE_END_TAG);
			}
		}
		return output.toString();
	}

	/**
	 * @param annotation
	 * @return
	 */
	private String getEndTagFromAnnotation(Annotation annotation)
	{
		String tag = EMPTY;
		String type = annotation.getType();

		if (type.equals(RTFConstants.BOLD_TYPE))
		{
			tag = BOLD_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = ITALIC_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = UNDERLINE_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = BOLD_END_TAG + ITALIC_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = BOLD_END_TAG + UNDERLINE_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = ITALIC_END_TAG + UNDERLINE_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			tag = BOLD_END_TAG + ITALIC_END_TAG + UNDERLINE_END_TAG + SPACE;
		}

		return tag;
	}

	/**
	 * @param annotation
	 * @return
	 */
	private String getStartTagFromAnnotation(Annotation annotation)
	{
		String tag = EMPTY;
		String type = annotation.getType();

		if (type.equals(RTFConstants.BOLD_TYPE))
		{
			tag = BOLD_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = ITALIC_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = UNDERLINE_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = BOLD_START_TAG + ITALIC_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = BOLD_START_TAG + UNDERLINE_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = ITALIC_START_TAG + UNDERLINE_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			tag = BOLD_START_TAG + ITALIC_START_TAG + UNDERLINE_START_TAG + SPACE;
		}

		return tag;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException
	{
		if (element instanceof RTFEditorInput)
		{
			return new RTFAnnotationModel((RTFEditorInput) element);
		}

		return super.createAnnotationModel(element);
	}
}

/**
 * Used by the parser to return a string and the last read character (similar to peek).
 *
 */
//CSOFF:
class ParserPair
{
	public final int fChar;

	public final String fString;

	public ParserPair(int ch, String str)
	{
		fChar = ch;
		fString = str;
	}
}
//CSON:
