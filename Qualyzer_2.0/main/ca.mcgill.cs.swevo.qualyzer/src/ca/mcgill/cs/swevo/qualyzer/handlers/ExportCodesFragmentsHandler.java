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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.io.Closeable;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;
import ca.mcgill.cs.swevo.qualyzer.util.FragmentUtil;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
public class ExportCodesFragmentsHandler extends AbstractHandler
{

	/**
	 * 
	 */
	private static final String CSV = ".csv"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String COMMA = ","; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String QUOTE = "\"";
	
	private final Logger fLogger = LoggerFactory.getLogger(ExportCodesFragmentsHandler.class);

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection structured = (IStructuredSelection) selection;
			Object element = structured.getFirstElement();
			
			if(element instanceof WrapperCode)
			{
				Project project = ((WrapperCode) element).getProject();
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setText(MessagesClient.getString("handlers.ExportCodesHandler.exportDestination", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")); //$NON-NLS-1$
				dialog.setFilterExtensions(new String[]{"*.csv"}); //$NON-NLS-1$
				String fileName = dialog.open();
				
				if(fileName !=  null)
				{
					int index = fileName.indexOf('.');
					if(index == -1 || !fileName.substring(index).equals(CSV))
					{
						fileName += CSV;
					}
					
					exportCodes(fileName, project, shell);
				}
			}
		}
		return null;
	}
	
	private void exportCodes(String fileName, Project project, Shell shell) 
	{
		Facade facade = Facade.getInstance();
		PrintWriter printer = null;
		
		try 
		{
			printer = new PrintWriter(new File(fileName));
			printDocuments(project.getTranscripts(), MessagesClient.getString("handlers.ExportCodesHandler.transcript", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), 
					facade, printer);
			printDocuments(project.getMemos(), MessagesClient.getString("handlers.ExportCodesHandler.memo", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), 
					facade, printer);
			
			MessageDialog.openInformation(shell, MessagesClient.getString(
			"handlers.ExportCodesHandler.exportSucessful", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), MessagesClient.getString(//$NON-NLS-1$ 
					"handlers.ExportCodesHandler.exportMessage", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + fileName); //$NON-NLS-1$ 
		}
		// CSOFF:
		catch(Exception e) 
		{
			fLogger.error("Error while exporting code fragments.", e);
			throw new QualyzerException(MessagesClient.getString(
			"handlers.ExportCodesHandler.writeFailed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")); //$NON-NLS-1$
		}
		// CSON:
		finally 
		{
			quietClose(printer);
		}
	}
	
	private void printDocuments(List<? extends IAnnotatedDocument> documents, String documentPrefix, 
			Facade facade, PrintWriter printer) 
	{
		for (IAnnotatedDocument document : documents) 
		{
			IAnnotatedDocument newDocument = facade.forceDocumentLoad(document);
			String text = FragmentUtil.getDocumentText(newDocument);
			for (Fragment fragment : newDocument.getFragments().values()) 
			{
				String fragmentText = cleanFragment(FragmentUtil.getFragmentText(fragment, text));
				for (CodeEntry codeEntry : fragment.getCodeEntries())
				{
					StringBuilder builder = new StringBuilder();
					builder.append(codeEntry.getCode().getCodeName());
					builder.append(COMMA);
					builder.append(fragmentText);
					builder.append(COMMA);
					builder.append(documentPrefix + document.getName());
					printer.println(builder.toString());
				}
			}
		}
	}
	
	private String cleanFragment(String fragmentText) 
	{
		StringBuilder builder = new StringBuilder();
		builder.append(QUOTE);
		builder.append(fragmentText.replace("\n", " ").replace("\r", "").replace(QUOTE, QUOTE+QUOTE));
		builder.append(QUOTE);
		return builder.toString();
	}
	
	// CSOFF:
	private void quietClose(Closeable closable) 
	{
		try 
		{
			if (closable != null) 
			{
				closable.close();
			}
		} 
		catch(Exception e) {
			
		}
	}
	// CSON:






}