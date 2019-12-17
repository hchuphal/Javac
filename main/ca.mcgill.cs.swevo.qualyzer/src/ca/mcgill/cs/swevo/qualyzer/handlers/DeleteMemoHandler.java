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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.MemoDeleteDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Verifies that the memos can be deleted and then prompts for confirmation and deletes them.
 *
 */
public class DeleteMemoHandler extends AbstractHandler implements ITestableHandler
{
	private static final String MEMO = File.separator + "memos" + File.separator; //$NON-NLS-1$
	private final Logger fLogger = LoggerFactory.getLogger(DeleteMemoHandler.class);
	private IDialogTester fTester = new NullTester();
	private boolean fTesting = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = view.getCommonViewer().getSelection();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			List<Memo> toDelete = new ArrayList<Memo>();
			List<Project> projects = new ArrayList<Project>();
			
			for(Object element : ((IStructuredSelection) selection).toArray())
			{
				if(element instanceof Memo)
				{
					Memo memo = (Memo) element;
					
					if(!projects.contains(memo.getProject()))
					{
						projects.add(memo.getProject());
					}
					
					toDelete.add(memo);	
				}
			}
			
			if(projects.size() > 1)
			{
				String warningMessage = MessagesClient.getString("handlers.DeleteMemoHandler.tooManyProjects", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
				fLogger.warn(warningMessage);
				MessageDialog.openError(shell, MessagesClient.getString(
						"handlers.DeleteMemoHandler.unableToDelete", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), warningMessage); //$NON-NLS-1$
			}
			else
			{
				proceedWithDeletion(page, shell, toDelete);
			}
		}
		return null;
	}

	/**
	 * @param page
	 * @param shell
	 * @param toDelete
	 */
	private void proceedWithDeletion(IWorkbenchPage page, Shell shell, List<Memo> toDelete)
	{
		MemoDeleteDialog dialog = new MemoDeleteDialog(shell, toDelete.size() > 1);
		dialog.create();
		dialog.setBlockOnOpen(!fTesting);
		dialog.open();
		fTester.execute(dialog);
			
		if(dialog.getReturnCode() == Window.OK)
		{	
			CommonNavigator view;
			view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
			
			for(Memo memo : toDelete)
			{
				delete(memo, shell, dialog.deleteCodes());
				view.getCommonViewer().refresh();
			}
			
			view.getCommonViewer().refresh();
		}	
	}

	/**
	 * @param memo
	 * @param shell
	 */
	private void delete(Memo memo, Shell shell, boolean deleteCodes)
	{
		Project project = memo.getProject();
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getFolderName());
		
		File file = new File(wProject.getLocation() + MEMO + memo.getFileName());
		if(!file.delete())
		{
			String warningMessage = MessagesClient.getString("handlers.DeleteMemoHandler.deleteFailed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
			fLogger.warn(warningMessage);
			MessageDialog.openWarning(shell, MessagesClient.getString(
					"handlers.DeleteMemoHandler.fileError", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), warningMessage); //$NON-NLS-1$
		}
		
		ArrayList<Code> codes = null;
		if(deleteCodes)
		{
			codes = findCodesToDelete(memo);
		}
		
		Facade.getInstance().deleteMemo(memo);
		
		if(codes != null)
		{
			for(Code code : codes)
			{
				Facade.getInstance().deleteCode(code);
			}
		}
	}

	/**
	 * @param memo
	 * @return
	 */
	private ArrayList<Code> findCodesToDelete(Memo memo)
	{
		ArrayList<Code> codes = new ArrayList<Code>();
		Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
		Project project = lMemo.getProject();
		for(Fragment fragment : lMemo.getFragments().values())
		{
			for(CodeEntry entry : fragment.getCodeEntries())
			{
				Code code = entry.getCode();
				if(!codes.contains(code))
				{
					codes.add(code);
				}
			}
		}
		for(Transcript transcript : project.getTranscripts())
		{
			Transcript lTrans = Facade.getInstance().forceTranscriptLoad(transcript);
			for(Fragment fragment : lTrans.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					Code code = entry.getCode();
					if(codes.contains(code))
					{
						codes.remove(code);
					}
				}
			}
		}
		for(Memo pMemo : project.getMemos())
		{
			if(!pMemo.equals(memo))
			{
				Memo lMem = Facade.getInstance().forceMemoLoad(pMemo);
				for(Fragment fragment : lMem.getFragments().values())
				{
					for(CodeEntry entry : fragment.getCodeEntries())
					{
						Code code = entry.getCode();
						if(codes.contains(code))
						{
							codes.remove(code);
						}
					}
				}
			}
		}
		return codes;
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
