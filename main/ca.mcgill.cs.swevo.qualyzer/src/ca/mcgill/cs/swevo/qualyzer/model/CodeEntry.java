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
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
  */
@Entity
public class CodeEntry
{
	private static final int NUM = 32363;
	private static final int NUM2 = 33841;
	
	private Code fCode;
	
	private Investigator fInvestigator;
	
	private Long fPersistenceId;

	/**
	 * @return
	 */
	@OneToOne
	public Code getCode()
	{
		return fCode;
	}

	/**
	 * @param code
	 */
	public void setCode(Code code)
	{
		this.fCode = code;
	}

	/**
	 * @return
	 */
	@OneToOne
	public Investigator getInvestigator()
	{
		return fInvestigator;
	}

	/**
	 * @param investigator
	 */
	public void setInvestigator(Investigator investigator)
	{
		this.fInvestigator = investigator;
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
		return new HashCodeBuilder(NUM, NUM2).append(fCode).append(fInvestigator).toHashCode();
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
			CodeEntry entry = (CodeEntry) obj;
			return new EqualsBuilder().append(fCode, entry.fCode).append(fInvestigator, entry.fInvestigator).isEquals();
		}
		
		return false;
	}
	
	
}
