import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import javax.swing.Timer;

import SumoMath.Algebra;
import SumoMath.Point_2D;
import SumoMath.Vector2D;
import SumoObjects.SumoRing;
import SumoObjects.SumoRobot;

@SuppressWarnings("serial")
class SumoGraphics extends JPanel implements ActionListener
{   
	private int ping_counter1 = 0;
	private int ping_counter2 = 0;
	
	private Timer time;
    private Graphics2D g2d;
    
	private SumoRing ring;
	
	private SumoAI r1 = new SumoAI();
	private SumoAI r2 = new SumoAI();

    private SumoRobot sumo1;
    private SumoRobot sumo2;
    
	private Shape sumo_shape2;
	private Shape sumo_shape1;
	
    public SumoGraphics() 
    {       
    	ring = new SumoRing(new Point_2D(0, 0), 770);    	
    	          
        r1.init(1, (int)ring.center.x, (int)ring.center.y-100, -1, this);
        r2.init(2, (int)ring.center.x, (int)ring.center.y+100, 1, this);
        
        sumo1 = r1.getSumo();
        sumo2 = r2.getSumo();
        
        time = new Timer(1, this);
        time.start();
    }
    
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		r1.iterate();
		r2.iterate();
		repaint();
	}
	
    public SumoRobot get_sumo(int requester_id)
    {
    	if(requester_id == 1)
    		return sumo2;
    	else
    		return sumo1;
    }
    
    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        doDrawing(g);   
    }
    
    private void doDrawing(Graphics g) 
    {   	 
    	g2d = (Graphics2D) g; 
    	
    	drawCoordinateReferencePoint(true);
    	drawSumoPosData(true);
    	drawRing(true);
    	drawSumo1(true);
    	drawSumo2(true);
    	drawCollisionDetectionLines(true);
        drawPingSensorData(true);
        drawSumoLabels(true);
        drawSumoLineSensors(true); 
    }	
    
    
    private void drawCoordinateReferencePoint(boolean b) 
    {
    	if(!b)
    		return;
    	
    	g2d.drawString("*{X=0, Y=0}", 0, 12);
	}

	private void drawSumoLineSensors(boolean b) 
    {
    	if(!b)
    		return;
    	
        g2d.setPaint(new Color(200, 0, 0));
        
    	if(sumo1.sensor_BR.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
    	g2d.fillOval((int)sumo1.sensor_BR.x-5, (int)sumo1.sensor_BR.y-5, 10, 10);
    	
    	if(sumo1.sensor_BL.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
    	g2d.fillOval((int)sumo1.sensor_BL.x-5, (int)sumo1.sensor_BL.y-5, 10, 10);
    	
    	if(sumo1.sensor_FR.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
    	g2d.fillOval((int)sumo1.sensor_FR.x-5, (int)sumo1.sensor_FR.y-5, 10, 10);
    	
    	if(sumo1.sensor_FL.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
		g2d.fillOval((int)sumo1.sensor_FL.x-5, (int)sumo1.sensor_FL.y-5, 10, 10);
		
		if(sumo2.sensor_BR.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
    	g2d.fillOval((int)sumo2.sensor_BR.x-5, (int)sumo2.sensor_BR.y-5, 10, 10);
    	
    	if(sumo2.sensor_BL.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
    	g2d.fillOval((int)sumo2.sensor_BL.x-5, (int)sumo2.sensor_BL.y-5, 10, 10);
    	
    	if(sumo2.sensor_FR.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
    	g2d.fillOval((int)sumo2.sensor_FR.x-5, (int)sumo2.sensor_FR.y-5, 10, 10);
    	
    	if(sumo2.sensor_FL.exceeds_distance(ring.center, ring.diameter_safe/2))
    		g2d.setPaint(new Color(200, 0, 0));
    	else
    		g2d.setPaint(new Color(0, 200, 0));
    	
		g2d.fillOval((int)sumo2.sensor_FL.x-5, (int)sumo2.sensor_FL.y-5, 10, 10);
	}

	private void drawSumoLabels(boolean b) 
    {
    	if(!b)
    		return;
    	
        g2d.setPaint(new Color(0, 0, 0));
        
        g2d.setFont(new Font("Test", Font.BOLD, 20));
        g2d.drawString("R1", (int)sumo1.center_point.x, (int)sumo1.center_point.y);
        g2d.drawString("R2", (int)sumo2.center_point.x, (int)sumo2.center_point.y);
	}

	private void drawPingSensorData(boolean b) 
    {
    	if(!b)
    		return;
    	
        g2d.setPaint(new Color(0, 200, 0));
        
        g2d.drawString("L",(int)sumo1.L_weel_point.x, (int)sumo1.L_weel_point.y);
        g2d.drawString("R", (int)sumo1.R_weel_point.x, (int)sumo1.R_weel_point.y);      
        
        
        g2d.drawString("L",(int)sumo2.L_weel_point.x, (int)sumo2.L_weel_point.y);
        g2d.drawString("R", (int)sumo2.R_weel_point.x, (int)sumo2.R_weel_point.y); 
        
        g2d.drawString(String.format("PING-hit-count: %d", ping_counter1), 5, 50);
        g2d.drawString(String.format("Dist: %f", r1.ping_sensor_distance), 5, 70);
        
        g2d.drawString(String.format("PING-hit-count: %d", ping_counter2), 650, 50);
        g2d.drawString(String.format("Dist: %f", r2.ping_sensor_distance), 650, 70);
        
        g2d.setStroke(new BasicStroke(5));
        
        if(r1.ping_performed && r1.ping_detected)
        {
        	
        	g2d.setPaint(new Color(200, 0, 0));
        	g2d.drawString("DETECTED!", 5, 30);
        	g2d.draw(sumo1.ping_sensor);  
        	ping_counter1++;
        }
        else if(r1.ping_performed)
        {
        	g2d.setPaint(new Color(0, 200, 000));
        	g2d.drawString("PING...", 5, 30);
        	g2d.draw(sumo1.ping_sensor);  
        }
        
        if(r2.ping_performed && r2.ping_detected)
        {
        	g2d.setPaint(new Color(200, 0, 0));
        	g2d.drawString("DETECTED!", 650, 30);
        	g2d.draw(sumo2.ping_sensor); 
        	ping_counter2++;
        }
        else if(r2.ping_performed)
        {
        	g2d.setPaint(new Color(0, 200, 000));
        	g2d.drawString("PING...", 650, 30);
        	g2d.draw(sumo2.ping_sensor); 
        }
	}

	private void drawCollisionDetectionLines(boolean b) 
    {
    	if(!b)
    		return;
    	
    	g2d.setPaint(new Color(200, 0, 0));              
        
        g2d.drawLine((int)sumo1.L_weel_point.x, (int)sumo1.L_weel_point.y, (int)sumo1.R_weel_point.x, (int)sumo1.R_weel_point.y);
	
		g2d.drawLine(
				(int)Algebra.translate_point(sumo1.center_point, sumo1.dir_robot, 50).x, 
				(int)Algebra.translate_point(sumo1.center_point, sumo1.dir_robot, 50).y, 
				(int)Algebra.translate_point(sumo1.center_point, sumo1.dir_robot, -50).x, 
				(int)Algebra.translate_point(sumo1.center_point, sumo1.dir_robot, -50).y);
		
		g2d.drawLine((int)sumo1.sensor_FL.x, (int)sumo1.sensor_FL.y, (int)sumo1.sensor_BR.x, (int)sumo1.sensor_BR.y);
		g2d.drawLine((int)sumo1.sensor_FR.x, (int)sumo1.sensor_FR.y, (int)sumo1.sensor_BL.x, (int)sumo1.sensor_BL.y);
        
        g2d.drawLine((int)sumo2.L_weel_point.x, (int)sumo2.L_weel_point.y, (int)sumo2.R_weel_point.x, (int)sumo2.R_weel_point.y);
    	
		g2d.drawLine(
				(int)Algebra.translate_point(sumo2.center_point, sumo2.dir_robot, 50).x, 
				(int)Algebra.translate_point(sumo2.center_point, sumo2.dir_robot, 50).y, 
				(int)Algebra.translate_point(sumo2.center_point, sumo2.dir_robot, -50).x, 
				(int)Algebra.translate_point(sumo2.center_point, sumo2.dir_robot, -50).y);
		
		g2d.drawLine((int)sumo2.sensor_FL.x, (int)sumo2.sensor_FL.y, (int)sumo2.sensor_BR.x, (int)sumo2.sensor_BR.y);
		g2d.drawLine((int)sumo2.sensor_FR.x, (int)sumo2.sensor_FR.y, (int)sumo2.sensor_BL.x, (int)sumo2.sensor_BL.y);
	}

	private void drawSumo2(boolean b) 
    {
    	if(!b)
    		return;
    	
        g2d.setPaint(new Color(200, 200, 0));
        
        AffineTransform transform_sumo2 = new AffineTransform();
        transform_sumo2.rotate(sumo2.robot_rotation_angle, sumo2.center_point.x, sumo2.center_point.y); 
        transform_sumo2.translate(sumo2.center_point.x - sumo2.initial_center_point.x, sumo2.center_point.y - sumo2.initial_center_point.y); 
        sumo_shape2 = transform_sumo2.createTransformedShape(sumo2);
        
        g2d.fill(sumo_shape2);  }

	private void drawSumo1(boolean b) 
	{
    	if(!b)
    		return;
    	
        g2d.setPaint(new Color(0, 0, 200));
        
        AffineTransform transform_sumo1 = new AffineTransform();
        transform_sumo1.rotate(sumo1.robot_rotation_angle, sumo1.center_point.x, sumo1.center_point.y); 
        transform_sumo1.translate(sumo1.center_point.x - sumo1.initial_center_point.x, sumo1.center_point.y - sumo1.initial_center_point.y); 
        sumo_shape1 = transform_sumo1.createTransformedShape(sumo1);
        
        g2d.fill(sumo_shape1);   
	}

	private void drawRing(boolean b) 
    {
    	if(!b)
    		return;
    	
    	g2d.setPaint(new Color(255, 255, 255));
    	g2d.fillOval(0, 0, ring.diameter_unsafe, ring.diameter_unsafe);
    	
    	g2d.setPaint(new Color(50, 50, 50));
    	g2d.drawOval(0, 0, ring.diameter_unsafe, ring.diameter_unsafe);
    	g2d.fillOval(12, 12, ring.diameter_safe, ring.diameter_safe);
        
    	g2d.setPaint(new Color(255, 255, 255));
    	g2d.fillOval((int)ring.center.x-5, (int)ring.center.x-5, 10, 10);
    	
    	g2d.setPaint(new Color(255, 255, 255));
    	g2d.drawString("Ring Center: {X=" + String.format("%.3f", ring.center.x) + ",Y=" + String.format("%.3f", ring.center.y) + "}", (int)ring.center.x, (int)ring.center.y - 10);
	}

	private void drawSumoPosData(boolean b) 
    {
    	if(!b)
    		return;
    	
    	g2d.setPaint(new Color(0, 0, 0));
    	
    	double degrees_robot1 = sumo1.dir_robot.angular_distance_to(new Vector2D(0,1));
    	degrees_robot1 = (double)Math.round(degrees_robot1 * 1000d) / 1000d;
    	
    	double degrees_servo1 = sumo1.dir_servo.angular_distance_to(sumo1.dir_robot);
    	degrees_servo1 = (double)Math.round(degrees_servo1 * 1000d) / 1000d;
    	
    	double degrees_robot2 = sumo2.dir_robot.angular_distance_to(new Vector2D(0,1));
    	degrees_robot2 = (double)Math.round(degrees_robot2 * 1000d) / 1000d;
    	
    	double degrees_servo2 = sumo2.dir_servo.angular_distance_to(sumo2.dir_robot);
    	degrees_servo2 = (double)Math.round(degrees_servo2 * 1000d) / 1000d;
    	
    	String sumo1_pos = "R1 pos: {X=" + String.format("%.3f", sumo1.center_point.x) + ",Y=" + String.format("%.3f", sumo1.center_point.y) + "} ";	
    	String sumo1_rot = 		 "Angle robot (degrees): " + Double.toString(degrees_robot1);	
    	String sumo1_servo_rot = "Angle servo (degrees): " + Double.toString(degrees_servo1);	
    	
    	String sumo2_pos = "R2 pos: {X=" + String.format("%.3f", sumo2.center_point.x) + ",Y=" + String.format("%.3f", sumo2.center_point.y) + "} ";	
    	String sumo2_rot = 		 "Angle robot (degrees): " + Double.toString(degrees_robot2);	
    	String sumo2_servo_rot = "Angle servo (degrees): " + Double.toString(degrees_servo2);	
    	    	
    	g2d.drawString(sumo1_pos, 		5, 190 + 570);
    	g2d.drawString(sumo1_rot, 		5, 205 + 570);
    	g2d.drawString(sumo1_servo_rot, 5, 220 + 570);
    	
    	g2d.drawString(sumo2_pos, 		595, 190 + 570);
    	g2d.drawString(sumo2_rot, 		595, 205 + 570);
    	g2d.drawString(sumo2_servo_rot, 595, 220 + 570);
	}
}