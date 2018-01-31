package SumoObjects;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import SumoMath.Algebra;
import SumoMath.Point_2D;
import SumoMath.Vector2D;

@SuppressWarnings("serial")
public class SumoRobot extends Rectangle2D.Double 
{    	
    public final int SUMO_SIDE_LENGTH = 100;
    public final int PING_RANGE = 400;
    
    public Point_2D initial_center_point;

	public Point_2D center_point = new Point_2D();
	public Point_2D L_weel_point;
	public Point_2D R_weel_point;
	
	public Point_2D sensor_BL;
	public Point_2D sensor_BR;
	public Point_2D sensor_FL;
	public Point_2D sensor_FR;
	
	public Vector2D dir_robot;
	public Vector2D dir_servo;
		
	public double robot_rotation_angle;
	public double servo_rotation_angle;
	public double velocity = 0;
	public double acceleration = 0;

	private Point_2D ping_endpoint = new Point_2D();
	private Point_2D ping_startpoint = new Point_2D();
	public Line2D.Double ping_sensor = new Line2D.Double();
		   
	public SumoRobot() 
    { 
    }
	    
    public SumoRobot(int center_pos_x, int center_pos_y, int direction) 
    { 
		set_placement(center_pos_x, center_pos_y);
		set_drive_direction(0, direction);
    }
    
    public void sweep_sensor(double angle, int direction)
    {
    	if(direction == 0)
    		return;
    	
    	dir_servo.rotate(angle * direction);
    	servo_rotation_angle = servo_rotation_angle + angle * direction;
    	
		ping_endpoint = Algebra.translate_point(center_point, dir_servo, PING_RANGE);
		ping_sensor.setLine(center_point.x, center_point.y, ping_endpoint.x, ping_endpoint.y);
    }
    
	public double ping(SumoRobot opponent) 
	{
		if(opponent == null)
			return 0;
		
		Line2D.Double[] intersectionLines = new Line2D.Double[4];
		
		intersectionLines[0] = new Line2D.Double(opponent.L_weel_point.x, opponent.L_weel_point.y, opponent.R_weel_point.x, opponent.R_weel_point.y);
		
		intersectionLines[1] = new Line2D.Double(
				Algebra.translate_point(opponent.center_point, opponent.dir_robot, 50).x, 
				Algebra.translate_point(opponent.center_point, opponent.dir_robot, 50).y, 
				Algebra.translate_point(opponent.center_point, opponent.dir_robot, -50).x, 
				Algebra.translate_point(opponent.center_point, opponent.dir_robot, -50).y);
		
		intersectionLines[2] = new Line2D.Double(opponent.sensor_FL.x, opponent.sensor_FL.y, opponent.sensor_BR.x, opponent.sensor_BR.y);
		intersectionLines[3] = new Line2D.Double(opponent.sensor_FR.x, opponent.sensor_FR.y, opponent.sensor_BL.x, opponent.sensor_BL.y);

		for(Line2D.Double l : intersectionLines)
		{
			if(ping_sensor.intersectsLine(l))
				return center_point.get_distance(opponent.center_point);
		}

		return 0;
	}
    
    public boolean check_line_sensor(Point_2D sensor)
    {
    	Point_2D center_of_ring = new Point_2D(385, 385);
    	
    	return sensor.exceeds_distance(center_of_ring, 770/2);
    }
    
    public void translate_in_direction(int direction) 
    { 
    	int step = 1;
    	
    	center_point = Algebra.translate_point(center_point, dir_robot, step * direction);
    	L_weel_point = Algebra.translate_point(L_weel_point, dir_robot, step * direction);
		R_weel_point = Algebra.translate_point(R_weel_point, dir_robot, step * direction);
		
		sensor_BL = Algebra.translate_point(sensor_BL, dir_robot, step * direction);
    	sensor_BR = Algebra.translate_point(sensor_BR, dir_robot, step * direction);
    	sensor_FL = Algebra.translate_point(sensor_FL, dir_robot, step * direction);
    	sensor_FR = Algebra.translate_point(sensor_FR, dir_robot, step * direction);
    	
    	ping_endpoint = Algebra.translate_point(ping_endpoint, dir_robot, step * direction);
    	ping_sensor.setLine(center_point.x, center_point.y, ping_endpoint.x, ping_endpoint.y);
    }
    
    public void rotate_around_center(int direction) 
    { 
		double hyp;
		double dx;
		double dy;
		double dist;
				
		robot_rotation_angle = robot_rotation_angle + Algebra.base_angle_in_rad * direction;
		servo_rotation_angle = servo_rotation_angle + Algebra.base_angle_in_rad * direction;
		
		dir_robot.rotate(Algebra.base_angle_in_rad * direction);
		dir_servo.rotate(Algebra.base_angle_in_rad * direction);
		
		hyp = SUMO_SIDE_LENGTH/2;
		dx = Math.cos(Algebra.base_angle_in_rad * direction) * hyp;
		dy = Math.tan(Algebra.base_angle_in_rad * direction) * dx;
		dx = hyp - dx;
		dist = Math.sqrt(dx * dx + dy * dy)* direction;
		L_weel_point = Algebra.translate_point(L_weel_point, dir_robot, dist);
		
		hyp = SUMO_SIDE_LENGTH/2;
		dx = Math.cos(Algebra.base_angle_in_rad * direction) * hyp;
		dy = Math.tan(Algebra.base_angle_in_rad * direction) * dx;
		dx = (hyp - dx);
		dist = Math.sqrt(dx * dx + dy * dy)* direction;
		R_weel_point = Algebra.translate_point(R_weel_point, dir_robot, -dist );
		
		sensor_BR = Algebra.translate_point(R_weel_point, dir_robot, -SUMO_SIDE_LENGTH/2);
		sensor_BL = Algebra.translate_point(L_weel_point, dir_robot, -SUMO_SIDE_LENGTH/2);
		
		sensor_FR = Algebra.translate_point(R_weel_point, dir_robot, SUMO_SIDE_LENGTH/2);
		sensor_FL = Algebra.translate_point(L_weel_point, dir_robot, SUMO_SIDE_LENGTH/2);
		
		Vector2D ping_vec = new Vector2D(ping_endpoint.x-center_point.x, ping_endpoint.y-center_point.y); 
		ping_vec = ping_vec.normalize();
		ping_vec.rotate(Algebra.base_angle_in_rad * direction);
		ping_endpoint = Algebra.translate_point(center_point, ping_vec, PING_RANGE);
		ping_sensor.setLine(center_point.x, center_point.y, ping_endpoint.x, ping_endpoint.y);
    }
    
    public void rotate_around_R(int direction, int move_dir)
    { 
		double hyp;
		double dx;
		double dy;
		double dist;
		
    	dir_robot.rotate(Algebra.base_angle_in_rad * direction);
    	dir_servo.rotate(Algebra.base_angle_in_rad * direction);
    	
		robot_rotation_angle = robot_rotation_angle + Algebra.base_angle_in_rad * direction;
		servo_rotation_angle = servo_rotation_angle + Algebra.base_angle_in_rad * direction;		
		
		//Calculate new L_WHEEL_point
		hyp = SUMO_SIDE_LENGTH;       				
		dx = Math.cos(Algebra.base_angle_in_rad * direction) * hyp;
		dy = Math.tan(Algebra.base_angle_in_rad * direction) * dx;
		
		dx = hyp - dx;
		
		dist = Math.sqrt(dx * dx + dy * dy);
		L_weel_point = Algebra.translate_point(L_weel_point, dir_robot, dist  * move_dir);
		
		//Calculate new CENTER_POINT
		hyp = SUMO_SIDE_LENGTH/2;       				
		dx = Math.cos(Algebra.base_angle_in_rad) * hyp;
		dy = Math.tan(Algebra.base_angle_in_rad) * dx;
		
		dx = hyp - dx;
		
		dist = Math.sqrt(dx * dx + dy * dy);
		
		center_point = Algebra.translate_point(center_point, dir_robot, dist  * move_dir);
		sensor_BR = Algebra.translate_point(R_weel_point, dir_robot, -SUMO_SIDE_LENGTH/2);
		sensor_BL = Algebra.translate_point(L_weel_point, dir_robot, -SUMO_SIDE_LENGTH/2);
		sensor_FR = Algebra.translate_point(R_weel_point, dir_robot, SUMO_SIDE_LENGTH/2);
		sensor_FL = Algebra.translate_point(L_weel_point, dir_robot, SUMO_SIDE_LENGTH/2);
    }
    
    public void rotate_around_L(int rotation_dir, int move_dir) 
    { 
		double hyp;
		double dx;
		double dy;
		double dist;
		
    	dir_robot.rotate(Algebra.base_angle_in_rad * rotation_dir);
    	dir_servo.rotate(Algebra.base_angle_in_rad * rotation_dir);
    	
		robot_rotation_angle = robot_rotation_angle + Algebra.base_angle_in_rad * rotation_dir;
		servo_rotation_angle = servo_rotation_angle + Algebra.base_angle_in_rad * rotation_dir;
		
		//Calculate new L_WHEEL_point
		hyp = SUMO_SIDE_LENGTH;       				
		dx = Math.cos(Algebra.base_angle_in_rad * rotation_dir) * hyp ;
		dy = Math.tan(Algebra.base_angle_in_rad * rotation_dir) * dx;
		
		dx = hyp - dx;
		
		dist = Math.sqrt(dx * dx + dy * dy);
		R_weel_point = Algebra.translate_point(R_weel_point, dir_robot, dist * move_dir);
		
		//Calculate new CENTER_POINT
		hyp = SUMO_SIDE_LENGTH/2;       				
		dx = Math.cos(Algebra.base_angle_in_rad * rotation_dir) * hyp;
		dy = Math.tan(Algebra.base_angle_in_rad * rotation_dir) * dx;
		
		dx = hyp - dx;
		
		dist = Math.sqrt(dx * dx + dy * dy);
		center_point = Algebra.translate_point(center_point, dir_robot, dist * move_dir);
		
		sensor_BR = Algebra.translate_point(R_weel_point, dir_robot, -SUMO_SIDE_LENGTH/2);
		sensor_BL = Algebra.translate_point(L_weel_point, dir_robot, -SUMO_SIDE_LENGTH/2);
		
		sensor_FR = Algebra.translate_point(R_weel_point, dir_robot, SUMO_SIDE_LENGTH/2);
		sensor_FL = Algebra.translate_point(L_weel_point, dir_robot, SUMO_SIDE_LENGTH/2);
    }

	public void set_placement(int x, int y) 
	{	
		initial_center_point = new Point_2D(x, y);
		
		setRect(x - SUMO_SIDE_LENGTH/2, y - SUMO_SIDE_LENGTH/2, SUMO_SIDE_LENGTH, SUMO_SIDE_LENGTH);
		center_point = new Point_2D(x ,y);
	}
	
	public void set_drive_direction(double cos_x, double sin_y) 
	{	
		robot_rotation_angle = 0;
		dir_robot = new Vector2D(cos_x, sin_y);
		servo_rotation_angle = 0;
		dir_servo = new Vector2D(cos_x, sin_y);
						
		if(cos_x == 0 && sin_y == 1)
		{
			L_weel_point = new Point_2D(center_point.x + SUMO_SIDE_LENGTH/2, center_point.y);
	    	R_weel_point = new Point_2D(center_point.x - SUMO_SIDE_LENGTH/2, center_point.y); 
	    	
	    	sensor_BL = new Point_2D(center_point.x + SUMO_SIDE_LENGTH/2, center_point.y - SUMO_SIDE_LENGTH/2);
	    	sensor_BR = new Point_2D(center_point.x - SUMO_SIDE_LENGTH/2, center_point.y - SUMO_SIDE_LENGTH/2);
	    	sensor_FL = new Point_2D(center_point.x + SUMO_SIDE_LENGTH/2, center_point.y + SUMO_SIDE_LENGTH/2);
	    	sensor_FR = new Point_2D(center_point.x - SUMO_SIDE_LENGTH/2, center_point.y + SUMO_SIDE_LENGTH/2);
	    }
		else
		{
			L_weel_point = new Point_2D(center_point.x - SUMO_SIDE_LENGTH/2, center_point.y);
	    	R_weel_point = new Point_2D(center_point.x + SUMO_SIDE_LENGTH/2, center_point.y); 
	    	
	    	sensor_BL = new Point_2D(center_point.x - SUMO_SIDE_LENGTH/2, center_point.y + SUMO_SIDE_LENGTH/2);
	    	sensor_BR = new Point_2D(center_point.x + SUMO_SIDE_LENGTH/2, center_point.y + SUMO_SIDE_LENGTH/2);
	    	sensor_FL = new Point_2D(center_point.x - SUMO_SIDE_LENGTH/2, center_point.y - SUMO_SIDE_LENGTH/2);
	    	sensor_FR = new Point_2D(center_point.x + SUMO_SIDE_LENGTH/2, center_point.y - SUMO_SIDE_LENGTH/2);
		}
		
		ping_endpoint = new Point_2D(center_point.x, center_point.y);
		ping_startpoint = new Point_2D(center_point.x, center_point.y);
		ping_endpoint = Algebra.translate_point(center_point, dir_robot, PING_RANGE);
		ping_sensor = new Line2D.Double(ping_startpoint.x, ping_startpoint.y, ping_endpoint.x, ping_endpoint.y);
	}
}