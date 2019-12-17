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
import org.hibernate.annotations.GenericGenerator;

/**
 * 
 *
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Timestamp
{
	private Transcript fTranscript;

	private int fLineNumber;

	private int fSeconds;
	
	private Long fPersistenceId;

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
	 * @return
	 */
	@ManyToOne(targetEntity = Transcript.class)
	@JoinColumn(name = "transcript_fk")
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

	/**
	 * 
	 * @return
	 */

	public int getLineNumber()
	{
		return fLineNumber;
	}

	/**
	 * 
	 * @param lineNumber
	 */
	public void setLineNumber(int lineNumber)
	{
		this.fLineNumber = lineNumber;
	}

	/**
	 * 
	 * @return
	 */
	public int getSeconds()
	{
		return fSeconds;
	}

	/**
	 *
	 * @param seconds
	 */
	public void setSeconds(int seconds)
	{
		this.fSeconds = seconds;
	}

}
