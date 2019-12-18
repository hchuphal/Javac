/**
 * 
 */
package treeviewertest.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import treeviewertest.model.Node;

/**
 * @author Jonathan Faubert
 *
 */
public class TreeModelLabelProvider extends LabelProvider implements ITableLabelProvider
{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return ((Node) element).getCodeName();
		case 2:
			return "" + ((Node) element).getAggragateFreq();
		case 1:
			return "" + ((Node) element).getLocalFreq();
		}
		return "This should not happen";
	}

	

}
