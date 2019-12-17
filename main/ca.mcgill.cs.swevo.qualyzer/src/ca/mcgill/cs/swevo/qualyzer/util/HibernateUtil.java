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
package ca.mcgill.cs.swevo.qualyzer.util;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;

/**
 * HibernateUtil provides utility methods to conveniently call the Hibernate API.
 * 
 * Most methods encapsulate thrown exceptions in a QualyzerException, log the exception, and perform cleanup actions if
 * necessary (e.g., rollback).
 * 
 * 
 */
public final class HibernateUtil
{
	private HibernateUtil()
	{

	}

	/**
	 * Close a session.
	 * @param session
	 */
	public static void quietClose(Session session)
	{
		if (session != null)
		{
			try
			{
				session.close();
			}
			catch (HibernateException e)
			{
				// logger.error("Error while closing a session.", e);
			}
		}
	}

	/**
	 * Rollback a transaction.
	 * @param transaction
	 */
	public static void quietRollback(Transaction transaction)
	{
		if (transaction != null)
		{
			try
			{
				transaction.rollback();
			}
			catch (HibernateException e)
			{
				// logger.error("Error while rolling back a transaction.", e);
			}
		}
	}

	/**
	 * Save an object.
	 * @param manager
	 * @param object
	 */
	public static void quietSave(HibernateDBManager manager, Object object)
	{
		Transaction t = null;
		Session session = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			session.saveOrUpdate(object);
			session.flush();
			t.commit();
		}
		catch (HibernateException e)
		{
			quietRollback(t);
		}
		finally
		{
			quietClose(session);
		}
	}

	/**
	 * Save many objects.
	 * @param manager
	 * @param objects
	 */
	public static void quietSave(HibernateDBManager manager, Object[] objects)
	{
		Transaction t = null;
		Session session = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			for (Object object : objects)
			{
				session.saveOrUpdate(object);
			}
			session.flush();
			t.commit();
		}
		catch (HibernateException e)
		{
			quietRollback(t);
		}
		finally
		{
			quietClose(session);
		}
	}

	/**
	 * Refresh an object.
	 * @param manager
	 * @param object
	 */
	public static void quietRefresh(HibernateDBManager manager, Object object)
	{
		Transaction t = null;
		Session session = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			session.refresh(object);
			t.commit();
		}
		catch (HibernateException e)
		{
			quietRollback(t);
		}
		finally
		{
			quietClose(session);
		}
	}

	/**
	 * Refresh many objects.
	 * @param manager
	 * @param objects
	 */
	public static void quietRefresh(HibernateDBManager manager, Object[] objects)
	{
		Transaction t = null;
		Session session = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			for (Object object : objects)
			{
				session.refresh(object);
			}
			t.commit();
		}
		catch (HibernateException e)
		{
			quietRollback(t);
		}
		finally
		{
			quietClose(session);
		}
	}
	
	/**
	 * 
	 * Returns an int representing a count.
	 * 
	 * Pre-conditions:
	 *   The query must be of form "select count(...) from ..."
	 *   The session that created the query must be open
	 *
	 * @param query
	 * @return
	 */
	public static int count(Query query)
	{
		return ((Number) query.iterate().next()).intValue();
	}

}
