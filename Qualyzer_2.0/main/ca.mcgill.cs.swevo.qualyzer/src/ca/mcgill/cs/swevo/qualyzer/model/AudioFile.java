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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An audio file corresponding to a transcript.
 */
@Entity
public class AudioFile
{
	private static final int NUM = 2403;
	private static final int NUM2 = 46665;
	
	// Path relative to the workspace.
	private String fRelativePath;
	
	private Long fPersistenceId;

	/**
	 * Gets the path of the audio file relative to the project.
	 * Has format /audio/transcriptName.extension
	 * @return
	 */
	public String getRelativePath()
	{
		return fRelativePath;
	}

	/**
	 * Sets the path of the audio file relative to the project.
	 * Has format /audio/transcriptName.extension
	 * @param relativePath
	 */
	public void setRelativePath(String relativePath)
	{
		this.fRelativePath = relativePath;
	}

	/**
	 * @return
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getPersistenceId()
	{
		return fPersistenceId;
	}

	/**
	 * @param persistenceId
	 */
	public void setPersistenceId(Long persistenceId)
	{
		this.fPersistenceId = persistenceId;
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM, NUM2).append(fRelativePath).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		else if(obj == this)
		{
			return true;
		}
		else if(obj.getClass().equals(getClass()))
		{
			AudioFile file = (AudioFile) obj;
			return new EqualsBuilder().append(fRelativePath, file.fRelativePath).isEquals();
		}
		
		return false;
	}
	
	
}
