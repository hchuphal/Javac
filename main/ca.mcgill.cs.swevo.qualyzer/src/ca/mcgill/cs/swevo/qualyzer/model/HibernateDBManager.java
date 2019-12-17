/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 * A HibernateDBManager manages the connections with a single database.
 */
public class HibernateDBManager
{
	private final SessionFactory fSessionFactory;
	private final Configuration fConfiguration;

	/**
	 * Initializes a Hibernate Configuration instance and a SessionFactory instance.
	 * 
	 * @param connectionString
	 * @param userName
	 * @param password
	 * @param driver
	 * @param dialect
	 */
	public HibernateDBManager(String connectionString, String userName, String password, String driver, String dialect)
	{
		try
		{
			AnnotationConfiguration tempConfiguration = new AnnotationConfiguration().setProperty(
					"hibernate.connection.url", connectionString) //$NON-NLS-1$
					.setProperty("hibernate.connection.username", userName).setProperty(//$NON-NLS-1$
							"hibernate.connection.password", password) //$NON-NLS-1$
					.setProperty("hibernate.dialect", dialect) //$NON-NLS-1$
					.setProperty("hibernate.connection.driver_class", driver); //$NON-NLS-1$
//					.setProperty("hibernate.connection.pool_size","0");//$NON-NLS-1$
			// .setProperty("hibernate.show_sql","true").setProperty("hibernate.format_sql",
			// "true");

			// Add classes
			tempConfiguration = tempConfiguration.addAnnotatedClass(Annotation.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(AudioFile.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Code.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(CodeEntry.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Fragment.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Investigator.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Memo.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Participant.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Project.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Transcript.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Timestamp.class);
			// Configure
			tempConfiguration = tempConfiguration.configure();

			fConfiguration = tempConfiguration;

			fSessionFactory = fConfiguration.buildSessionFactory();
		}
		catch (HibernateException ex)
		{
			// logger.error("Error while initializing HibernateUtil", ex);
			throw new QualyzerException(ex);
		}
	}
	
	/**
	 * @return
	 */
	public Configuration getConfiguration()
	{
		return fConfiguration;
	}

	/**
	 * @return
	 */
	public SessionFactory getSessionFactory()
	{
		return fSessionFactory;
	}

	/**
	 * @return
	 */
	public Session openSession()
	{
		return fSessionFactory.openSession();
	}

	/**
	 * Sends a shutdown request to the DB server.
	 */
	public void shutdownDBServer() 
	{
//		// This is not required for H2, but may be required for other databases like HSQLDB.
//		Session session = null;
//		try
//		{
//			session = openSession();
//			session.createSQLQuery("SHUTDOWN IMMEDIATELY").executeUpdate(); //$NON-NLS-1$
//		}
//		finally
//		{
//			HibernateUtil.quietClose(session);
//		}
	}
	
	/**
	 * Closes the SessionFactory and releases all resources (db connections, cache, etc.). Does something.
	 * 
	 */
	public void close()
	{
		fSessionFactory.close();
	}
}
