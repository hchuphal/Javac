package ca.mcgill.cs.swevo.qualyzer.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.QualyzerWizardDialog;
import ca.mcgill.cs.swevo.qualyzer.handlers.ImportTranscriptHandler;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.ImportTranscriptPage;

public class RTFParserTest
{
	private Project fProject;
	private IProject wProject;
	private IWorkbenchPage fPage;
	
	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String PART = "Part";
	private static final String DOC = "ImportDocument";
	private static final String DOC_UNICODE = "UnicodeStyleOO3";
	private static final String GERMAN_UNICODE = "GermanUnicode";
	private static final String IMAGE_UNICODE = "ImageUnicode";
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);

		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		Facade.getInstance().createParticipant(PART, "", fProject);

		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}

	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}

	private void importTranscript(final String filePath, final String name, final String textFilePath)
	{
		TestUtil.setProjectExplorerSelection(wProject);

		ImportTranscriptHandler handler = new ImportTranscriptHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{

			@Override
			public void execute(Dialog dialog)
			{
				QualyzerWizardDialog wizard = (QualyzerWizardDialog) dialog;

				ImportTranscriptPage page = (ImportTranscriptPage) wizard.getCurrentPage();
				page.getFileText().setText(filePath);
				page.getNameText().setText(name);
				page.getTable().select(0);

				wizard.finishPressed();
			}
		});

		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}

		File textFile = new File(textFilePath);
		String textFileContent = TestUtil.readFile(textFile);
		
		IEditorPart editor = fPage.getActiveEditor();
		TranscriptEditor tEditor = (TranscriptEditor) editor;
		RTFDocument rtfDocument = (RTFDocument) tEditor.getDocumentProvider().getDocument(tEditor.getEditorInput());
		assertEquals(rtfDocument.get(), textFileContent);
		
	}
	
	@Test
	public void importRTF()
	{
		// Import Custom RTF document
		importTranscript(DOC + ".rtf", DOC, DOC + ".txt");
	}
	
	@Test
	public void importUnicodeStyleRTF()
	{
		// Import Custom RTF document
		importTranscript(DOC_UNICODE + ".rtf", DOC_UNICODE, DOC_UNICODE + ".txt");
	}
	
	@Test
	public void importGermanUnicode()
	{
		// Import Custom RTF document
		importTranscript(GERMAN_UNICODE + ".rtf", GERMAN_UNICODE, GERMAN_UNICODE + ".txt");
	}
	
	@Test
	public void importImageUnicode()
	{
		// Import Custom RTF document
		importTranscript(IMAGE_UNICODE + ".rtf", IMAGE_UNICODE, IMAGE_UNICODE + ".txt");
	}
}
