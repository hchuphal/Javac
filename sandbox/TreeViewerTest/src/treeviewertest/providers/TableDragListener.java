/**
 * 
 */
package treeviewertest.providers;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import treeviewertest.model.Code;

/**
 * @author Jonathan Faubert
 *
 */
public class TableDragListener implements DragSourceListener
{

	private final TableViewer fViewer;
	
	public TableDragListener(TableViewer viewer)
	{
		fViewer = viewer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragSetData(DragSourceEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		Code code = (Code) selection.getFirstElement();
		
		if(TextTransfer.getInstance().isSupportedType(event.dataType))
		{
			event.data = code.getName() + ":" + code.getFrequency();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragStart(DragSourceEvent event)
	{
	}

}
