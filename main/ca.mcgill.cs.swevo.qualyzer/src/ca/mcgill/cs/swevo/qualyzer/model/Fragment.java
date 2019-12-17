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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Fragment
{
	private static final int NUM1 = 37837;
	private static final int NUM2 = 20661;

	private int fOffset;
	private int fLength;
	private List<Annotation> fAnnotations = new ArrayList<Annotation>();
	private List<CodeEntry> fCodeEntries = new ArrayList<CodeEntry>();
	private Long fPersistenceId;
	private IAnnotatedDocument fDocument;

	/**
	 * @return
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<Annotation> getAnnotations()
	{
		return fAnnotations;
	}

	/**
	 * @param annotations
	 */
	public void setAnnotations(List<Annotation> annotations)
	{
		this.fAnnotations = annotations;
	}

	/**
	 * @return
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<CodeEntry> getCodeEntries()
	{
		return fCodeEntries;
	}

	/**
	 * @param codeEntries
	 */
	public void setCodeEntries(List<CodeEntry> codeEntries)
	{
		this.fCodeEntries = codeEntries;
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

	/**
	 * 
	 * @param position
	 */
	public void setOffset(int offset)
	{
		fOffset = offset;
	}

	/**
	 * 
	 * @param length
	 */
	public void setLength(int length)
	{
		fLength = length;
	}

	/**
	 * 
	 * @return
	 */
	public int getOffset()
	{
		return fOffset;
	}

	/**
	 * 
	 * @return
	 */
	public int getLength()
	{
		return fLength;
	}

	/**
	 * 
	 */
	@ManyToOne(targetEntity = AnnotatedDocument.class)
	@JoinColumn(name = "document_fk")
	public IAnnotatedDocument getDocument()
	{
		return fDocument;
	}

	/**
	 * 
	 * @param transcript
	 */
	public void setDocument(IAnnotatedDocument document)
	{
		this.fDocument = document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		else if (obj == this)
		{
			return true;
		}
		else if (obj.getClass().equals(getClass()))
		{
			Fragment fragment = (Fragment) obj;
			return new EqualsBuilder().append(fOffset, fragment.fOffset).append(fLength, fragment.fLength).append(
					fDocument, fragment.fDocument).isEquals();
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).append(fOffset).append(fLength).append(fDocument).toHashCode();
	}
}
