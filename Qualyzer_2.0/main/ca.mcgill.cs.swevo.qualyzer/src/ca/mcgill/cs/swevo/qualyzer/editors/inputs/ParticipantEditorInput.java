/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Martin Robillard
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors.inputs;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;

/**
 * The input object for the participants form editor.
 */
public class ParticipantEditorInput implements IEditorInput
{
	private static final int NUM1 = 18181;
	private static final int NUM2 = 26569;
	
	private Participant fParticipant;
	
	/**
	 * Creates a new object that wraps the participant to edit.
	 * @param participant The participant object to edit.
	 */
	public ParticipantEditorInput(Participant participant)
	{
		fParticipant = participant;
	}
	
	@Override
	public boolean exists()
	{
		return fParticipant != null;
	}

	// This does not seem to be the method called by the treeviewer, so whatever is
	// returned here is of no importance. Nevertheless, to respect the interface's 
	// postcondition, it should not be null.
	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return ImageDescriptor.getMissingImageDescriptor();
	}

	@Override
	public String getName()
	{
		String proj = fParticipant.getProject().getName();
		return proj + ".editor.participant."+fParticipant.getParticipantId(); //$NON-NLS-1$
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return null; // TODO This cannot return null.
	}

	@Override
	public String getToolTipText()
	{
		return fParticipant.getFullName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	/**
	 * @return
	 */
	public Participant getParticipant()
	{
		return fParticipant;
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).append(fParticipant).toHashCode();
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object == null)
		{
			return false;
		}
		if(object == this)
		{
			return true;
		}
		if(object.getClass().equals(getClass()))
		{
			ParticipantEditorInput rhs = (ParticipantEditorInput) object;
			return new EqualsBuilder().append(fParticipant, rhs.fParticipant).isEquals();
		}
		return false;
	}
}
