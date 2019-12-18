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
package ca.mcgill.cs.swevo.qualyzer.model;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class DebugListener implements CodeListener, InvestigatorListener, ParticipantListener, ProjectListener,
		TranscriptListener, MemoListener
{

	private List<ListenerEvent> fEvents = new ArrayList<ListenerEvent>();

	/**
	 * 
	 * @return
	 */
	public List<ListenerEvent> getEvents()
	{
		return fEvents;
	}

	@Override
	public void investigatorChanged(ChangeType cType, Investigator[] investigators, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, investigators));
	}

	@Override
	public void participantChanged(ChangeType cType, Participant[] participants, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, participants));
	}

	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, project));
	}

	@Override
	public void codeChanged(ChangeType cType, Code[] codes, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, codes));
	}

	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, transcripts));
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.MemoListener#memoChanged(ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Memo[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void memoChanged(ChangeType cType, Memo[] memos, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, memos));
	}

}
