/**
 * 
 */
package rtfeditor.editors;

import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.AnnotationPainter.ITextStyleStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class RTFDecorationSupport extends SourceViewerDecorationSupport
{
	
	private static final Color BLACK = new Color(Display.getDefault(), new RGB(0,0,0));
	
	private static final String BOLD = "BOLD";
	private static final String ITALIC = "ITALIC";
	private static final String BOLD_UNDERLINE = "BOLDUNDERLINE";
	private static final String BOLD_ITALIC = "BOLDITALIC";
	private static final String ITALIC_UNDERLINE = "ITALICUNDERLINE";
	private static final String BOLD_ITALIC_UNDERLINE = "BOLDITALICUNDERLINE";

	
	private static ITextStyleStrategy gBoldStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD;
		}
	};
	
	private static ITextStyleStrategy gItalicStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.ITALIC;
		}
	};
	
	private static ITextStyleStrategy gBoldUnderlineStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD;
			styleRange.underline = true;
			styleRange.underlineColor = annotationColor;
		}
	};
	
	private static ITextStyleStrategy gBoldItalicStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
		}
	};
	
	private static ITextStyleStrategy gItalicUnderlineStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.ITALIC;
			styleRange.underline = true;
			styleRange.underlineColor = annotationColor;
		}
	};
	
	private static ITextStyleStrategy gBoldItalicUnderlineStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
			styleRange.underline = true;
			styleRange.underlineColor = annotationColor;
		}
	};

	/**
	 * @param sourceViewer
	 * @param overviewRuler
	 * @param annotationAccess
	 * @param sharedTextColors
	 */
	public RTFDecorationSupport(ISourceViewer sourceViewer, IOverviewRuler overviewRuler,
			IAnnotationAccess annotationAccess, ISharedTextColors sharedTextColors)
	{
		super(sourceViewer, overviewRuler, annotationAccess, sharedTextColors);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.SourceViewerDecorationSupport#createAnnotationPainter()
	 */
	@Override
	protected AnnotationPainter createAnnotationPainter()
	{
		AnnotationPainter painter =  super.createAnnotationPainter();
		
		painter.addTextStyleStrategy(BOLD, gBoldStrategy);
		painter.addTextStyleStrategy(ITALIC, gItalicStrategy);
		painter.addTextStyleStrategy(BOLD_ITALIC, gBoldItalicStrategy);
		painter.addTextStyleStrategy(BOLD_UNDERLINE, gBoldUnderlineStrategy);
		painter.addTextStyleStrategy(ITALIC_UNDERLINE, gItalicUnderlineStrategy);
		painter.addTextStyleStrategy(BOLD_ITALIC_UNDERLINE, gBoldItalicUnderlineStrategy);
		
		painter.addAnnotationType(RTFConstants.BOLD_TYPE, BOLD);
		painter.addAnnotationType(RTFConstants.ITALIC_TYPE, ITALIC);
		painter.addAnnotationType(RTFConstants.BOLD_ITALIC_TYPE, BOLD_ITALIC);
		painter.addAnnotationType(RTFConstants.BOLD_UNDERLINE_TYPE, BOLD_UNDERLINE);
		painter.addAnnotationType(RTFConstants.ITALIC_UNDERLINE_TYPE, ITALIC_UNDERLINE);
		painter.addAnnotationType(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, BOLD_ITALIC_UNDERLINE);
		
		painter.setAnnotationTypeColor(RTFConstants.BOLD_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.ITALIC_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.BOLD_ITALIC_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.BOLD_UNDERLINE_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.ITALIC_UNDERLINE_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, BLACK);

		return painter;
	}

}
