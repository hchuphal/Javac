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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;

/**
 *
 */
public class InvestigatorEditorInput implements IEditorInput
{
	private static final int NUM1 = 2405;
	private static final int NUM2 = 20525;
	
	private Investigator fInvestigator;
	
	/**
	 * Constructor.
	 * @param investigator
	 */
	public InvestigatorEditorInput(Investigator investigator)
	{
		fInvestigator = investigator;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists()
	{
		return fInvestigator != null && fInvestigator.getNickName().length() > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName()
	{
		String proj = fInvestigator.getProject().getName();
		return proj + ".editor.investigator."+fInvestigator.getNickName(); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText()
	{
		return fInvestigator.getFullName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	/**
	 * Get the investigator to edit.
	 * @return
	 */
	public Investigator getInvestigator()
	{
		return fInvestigator;
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).append(fInvestigator).toHashCode();
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
			InvestigatorEditorInput rhs = (InvestigatorEditorInput) obj;
			return new EqualsBuilder().append(fInvestigator, rhs.fInvestigator).isEquals();
		}
		return false;
	}
	
}
