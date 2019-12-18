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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * View all the associated fragments actions. Asks which code the user wants to examine and then
 * opens the fragment viewer.
 */
public class ViewFragmentsAction extends Action
{
	private RTFEditor fEditor;
	private RTFSourceViewer fViewer;
	
	/**
	 * @param rtfEditor
	 */
	public ViewFragmentsAction(RTFEditor rtfEditor, RTFSourceViewer viewer)
	{
		fEditor = rtfEditor;
		fViewer = viewer;
		setText(MessagesClient.getString("editors.ViewFragmentsAction.viewFragments", "ca.mcgill.cs.swevo.qualyzer.editors.Messages")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		Point selection = fViewer.getSelectedRange();
		Fragment fragment = fEditor.getDocument().getFragments().get(selection.x);
		
		Code toOpen = chooseCode(fragment);
		
		if(toOpen != null)
		{
			ResourcesUtil.openEditor(fEditor.getSite().getPage(), toOpen);
		}
	}

	/**
	 * Opens the selection dialog and then returns the code that the user wants to view.
	 * @param fragment
	 * @return
	 */
	private Code chooseCode(Fragment fragment)
	{
		Code toOpen = fragment.getCodeEntries().get(0).getCode();
		if(fragment.getCodeEntries().size() > 1)
		{
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					fEditor.getSite().getShell(), new LabelProvider());
			String[] codes = new String[fragment.getCodeEntries().size()];
			
			for(int i = 0; i < codes.length; i++)
			{
				codes[i] = fragment.getCodeEntries().get(i).getCode().getCodeName();
			}
			dialog.setMessage(MessagesClient.getString("editors.ViewFragmentsAction.chooseCode", "ca.mcgill.cs.swevo.qualyzer.editors.Messages")); //$NON-NLS-1$
			dialog.setElements(codes);
			dialog.setMultipleSelection(false);
			
			if(dialog.open() == Window.OK)
			{
				Object[] choice = dialog.getResult();
				String codeName = (String) choice[0];
				
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					if(entry.getCode().getCodeName().equals(codeName))
					{
						toOpen = entry.getCode();
						break;
					}
				}
			}
			else
			{
				return null;
			}
		}
		return toOpen;
	}

	
	
	
}
