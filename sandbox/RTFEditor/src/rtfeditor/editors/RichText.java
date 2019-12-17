package rtfeditor.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class RichText extends EditorPart
{
	private IFile fInputFile;
	
	private int fBoldTag;
	private int fItalicTag;
	private int fUnderlineTag;
	private boolean fIsDirty;
	private int fLastCount;
	
	private ArrayList<StyleRange> fStyleRanges;

	private StyledText fText;

	/**
	 * 
	 */
	public RichText()
	{
		fBoldTag = -1;
		fItalicTag = -1;
		fUnderlineTag = -1;
		fStyleRanges = new ArrayList<StyleRange>();
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		StyleRange[] ranges = fText.getStyleRanges(0, fText.getCharCount(), true);
		String text = fText.getText();
		String outputText;
		
		outputText = buildRTFString(text, ranges);

		InputStream data = new ByteArrayInputStream(outputText.getBytes());
		try
		{
			fInputFile.setContents(data, IResource.FORCE, new NullProgressMonitor());
			setNotDirty();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 */
	private void setNotDirty()
	{
		fIsDirty = false;
		fLastCount = fText.getCharCount();
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * @param text
	 * @param ranges
	 * @return
	 */
	private String buildRTFString(String text, StyleRange[] ranges)
	{
		String output = "{\\rtf1\\ansi\\deff0\n";
		
		StyleRange style = null;
		
		for(int i = 0; i < text.length(); i++)
		{
			if(style == null)
			{
				for(int j = 0; j < ranges.length; j++)
				{
					StyleRange range = ranges[j];
					if(range.start == i)
					{
						style = range;
						break;
					}
				}
				
				if(style != null)
				{
					if((style.fontStyle & SWT.BOLD) == SWT.BOLD)
					{
						output += "\\b ";
					}
					if((style.fontStyle & SWT.ITALIC) == SWT.ITALIC)
					{
						output += "\\i ";
					}
					if(style.underline)
					{
						output += "\\ul ";
					}
				}
			}
			
			char c = text.charAt(i);
			
			if(c != '\n' && c != '\t' && c!= '\0')
			{
				output += c;
			}
			
			if(style != null && i >= style.start + style.length - 1)
			{
				if((style.fontStyle & SWT.BOLD) == SWT.BOLD)
				{
					output += "\\b0 ";
				}
				if((style.fontStyle & SWT.ITALIC) == SWT.ITALIC)
				{
					output += "\\i0 ";
				}
				if(style.underline)
				{
					output += "\\ulnone ";
				}
				
				style = null;
			}
			
			if(c == '\n')
			{
				output += "\\par \n";
			}
			else if(c == '\t')
			{
				output += "\\tab ";
			}
			
		}
					
		return output + "\n}\n\0";
	}

	@Override
	public void doSaveAs()
	{
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
		if(input instanceof FileEditorInput)
		{
			fInputFile = ((FileEditorInput) input).getFile();
		}
	}

	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout());
		
		Label label = new Label(parent, SWT.NULL);
		label.setText("This is a music player... Use your imagination.");
		label.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		Slider slider = new Slider(parent, SWT.HORIZONTAL);
		slider.setValues(0, 0, 100, 5, 1, 5);
		slider.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		fText = new StyledText(parent, SWT.WRAP);
		fText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fText.addKeyListener(hookupKeyListener());
		
		try
		{
			InputStream ioStream = fInputFile.getContents();
			int c;
			boolean justStarted = true;
			while((c = ioStream.read()) != -1)
			{
				char ch = (char) c;
				if(ch == '{' || ch == '}')
				{
					if(justStarted)
					{
						justStarted = false;
						continue;
					}
					else if(ch == '{')
					{
						int count = 1;
						while(count >= 1)
						{
							ch = (char) ioStream.read();
							if(ch == '{')
							{
								count++;
							}
							else if(ch == '}')
							{
								count--;
							}
						}
					}
				}
				else if(ch == '\\')
				{
					String escape = " ";
					do
					{
						escape = nextTag(ioStream);
						handleTag(fText, escape);
					}while(escape.charAt(escape.length() - 1) == '\\');
					
				}
				else if((!Character.isWhitespace(ch) && ch != '\0') || ch == ' ')
				{
					fText.append(""+ch);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		fStyleRanges = mergeStyleRanges();
		fText.setStyleRanges(fStyleRanges.toArray(new StyleRange[0]));
		
		fText.addVerifyKeyListener(hookupVerifyKeyListener());
		fText.setKeyBinding(SWT.CONTROL | 'b', SWT.NULL);
		fText.setKeyBinding(SWT.CONTROL | 'u', SWT.NULL);
		fText.setKeyBinding(SWT.CONTROL | 'i', SWT.NULL);
		
	}

	/**
	 * @return
	 */
	private VerifyKeyListener hookupVerifyKeyListener()
	{
		return new VerifyKeyListener(){

			@Override
			public void verifyKey(VerifyEvent event)
			{		
				System.out.println((int)event.character);
				System.out.println(event.keyCode);
				System.out.println(event.stateMask);
				
				if(event.stateMask == SWT.CONTROL)
				{	
					if(event.keyCode == 'b')
					{
						boldSelection();
					}
					else if(event.keyCode == 'u')
					{
						underlineSelection();
					}
					else if(event.keyCode == 'i')
					{
						event.doit = false;
						italicizeSelection();
					}
				}
				
				
			}};
	}

	/**
	 * 
	 */
	protected void underlineSelection()
	{
		Point point = fText.getSelection();
		
		if(point.y == 0)
		{
			return;
		}
		
		StyleRange[] existing = fText.getStyleRanges(point.x, point.y - point.x);
		
		for(StyleRange range : existing)
		{
			range.underline = !range.underline;
			fText.setStyleRange(range);
		}
		
		if(existing.length == 0)
		{
			StyleRange style = new StyleRange();
			style.start = point.x;
			style.length = point.y - point.x;
			style.underline = true;
			
			fText.setStyleRange(style);
		}

		setDirty();
	}

	/**
	 * 
	 */
	protected void italicizeSelection()
	{
		Point point = fText.getSelection();
		
		if(point.y == 0)
		{
			return;
		}
		
		StyleRange[] existing = fText.getStyleRanges(point.x, point.y - point.x);
		
		for(StyleRange range : existing)
		{
			if((range.fontStyle & SWT.ITALIC) == SWT.ITALIC)
			{
				range.fontStyle = range.fontStyle & SWT.BOLD;
			}
			else
			{
				range.fontStyle = range.fontStyle | SWT.ITALIC;
			}
			fText.setStyleRange(range);
		}
		
		if(existing.length == 0)
		{
			StyleRange style = new StyleRange();
			style.start = point.x;
			style.length = point.y - point.x;
			style.fontStyle = SWT.ITALIC;
			
			fText.setStyleRange(style);
		}

		setDirty();
	}

	/**
	 * 
	 */
	protected void boldSelection()
	{
		Point point = fText.getSelection();
		
		if(point.y == 0)
		{
			return;
		}
		
		StyleRange[] existing = fText.getStyleRanges(point.x, point.y - point.x);
		
		for(StyleRange range : existing)
		{
			if((range.fontStyle & SWT.BOLD) == SWT.BOLD)
			{
				range.fontStyle = range.fontStyle & SWT.ITALIC;
			}
			else
			{
				range.fontStyle = range.fontStyle | SWT.BOLD;
			}
			fText.setStyleRange(range);
		}
		
		if(existing.length == 0)
		{
			StyleRange style = new StyleRange();
			style.start = point.x;
			style.length = point.y - point.x;
			style.fontStyle = SWT.BOLD;
			
			fText.setStyleRange(style);
		}

		setDirty();
	}

	/**
	 * @return
	 */
	private KeyAdapter hookupKeyListener()
	{
		return new KeyAdapter(){

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(fText.getCharCount() != fLastCount)
				{
					setDirty();
				}
			}

		};
	}
	
	private void setDirty()
	{
		if(!fIsDirty)
		{
			fLastCount = fText.getCharCount();
			fIsDirty = true;
			firePropertyChange(PROP_DIRTY);
		}
	}

	/**
	 * @param text
	 * @param escape
	 */
	private void handleTag(StyledText text, String escape)
	{
		String string = escape.trim();
		if(escape.charAt(escape.length() - 1) == '\\')
		{
			string = escape.substring(0, escape.length() - 1);
		}
		string = string.trim();
		
		if(string.equals("par"))
		{
			text.append("\n");
		}
		else if(string.equals("tab"))
		{
			text.append("\t");
		}
		else if(string.equals("b"))
		{
			fBoldTag = text.getCharCount();
		}
		else if(string.equals("b0"))
		{	
			StyleRange style = new StyleRange();
			style.fontStyle = SWT.BOLD;
			style.start = fBoldTag;
			style.length = text.getCharCount() - fBoldTag;
			
			fBoldTag = -1;
			fStyleRanges.add(style);
		}
		else if(string.equals("i"))
		{
			fItalicTag = text.getCharCount();
		}
		else if(string.equals("i0"))
		{
			
			StyleRange style = new StyleRange();
			style.fontStyle = SWT.ITALIC;
			style.start = fItalicTag;
			style.length = text.getCharCount() - fItalicTag;
			
			fItalicTag = -1;
			fStyleRanges.add(style);
		}
		else if(string.equals("ul"))
		{
			fUnderlineTag = text.getCharCount();
		}
		else if(string.equals("ulnone"))
		{
			
			StyleRange style = new StyleRange();
			style.fontStyle = SWT.NORMAL;
			style.start = fUnderlineTag;
			style.length = text.getCharCount() - fUnderlineTag;
			style.underline = true;
			
			fUnderlineTag = -1;
			fStyleRanges.add(style);
		}
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}
	
	private String nextTag(InputStream ioStream) throws IOException
	{
		String escape = "";
		char ch2 = (char) ioStream.read();
		while(ch2 != ' ' && ch2 != '{' && ch2 != '}' && ch2 != '\\' && ch2 != '\n')
		{
			escape += ch2;
			ch2 = (char) ioStream.read();
		}
		
		if(ch2 == '\\')
		{
			escape += "\\";
		}
		
		if(ch2 == '{')
		{
			int count = 1;
			while(count >= 1)
			{
				ch2 = (char) ioStream.read();
				if(ch2 == '{')
				{
					count++;
				}
				else if(ch2 == '}')
				{
					count--;
				}
			}
		}
		
		return escape;
	}
	
	private ArrayList<StyleRange> mergeStyleRanges()
	{
		ArrayList<StyleRange> ranges = new ArrayList<StyleRange>();
		
		while(fStyleRanges.size() > 0)
		{
			StyleRange style = fStyleRanges.get(0);
			for(StyleRange style2 : fStyleRanges)
			{
				if(style2.start < style.start)
				{
					style = style2;
				}
			}
			ranges.add(style);
			fStyleRanges.remove(style);
		}
		
		fStyleRanges = ranges;
		ranges = new ArrayList<StyleRange>();
		
		while(fStyleRanges.size() > 0)
		{
			StyleRange style = fStyleRanges.remove(0);
			
			if(style.length <= 1)
			{
				continue;
			}
			
			if(fStyleRanges.isEmpty())
			{
				ranges.add(style);
				break;
			}
			
			StyleRange style2 = fStyleRanges.get(0);
			
			if(style.start == style2.start)
			{
				fStyleRanges.remove(0);
				if(style.length > style2.length) //now assume that style is shorter
				{
					StyleRange temp = style;
					style = style2;
					style2 = temp;
				}
				
				StyleRange newRange = new StyleRange();
				newRange.start = style.start;
				newRange.length = style.length;
				newRange.fontStyle = style.fontStyle | style2.fontStyle;
				newRange.underline = style.underline || style2.underline;
				
				StyleRange newRange2 = new StyleRange();
				newRange2.start = style.start + style.length;
				newRange2.length = style2.length - style.length;
				newRange2.fontStyle = style2.fontStyle;
				newRange2.underline = style2.underline;
				
				fStyleRanges.add(0, newRange);
				
				for(int i = 0; i < fStyleRanges.size(); i++)
				{
					if(newRange2.start <= fStyleRanges.get(i).start)
					{
						fStyleRanges.add(i, newRange2);
						break;
					}
				}
				
				if(!fStyleRanges.contains(newRange2))
				{
					fStyleRanges.add(newRange2);
				}				
			}
			else if(style2.start < style.start + style.length)
			{
				StyleRange newRange = new StyleRange();
				newRange.start = style.start;
				newRange.length = style2.start - style.start;
				newRange.fontStyle = style.fontStyle;
				newRange.underline = style.underline;
				
				ranges.add(newRange);
				style.start = style2.start;
				style.length -= newRange.length;
				fStyleRanges.add(0, style);
			}
			else
			{
				ranges.add(style);
			}
		}
		
		return ranges;
	}
}
