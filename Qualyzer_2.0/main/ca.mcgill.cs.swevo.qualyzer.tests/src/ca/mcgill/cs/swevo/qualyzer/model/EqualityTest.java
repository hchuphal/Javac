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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.providers.ProjectWrapper;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperInvestigator;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperMemo;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperTranscript;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class EqualityTest
{
	private static final String NICKNAME = "NICKNAME";

	private static final String CODE_NAME = "CODE NAME";

	private static final String OTHER_NAME = "OTHER NAME";

	private static final String PROJ_NAME = "QUAL_TEST_PROJ";
	
	private Project fProject1;
	
	private Project fProject2;
	
	/**
	 * Creates the two project objects.
	 */
	@Before
	public void setUp()
	{
		fProject1 = new Project();
		fProject1.setName(PROJ_NAME);
		
		fProject2 = new Project();
		fProject2.setName(PROJ_NAME);
	}
	
	/**
	 * Verifies that the two projects are equal.
	 */
	@Test
	public void testProjectEquality()
	{
		assertEquals(fProject1, fProject2);
		assertEquals(fProject1.hashCode(), fProject2.hashCode());
		
		assertEquals(fProject1, fProject1);
		
		assertFalse(fProject1.equals(null));
		assertFalse(fProject1.equals(new String()));
		
		fProject1.setName(OTHER_NAME);
		
		assertFalse(fProject1.equals(fProject2));
		assertFalse(fProject1.hashCode() == fProject2.hashCode());
	}
	
	/**
	 * Verifies that the various wrappers are equal.
	 */
	@Test
	public void testProjectWrapperEquality()
	{
		ProjectWrapper wrapper1 = new WrapperCode(fProject1);
		ProjectWrapper wrapper2 = new WrapperCode(fProject2);
		
		assertEquals(wrapper1, wrapper2);
		assertEquals(wrapper1.hashCode(), wrapper2.hashCode());
		
		wrapper1 = new WrapperInvestigator(fProject1);
		wrapper2 = new WrapperInvestigator(fProject2);
		
		assertEquals(wrapper1, wrapper2);
		assertEquals(wrapper1.hashCode(), wrapper2.hashCode());
		
		wrapper1 = new WrapperMemo(fProject1);
		wrapper2 = new WrapperMemo(fProject2);
		
		assertEquals(wrapper1, wrapper2);
		assertEquals(wrapper1.hashCode(), wrapper2.hashCode());
		
		wrapper1 = new WrapperParticipant(fProject1);
		wrapper2 = new WrapperParticipant(fProject2);
		
		assertEquals(wrapper1, wrapper2);
		assertEquals(wrapper1.hashCode(), wrapper2.hashCode());
		
		wrapper1 = new WrapperTranscript(fProject1);
		wrapper2 = new WrapperTranscript(fProject2);
		
		assertEquals(wrapper1, wrapper2);
		assertEquals(wrapper1.hashCode(), wrapper2.hashCode());
	}
	
	/**
	 * Verifies that different types of wrappers are not equal.
	 */
	@Test
	public void testWrapperEquality2()
	{
		ProjectWrapper wrapper1 = new WrapperCode(fProject1);
		ProjectWrapper wrapper2 = new WrapperInvestigator(fProject2);
		
		assertFalse(wrapper1.equals(wrapper2));
		assertFalse(wrapper1.hashCode() == wrapper2.hashCode());
		
		wrapper2 = new WrapperMemo(fProject2);
		assertFalse(wrapper1.equals(wrapper2));
		assertFalse(wrapper1.hashCode() == wrapper2.hashCode());

		wrapper2 = new WrapperParticipant(fProject2);
		assertFalse(wrapper1.equals(wrapper2));
		assertFalse(wrapper1.hashCode() == wrapper2.hashCode());

		wrapper2 = new WrapperTranscript(fProject2);
		assertFalse(wrapper1.equals(wrapper2));
		assertFalse(wrapper1.hashCode() == wrapper2.hashCode());
		
		assertEquals(wrapper1, wrapper1);
		assertFalse(wrapper1.equals(null));
		assertFalse(wrapper1.equals(new String()));
		
	}
	
	/**
	 * Verifies the equality of participants.
	 */
	@Test
	public void testParticipantEquality()
	{
		String participantId = "P1"; 
		
		Participant p1 = new Participant();
		p1.setProject(fProject1);
		p1.setParticipantId(participantId);
		
		Participant p2 = new Participant();
		p2.setProject(fProject2);
		p2.setParticipantId(participantId);
		
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
		assertEquals(p1.compareTo(p2), 0);
		
		assertEquals(p1, p1);
		assertFalse(p1.equals(null));
		assertFalse(p1.equals(new String()));
		
		p2.setParticipantId("p2");
		assertFalse(p1.equals(p2));
		assertFalse(p1.hashCode() == p2.hashCode());
		
		fProject2.setName(OTHER_NAME);
		assertFalse(p1.equals(p2));
		assertFalse(p1.hashCode() == p2.hashCode());
		assertFalse(p1.compareTo(p2) == 0);
	}
	
	/**
	 * Verifies the equality of Investigators.
	 */
	@Test
	public void testInvestigatorEquality()
	{
		Investigator i1 = new Investigator();
		i1.setProject(fProject1);
		i1.setNickName(NICKNAME);
		
		Investigator i2 = new Investigator();
		i2.setProject(fProject2);
		i2.setNickName(NICKNAME);
		
		assertEquals(i1, i2);
		assertEquals(i1.hashCode(), i2.hashCode());
		
		assertEquals(i1, i1);
		assertFalse(i1.equals(null));
		assertFalse(i1.equals(new String()));
		
		i2.setNickName(OTHER_NAME);
		assertFalse(i1.equals(i2));
		assertFalse(i1.hashCode() == i2.hashCode());
		
		i2.setNickName(NICKNAME);
		fProject2.setName(OTHER_NAME);
		assertFalse(i1.equals(i2));
		assertFalse(i1.hashCode() == i2.hashCode());
	}
	
	
	/**
	 * Verifies the equality of Codes.
	 */
	@Test
	public void testCodeEquality()
	{
		String codeName = CODE_NAME;
		
		Code c1 = new Code();
		c1.setProject(fProject1);
		c1.setCodeName(codeName);
		
		Code c2 = new Code();
		c2.setProject(fProject2);
		c2.setCodeName(codeName);
		
		assertEquals(c1, c2);
		assertEquals(c1.hashCode(), c2.hashCode());
		
		assertEquals(c1, c2);
		assertFalse(c1.equals(null));
		assertFalse(c1.equals(new String()));
		
		c2.setCodeName(OTHER_NAME);
		assertFalse(c1.equals(c2));
		assertFalse(c1.hashCode() == c2.hashCode());
		
		c2.setCodeName(codeName);
		c2.setProject(new Project());
		assertFalse(c1.equals(c2));
		assertFalse(c1.hashCode() == c2.hashCode());
	}
	
	/**
	 * Verifies the equality of Memos.
	 */
	@Test
	public void testMemoEquality()
	{
		String nickName = "NICK NAME";
		String fileName = "FILE NAME";
		String name = "MEMO NAME";

		Investigator i1 = new Investigator();
		i1.setProject(fProject1);
		i1.setNickName(nickName);
		
		Investigator i2 = new Investigator();
		i2.setProject(fProject2);
		i2.setNickName(nickName);
		
		Memo m1 = new Memo();
		m1.setProject(fProject1);
		m1.setFileName(fileName);
		m1.setName(name);
		m1.setAuthor(i1);
		
		Memo m2 = new Memo();
		m2.setProject(fProject2);
		m2.setFileName(fileName);
		m2.setName(name);
		m2.setAuthor(i2);
		
		assertEquals(m1, m2);
		assertEquals(m1.hashCode(), m2.hashCode());
		assertEquals(m1.compareTo(m2), 0);
		
		assertEquals(m1, m1);
		assertFalse(m1.equals(null));
		assertFalse(m1.equals(name));
		
		m2.setName(OTHER_NAME);
		assertFalse(m1.equals(m2));
		assertFalse(m1.hashCode() == m2.hashCode());
		assertFalse(m1.compareTo(m2) == 0);
		
		m2.setName(name);
		m2.setFileName(OTHER_NAME);
		assertFalse(m1.equals(m2));
		assertFalse(m1.hashCode() == m2.hashCode());
		
		m2.setFileName(fileName);
		m2.setProject(new Project());
		assertFalse(m1.equals(m2));
		assertFalse(m1.hashCode() == m2.hashCode());

		m2.setProject(fProject2);
		m2.setAuthor(new Investigator());
		assertFalse(m1.equals(m2));
		assertFalse(m1.hashCode() == m2.hashCode());
	}
	
	/**
	 * Verifies the equality of Transcripts.
	 */
	@Test
	public void testTranscriptEquality()
	{
		String name = "TRANSCRIPT NAME";
		String fileName = "FILE NAME";

		Transcript t1 = new Transcript();
		t1.setProject(fProject1);
		t1.setName(name);
		t1.setFileName(fileName);
		
		Transcript t2 = new Transcript();
		t2.setProject(fProject2);
		t2.setName(name);
		t2.setFileName(fileName);
		
		assertEquals(t1, t2);
		assertEquals(t1.hashCode(), t2.hashCode());
		assertEquals(t1.compareTo(t2), 0);
		
		assertEquals(t1, t1);
		assertFalse(t1.equals(name));
		assertFalse(t1.equals(null));
		
		t2.setProject(new Project());
		assertFalse(t1.equals(t2));
		assertFalse(t1.hashCode() == t2.hashCode());
		
		t2.setProject(fProject2);
		t2.setName(OTHER_NAME);
		assertFalse(t1.equals(t2));
		assertFalse(t1.hashCode() == t2.hashCode());
		assertFalse(t1.compareTo(t2) == 0);
		
		t2.setName(name);
		t2.setFileName(OTHER_NAME);
		assertFalse(t1.equals(t2));
		assertFalse(t1.hashCode() == t2.hashCode());
	}
	
	/**
	 * Verifies the equality of Audio Files.
	 */
	@Test
	public void testAudioFileEquality()
	{
		AudioFile f1 = new AudioFile();
		f1.setRelativePath("PATH");
		
		AudioFile f2 = new AudioFile();
		f2.setRelativePath("PATH");
		
		assertEquals(f1, f2);
		assertEquals(f1.hashCode(), f2.hashCode());
		
		assertEquals(f1, f1);
		assertFalse(f1.equals(null));
		assertFalse(f1.equals(new String()));
		
		f2.setRelativePath("OTHER PATH");
		
		assertFalse(f1.equals(f2));
		assertFalse(f1.hashCode() == f2.hashCode());
	}
	
	/**
	 * Verifies the equality of code entries.
	 */
	@Test
	public void testCodeEntryEquality()
	{
		Investigator i1 = new Investigator();
		i1.setProject(fProject1);
		i1.setNickName(NICKNAME);

		Investigator i2 = new Investigator();
		i2.setProject(fProject2);
		i2.setNickName(NICKNAME);
		
		Code c1 = new Code();
		c1.setProject(fProject1);
		c1.setCodeName(CODE_NAME);
		
		Code c2 = new Code();
		c2.setProject(fProject2);
		c2.setCodeName(CODE_NAME);
		
		CodeEntry ce1 = new CodeEntry();
		ce1.setInvestigator(i1);
		ce1.setCode(c1);
		
		CodeEntry ce2 = new CodeEntry();
		ce2.setInvestigator(i2);
		ce2.setCode(c2);
		
		assertEquals(ce1, ce2);
		assertEquals(ce1.hashCode(), ce2.hashCode());
		
		assertEquals(ce1, ce1);
		assertFalse(ce1.equals(null));
		assertFalse(ce1.equals(CODE_NAME));
		
		ce2.setInvestigator(new Investigator());
		assertFalse(ce1.equals(ce2));
		assertFalse(ce1.hashCode() == ce2.hashCode());
		
		ce2.setInvestigator(i2);
		ce2.setCode(new Code());
		assertFalse(ce1.equals(ce2));
		assertFalse(ce1.hashCode() == ce2.hashCode());
	}
	
	@Test
	public void testFragmentEquality()
	{
		String name = "TRANSCRIPT NAME";
		String fileName = "FILE NAME";
		
		Transcript t1 = new Transcript();
		t1.setProject(fProject1);
		t1.setName(name);
		t1.setFileName(fileName);
		
		Transcript t2 = new Transcript();
		t2.setProject(fProject2);
		t2.setName(name);
		t2.setFileName(fileName);
		
		Fragment f1 = new Fragment();
		f1.setDocument(t1);
		f1.setOffset(5);
		f1.setLength(9);

		Fragment f2 = new Fragment();
		f2.setDocument(t2);
		f2.setOffset(5);
		f2.setLength(9);
		
		assertEquals(f1, f2);
		assertEquals(f1.hashCode(), f2.hashCode());
		
		assertEquals(f1, f1);
		assertFalse(f1.equals(null));
		assertFalse(f1.equals(t1));
		
		t1.setName("Other");
		
		assertFalse(f1.equals(f2));
		assertFalse(f1.hashCode() == f2.hashCode());
		
		t2.setName(name);
		f1.setOffset(2);
		assertFalse(f1.equals(f2));
		assertFalse(f1.hashCode() == f2.hashCode());
		
		f1.setOffset(5);
		f1.setLength(5);
		assertFalse(f1.equals(f2));
		assertFalse(f1.hashCode() == f2.hashCode());
	}
}
