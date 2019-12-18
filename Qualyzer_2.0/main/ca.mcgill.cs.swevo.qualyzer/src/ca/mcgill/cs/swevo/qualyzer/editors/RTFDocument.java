/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/

package ca.mcgill.cs.swevo.qualyzer.editors;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * The model for the Rich Text Document. Used to gather all the rich text tags and fragments when
 * the document is being parsed.
 *
 */
public class RTFDocument extends Document
{
	/**
	 * Stores Rich Text Tags as annotations.
	 */
	private HashMap<Position, Annotation> fAnnotations;
	
	private HashMap<Position, FragmentAnnotation> fFragments;
	
	/**
	 * Constructor.
	 */
	public RTFDocument()
	{
		fAnnotations = new HashMap<Position, Annotation>();
		fFragments = new HashMap<Position, FragmentAnnotation>();
	}
	
	/**
	 * Add an annotation at the given position.
	 * @param position
	 * @param annotation
	 */
	public void addAnnotation(Position position, Annotation annotation)
	{
		if(annotation instanceof FragmentAnnotation)
		{
			fFragments.put(position, (FragmentAnnotation) annotation);
		}
		else
		{
			fAnnotations.put(position, annotation);
		}
	}
	
	/**
	 * Get the set of positions that have annotations.
	 * @return
	 */
	public Set<Position> getKeysForAnnotations()
	{
		return fAnnotations.keySet();
	}
	
	/**
	 * Get the set of positions that have fragments.
	 * @return
	 */
	public Set<Position> getKeysForFragments()
	{
		return fFragments.keySet();
	}
	
	/**
	 * Get the annotation for the given position.
	 * @param position
	 * @return
	 */
	public Annotation getAnnotation(Position position)
	{
		return fAnnotations.get(position);
	}
	
	/**
	 * Get the fragment for the given position.
	 * @param position
	 * @return
	 */
	public FragmentAnnotation getFragment(Position position)
	{
		return fFragments.get(position);
	}

}

