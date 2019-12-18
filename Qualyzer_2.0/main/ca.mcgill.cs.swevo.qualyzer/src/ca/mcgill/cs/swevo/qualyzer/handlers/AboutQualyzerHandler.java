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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;

/**
 * Handler for the About Qualyzer Command.
 */
public class AboutQualyzerHandler extends AbstractHandler
{
	private static final String RELEASE = MessagesClient.getString("handlers.AboutQualyzerHandler.releaseTag", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
		" " + QualyzerActivator.CURRENT_VERSION + "\n\n"; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final String MCGILL = MessagesClient.getString("handlers.AboutQualyzerHandler.mcGillTag", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")+ //$NON-NLS-1$
		"\n\n"; //$NON-NLS-1$ 
	
	private static final String DESCRIPTION = 
		MessagesClient.getString("handlers.AboutQualyzerHandler.description1", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.description2", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
	
	private static final String WEB = MessagesClient.getString("handlers.AboutQualyzerHandler.website", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
	
	private static final String COPYRIGHT = 
		MessagesClient.getString("handlers.AboutQualyzerHandler.copyright1", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + "\n"+//$NON-NLS-1$ //$NON-NLS-2$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright2", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright3", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright4", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright5", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright5a", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright5b", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright5c", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright6", "ca.mcgill.cs.swevo.qualyzer.handlers.messages") + //$NON-NLS-1$
			MessagesClient.getString("handlers.AboutQualyzerHandler.copyright8", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"); //$NON-NLS-1$
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		
		MessageDialog.openInformation(shell, 
				MessagesClient.getString("handlers.AboutQualyzerHandler.about", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), //$NON-NLS-1$
				RELEASE+MCGILL+DESCRIPTION+WEB+COPYRIGHT);
		return null;
	}

}
