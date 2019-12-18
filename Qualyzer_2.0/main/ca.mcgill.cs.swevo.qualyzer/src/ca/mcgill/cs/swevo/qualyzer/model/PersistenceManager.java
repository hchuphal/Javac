/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *     Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 */
public final class PersistenceManager
{
	
	public static final String DB_FOLDER = ".db"; //$NON-NLS-1$
	public static final String QUALYZER_DB_NAME = "qualyzer_db"; //$NON-NLS-1$
	public static final String QUALYZER_DB_FILE_NAME = "qualyzer_db.h2.db"; //$NON-NLS-1$
	public static final String DB_CONNECTION_STRING = "jdbc:h2:%s"; //$NON-NLS-1$
	// Could also be ;CACHE_SIZE=131072 (in KB) for H2
	//	public static final String DB_INIT_STRING = ";hsqldb.default_table_type=cached"; //$NON-NLS-1$
	public static final String DB_INIT_STRING = ""; //$NON-NLS-1$
	public static final String DB_USERNAME = "sa"; //$NON-NLS-1$
	public static final String DB_DIALECT = "org.hibernate.dialect.H2Dialect"; //$NON-NLS-1$
	public static final String DB_DRIVER = "org.h2.Driver"; //$NON-NLS-1$

	private static final PersistenceManager INSTANCE = new PersistenceManager();
	private static final String PER_S = "%s"; //$NON-NLS-1$

	private final QualyzerActivator fActivator;

	private PersistenceManager()
	{
		fActivator = QualyzerActivator.getDefault();
	}

	/**
	 * @return
	 */
	public static PersistenceManager getInstance()
	{
		return INSTANCE;
	}

	/**
	 * @param project
	 * @return
	 */
	public IPath getDBPath(IProject project)
	{
		return project.getFolder(DB_FOLDER).getFile(QUALYZER_DB_NAME).getRawLocation();
	}

	/**
	 * @param project
	 * @return
	 */
	public IPath getDBFilePath(IProject project)
	{
		return project.getFolder(DB_FOLDER).getFile(QUALYZER_DB_FILE_NAME).getRawLocation();
	}

	/**
	 * 
	 * Creates the HSQLDB database in the .db folder of project.
	 * 
	 * @param project
	 */
	public void initDB(IProject project)
	{
		setupDBFolder(project);
		String dbPath = getDBPath(project).toOSString();
		String connectionString = DB_CONNECTION_STRING.replace(PER_S, dbPath) + DB_INIT_STRING; //$NON-NLS-1$

		HibernateDBManager dbManager;
		dbManager = new HibernateDBManager(connectionString, DB_USERNAME, "", DB_DRIVER, DB_DIALECT); //$NON-NLS-1$

		// Add DB Manager
		fActivator.getHibernateDBManagers().put(project.getName(), dbManager);

		// Init DB
		SchemaExport export = new SchemaExport(dbManager.getConfiguration());
		export.execute(false, true, false, false);
	}
	
	/**
	 * Update the database of a project.
	 * @param project
	 */
	public void updateDB(IProject project) 
	{
		String dbPath = getDBPath(project).toOSString();
		String connectionString = DB_CONNECTION_STRING.replace(PER_S, dbPath) + DB_INIT_STRING; //$NON-NLS-1$

		HibernateDBManager dbManager;
		dbManager = new HibernateDBManager(connectionString, DB_USERNAME,
				"", DB_DRIVER, DB_DIALECT); //$NON-NLS-1$

		// Init DB
		SchemaUpdate update = new SchemaUpdate(dbManager.getConfiguration());
		update.execute(false, true);

		dbManager.getSessionFactory().close();

		if (!update.getExceptions().isEmpty()) 
		{
			throw new QualyzerException(MessagesClient.getString("model.PersistenceManager.upgradeError", "ca.mcgill.cs.swevo.qualyzer.model.messages"), //$NON-NLS-1$
					(Throwable) update.getExceptions().get(0));
		}
	}
	
	/**
	 * Makes sure that project has a HibernateDBManager registered.
	 * Should be called when Qualyzer launches so that the workspace can be properly propagated.
	 * @param project The project whose HibernateDBManager needs to be refreshed.
	 */
	public void refreshManager(IProject project)
	{
		String dbPath = getDBPath(project).toOSString();
		
		File file = new File(dbPath+".h2.db"); //$NON-NLS-1$
		if(!file.exists())
		{
			return;
		}
		
		String connectionString = DB_CONNECTION_STRING.replace(PER_S, dbPath) + DB_INIT_STRING; //$NON-NLS-1$

		try
		{
			HibernateDBManager dbManager;
			dbManager = fActivator.getHibernateDBManagers().get(project.getName());
			if(dbManager == null)
			{
				dbManager = new HibernateDBManager(connectionString, DB_USERNAME,
						"", DB_DRIVER, DB_DIALECT); //$NON-NLS-1$
				// Add DB Manager
				fActivator.getHibernateDBManagers().put(project.getName(), dbManager);
			}
		}
		catch(QualyzerException e)
		{
			//Stop the exception
		}
		
		
	}

	/**
	 * 
	 * Gets the .db folder in the project. Creates it if it does not exist.
	 * 
	 * @param project
	 * @return
	 */
	private IFolder setupDBFolder(IProject project)
	{
		IFolder dbFolder = project.getFolder(DB_FOLDER);
		if (!dbFolder.exists())
		{
			try
			{
				dbFolder.create(true, true, new NullProgressMonitor());
			}
			catch (CoreException ce)
			{
				String message = "Could not create .db folder in project " + project.getName(); //$NON-NLS-1$
				throw new QualyzerException(message, ce);
			}
		}
		return dbFolder;
	}

	/**
	 * Gets the Project object represented by the given name.
	 * @param name The name of the expected Project
	 * @return The Project represented by name or null if no such Qualyzer project.
	 */
	public Project getProject(String name)
	{
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(name.replace(' ', '_'));

		if(dbManager == null)
		{
			return null;
		}
		
		Session session = dbManager.openSession();
		Project project = null;
		try
		{
			project = (Project) session.createQuery("from Project").uniqueResult(); //$NON-NLS-1$
		}
		catch(HibernateException e)
		{
			return null;
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
		return project;
	}

	/**
	 * 
	 * Does something.
	 * 
	 * @param project
	 * @return
	 */
	public void initializeDocument(IAnnotatedDocument document)
	{
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(document.getProject().getFolderName());

		Session session = dbManager.openSession();
		try
		{
			// Reattach
			session.buildLockRequest(LockOptions.NONE).lock(document);
			Hibernate.initialize(document.getParticipants());
			Hibernate.initialize(document.getFragments());
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}

}
