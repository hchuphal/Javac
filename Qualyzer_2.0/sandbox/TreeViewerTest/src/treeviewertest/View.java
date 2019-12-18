package treeviewertest;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import treeviewertest.model.Code;
import treeviewertest.model.Node;
import treeviewertest.model.Project;
import treeviewertest.model.TreeModel;
import treeviewertest.providers.TableContentProvider;
import treeviewertest.providers.TableDragListener;
import treeviewertest.providers.TableLabelProvider;
import treeviewertest.providers.TreeDropListener;
import treeviewertest.providers.TreeModelContentProvider;
import treeviewertest.providers.TreeModelLabelProvider;


public class View extends ViewPart {
	public static final String ID = "TreeViewerTest.view";

	private TreeViewer viewer;
	private TableViewer tableViewer;

	private Project fProject;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {	
		parent.setLayout(new GridLayout(2, true));
		
		viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		TreeColumn col = new TreeColumn(viewer.getTree(), SWT.NONE);
		col.setWidth(100);
		col.setText("Code names");
		
		col = new TreeColumn(viewer.getTree(), SWT.NONE);
		col.setWidth(100);
		col.setText("Frequency");
		
		col = new TreeColumn(viewer.getTree(), SWT.NONE);
		col.setWidth(100);
		col.setText("Total frequency");
		
		viewer.setContentProvider(new TreeModelContentProvider());
		viewer.setLabelProvider(new TreeModelLabelProvider());
		viewer.setInput(getInitialInput());
		
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		viewer.setSorter(new ViewerSorter());
		
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.SINGLE);
		TableColumn tCol = new TableColumn(tableViewer.getTable(), SWT.NONE);
		tCol.setText("Code Name");
		tCol.setWidth(100);
		
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setInput(fProject);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		tableViewer.setSorter(new ViewerSorter());
		
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] types = new Transfer[]{TextTransfer.getInstance()};
		tableViewer.addDragSupport(ops, types, new TableDragListener(tableViewer));
		

		types = new Transfer[]{TextTransfer.getInstance()};
		viewer.addDropSupport(ops, types, new TreeDropListener(viewer));
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				System.out.println(((Node) sel.getFirstElement()).getPathToRoot());
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public Node getInitialInput()
	{
		fProject = new Project("Project");
		
		Code code = new Code("a");
		fProject.getCodes().add(code);
		code.getParents().add("/");
	
		code = new Code("c");
		fProject.getCodes().add(code);
		code.getParents().add("d");
		code.getParents().add("a");

		code = new Code("f");
		fProject.getCodes().add(code);
		code.getParents().add("g/b/e");
		code.getParents().add("a/c");
		
		code = new Code("b");
		fProject.getCodes().add(code);
		code.getParents().add("g");
		code.getParents().add("a");
	
		
		code = new Code("g");
		fProject.getCodes().add(code);
		code.getParents().add("/");
		
		code = new Code("e");
		fProject.getCodes().add(code);
		code.getParents().add("g/b");
		code.getParents().add("d");

		code = new Code("h");
		fProject.getCodes().add(code);
		code.getParents().add("g");
		
		code = new Code("d");
		fProject.getCodes().add(code);
		code.getParents().add("/");
		
		for(Code aCode : fProject.getCodes())
		{
			aCode.setFrequency(2);
		}

		TreeModel model = TreeModel.getTreeModel(fProject);
		
		return model.getRoot();
	}
}