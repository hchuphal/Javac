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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameMemoDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * 
 *
 */
public class RenameMemoHandler extends AbstractHandler implements ITestableHandler
{

	
	/**
	 * 
	 */
	private static final String DOT = "."; //$NON-NLS-1$
	private static final String MEMO = File.separator + "memos" + File.separator; //$NON-NLS-1$
	private static final String EXT = ".rtf"; //$NON-NLS-1$
	private boolean fClosed = false;
	private IDialogTester fTester = new NullTester();
	private boolean fTesting = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		ISelection selection = view.getCommonViewer().getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			Project project = ResourcesUtil.getProject(element);
			if(element instanceof Memo)
			{
				String currentName = ((Memo) element).getName();
				RenameMemoDialog dialog = new RenameMemoDialog(shell, project);
				dialog.setOldName(currentName);
				dialog.create();
				dialog.setBlockOnOpen(!fTesting);
				dialog.open();
				fTester.execute(dialog);
				
				if(dialog.getReturnCode() == Window.OK)
				{
					rename((Memo) element, dialog.getName());
					
					Facade.getInstance().saveMemo((Memo) element);
					
					view.getCommonViewer().refresh();
					
					if(fClosed)
					{
						ResourcesUtil.openEditor(page, (Memo) element);
					}
				}
			}
		}
		return null;
	}
	
	private void rename(Memo memo, String name)
	{		
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = activePage.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			String editorName = memo.getProject().getName() + DOT + Memo.class.getSimpleName() + DOT;
			if(editor.getName().equals(editorName + memo.getFileName()))
			{
				activePage.closeEditor(editor.getEditor(true), true);
				fClosed  = true;
			}
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(memo.getProject().getFolderName());
		
		String projectPath = project.getLocation().toString();
		File origFile = new File(projectPath + MEMO + memo.getFileName());
		File newFile = new File(projectPath + MEMO + name.replace(' ', '_') + EXT);
		
		origFile.renameTo(newFile);
		
		memo.setName(name);
		memo.setFileName(name.replace(' ', '_')+EXT);
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#getTester()
	 */
	@Override
	public IDialogTester getTester()
	{
		return fTester;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#isWindowsBlock()
	 */
	@Override
	public boolean isTesting()
	{
		return fTesting;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setTester(
	 * ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester)
	 */
	@Override
	public void setTester(IDialogTester tester)
	{
		fTester = tester;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setWindowsBlock(boolean)
	 */
	@Override
	public void setTesting(boolean windowsBlock)
	{
		fTesting = windowsBlock;
	}

}
