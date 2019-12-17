/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.util;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;

import ca.mcgill.cs.swevo.qualyzer.editors.RTFDocumentProvider2;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * FragmentUtil provides methods to interact with fragments' content.
 * 
 */
public final class FragmentUtil
{

	private FragmentUtil()
	{
		
	}
	
	/**
	 * 
	 *
	 * @return
	 */
	public static String getDocumentText(IAnnotatedDocument document) 
	{
		RTFDocumentProvider2 provider = new RTFDocumentProvider2();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(document.getProject().getFolderName());
		IFile file;
		if(document instanceof Transcript)
		{
			file = project.getFile("transcripts" + File.separator + document.getFileName()); //$NON-NLS-1$
		}
		else
		{
			file = project.getFile("memos" + File.separator + document.getFileName()); //$NON-NLS-1$
		}
		
		RTFEditorInput input = new RTFEditorInput(file, Facade.getInstance().forceDocumentLoad(document));
		IDocument createdDocument = provider.getCreatedDocument(input);
		return createdDocument.get();
	}
	
	/**
	 * 
	 *
	 * @param fragment
	 * @param documentText
	 * @return
	 */
	public static String getFragmentText(Fragment fragment, String documentText) 
	{
		return documentText.substring(fragment.getOffset(), fragment.getOffset() + fragment.getLength());
	}
	
	/**
	 * @param fragments
	 */
	public static void sortFragments(List<Fragment> fragments)
	{
		Collections.sort(fragments, new Comparator<Fragment>(){

			@Override
			public int compare(Fragment o1, Fragment o2)
			{
				return o1.getOffset() - o2.getOffset();
			}
		});
	}
	
}
