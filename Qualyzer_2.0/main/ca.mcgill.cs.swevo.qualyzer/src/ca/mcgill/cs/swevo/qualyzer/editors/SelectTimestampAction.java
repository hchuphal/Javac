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

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.SelectMarkerRulerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;

/**
 * The action that occurs when the user clicks on a Timestamp in the vertical ruler.
 */
public class SelectTimestampAction extends SelectMarkerRulerAction
{
	private static Logger gLogger = LoggerFactory.getLogger(SelectTimestampAction.class);
	
	private TranscriptEditor fEditor;
	private IVerticalRulerInfo fInfo;
	
	/**
	 * @param bundle
	 * @param prefix
	 * @param editor
	 * @param ruler
	 */
	public SelectTimestampAction(ResourceBundle bundle, String prefix, TranscriptEditor editor, 
			IVerticalRulerInfo ruler)
	{
		super(bundle, prefix, editor, ruler);
		fEditor = editor;
		fInfo = ruler;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.SelectMarkerRulerAction#run()
	 */
	@Override
	public void run()
	{
		int line = fInfo.getLineOfLastMouseButtonActivity();
		
		IFile file = ((RTFEditorInput) fEditor.getEditorInput()).getFile();
		try
		{
			for(IMarker marker : file.findMarkers(RTFConstants.TIMESTAMP_MARKER_ID, false, 0))
			{
				int theLine = marker.getAttribute(IMarker.LINE_NUMBER, -1);
				int time = marker.getAttribute("time", 0); //$NON-NLS-1$
				if(marker.exists() &&  theLine == line + 1)
				{
					fEditor.seekToTime(time - 1);
					fEditor.moveCursorToLine(line);
				}
				else if(!marker.exists())
				{
					marker.delete();
				}
			}
		}
		catch (CoreException e)
		{
			gLogger.error("Select Timestamp failed", e); //$NON-NLS-1$
		}
	}
	
	
}
