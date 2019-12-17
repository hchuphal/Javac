/**
 * 
 */
package rtfeditor.editors;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class RTFDocument extends Document
{
	private HashMap<Position, Annotation> fAnnotations;
	
	/**
	 * 
	 */
	public RTFDocument()
	{
		fAnnotations = new HashMap<Position, Annotation>();
	}
	
	public void addAnnotation(Position position, Annotation annotation)
	{
		fAnnotations.put(position, annotation);
	}
	
	public Set<Position> getKeys()
	{
		return fAnnotations.keySet();
	}
	
	public Annotation getAnnotation(Position position)
	{
		return fAnnotations.get(position);
	}

}
