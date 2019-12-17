package rtfeditor.handlers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class OpenEditorHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("Project");
		
		if(!project.exists())
		{
			try
			{
				project.create(new NullProgressMonitor());
				project.open(new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		if(!project.isOpen())
		{
			try
			{
				project.open(new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		File fileHandle = new File(project.getLocation()+File.separator+"doc.rtf");
		if(!fileHandle.exists())
		{
			try
			{
				fileHandle.createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		IFile file = project.getFile("doc.rtf");
		
		if(!file.exists())
		{	
			try
			{
				file.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileEditorInput input = new FileEditorInput(file);
		
		try
		{
			page.openEditor(input, "RTFEditor.editors.rtfEditor");
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
