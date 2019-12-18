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

import java.util.List;
import java.util.Map;

/**
 */
public interface IAnnotatedDocument
{

	/**
	 * @return
	 * Returns the name of the annotated document
	 */
	String getName();

	/**
	 * @return
	 * Returns the list of participants in the annotated document
	 */
	List<Participant> getParticipants();

	/**
	 * @return
	 * Returns the Fragments in the annotated document
	 */
	Map<Integer, Fragment> getFragments();

	/**
	 * @return
	 * Returns the project
	 */
	Project getProject();

	/**
	 * Get the file name.
	 * 
	 * @return
	 */
	String getFileName();

	/**
	 * 
	 * @return
	 * Returns the date which has the format MM/DD/YYYY
	 */
	String getDate();

	/**
	 * Set the fragments.
	 * 
	 * @param fragments
	 */
	void setFragments(Map<Integer, Fragment> fragments);

	/**
	 * Set the project.
	 * 
	 * @param project
	 */
	void setProject(Project project);

	/**
	 * Set a new name for an object
	 * 
	 * @param name
	 * 
	 */
	void setName(String name);

	/**
	 * Set a date for an object
	 * 
	 * @param date
	 */
	void setDate(String date);
}
