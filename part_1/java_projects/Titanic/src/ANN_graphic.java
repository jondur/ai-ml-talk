import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

class ANN_graphic extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private ANN ann = null;
	
   	public double ticket_class = 0.0;
   	public double age = 0.0;
   	public double gender = 0.0;
   	
	public double t = 0.0;
	public double e = 0.0;
	public int estimate_correct = 0;
	
    public ANN_graphic(ANN ann_ref) 
    {
    	ann = ann_ref;
    }

    private void doDrawing(Graphics g) 
    {    	
       	Graphics2D g2d = (Graphics2D) g;
       	
       	drawConnectingLines(g2d);
       	
       	drawInput1(g2d, 10, 50, 150, 30);
       	drawInput2(g2d, 10, 200, 150, 30);
       	drawInput3(g2d, 10, 350, 150, 30);
       	
       	drawMid1(g2d, 450, 125, 30, 30);
       	drawMid2(g2d, 450, 275, 30, 30);
       	
       	drawOutput(g2d, 800, 200, 30, 30);
       	
       	drawEstimationResult(g2d, estimate_correct);
    }

	private void drawConnectingLines(Graphics2D g2d) 
	{
		g2d.setPaint(Color.gray);
		g2d.setFont(new Font("Arial", Font.BOLD, 12));
		
		g2d.drawLine(160, 65, 450, 140);
		g2d.drawLine(160, 65, 450, 290);
		
		g2d.drawLine(160, 215, 450, 140);
		g2d.drawLine(160, 215, 450, 290);
		
		g2d.drawLine(160, 365, 450, 140);
		g2d.drawLine(160, 365, 450, 290);
		
		g2d.drawLine(480, 140, 800, 215);
		g2d.drawLine(480, 290, 800, 215);
		
		g2d.setPaint(Color.black);
	}

	private void drawEstimationResult(Graphics2D g2d, int correctness) 
	{
		
		if (correctness == 0)
		{
			g2d.drawString("Training...", 700, 50);
		}
		else if(correctness == 1)
		{
			g2d.drawString("CORRECT!", 700, 50);
		}
		else
		{
			g2d.drawString("INCORRECT", 700, 50);
		}
	}

	private void drawMid1(Graphics2D g2d, int xpos, int ypos, int width, int hight) 
	{
		g2d.drawRect(xpos, ypos, width, hight);
		
		g2d.drawString("M1", (xpos + 8), ypos + 20);
		
		g2d.drawString("OUT w1: " + Double.toString(ann.output_layer[0][0][0]), (xpos + width + 20), ypos + 20);
	}
	
	private void drawMid2(Graphics2D g2d, int xpos, int ypos, int width, int hight) 
	{
		g2d.drawRect(xpos, ypos, width, hight);
		
		g2d.drawString("M2", (xpos + 8), ypos + 20);
		
		g2d.drawString("OUT w2: " + Double.toString(ann.output_layer[0][1][0]), (xpos + width + 20), ypos + 20);
	}

	private void drawOutput(Graphics2D g2d, int xpos, int ypos, int width, int hight) 
	{
		g2d.drawRect(xpos, ypos, width, hight);
		
		g2d.drawString("OUT", (xpos + 3), ypos + 20);
		
		g2d.drawString("Estimate: " + Double.toString(e), (xpos + width + 20), ypos + 10);
		g2d.drawString("Target: " + Double.toString(t), (xpos + width + 20), ypos + 30);
	}
	
	private void drawInput3(Graphics2D g2d, int xpos, int ypos, int width, int hight) 
    {
		g2d.drawRect(xpos, ypos, width, hight);
       	g2d.drawString("[IN3] Gender", xpos + 10, ypos + 20);
       	g2d.drawString("[" + gender + "]", (xpos + 10) + 90, ypos + 20);

		g2d.drawString("M1 w: " + Double.toString(ann.hidden_layer[0][2][0]), (xpos + width + 10), ypos + 15);
		g2d.drawString("M2 w: " + Double.toString(ann.hidden_layer[1][2][0]), (xpos + width + 10), ypos + 25);
    }

	private void drawInput2(Graphics2D g2d, int xpos, int ypos, int width, int hight) {
		
		g2d.drawRect(xpos, ypos, width, hight);
       	g2d.drawString("[IN2] Age", xpos + 10, ypos + 20);
       	g2d.drawString("[" + age + "]", (xpos + 10) + 90, ypos + 20);

		g2d.drawString("M1 w: " + Double.toString(ann.hidden_layer[0][1][0]), (xpos + width + 10), ypos + 15);
		g2d.drawString("M2 w: " + Double.toString(ann.hidden_layer[1][1][0]), (xpos + width + 10), ypos + 25);
	}

	private void drawInput1(Graphics2D g2d, int xpos, int ypos, int width, int hight) 
	{
		g2d.drawRect(xpos, ypos, width, hight);
       	g2d.drawString("[IN1] Ticket", xpos + 10, ypos + 20);
       	g2d.drawString("[" + ticket_class + "]", (xpos + 10) + 90, ypos + 20);
	
		g2d.drawString("M1 w: " + Double.toString(ann.hidden_layer[0][0][0]), (xpos + width + 10), ypos + 15);
		g2d.drawString("M2 w: " + Double.toString(ann.hidden_layer[1][0][0]), (xpos + width + 10), ypos + 25);
	}

	@Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        doDrawing(g);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}