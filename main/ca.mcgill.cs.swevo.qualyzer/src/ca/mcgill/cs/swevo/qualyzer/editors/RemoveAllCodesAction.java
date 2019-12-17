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

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Point;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 * Removes all the codes from a given fragment. Then deletes that fragment.
 */
public class RemoveAllCodesAction extends Action
{
	
	private RTFEditor fEditor;
	private RTFSourceViewer fSourceViewer;
	
	/**
	 * @param editor 
	 * @param viewer 
	 * 
	 */
	public RemoveAllCodesAction(RTFEditor editor, RTFSourceViewer viewer)
	{
		super(MessagesClient.getString("editors.RemoveAllCodesAction.removeAllCodes", "ca.mcgill.cs.swevo.qualyzer.editors.Messages")); //$NON-NLS-1$
		fEditor = editor;
		fSourceViewer = viewer;
	}
	
	/**
	 * Opens a confirmation dialog and then removes all the codes from the selected fragment.
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run()
	{
		boolean check = MessageDialog.openConfirm(fEditor.getSite().getShell(), 
				MessagesClient.getString("editors.RemoveAllCodesAction.removeAllCodes", "ca.mcgill.cs.swevo.qualyzer.editors.Messages"), //$NON-NLS-1$
				MessagesClient.getString("editors.RemoveAllCodesAction.confirm", "ca.mcgill.cs.swevo.qualyzer.editors.Messages")); //$NON-NLS-1$ 
		
		if(!check)
		{
			return;
		}
		
		Point selection = fSourceViewer.getSelectedRange();
		RTFAnnotationModel model = (RTFAnnotationModel) fSourceViewer.getAnnotationModel();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(annotation instanceof FragmentAnnotation)
			{
				Position pos = model.getPosition(annotation);
				if(pos.offset == selection.x && pos.length == selection.y)
				{
					model.removeAnnotationOnly(annotation);
					Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
					Facade.getInstance().deleteFragment(fragment);
					fEditor.setDirty();
					break;
				}
			}
		}
	}
}
