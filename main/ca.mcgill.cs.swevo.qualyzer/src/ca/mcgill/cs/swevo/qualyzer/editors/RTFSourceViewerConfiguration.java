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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.editors;

import java.util.Iterator;

import net.sf.colorer.eclipse.editors.ColorerSourceViewerConfiguration;
import net.sf.colorer.eclipse.jface.TextColorer;

import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;

/**
 * Implements our text hover strategy, our double click strategy, and defines what is shown in the overview ruler.
 */
public class RTFSourceViewerConfiguration extends ColorerSourceViewerConfiguration
{
	
	/**
	 * @param textColorer
	 */
	public RTFSourceViewerConfiguration(TextColorer textColorer)
	{
		super(textColorer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(
	 * org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
	{
		return new DefaultTextHover(sourceViewer)
		{
			/* (non-Javadoc)
			 * @see org.eclipse.jface.text.DefaultTextHover#isIncluded(org.eclipse.jface.text.source.Annotation)
			 */
			@Override
			protected boolean isIncluded(Annotation annotation)
			{
				//This makes it so that the text hover does not include the time stamps which
				// were displaying over the code name.
				return annotation instanceof FragmentAnnotation;
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(
	 * org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		return new FragmentDoubleClickStrategy();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#isShowInOverviewRuler(
	 * org.eclipse.jface.text.source.Annotation)
	 */
	@Override
	protected boolean isShowInOverviewRuler(Annotation annotation)
	{
		//Only allows Fragment annotations to be shown in the overview ruler.
		return annotation instanceof FragmentAnnotation;
	}
	/**
	 * DoubleClickStrategy for marked fragments. Selects the entire fragment. If double clicking on something 
	 * else then it uses the default double click strategy.
	 */
	private class FragmentDoubleClickStrategy extends DefaultTextDoubleClickStrategy
	{	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.DefaultTextDoubleClickStrategy#doubleClicked(org.eclipse.jface.text.ITextViewer)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void doubleClicked(ITextViewer text)
		{	
			boolean found = false;
			if(text instanceof ISourceViewer)
			{
				Point selection = text.getSelectedRange();
				IAnnotationModel model = ((ISourceViewer) text).getAnnotationModel();
				Iterator<Annotation> iter = model.getAnnotationIterator();
				while(!found && iter.hasNext())
				{
					Annotation annotation = iter.next();
					if(annotation instanceof FragmentAnnotation)
					{
						Position position = model.getPosition(annotation);
						if(selection.x >= position.offset && selection.x <= position.offset + position.length)
						{
							text.setSelectedRange(position.offset, position.length);
							found = true;
						}
					}
				}
			}
			if(!found)
			{
				super.doubleClicked(text);
			}
		}
	}
}
