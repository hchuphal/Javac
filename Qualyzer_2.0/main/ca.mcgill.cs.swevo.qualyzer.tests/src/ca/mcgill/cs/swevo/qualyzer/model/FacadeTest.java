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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * 
 */
public class FacadeTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";

	private ListenerManager fListenerManager;

	private Project fProject;

	private Facade fFacade;

	private DebugListener fListener;

	/**
	 * 
	 */
	@Before
	public void setUp()
	{
		fListener = new DebugListener();
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		fListenerManager = fFacade.getListenerManager();
		fListenerManager.registerCodeListener(fProject, fListener);
		fListenerManager.registerInvestigatorListener(fProject, fListener);
		fListenerManager.registerParticipantListener(fProject, fListener);
		fListenerManager.registerProjectListener(fProject, fListener);
		fListenerManager.registerTranscriptListener(fProject, fListener);
		fListenerManager.registerMemoListener(fProject, fListener);
	}

	/**
	 * 
	 */
	@After
	public void tearDown()
	{
		fListenerManager.unregisterCodeListener(fProject, fListener);
		fListenerManager.unregisterInvestigatorListener(fProject, fListener);
		fListenerManager.unregisterParticipantListener(fProject, fListener);
		fListenerManager.unregisterProjectListener(fProject, fListener);
		fListenerManager.unregisterTranscriptListener(fProject, fListener);
		fListenerManager.unregisterMemoListener(fProject, fListener);
		fFacade.deleteProject(fProject);
	}

	/**
	 * Verifies db state and listeners.
	 */
	@Test
	public void testCreateParticipant()
	{
		String pId = "p1";
		String pName = "Toto";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		ListenerEvent event = fListener.getEvents().get(0);

		// Test DB
		assertNotNull(participant);
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getParticipants().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Participant[] { participant }, (Object[]) event.getObject());

	}

	/**
	 * Verifies db state and listeners.
	 */
	@Test
	public void testCreateInvestigator()
	{
		Investigator investigator = fFacade.createInvestigator("TestInvestigator", "TestInvestigator FullName",
				"McGill", fProject, true);
		ListenerEvent event = fListener.getEvents().get(0);

		// Test DB
		assertNotNull(investigator);
		assertEquals(2, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getInvestigators().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Investigator[] { investigator }, (Object[]) event.getObject());

	}

	/**
	 * Verifies db state and listeners.
	 */
	@Test
	public void testCreateTranscript()
	{
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);

		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);
		ListenerEvent event = fListener.getEvents().get(1);

		// Test DB
		assertNotNull(transcript);
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getTranscripts().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Transcript[] { transcript }, (Object[]) event.getObject());
	}

	/**
	 * Verifies db state and listeners.
	 */
	@Test
	public void testCreateMemo()
	{
		String pId = "p1";
		String pName = "Toto";
		String memoName = "m1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Investigator investigator = fProject.getInvestigators().get(0);

		Memo memo = fFacade.createMemo(memoName, "6/26/2010", investigator, participants, fProject, null, null);
		ListenerEvent event = fListener.getEvents().get(1);

		// Test DB
		assertNotNull(memo);
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getMemos().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Memo[] { memo }, (Object[]) event.getObject());
	}
	
	/**
	 * 
	 * Verifies if a fragment can be created on an initialized and uninitialized transcript
	 * 
	 */
	@Test
	public void testCreateFragment()
	{
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);

		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);

		// Create a fragment from newly created transcript: ok
		Fragment fragment = fFacade.createFragment(transcript, 0, 1);

		// Test Local
		assertNotNull(fragment);
		// Test event
		ListenerEvent event = fListener.getEvents().get(2);
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		assertArrayEquals(new Transcript[] { transcript }, (Object[]) event.getObject());

		// Create a fragment from initialized transcript: ok
		transcript = fFacade.forceTranscriptLoad(PersistenceManager.getInstance().getProject(fProject.getName())
				.getTranscripts().get(0));
		fragment = fFacade.createFragment(transcript, 0, 1);

		// Test Local
		assertNotNull(fragment);
		// Test event
		event = fListener.getEvents().get(3);
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		assertArrayEquals(new Transcript[] { transcript }, (Object[]) event.getObject());

		// Create a fragment from uninitialized transcript: not ok
		try
		{
			transcript = PersistenceManager.getInstance().getProject(fProject.getName()).getTranscripts().get(0);
			fragment = fFacade.createFragment(transcript, 0, 1);
			fail();
		}
		catch (QualyzerException qe)
		{
			assertTrue(true);
		}
	}

	/**
	 * Verifies db state and listeners after creating a code.
	 */
	@Test
	public void testCreateCode()
	{
		Code code = fFacade.createCode("c1", "", fProject);
		ListenerEvent event = fListener.getEvents().get(0);

		// Test DB
		assertNotNull(code);
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getCodes().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Code[] { code }, (Object[]) event.getObject());

	}

	/**
	 * 
	 * Ensures that the project and the files are deleted.
	 * 
	 */
	@Test
	public void testDeleteProject()
	{
		Project tempProject = fFacade.createProject("projectb", TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		DebugListener tempListener = new DebugListener();
		fFacade.getListenerManager().registerProjectListener(tempProject, tempListener);
		fFacade.deleteProject(tempProject);

		ListenerEvent event = tempListener.getEvents().get(0);
		assertEquals(ChangeType.DELETE, event.getChangeType());
		assertNotNull(event.getObject());

		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject("projectb");
		assertFalse(iProject.exists());

	}

	/**
	 * Verifies that a participant can be deleted.
	 * 
	 */
	@Test
	public void testDeleteParticipant()
	{
		String pId = "p1";
		String pName = "Toto";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		fFacade.deleteParticipant(PersistenceManager.getInstance().getProject(fProject.getName()).getParticipants()
				.get(0));

		ListenerEvent event = fListener.getEvents().get(1);
		assertEquals(ChangeType.DELETE, event.getChangeType());
		assertArrayEquals(new Participant[] { participant }, (Object[]) event.getObject());
		assertEquals(0, PersistenceManager.getInstance().getProject(fProject.getName()).getParticipants().size());

		// Test participants associated with a transcript
		fProject = PersistenceManager.getInstance().getProject(fProject.getName());
		participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		fFacade.createTranscript("t1", "6/26/2010", "", participants, fProject);
		try
		{
			fFacade.deleteParticipant(participant);
			fail();
		}
		catch (QualyzerException qe)
		{
			assertTrue(true);
		}
	}

	/**
	 * Verifies that an investigator can be deleted.
	 * 
	 */
	@Test
	public void testDeleteInvestigator()
	{
		Investigator investigator = fFacade.createInvestigator("TestInvestigator", "TestInvestigator FullName",
				"McGill", fProject, true);
		fFacade.deleteInvestigator(PersistenceManager.getInstance().getProject(fProject.getName()).getInvestigators()
				.get(1));

		ListenerEvent event = fListener.getEvents().get(1);
		assertEquals(ChangeType.DELETE, event.getChangeType());
		assertArrayEquals(new Investigator[] { investigator }, (Object[]) event.getObject());
		assertEquals(0, PersistenceManager.getInstance().getProject(fProject.getName()).getParticipants().size());
	}

	/**
	 * 
	 * Verifies that a transcript can be deleted.
	 * 
	 */
	@Test
	public void testDeleteTranscript()
	{
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);
		Fragment fragment = fFacade.createFragment(transcript, 1, 1);
		fFacade.saveTranscript(transcript);
		long id = fragment.getPersistenceId();

		fFacade.deleteTranscript(PersistenceManager.getInstance().getProject(fProject.getName()).getTranscripts()
				.get(0));

		// event 0: create part, 1: create tr, 2: create fr., 3: modify tr, 4: delete tr.
		ListenerEvent event = fListener.getEvents().get(4);
		assertEquals(ChangeType.DELETE, event.getChangeType());
		assertArrayEquals(new Transcript[] { transcript }, (Object[]) event.getObject());
		assertEquals(0, PersistenceManager.getInstance().getProject(fProject.getName()).getTranscripts().size());

		// Test Fragment
		HibernateDBManager dbManager = QualyzerActivator.getDefault().getHibernateDBManagers().get(TEST_PROJECT_NAME);
		Session session = dbManager.openSession();
		// It should be impossible to retrieve the fragment.
		try
		{
			assertNull(session.get(Fragment.class, id));
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}
	
	/**
	 * 
	 * Verifies that a memo can be deleted.
	 * 
	 */
	@Test
	public void testDeleteMemo()
	{
		String pId = "p1";
		String pName = "Toto";
		String memoName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Investigator inves = fProject.getInvestigators().get(0);
		Memo memo = fFacade.createMemo(memoName, "6/26/2010", inves, participants, fProject, null, null);
		Fragment fragment = fFacade.createFragment(memo, 1, 1);
		fFacade.saveMemo(memo);
		long id = fragment.getPersistenceId();

		fFacade.deleteMemo(PersistenceManager.getInstance().getProject(fProject.getName()).getMemos()
				.get(0));

		// event 0: create part, 1: create memo, 2: create fr., 3: modify memo, 4: delete memo.
		ListenerEvent event = fListener.getEvents().get(4);
		assertEquals(ChangeType.DELETE, event.getChangeType());
		assertArrayEquals(new Memo[] { memo }, (Object[]) event.getObject());
		assertEquals(0, PersistenceManager.getInstance().getProject(fProject.getName()).getMemos().size());

		// Test Fragment
		HibernateDBManager dbManager = QualyzerActivator.getDefault().getHibernateDBManagers().get(TEST_PROJECT_NAME);
		Session session = dbManager.openSession();
		// It should be impossible to retrieve the fragment.
		try
		{
			assertNull(session.get(Fragment.class, id));
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}

	/**
	 * 
	 * Verifies that transcriptload() really loads the various lists.
	 * 
	 */
	@Test
	public void testForceTranscriptLoad()
	{
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);

		Transcript tempTranscript = PersistenceManager.getInstance().getProject(fProject.getName()).getTranscripts()
				.get(0);

		try
		{
			tempTranscript.getFragments().size();
			fail();
		}
		catch (HibernateException he)
		{
			assertTrue(true);
		}

		Transcript newTempTranscript = fFacade.forceTranscriptLoad(tempTranscript);
		assertEquals(0, newTempTranscript.getFragments().size());
	}
	
	/**
	 * 
	 * Verifies that memoload() really loads the various lists.
	 * 
	 */
	@Test
	public void testForceMemoLoad()
	{
		String pId = "p1";
		String pName = "Toto";
		String memoName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Investigator inves = fProject.getInvestigators().get(0);
		fFacade.createMemo(memoName, "6/26/2010", inves, participants, fProject, null, null);

		Memo tempMemo = PersistenceManager.getInstance().getProject(fProject.getName()).getMemos()
				.get(0);

		try
		{
			tempMemo.getFragments().size();
			fail();
		}
		catch (HibernateException he)
		{
			assertTrue(true);
		}

		Memo newTempMemo = fFacade.forceMemoLoad(tempMemo);
		assertEquals(0, newTempMemo.getFragments().size());
	}
	
	/**
	 * 
	 * Verifies that transcriptload() really loads the various lists.
	 * 
	 */
	@Test
	public void testForceDocumentLoad()
	{
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Investigator inves = fProject.getInvestigators().get(0);
		fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);
		fFacade.createMemo(transcriptName, "6/26/2010", inves, participants, fProject, null, null);

		Transcript tempTranscript = PersistenceManager.getInstance().getProject(fProject.getName()).getTranscripts()
				.get(0);
		
		Memo tempMemo = PersistenceManager.getInstance().getProject(fProject.getName()).getMemos()
			.get(0);

		try
		{
			tempTranscript.getFragments().size();
			fail();
		}
		catch (HibernateException he)
		{
			assertTrue(true);
		}

		IAnnotatedDocument newTempTranscript = fFacade.forceDocumentLoad(tempTranscript);
		assertEquals(0, newTempTranscript.getFragments().size());
		
		try
		{
			tempMemo.getFragments().size();
			fail();
		}
		catch (HibernateException he)
		{
			assertTrue(true);
		}

		IAnnotatedDocument newTempMemo = fFacade.forceDocumentLoad(tempMemo);
		assertEquals(0, newTempMemo.getFragments().size());
	}

	/**
	 * 
	 * Verifies that a change of description is correctly saved.
	 * 
	 */
	@Test
	public void testSaveCode()
	{
		String newDescription = "Hello World";
		Code code = fFacade.createCode("c1", "", fProject);
		code.setDescription(newDescription);
		fFacade.saveCodes(new Code[] {code});
		ListenerEvent event = fListener.getEvents().get(1);

		// Test DB
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getCodes().size());
		assertEquals(newDescription, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getCodes().get(0)
				.getDescription());
		// Test event
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		assertArrayEquals(new Code[] { code }, (Object[]) event.getObject());
	}

	/**
	 * Verifies that multiple codes can be modified together.
	 */
	@Test
	public void testSaveCodes()
	{
		String newDescription = "Hello World";
		String newName = "New Name";
		Code code1 = fFacade.createCode("c1", "", fProject);
		code1.setDescription(newDescription);
		Code code2 = fFacade.createCode("c2", "", fProject);
		code2.setCodeName(newName);

		fFacade.saveCodes(new Code[] { code1, code2 });

		ListenerEvent event = fListener.getEvents().get(2);

		// Test DB
		assertEquals(2, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getCodes().size());
		assertEquals(newDescription, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getCodes().get(0)
				.getDescription());
		assertEquals(newName, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getCodes().get(1)
				.getCodeName());
		// Test event
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		assertArrayEquals(new Code[] { code1, code2 }, (Object[]) event.getObject());
	}

	/**
	 * Verifies that a change of institution is correctly saved.
	 */
	@Test
	public void testSaveInvestigator()
	{
		String newInstitution = "McGill U.";
		Investigator investigator = fProject.getInvestigators().get(0);
		investigator.setInstitution(newInstitution);
		fFacade.saveInvestigator(investigator);

		ListenerEvent event = fListener.getEvents().get(0);

		// Test DB
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getInvestigators().size());
		assertEquals(newInstitution, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getInvestigators()
				.get(0).getInstitution());
		// Test event
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		assertArrayEquals(new Investigator[] { investigator }, (Object[]) event.getObject());
	}

	/**
	 * 
	 * Verifies that a change of participant id is correctly saved.
	 * 
	 */
	@Test
	public void testSaveParticipant()
	{
		String newId = "p1b";
		Participant participant = fFacade.createParticipant("p1", "Toto", fProject);
		participant.setParticipantId(newId);
		fFacade.saveParticipant(participant);

		ListenerEvent event = fListener.getEvents().get(1);

		// Test DB
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getParticipants().size());
		assertEquals(newId, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getParticipants().get(0)
				.getParticipantId());
		// Test event
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		assertArrayEquals(new Participant[] { participant }, (Object[]) event.getObject());
	}

	/**
	 * Verifies that a transcript's name can be changed.
	 * 
	 */
	@Test
	public void testSaveTranscript()
	{
		String newTranscriptName = "t1b";
		Participant participant = fFacade.createParticipant("p1", "Toto", fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);

		Transcript transcript = fFacade.createTranscript("t1", "6/26/2010", "", participants, fProject);

		transcript.setName(newTranscriptName);
		fFacade.saveTranscript(transcript);

		ListenerEvent event = fListener.getEvents().get(2);

		// Test DB
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getTranscripts().size());
		assertEquals(newTranscriptName, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getTranscripts()
				.get(0).getName());
		// Test event
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		assertArrayEquals(new Transcript[] { transcript }, (Object[]) event.getObject());
	}

	/**
	 * Verifies that a transcript's name can be changed.
	 * 
	 */
	@Test
	public void testSaveTranscriptFragment()
	{
		String newTranscriptName = "t1b";
		Participant participant = fFacade.createParticipant("p1", "Toto", fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);

		Transcript transcript = fFacade.createTranscript("t1", "6/26/2010", "", participants, fProject);
		fFacade.createFragment(transcript, 1, 1);

		transcript.setName(newTranscriptName);
		fFacade.saveTranscript(transcript);

		// Test DB
		Transcript newTranscript = PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getTranscripts().get(
				0);
		newTranscript = fFacade.forceTranscriptLoad(newTranscript);
		assertNotNull(newTranscript.getFragments().get(1));
		assertEquals(transcript, newTranscript.getFragments().get(1).getDocument());
	}
	
	/**
	 * Verifies that we can save multiple code entries in a fragment.
	 */
	@Test
	public void testCodeEntries() {
		Code code1 = fFacade.createCode("c1", "", fProject);
		Code code2 = fFacade.createCode("c2", "", fProject);
		fFacade.createInvestigator("TestInvestigator", "TestInvestigator FullName",
				"McGill", fProject, true);
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);
		Transcript newTranscript = fFacade.forceTranscriptLoad(transcript);
		fFacade.createFragment(newTranscript, 1, 1);
		fFacade.saveTranscript(newTranscript);
		
		
		Fragment newFragment = newTranscript.getFragments().get(1);
		CodeEntry ce1 = new CodeEntry();
		ce1.setCode(code1);
//		ce1.setInvestigator(investigator);
		newFragment.getCodeEntries().add(ce1);
		fFacade.saveTranscript(newTranscript);
		
		CodeEntry ce2 = new CodeEntry();
		ce2.setCode(code2);
//		ce2.setInvestigator(investigator);
		newFragment.getCodeEntries().add(ce2);
		fFacade.saveTranscript(newTranscript);
	}

	@Test
	public void testDeleteCode()
	{
		String cName = "code";
		String cDesc = "description";
		Code code = fFacade.createCode(cName, cDesc, fProject);
		fFacade.deleteCode(PersistenceManager.getInstance().getProject(fProject.getName()).getCodes()
				.get(0));

		ListenerEvent event = fListener.getEvents().get(1);
		assertEquals(ChangeType.DELETE, event.getChangeType());
		assertArrayEquals(new Code[] { code }, (Object[]) event.getObject());
		assertEquals(0, PersistenceManager.getInstance().getProject(fProject.getName()).getCodes().size());
	}
	
	@Test
	public void testDeleteFragment()
	{
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);

		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);

		Fragment fragment = fFacade.createFragment(transcript, 0, 1);
		
		Facade.getInstance().deleteFragment(fragment);
		
		assertEquals(transcript.getFragments().size(), 0);
		ListenerEvent event = fListener.getEvents().get(3);
		assertEquals(ChangeType.MODIFY, event.getChangeType());
		
	}
	
	@Test
	public void testCreateTimestamp()
	{
		int lineNumber = 10;
		int seconds = 93;
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);
		
		Timestamp tstamp = fFacade.createTimestamp(transcript, lineNumber, seconds);
		fFacade.saveTranscript(transcript);
		
		Transcript newTranscript = fFacade.forceTranscriptLoad(transcript);
		assertNotNull(tstamp);
		assertEquals(newTranscript.getTimestamps().get(lineNumber).getSeconds(),seconds);
	}
	
	@Test
	public void testDeleteTimestamp()
	{
		int lineNumber = 10;
		int seconds = 93;
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);
		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);
		
		Timestamp tstamp = fFacade.createTimestamp(transcript, lineNumber, seconds);
		fFacade.saveTranscript(transcript);
		
		
		fFacade.deleteTimestamp(tstamp);
		Transcript newTranscript = fFacade.forceTranscriptLoad(transcript);
		assertTrue(newTranscript.getTimestamps().isEmpty());
	}
	
	
	
}
