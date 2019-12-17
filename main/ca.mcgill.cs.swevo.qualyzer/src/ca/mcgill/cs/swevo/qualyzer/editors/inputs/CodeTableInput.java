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
package ca.mcgill.cs.swevo.qualyzer.editors.inputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Input for the TableViewer in CodeEditorPage.
 * Contains all the codes as well their frequencies. Also keeps the CodeTableRows so that
 * any changes made to them will be properly reflected.
 * Also used by the TreeModel to easily get the frequencies.
 */
public class CodeTableInput
{
	private Project fProject;
	private Map<Long, Integer> fFreqs;
	private Map<Long, Code> fCodes;
	private List<CodeTableRow> fData;
	
	/**
	 * Creates the model from a project.
	 * @param project
	 */
	public CodeTableInput(Project project)
	{
		fProject = project;
		fFreqs = new HashMap<Long, Integer>();
		fCodes = new HashMap<Long, Code>();
		fData = null;
		
		for(Code code : fProject.getCodes())
		{
			fCodes.put(code.getPersistenceId(), code);
		}
		
		countFrequencies();	
	}
	
	/**
	 * 
	 * @return
	 */
	public Project getProject()
	{
		return fProject;
	}

	/**
	 * Traverses all the memos and transcripts to build the frequency data for all the codes.
	 */
	private void countFrequencies()
	{
		for(Code code : fProject.getCodes())
		{
			fFreqs.put(code.getPersistenceId(), 0);
		}
		
		for(Transcript transcript : fProject.getTranscripts())
		{
			Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			for(Fragment fragment : lTranscript.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					int count = fFreqs.get(entry.getCode().getPersistenceId());
					fFreqs.put(entry.getCode().getPersistenceId(), ++count);
				}
			}
		}
		
		for(Memo memo : fProject.getMemos())
		{
			Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
			for(Fragment fragment : lMemo.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					int count = fFreqs.get(entry.getCode().getPersistenceId());
					fFreqs.put(entry.getCode().getPersistenceId(), ++count);
				}
			}
		}
	}
	
	/**
	 * Get the data represented by this input.
	 * @return The array of CodeTableRows that will be used by the table to display the code information. It
	 * is also used by the TreeModel whenever updates to the frequency values in the TreeViewer might be needed.
	 */
	public CodeTableRow[] getData()
	{
		if(fData == null)
		{
			fData = new ArrayList<CodeTableRow>();
			
			for(Long key : fFreqs.keySet())
			{
				fData.add(new CodeTableRow(fCodes.get(key), fFreqs.get(key)));
			}
		}
		
		return fData.toArray(new CodeTableRow[0]);
	}
	
	/**
	 * Get all the code frequencies indexed by persistence id.
	 * @return
	 */
	public Map<Long, Integer> getFrequencies()
	{
		return fFreqs;
	}
	
	/**
	 * The actual data used by the TableViewer and to create Nodes.
	 *
	 */
	public class CodeTableRow
	{
		private Code fCode;
		private String fName;
		private String fDescription;
		private int fFreq;
		private Long fPersistenceId;
		
		/**
		 * 
		 * @param name
		 * @param count
		 */
		public CodeTableRow(Code code, int count)
		{
			fCode = code;
			fName = fCode.getCodeName();
			fDescription = fCode.getDescription();
			fPersistenceId = fCode.getPersistenceId();
			fFreq = count;
		}
		
		/**
		 * 
		 */
		public CodeTableRow(String name, Long persistenceId, int freq)
		{
			fCode = null;
			fName = name;
			fPersistenceId = persistenceId;
			fFreq = freq;
			fDescription = ""; //$NON-NLS-1$
		}

		/**
		 * 
		 * @return
		 */
		public String getName()
		{
			return fName;
		}
		
		/**
		 * 
		 * @return
		 */
		public int getFrequency()
		{
			return fFreq;
		}
		
		/**
		 * 
		 * @param name
		 */
		public void setName(String name)
		{
			fName = name;
		}
		
		/**
		 * 
		 * @param desc
		 */
		public void setDescription(String desc)
		{
			fDescription = desc;
		}

		/**
		 * @return
		 */
		public String getDescription()
		{
			return fDescription;
		}

		/**
		 * Gets the code with its description and name modified if the local copies were modified.
		 * @return
		 */
		public Code getCodeToSave()
		{
			if(isDirty())
			{
				fCode.setCodeName(fName);
				fCode.setDescription(fDescription);
				return fCode;
			}
			return null;
		}

		/**
		 * @return
		 */
		public boolean isDirty()
		{
			return !fName.equals(fCode.getCodeName()) || !fDescription.equals(fCode.getDescription());
		}

		/**
		 * Gets the original code.
		 * @return
		 */
		public Code getCode()
		{
			return fCode;
		}
		
		/**
		 * 
		 * @return
		 */
		public Long getPersistenceId()
		{
			return fPersistenceId;
		}
	}
	
}
