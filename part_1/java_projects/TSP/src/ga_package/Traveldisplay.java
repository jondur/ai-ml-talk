package ga_package;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

class Traveldisplay extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private static final int OFFSET_X = 30;
	private static final int OFFSET_Y = 10;
	private static final double SCALE_FACTOR = 1.13;
    
	private GA gc;

    public Traveldisplay(GA g) 
    {
    	gc = g;
    }

    private void doDrawing(Graphics g) 
    {
       	Offspring bestOffspring = gc.currBestSolution();

       	Graphics2D g2d = (Graphics2D) g;
       	
        g2d.setPaint(Color.red);
        
        double x1 = 0;
        double x2 = 0;
        double y1 = 0;
        double y2 = 0;
        
        for (int i = 0; i < GA.GENE_COUNT - 1; i++)
        {	
    		x1 = OFFSET_X + (bestOffspring.dna[i].x/2) * SCALE_FACTOR;
    		y1 = OFFSET_Y + (bestOffspring.dna[i].y/2) * SCALE_FACTOR;
    		x2 = OFFSET_X + (bestOffspring.dna[i+1].x/2) * SCALE_FACTOR;
    		y2 = OFFSET_Y + (bestOffspring.dna[i+1].y/2) * SCALE_FACTOR;
    		
    		g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    		
    		g2d.setPaint(Color.gray);
    		g2d.setFont(new Font("Arial", Font.BOLD, 12));
    		g2d.drawString(Integer.toString(bestOffspring.dna[i].id), (int)x1, (int)y1);
    		
    		g2d.setPaint(Color.black);
    		g2d.setFont(new Font("Arial", Font.BOLD, 12));
    		g2d.drawString(Integer.toString(i), (int)x1, (int)y1 + 10);
    		
    		g2d.setPaint(Color.red);
        }
        
        x1 = OFFSET_X + (bestOffspring.dna[0].x/2) * SCALE_FACTOR;
		y1 = OFFSET_Y + (bestOffspring.dna[0].y/2) * SCALE_FACTOR;
		x2 = OFFSET_X + (bestOffspring.dna[51].x/2) * SCALE_FACTOR;
		y2 = OFFSET_Y + (bestOffspring.dna[51].y/2) * SCALE_FACTOR;
		
		g2d.setPaint(Color.red);
		g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		
		g2d.setPaint(Color.gray);
		g2d.drawString(Integer.toString(bestOffspring.dna[51].id), (int)x2, (int)y2);
		
		g2d.setPaint(Color.black);
		g2d.drawString(Integer.toString(51), (int)x2, (int)y2 + 10);
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