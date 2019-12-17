/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 * Dialog to allow users to report issues to the Qualyzer development team.
 */
@SuppressWarnings("restriction")
public class ReportIssueDialog extends Dialog
{
	private static final String QUALYZER_LOG = "qualyzer.log";
	private static final int REPORT_HEIGHT = 150;
	private static final int REPORT_WIDTH = 500;
	private static final int MAX_LOG_LINES = 500;
	
	private Text fReportText;
	private Button fCopyButton;
	private Clipboard fClipboard;

	/**
	 * Constructor. Stores a clipboard in this dialog instance.
	 * 
	 * @param shell
	 */
	public ReportIssueDialog(Shell shell)
	{
		super(shell);
		fClipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		super.getShell().setText(MessagesClient.getString("dialogs.ReportIssueDialog.title", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"));
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gData.widthHint = REPORT_WIDTH;
		gData.minimumWidth = REPORT_WIDTH;
		composite.setLayoutData(gData);
		
		Text label = new Text(composite, SWT.LEFT | SWT.WRAP | SWT.READ_ONLY | SWT.NO_SCROLL);
		label.setText(MessagesClient.getString("dialogs.ReportIssueDialog.message", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"));
		gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gData.widthHint = REPORT_WIDTH;
		label.setLayoutData(gData);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		// CSOFF:
		fReportText = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		// CSON:
		
		gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gData.heightHint = REPORT_HEIGHT;
		gData.widthHint = REPORT_WIDTH;
		fReportText.setLayoutData(gData);
		fReportText.setText(getReportText());

		fCopyButton = new Button(composite, SWT.PUSH);
		fCopyButton.setText(MessagesClient.getString("dialogs.ReportIssueDialog.copyButton", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"));
		fCopyButton.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				copyToClipboard();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		return parent;
	}

	// CSOFF:
	private String getReportText()
	{
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("=== QUALYZER ISSUE REPORT ===\n\n"); //$NON-NLS-1$
		sBuilder.append("==== System Information ====\n"); //$NON-NLS-1$
		sBuilder.append(String.format("Qualyzer Version=%s\n", QualyzerActivator.CURRENT_VERSION)); //$NON-NLS-1$
		sBuilder.append(String.format("Java Version=%s\n", System.getProperty("java.version"))); //$NON-NLS-1$
		sBuilder.append(String.format("Java Vendor=%s\n", System.getProperty("java.vendor"))); //$NON-NLS-1$
		/*sBuilder.append(String.format("BootLoader constants: OS=%s, ARCH=%s, WS=%s, NL=%s\n", 
								EclipseEnvironmentInfo.getDefault().getOS(), 
								EclipseEnvironmentInfo.getDefault().getOSArch(), 
								EclipseEnvironmentInfo.getDefault().getWS(), 
								EclipseEnvironmentInfo.getDefault().getNL())); //$NON-NLS-1$
		sBuilder.append(String.format("Command-line arguments: %s\n", 
								(Object[])EclipseEnvironmentInfo.getDefault().getCommandLineArgs())); //$NON-NLS-1$*/
		sBuilder.append("==== End of System Information ====\n\n"); //$NON-NLS-1$
		
		sBuilder.append("==== Qualyzer Log ====\n"); //$NON-NLS-1$
		String qualyzerPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(QUALYZER_LOG).toOSString();
		sBuilder.append(readFile(qualyzerPath));
		sBuilder.append("==== End of Qualyzer Log ====\n\n"); //$NON-NLS-1$
		
		sBuilder.append("==== Workspace Log ====\n"); //$NON-NLS-1$
		String workspaceLogPath = InternalPlatform.getDefault().getFrameworkLog().getFile().getAbsolutePath();
		sBuilder.append(readFile(workspaceLogPath));
		sBuilder.append("==== End of Workspace Log ====\n\n"); //$NON-NLS-1$
		
		sBuilder.append("=== END OF REPORT ===\n"); //$NON-NLS-1$

		return sBuilder.toString();
	}
	// CSON:
	
	private String readFile(String filePath) 
	{
		File file = new File(filePath);
		if (!file.exists()) 
		{
			return String.format("Log file not found: %s\n", filePath);
		}
		
		StringBuilder sBuilder = new StringBuilder();
		int lineNumber = 0;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				lineNumber++;
				if(lineNumber > MAX_LOG_LINES)
				{
					break;
				}
				sBuilder.append(line);
				sBuilder.append("\n"); //$NON-NLS-1$
			}
			reader.close();
		}
		catch(IOException ioException)
		{
			throw new QualyzerException("Error while reading log file " + filePath, ioException);
		}
		
		return sBuilder.toString();
	}

	/**
	 * Copy the content of the textbox in the clipboard. 
	 */
	public void copyToClipboard()
	{
		// This is to give the illusion that something happened. 
		// Otherwise, the users will see nothing.
		fReportText.selectAll();
		String textData = fReportText.getText();
		TextTransfer textTransfer = TextTransfer.getInstance();
		fClipboard.setContents(new Object[] { textData }, new Transfer[] { textTransfer });
	}

	@Override
	public void okPressed()
	{
		super.okPressed();
	}

	@Override
	public boolean close()
	{
		if (fClipboard != null)
		{
			fClipboard.dispose();
			fClipboard = null;
		}

		return super.close();
	}
	
	/**
	 * @return The Text widget (for testing)
	 */
	public Text getReportTextWidget()
	{
		return fReportText;
	}
	
	/**
	 * @return The clipboard (for testing)
	 */
	public Clipboard getClipboard()
	{
		return fClipboard;
	}
}
