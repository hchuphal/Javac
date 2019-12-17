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

package ca.mcgill.cs.swevo.qualyzer.editors.inputs;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;

/**
 *	Input for the RTFEditor.
 */
public class RTFEditorInput extends FileEditorInput
{
	/**
	 * 
	 */
	private static final String DOT = "."; //$NON-NLS-1$
	private static final int NUM1 = 39753;
	private static final int NUM2 = 50071;
	
	private IAnnotatedDocument fDocument;
	
	/**
	 * @param file
	 */
	public RTFEditorInput(IFile file, IAnnotatedDocument document)
	{
		super(file);
		fDocument = document;
	}
	
	/**
	 * Get the transcript that serves as input.
	 * @return
	 */
	public IAnnotatedDocument getDocument()
	{
		return fDocument;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.FileEditorInput#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		else if(obj.getClass().equals(getClass()))
		{
			RTFEditorInput rhs = (RTFEditorInput) obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(fDocument, rhs.fDocument).isEquals();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.FileEditorInput#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).appendSuper(super.hashCode()).append(fDocument).toHashCode();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.FileEditorInput#getName()
	 */
	@Override
	public String getName()
	{
		return fDocument.getProject().getName() + DOT + fDocument.getClass().getSimpleName() + DOT + super.getName();
	}
	
}
