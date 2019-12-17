package ca.mcgill.cs.swevo.qualyzer.providers;
//https://www.roseindia.net/java/example/java/swing/draw-simple-bar-chart.shtml
//Color for each bar examples
//http://helpdesk.objects.com.au/java/how-to-display-bar-chart-using-swing
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Barchart extends JPanel {
	
	  private double[] value;
	  private String[] languages;
	  private String title;
	  private Color[] color;

	  public Barchart(double[] val, String[] lang, Color[] color, String t) {
		  languages = lang;
		  value = val;
		  title = t;
		  this.color = color;
	  }
	  
  public void paintComponent(Graphics graphics) {
	  super.paintComponent(graphics);
  
	  if (value == null || value.length == 0)
		  return;
	  
	  double minValue = 0;
	  double maxValue = 0;
	  
	  for (int i = 0; i < value.length; i++) {
		  if (minValue > value[i])
			  minValue = value[i];
		  if (maxValue < value[i])
			  maxValue = value[i];
	  }
	  
	  Dimension dim = getSize();
	  
	  int clientWidth = dim.width;
	  int clientHeight = dim.height;
	  int barWidth = clientWidth / value.length;
	  
	  Font titleFont = new Font("Book Antiqua", Font.BOLD, 15);
	  FontMetrics titleFontMetrics = graphics.getFontMetrics(titleFont);
	  Font labelFont = new Font("Book Antiqua", Font.PLAIN, 10);
	  FontMetrics labelFontMetrics = graphics.getFontMetrics(labelFont);
	  
	  int titleWidth = titleFontMetrics.stringWidth(title);
	  int q = titleFontMetrics.getAscent();
	  int p = (clientWidth - titleWidth) / 2;
	  
	  graphics.setFont(titleFont);
	  graphics.drawString(title, p, q);
	  
	  int top = titleFontMetrics.getHeight();
	  int bottom = labelFontMetrics.getHeight();
	  
	  if (maxValue == minValue)
		  return;
	  
	  
	  double scale = (clientHeight - top - bottom) / (maxValue - minValue);
	  
	  q = clientHeight - labelFontMetrics.getDescent();
	  graphics.setFont(labelFont);
	  	
	  for (int j = 0; j < value.length; j++) {
		  int valueP = j * barWidth + 1;
		  int valueQ = top;
		  int height = (int) (value[j] * scale);
		  	if (value[j] >= 0)
		  		valueQ += (int) ((maxValue - value[j]) * scale);
		  	else {
		  		valueQ += (int) (maxValue * scale);
		  		height = -height;
		  }
		  	
	  //The bar fillings
	  graphics.setColor(color[j]);
	  graphics.fillRect(valueP, valueQ, barWidth - 2, height);
	  
	  //Fonts and borders
	  graphics.setColor(Color.black);
	  graphics.drawRect(valueP, valueQ, barWidth - 2, height);
	  
	  int labelWidth = labelFontMetrics.stringWidth(languages[j]);
	  p = j * barWidth + (barWidth - labelWidth) / 2;
	  
	  graphics.drawString(languages[j], p, q);
	  }
  	}
 }
