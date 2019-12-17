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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;

/**
 * 
 *
 */
public class ExportCodesHandler extends AbstractHandler
{

	/**
	 * 
	 */
	private static final String CSV = ".csv"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String COMMA = ","; //$NON-NLS-1$

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
				Facade facade = Facade.getInstance();
				
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
					
					Map<String, List<String>> documentMap = new HashMap<String, List<String>>();
					Map<String, List<Integer>> freqMap = new HashMap<String, List<Integer>>();
					Map<String, String> codeMap = buildCodeMap(project);
					
					detectDocuments(project, facade, documentMap, freqMap);
					StringBuilder buffer = buildStringBuilder(documentMap, freqMap, codeMap);
					
					writeFile(shell, fileName, buffer);
				}
			}
		}
		return null;
	}

	/**
	 * Does something.
	 *
	 * @param project
	 * @return
	 */
	private Map<String, String> buildCodeMap(Project project)
	{
		Map<String, String> codeMap = new HashMap<String, String>();
		for (Code code : project.getCodes())
		{
			codeMap.put(code.getCodeName(), code.getDescription());
		}
		return codeMap;
	}

	/**
	 * @param shell
	 * @param fileName
	 * @param buffer
	 */
	private void writeFile(Shell shell, String fileName, StringBuilder buffer)
	{
		File file = new File(fileName);
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(file);
			writer.write(buffer.toString());
			MessageDialog.openInformation(shell, MessagesClient.getString(
					"handlers.ExportCodesHandler.exportSucessful", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), MessagesClient.getString(//$NON-NLS-1$ 
							"handlers.ExportCodesHandler.exportMessage", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + fileName); //$NON-NLS-1$ 
		}
		catch (IOException e)
		{
			throw new QualyzerException(MessagesClient.getString(
					"handlers.ExportCodesHandler.writeFailed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				if(writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
				throw new QualyzerException(MessagesClient.getString(
						"handlers.ExportCodesHandler.closeFailed", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @param transcriptMap
	 * @param freqMap
	 * @return
	 */
	private StringBuilder buildStringBuilder(Map<String, List<String>> transcriptMap, 
			Map<String, List<Integer>> freqMap, Map<String, String> codeMap)
	{
		StringBuilder builder = new StringBuilder();
		for(String codeName : transcriptMap.keySet())
		{
			int totalFreq = sum(freqMap.get(codeName));
			builder.append(codeName);
			builder.append(COMMA);
			builder.append(codeMap.get(codeName));
			builder.append(COMMA);
			builder.append(totalFreq);
			builder.append(COMMA);
			List<String> transcripts = transcriptMap.get(codeName);
			List<Integer> frequencies = freqMap.get(codeName);
			
			for(int i = 0; i < transcripts.size(); i++)
			{
				String transcriptName = transcripts.get(i);
				Integer frequency = frequencies.get(i);
				builder.append(transcriptName + COMMA + frequency + COMMA);
			}
			builder.append("\n"); //$NON-NLS-1$
		}
		return builder;
	}

	/**
	 * @param project
	 * @param facade
	 * @param documentMap
	 * @param freqMap
	 */
	private void detectDocuments(Project project, Facade facade, Map<String, List<String>> documentMap,
			Map<String, List<Integer>> freqMap)
	{
		for(Code code : project.getCodes())
		{
			List<String> documents = new ArrayList<String>();
			List<Integer> frequencies = new ArrayList<Integer>();
			
			detectTranscripts(project, facade, code, documents, frequencies);
			
			detectMemos(project, facade, code, documents, frequencies);
			
			documentMap.put(code.getCodeName(), documents);
			freqMap.put(code.getCodeName(), frequencies);
		}
	}

	/**
	 * @param project
	 * @param facade
	 * @param code
	 * @param documents
	 * @param frequencies
	 */
	private void detectMemos(Project project, Facade facade, Code code, List<String> documents,
			List<Integer> frequencies)
	{
		for(Memo memo : project.getMemos())
		{
			Memo lMemo = facade.forceMemoLoad(memo);
			int freq = 0;
			
			for(Fragment fragment : lMemo.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					if(entry.getCode().equals(code))
					{
						freq++;
						break;
					}
				}
			}
			
			if(freq > 0)
			{
				documents.add(MessagesClient.getString("handlers.ExportCodesHandler.memo", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + memo.getName()); //$NON-NLS-1$
				frequencies.add(freq);
			}
		}
	}

	/**
	 * @param project
	 * @param facade
	 * @param code
	 * @param documents
	 * @param frequencies
	 */
	private void detectTranscripts(Project project, Facade facade, Code code, List<String> documents,
			List<Integer> frequencies)
	{
		for(Transcript transcript : project.getTranscripts())
		{
			Transcript lTranscript = facade.forceTranscriptLoad(transcript);
			int freq = 0;
			
			for(Fragment fragment : lTranscript.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					if(entry.getCode().equals(code))
					{
						freq++;
						break;
					}
				}
			}
			
			if(freq > 0)
			{
				documents.add(MessagesClient.getString("handlers.ExportCodesHandler.transcript", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
						transcript.getName()); 
				frequencies.add(freq);
			}
		}
	}

	/**
	 * @param list
	 * @return
	 */
	private int sum(List<Integer> list)
	{
		int sum = 0;
		
		for(Integer val : list)
		{
			sum += val;
		}
		
		return sum;
	}


}
