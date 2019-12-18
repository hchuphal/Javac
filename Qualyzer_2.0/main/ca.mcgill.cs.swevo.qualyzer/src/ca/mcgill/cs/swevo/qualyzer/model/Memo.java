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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 */
@Entity
@DiscriminatorValue("Memo")
public class Memo extends AnnotatedDocument implements Comparable<Memo>
{
	private static final int NUM = 58793;
	private static final int NUM2 = 1651;

	private Investigator fAuthor;
	private Code fCode;
	private Transcript fTranscript;

	/**
	 * @return
	 */
	@ManyToOne
	public Investigator getAuthor()
	{
		return fAuthor;
	}

	/**
	 * @param author
	 */
	public void setAuthor(Investigator author)
	{
		this.fAuthor = author;
	}
	
	/**
	 * 
	 * @return
	 */
	@ManyToOne
	public Code getCode()
	{
		return fCode;
	}
	
	/**
	 * 
	 * @param code
	 */
	public void setCode(Code code)
	{
		this.fCode = code;
	}
	
	/**
	 * 
	 * @return
	 */
	@ManyToOne
	public Transcript getTranscript()
	{
		return fTranscript;
	}
	
	/**
	 * 
	 * @param transcript
	 */
	public void setTranscript(Transcript transcript)
	{
		this.fTranscript = transcript;
	}

	@Override
	public int compareTo(Memo memo)
	{
		return this.getName().compareTo(memo.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM, NUM2).appendSuper(super.hashCode()).append(fAuthor).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		else
		{
			Memo memo = (Memo) obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(fAuthor, memo.fAuthor).isEquals();
		}
	}

}
