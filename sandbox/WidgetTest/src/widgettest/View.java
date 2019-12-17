package widgettest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart {
	public static final String ID = "WidgetTest.view";
	

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		
		parent.setLayout(new GridLayout(1, true));
		Button button =new Button(parent, SWT.PUSH);
		button.setText("click");
		
		Composite comp = new Composite(parent, SWT.NULL);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		comp.setLayout(new GridLayout(2, true));
		
		Composite left = new Composite(comp, SWT.NULL);
		//StackLayout leftLayout = new StackLayout();
		left.setLayout(new GridLayout(1, true));
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite right = new Composite(comp, SWT.NULL);
		//StackLayout rightLayout
		right.setLayout(new GridLayout(1, true));
		right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Label leftLabel = new Label(left, SWT.NULL);
		leftLabel.setText("Left Label");
		
		final Label rightLabel = new Label(right, SWT.NULL);
		rightLabel.setText("Right Label");
		
		button.addSelectionListener(new SelectionAdapter()
		{
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Composite lParent = leftLabel.getParent();
				Composite rParent = rightLabel.getParent();
				
				leftLabel.setParent(rParent);
				rightLabel.setParent(lParent);
				
				lParent.layout();
				rParent.layout();
			}
		});
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		
	}
}