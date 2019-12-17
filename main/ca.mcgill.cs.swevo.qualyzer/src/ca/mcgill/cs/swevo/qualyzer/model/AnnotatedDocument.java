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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.ForceDiscriminator;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * 
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@ForceDiscriminator
public abstract class AnnotatedDocument implements IAnnotatedDocument
{

	private static final int NUM1 = 7;
	private static final int NUM2 = 23;

	private Long fPersistenceId;
	private Project fProject;
	private List<Participant> fParticipants = new ArrayList<Participant>();
	private Map<Integer, Fragment> fFragments = new HashMap<Integer, Fragment>();
	private String fName;
	private String fFileName;

	/**
	 * Has the format MM/DD/YYYY. Always entered using a DateTime Widget.
	 */
	private String fDate;

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

	@ManyToMany(fetch = FetchType.LAZY)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	@Override
	public List<Participant> getParticipants()
	{
		return fParticipants;
	}

	/**
	 * @param participants
	 */
	public void setParticipants(List<Participant> participants)
	{
		this.fParticipants = participants;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "document")
	@MapKey(name = "offset")
	@Override
	public Map<Integer, Fragment> getFragments()
	{
		return fFragments;
	}

	/**
	 * @param fragments
	 */
	public void setFragments(Map<Integer, Fragment> fragments)
	{
		this.fFragments = fragments;
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return fName;
	}

	/**
	 * @param name
	 */
	public void setName(String name)
	{
		this.fName = name;
	}

	/**
	 * @return
	 */
	public String getFileName()
	{
		return fFileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName)
	{
		this.fFileName = fileName;
	}

	@ManyToOne
	@JoinColumn(name = "project_persistenceid", nullable = false, insertable = false, updatable = false)
	@Override
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
	 * @param Date
	 */
	public void setDate(String date)
	{
		this.fDate = date;
	}

	/**
	 * 
	 * @return
	 */
	public String getDate()
	{
		return fDate;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).append(fName).append(fFileName).append(fProject).toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (obj.getClass() != getClass())
		{
			return false;
		}

		AnnotatedDocument document = (AnnotatedDocument) obj;

		return new EqualsBuilder().append(fName, document.fName).append(fFileName, document.fFileName).append(fProject,
				document.fProject).isEquals();
	}

}
