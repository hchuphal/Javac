package ca.mcgill.cs.swevo.qualyzer.providers;
import ca.mcgill.cs.swevo.qualyzer.editors.MarkTextAction;
import ca.mcgill.cs.swevo.qualyzer.editors.RTFSourceViewer;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.providers.Barchart;
import java.awt.*;
import javax.swing.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.editor.FormEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.awt.event.*;

public class CodeBarChart {
	
	public CodeBarChart(Project project, CodeTableRow[] row) {		
		JFrame frame = new JFrame();
		frame.setSize(600, 500);
	        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
			
        double[] codeFrequency = new double[row.length];
		String[] codeName = new String[row.length];
		Color[] color = new Color [row.length];
		
		//Random random = new Random();
		
		for (int i = 0; i < row.length; i++) {
			codeFrequency[i] = row[i].getFrequency();
			codeName[i] = row[i].getName();
			
			//float r = random.nextFloat();
			//float g = random.nextFloat();
			//float b = random.nextFloat();
			
			Code currentCode = row[i].getCode();			
			ArrayList<Integer> codeColour = RTFSourceViewer.getCodes().get(currentCode);

			
			//Color randomColor = new Color(r, g, b);
			
			//color[i] = randomColor;
			if (codeColour != null) {
				System.out.println("Not null");
				float fR = codeColour.get(0) / 255.0F;
				float fG = codeColour.get(1) / 255.0F;
				float fB = codeColour.get(2) / 255.0F;
				Color randomColor = new Color(fR, fG, fB);
				color[i] = randomColor;
			} else {
				System.out.println("Null");
				Color randomColor = new Color(1, 0, 0);
				color[i] = randomColor;
			}

		}
		  
		frame.getContentPane().add(new Barchart(codeFrequency, codeName, color, "Code Barchart"));
		
		WindowListener winListener = new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
			}
		};
  
	    frame.addWindowListener(winListener);
	    frame.setVisible(true);
	}

}
	



