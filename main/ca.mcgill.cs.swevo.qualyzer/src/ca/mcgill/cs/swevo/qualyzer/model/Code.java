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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

/**
 */
@Entity
public class Code implements Comparable<Code>
{
	private static final int NUM = 24651;
	private static final int NUM2 = 23959;

	private String fCodeName;
	private String fDescription;
	private Project fProject;
	private Long fPersistenceId;
	private List<String> fParents = new ArrayList<String>();

	/**
	 * @return the codeName
	 */
	public String getCodeName()
	{
		return fCodeName;
	}

	/**
	 * @param codeName
	 *            the codeName to set
	 */
	public void setCodeName(String codeName)
	{
		this.fCodeName = codeName;
	}

	/**
	 * @return
	 */
	@Type(type = "text")
	public String getDescription()
	{
		return fDescription;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.fDescription = description;
	}

	/**
	 * @param persistenceId
	 *            the persistenceId to set
	 */
	public void setPersistenceId(Long persistenceId)
	{
		this.fPersistenceId = persistenceId;
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
	 * 
	 * @return
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = "parents_index")
	public List<String> getParents()
	{
		return fParents;
	}

	/**
	 * 
	 * Does something.
	 * 
	 * @param parents
	 */
	public void setParents(List<String> parents)
	{
		this.fParents = parents;
	}

	@Override
	public int compareTo(Code code)
	{
		return this.getCodeName().compareTo(code.getCodeName());
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM, NUM2).append(fCodeName).append(fProject).toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj == null)
		{
			return false;
		}
		else if (obj.getClass().equals(getClass()))
		{
			Code code = (Code) obj;
			return new EqualsBuilder().append(fCodeName, code.fCodeName).append(fProject, code.fProject).isEquals();
		}
		return false;
	}
}
