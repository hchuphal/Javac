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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 */
@Entity
@DiscriminatorValue("Transcript")
public class Transcript extends AnnotatedDocument implements Comparable<Transcript>
{
	private static final int NUM1 = 3;
	private static final int NUM2 = 67;

	private AudioFile fAudioFile;
	private Map<Integer, Timestamp> fTimestamps = new HashMap<Integer, Timestamp>();

	/**
	 * @return
	 */
	@OneToOne(cascade = { CascadeType.ALL })
	public AudioFile getAudioFile()
	{
		return fAudioFile;
	}

	/**
	 * @param audioFile
	 */
	public void setAudioFile(AudioFile audioFile)
	{
		this.fAudioFile = audioFile;
	}

	/**
	 *
	 * @return
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "transcript")
	@MapKey(name = "lineNumber")
	public Map<Integer, Timestamp> getTimestamps()
	{
		return fTimestamps;
	}

	/**
	 * @param fragments
	 */
	public void setTimestamps(Map<Integer, Timestamp> timestamps)
	{
		this.fTimestamps = timestamps;
	}

	@Override
	public int compareTo(Transcript transcript)
	{
		return this.getName().compareTo(transcript.getName());
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).appendSuper(super.hashCode()).toHashCode();
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

		return new EqualsBuilder().appendSuper(super.equals(obj)).isEquals();
	}

}
