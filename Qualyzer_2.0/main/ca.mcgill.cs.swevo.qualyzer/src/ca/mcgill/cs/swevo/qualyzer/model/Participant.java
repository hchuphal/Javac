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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

/**
 */
@Entity
public class Participant implements Comparable<Participant>
{
	private static final int NUM = 23339;
	private static final int NUM2 = 34905;
	
	private String fParticipantId;
	private String fFullName;
	private String fNotes;
	private String fContactInfo;
	private Project fProject;
	private Long fPersistenceId;

	/**
	 * @return
	 */
	public String getParticipantId()
	{
		return fParticipantId;
	}

	/**
	 * @param participantId
	 */
	public void setParticipantId(String participantId)
	{
		this.fParticipantId = participantId;
	}

	/**
	 * @return
	 */
	public String getFullName()
	{
		return fFullName;
	}

	/**
	 * @param fullName
	 */
	public void setFullName(String fullName)
	{
		this.fFullName = fullName;
	}

	/**
	 * @return
	 */
	@Type(type = "text")
	public String getNotes()
	{
		return fNotes;
	}

	/**
	 * @param notes
	 */
	public void setNotes(String notes)
	{
		this.fNotes = notes;
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
	public int compareTo(Participant participant)
	{
		return this.getParticipantId().compareTo(participant.getParticipantId());
	}
	
	/**
	 * @return
	 */
	@ManyToOne
	@JoinColumn(name = "project_persistenceid", nullable = false, insertable = false, updatable = false)
	public Project getProject()
	{
		return fProject;
	}

	/**
	 * @param project
	 */
	public void setProject(Project project)
	{
		this.fProject = project;
	}

	/**
	 * @param contactInfo
	 */
	public void setContactInfo(String contactInfo)
	{
		this.fContactInfo = contactInfo;
	}

	/**
	 * @return
	 */
	public String getContactInfo()
	{
		return fContactInfo;
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM, NUM2).append(fParticipantId)
			.append(fProject).toHashCode();
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object == null)
		{
			return false;
		}
		else if(object == this)
		{
			return true;
		}
		else if(object.getClass().equals(getClass()))
		{
			Participant part = (Participant) object;
			return new EqualsBuilder().append(fParticipantId, part.fParticipantId)
				.append(fProject, part.fProject).isEquals();
		}
		return false;
	}

}
