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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.util.CollectionUtil;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class PersistenceManagerTest
{

	public static final String TEST_PROJECT_NAME = "TEST_QUAL_STUDY";

	private static final String A_CODE = "a";

	private static final String B_CODE = "b";

	private QualyzerActivator fActivator;

	private PersistenceManager fManager;

	private IProject fProject;

	/**
	 * Creates a project.
	 * 
	 */
	@Before
	public void setUp()
	{
		fManager = PersistenceManager.getInstance();
		fProject = ResourcesPlugin.getWorkspace().getRoot().getProject(TEST_PROJECT_NAME);
		if (!fProject.exists())
		{
			try
			{
				fProject.create(new NullProgressMonitor());
				fProject.open(new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
		fActivator = QualyzerActivator.getDefault();
	}

	/**
	 * Deletes the project.
	 * 
	 */
	@After
	public void tearDown()
	{
		try
		{
			HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
					fProject.getName());
			if (manager != null) 
			{
				manager.close();
			}
			fProject.delete(true, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Verifies that the .db folder is created correctly.
	 * 
	 */
	@Test
	public void testDBInit()
	{
		fManager.initDB(fProject);
		IPath path = fManager.getDBFilePath(fProject);
		assertTrue(path.toFile().exists());
	}
	
	/**
	 * Verifies that an up-to-date db can be updated without throwing exceptions.
	 * 
	 */
	@Test
	public void testDBUpdate()
	{
		fManager.initDB(fProject);
		Project projectDB = new Project();
		projectDB.setName(TEST_PROJECT_NAME);
		HibernateUtil.quietSave(fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME), projectDB);
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(
				fProject.getName());
		manager.close();
		
		fManager.updateDB(fProject);
	
	}

	/**
	 * Tests getProject.
	 * 
	 */
	@Test
	public void testGetProject()
	{
		fManager.initDB(fProject);
		Project projectDB = new Project();
		projectDB.setName(TEST_PROJECT_NAME);
		HibernateUtil.quietSave(fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME), projectDB);
		projectDB = fManager.getProject(TEST_PROJECT_NAME);
		assertNotNull(projectDB);
	}

	/**
	 * 
	 * Tests whether an ordered list is always ordered after an addition and a save.
	 * 
	 * Warning: in this method, there are many calls to HibernateUtil. This is an anti-pattern as multiple sessions are
	 * created/closed in a single method. All the calls (saves, refreshes) should be inlined in one session. This is
	 * acceptable for testing purpose though.
	 */
	@Test
	public void testHibernateListInsert()
	{
		fManager.initDB(fProject);
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME);
		Project projectDB = new Project();
		projectDB.setName(TEST_PROJECT_NAME);
		HibernateUtil.quietSave(dbManager, projectDB);

		Code code1 = new Code();
		code1.setCodeName(B_CODE);
		CollectionUtil.insertSorted(projectDB.getCodes(), code1);
		HibernateUtil.quietSave(dbManager, projectDB);

		Code code2 = new Code();
		code2.setCodeName(A_CODE);
		CollectionUtil.insertSorted(projectDB.getCodes(), code2);
		HibernateUtil.quietSave(dbManager, projectDB);

		Code tempCode = projectDB.getCodes().get(0);
		assertEquals(A_CODE, tempCode.getCodeName());

		projectDB = fManager.getProject(TEST_PROJECT_NAME);
		tempCode = projectDB.getCodes().get(0);
		assertEquals(A_CODE, tempCode.getCodeName());
	}

	private Project createProject()
	{
		Project projectDB = new Project();
		projectDB.setName(TEST_PROJECT_NAME);
		projectDB.setFolderName(TEST_PROJECT_NAME.replace(' ', '_'));
		Code code = new Code();
		code.setCodeName(B_CODE);
		projectDB.getCodes().add(code);
		code = new Code();
		code.setCodeName(A_CODE);
		projectDB.getCodes().add(code);
		return projectDB;
	}

	/**
	 * Tests that the bidirectional relationship (e.g., from Project to codes and from a code to a project) is correctly
	 * persisted.
	 * 
	 */
	@Test
	public void testHibernateBidirectional()
	{
		fManager.initDB(fProject);
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME);
		Project projectDB = createProject();
		HibernateUtil.quietSave(dbManager, projectDB);

		projectDB = fManager.getProject(TEST_PROJECT_NAME);
		assertNotNull(projectDB.getCodes().get(0).getProject());
	}

	/**
	 * This is an example of using Hibernate API in a single session.
	 * 
	 */
	// CSOFF:
	@Test
	public void testHibernateCascade()
	{
		Session session = null;
		try
		{
			fManager.initDB(fProject);
			HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME);
			session = dbManager.openSession();
			Transaction t = session.beginTransaction();
			Project projectDB = createProject();
			// Test save cascade
			session.saveOrUpdate(projectDB);
			t.commit();
			// The list is still local
			Code tempCode = projectDB.getCodes().get(0);
			assertEquals("b", tempCode.getCodeName());

			// Codes should be persisted on the DB.
			// Note: this is probably a bad query performance-wise.
			Query query = session
					.createQuery(
							"select count(code) from Code as code, Project as project where project = ? and code in elements(project.codes)")
					.setEntity(0, projectDB);
			assertEquals(2, HibernateUtil.count(query));

			t = session.beginTransaction();
			// Test delete cascade
			session.delete(projectDB);
			t.commit();

			// The codes were deleted from the DB.
			assertEquals(0, HibernateUtil.count(query));
		}
		catch (HibernateException e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}

	/**
	 * Verifies that the fetching strategies (eager v.s. lazy) are correctly implemented.
	 */
	@Test
	public void testHibernateFetchStrategies()
	{
		fManager.initDB(fProject);
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME);
		Project projectDB = createProject();
		Transcript t = new Transcript();
		t.setName("Transcript 1");
		Participant p = new Participant();
		p.setParticipantId("P1");
		t.getParticipants().add(p);
		CollectionUtil.insertSorted(projectDB.getTranscripts(), t);
		CollectionUtil.insertSorted(projectDB.getParticipants(), p);
		HibernateUtil.quietSave(dbManager, projectDB);

		projectDB = fManager.getProject(TEST_PROJECT_NAME);
		// Should work because codes are loaded eagerly
		assertNotNull(projectDB.getCodes().get(0));

		Transcript tempTranscript = projectDB.getTranscripts().get(0);
		try
		{
			// Should not work as Participants are not loaded by default.
			tempTranscript.getParticipants().get(0);
			fail();
		}
		catch (LazyInitializationException e)
		{
		}

		fManager.initializeDocument(tempTranscript);
		assertNotNull(tempTranscript.getParticipants().get(0));
	}

	// CSON:

	
	
	@Test
	public void testRefreshManager()
	{
		Facade facade = Facade.getInstance();
		Project project = facade.createProject("project2", "Invest1", "", "");
		QualyzerActivator activator = QualyzerActivator.getDefault();
		activator.getHibernateDBManagers().remove(project.getName());
		
		PersistenceManager.getInstance().refreshManager(ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName()));
		
		assertNotNull(activator.getHibernateDBManagers().get(project.getName()));
	}
	
	@Test
	public void testHibernateListOfElements()
	{
		fManager.initDB(fProject);
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME);
		Project projectDB = new Project();
		projectDB.setName(TEST_PROJECT_NAME);
		HibernateUtil.quietSave(dbManager, projectDB);

		Code code1 = new Code();
		code1.setCodeName(B_CODE);
		code1.getParents().add("codeA/codeC");
		code1.getParents().add("codeA/codeD");
		projectDB.getCodes().add(code1);
		HibernateUtil.quietSave(dbManager, projectDB);
		
		
		projectDB = fManager.getProject(TEST_PROJECT_NAME);
		Code tempCode = projectDB.getCodes().get(0);
		assertEquals("codeA/codeD", tempCode.getParents().get(1));
	}

}
