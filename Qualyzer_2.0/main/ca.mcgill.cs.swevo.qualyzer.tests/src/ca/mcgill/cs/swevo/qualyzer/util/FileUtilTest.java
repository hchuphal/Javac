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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class FileUtilTest
{
	/**
	 * 
	 */
	private static final String INVESTIGATOR = "Investigator";
	/**
	 * 
	 */
	private static final String PROJECT = "Project6548";
	/**
	 * 
	 */
	private static final int NUM = 1000;
	private File fIn;
	private File fOut;
	
	/**
	 * Setup the tests.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException
	{
		fIn = new File("in.txt");
		fOut = new File("out.txt");
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(fIn);
			for(int i = 0; i < NUM; i++)
			{
				writer.write('0');
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(writer != null)
			{
				writer.close();
			}
		}
	}
	
	/**
	 * Tests the file copy utility.
	 */
	@Test
	public void copyFileTest()
	{
		boolean caught = false;
		try
		{
			FileUtil.copyFile(fIn, fOut);
		}
		catch (IOException e)
		{
			caught = true;
		}
		
		assertFalse(caught);
		
		try
		{
			FileReader reader1 = new FileReader(fIn);
			FileReader reader2 = new FileReader(fOut);
			
			int c;
			while((c = reader1.read()) != -1)
			{
				int c2 = reader2.read();
				
				assertEquals(c, c2);
			}
			
			assertEquals(reader2.read(), -1);
		}
		catch(IOException e)
		{
			assertFalse(true);
		}
	}
	
	/**
	 * Tests the import of text file into RTF.
	 */
	@Test
	public void importTextFileTest()
	{
		File lIn = new File("TextTranscript.txt");
		File lOut = new File("ImportTestOut.rtf");
		
		try
		{
			FileUtil.importTextFile(lIn,lOut);
		}
		catch (IOException e)
		{
			fail("IOException in test");
		}
		
		try
		{
			FileReader reader1 = new FileReader(lOut);
			FileReader reader2 = new FileReader("OracleImportTest.rtf");
			
			int c;
			while((c = reader1.read()) != -1)
			{
				int c2 = reader2.read();
				
				assertEquals(c, c2);
			}
			
			assertEquals(reader2.read(), -1);
		}
		catch(IOException e)
		{
			fail("IOException in test");
		}
	}
	
	/**
	 * Tests the import of an empty text file into RTF.
	 */
	@Test
	public void importEmptyTextFileTest()
	{
		File lIn = new File("EmptyFile.txt");
		File lOut = new File("ImportTestOut.rtf");
		
		try
		{
			FileUtil.importTextFile(lIn,lOut);
		}
		catch (IOException e)
		{
			fail("IOException in test");
		}
		
		try
		{
			BufferedReader reader1 = new BufferedReader(new FileReader(lOut));
			String line = reader1.readLine();	
			assertEquals(line,"{\\rtf1\\ansi\\deff0");
			line = reader1.readLine();
			assertEquals(line,"");
			line = reader1.readLine();
			assertEquals(line,"");
			line = reader1.readLine();
			assertEquals(line,"}");
			
		}
		catch(Exception e)
		{
			fail("Exception in test");
		}
	}
	
	/**
	 * Try copying when the second file already exists.
	 */
	@Test
	public void fileExistsTest()
	{
		boolean caught = false;
		try
		{
			FileUtil.copyFile(fIn, fOut);
		}
		catch (IOException e)
		{
			caught = true;
		}
		
		assertFalse(caught);
		
		try
		{
			FileReader reader1 = new FileReader(fIn);
			FileReader reader2 = new FileReader(fOut);
			
			int c;
			while((c = reader1.read()) != -1)
			{
				int c2 = reader2.read();
				
				assertEquals(c, c2);
			}
			
			assertEquals(reader2.read(), -1);
		}
		catch(IOException e)
		{
			assertFalse(true);
		}
	}
	
	@Test
	public void projectPropertiesTest()
	{
		Project project = Facade.getInstance().createProject(PROJECT, INVESTIGATOR, "", "");
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
		
		try
		{
			String prop = FileUtil.getProjectProperty(wProject, FileUtil.ACTIVE_INV);
			assertEquals(prop, INVESTIGATOR);
			
			prop = FileUtil.getProjectProperty(wProject, "SomeProperty");
			assertTrue(prop.isEmpty());
			
			FileUtil.setProjectProperty(wProject, "MyProperty", "SomeValue");
			FileUtil.setProjectProperty(wProject, "MyOtherProperty", "SomeOtherValue");
			FileUtil.setProjectProperty(wProject, "Last Property", "This is a long value");
			
			prop = FileUtil.getProjectProperty(wProject, "MyProperty");
			assertEquals(prop, "SomeValue");
			
			prop = FileUtil.getProjectProperty(wProject, "MyOtherProperty");
			assertEquals(prop, "SomeOtherValue");
			
			prop = FileUtil.getProjectProperty(wProject, "Last Property");
			assertEquals(prop, "This is a long value");
			
			prop = FileUtil.getProjectProperty(wProject, FileUtil.ACTIVE_INV);
			assertEquals(prop, INVESTIGATOR);
			
			FileUtil.setProjectProperty(wProject, "MyOtherProperty", "This better work");
			prop = FileUtil.getProjectProperty(wProject, "MyOtherProperty");
			assertEquals(prop, "This better work");
		}
		catch (CoreException e)
		{
			fail();
		}
		finally
		{
			Facade.getInstance().deleteProject(project);
		}
	}
	
}
