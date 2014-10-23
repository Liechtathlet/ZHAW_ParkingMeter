/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * @author Daniel Brun
 * 
 *         Panel which displays a parking meter.
 */
public class ParkingMeterPanel extends JPanel {

	private static final Color BG = new Color(93, 92, 102);
	private static final Color BG_INNER = new Color(26, 51, 115);
	private static final Color BG_DISPLAY = new Color(95, 109, 96);
	private static final Color BG_MARKER = new Color(200,139,50);

	private double factor = 3.0;
	private int initialHeight;
	private int initialWidth;

	public ParkingMeterPanel() {

		initialHeight = 300;
		initialWidth = 100;
		setPreferredSize(new Dimension((int)(initialWidth*factor), (int)(initialHeight*factor)));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        
		int initialDisplayHeight = 25;

		int currentX = 0;
		int currentY = 0;

		// Draw background
		g2.setColor(BG);
		g2.fillRect((int) (currentX * factor), (int) (currentY * factor),
				(int) (initialWidth * factor), (int) (initialHeight * factor));

		// Draw inner background
		currentX += 5;
		currentY += 10;
		
		int innerWidth = initialWidth - 10;
		int innerHeight = initialHeight - 60;
		
		g2.setColor(BG_INNER);
		g2.fillRect((int) (currentX * factor), (int) (currentY * factor),
				(int) ((innerWidth-1) * factor), (int) ((innerHeight-1) * factor));
				
		// Draw inner border / shadow
		g2.setColor(Color.BLACK);
		g2.draw3DRect((int) (currentX * factor), (int) (currentY * factor),
				(int) ((innerWidth-1) * factor),
				(int) ((innerHeight-1) * factor), true);


		//Draw display background
		currentX += 20;
		currentY += 15;
		
		int displayWidth = innerWidth - 40;
		
		g2.setColor(BG_DISPLAY);
		g2.fillRect((int) (currentX * factor), (int) (currentY * factor),
				(int) ((displayWidth-1) * factor), (int) ((initialDisplayHeight-1) * factor));
		
		// Draw display border
		g2.setColor(Color.BLACK);
		g2.draw3DRect((int) (currentX * factor), (int) (currentY * factor),
				(int) ((displayWidth-1) * factor),
				(int) ((initialDisplayHeight-1) * factor), true);
		
		//Draw display text
		drawDisplayText(g,currentX,currentY,currentX + displayWidth, currentY + initialDisplayHeight);
		
		//Calculate marker triangle
		int middle = initialWidth / 2;
		currentY += initialDisplayHeight;
		currentY += 3;
		int infoTriangleWidth = displayWidth - 20;
		
		int tp1y = (int)(currentY*factor);
		int tp2y = (int)((currentY + (infoTriangleWidth/3))*factor);;
		
		int tp1x = (int)((middle - (infoTriangleWidth/2))*factor);
		int tp2x = (int)((middle + (infoTriangleWidth/2))*factor);
		int tp3x = (int)((middle *factor));
		
		//Draw triangle background
		g2.setColor(BG_MARKER);
		g2.fillPolygon(new int[]{tp1x,tp2x,tp3x,tp1x}, new int[]{tp1y,tp1y,tp2y,tp1y}, 4);
		
		//Draw triangle border
		g2.setColor(Color.black);
		g2.drawPolyline(new int[]{tp1x,tp2x,tp3x,tp1x}, new int[]{tp1y,tp1y,tp2y,tp1y}, 4);
	}

	
	/**
	 * Draws the display text.
	 * 
	 * @param g The graphic object.
	 * @param xFrom The x start position of the display.
	 * @param xTo	The x end position of the display.
	 * @param yFrom	The y start position of the display.
	 * @param yTo 	The y end position of the display.
	 */
	private void drawDisplayText(Graphics g,int xFrom,int xTo,int yFrom,int yTo) {
		// TODO Auto-generated method stub
		
	}

}
