import java.util.ArrayList;
import java.util.Random;

import SumoMath.Point_2D;
import SumoObjects.SumoRobot;

public class SumoAI 
{	
	private int ai_id = 0;

	public SumoRobot sumo;
	private SumoRobot opponent;
	
	private boolean line_sensor_event = SumoLineEvent.LINE_NOT_DETECTED;
	private boolean line_sensors_status[] = new boolean[4];
	private Point_2D line_sensors[] = new Point_2D[4];
	
	private ArrayList<SumoMovement> next_movement = new ArrayList<SumoMovement>();
	
	private Thread ping_sensor_control = new Thread(new SensorManeuver());
	public double ping_sensor_distance = 0;
	private int servo_direction = 1;
	public boolean ping_performed = false;
	public boolean ping_detected = false;
	
	private double detection_memory = 0;
	
	Random rand = new Random();
	
	private SumoGraphics graphics;
	
	private final int FORWARD = 1;
	private final int REVERSE = 2;
	private final int ROTATE_L = 3;
	private final int ROTATE_R = 4;
	
	private int action_counter = 0;
		
	public void init(int id, int center_pos_x, int center_pos_y, int direction, SumoGraphics ref) 
	{
		ai_id = id;
		graphics = ref;
		sumo = new SumoRobot(center_pos_x, center_pos_y, direction);
		
		line_sensors_status[0] = false;//FL
		line_sensors_status[1] = false;//FR
		line_sensors_status[2] = false;//BL
		line_sensors_status[3] = false;//BR
				
		ping_sensor_control.start();
	}
	
	public void iterate() 
	{
		sense();
		plan();
		act();
	}

	private void sense() 
	{
		line_sensors[0] = sumo.sensor_FL;
		line_sensors[1] = sumo.sensor_FR;
		line_sensors[2] = sumo.sensor_BL;
		line_sensors[3] = sumo.sensor_BR;
		
		int sensor_index = 0;
		
		for(Point_2D sensor : line_sensors)
		{
			if(sumo.check_line_sensor(sensor))
				line_sensors_status[sensor_index] = true;
			else
				line_sensors_status[sensor_index] = false;
			
			sensor_index++;
		}
					
		for(boolean indication : line_sensors_status)
		{
			if(indication == true)
			{
				line_sensor_event = SumoLineEvent.LINE_DETECTED;
				return;	
			}
		}
			
		line_sensor_event = SumoLineEvent.LINE_NOT_DETECTED;
	}
	
	private void plan() 
	{		
		double ping_angle = sumo.dir_servo.angular_distance_to(sumo.dir_robot);

		if(line_sensor_event == SumoLineEvent.LINE_NOT_DETECTED)
		{
			if(ping_sensor_distance == 0)
			{
				if(servo_direction == 0)
					servo_direction = 1;
				
				//if we have an earlier known location
				if(any_prev_known_positions())
				{
					look_at_prev_pos();
					return;
				}
				
				next_movement.add(new SumoMovement(FORWARD, 1));
			}
			else
			{
				if(action_counter < 0)
				{
					servo_direction = 1;
					next_movement.clear();
				
					if(ping_sensor_distance > 0)
					{				
						add_pos_to_mem(ping_angle);
					}
					servo_direction = 0;
					
					if(opponent_is_at_front(ping_angle))
					{
						next_movement.add(new SumoMovement(FORWARD, 1));	
					}
					else if(opponent_is_at_right(ping_angle) )
					{
						next_movement.add(new SumoMovement(ROTATE_L, 50));
						action_counter = 50;
					}
					else if (opponent_is_at_left(ping_angle))
					{
						next_movement.add(new SumoMovement(ROTATE_R, 50));
						action_counter = 50;
					}
				}
				else
				{
					action_counter--;
				}
			}
		}
		else if(line_sensor_event == SumoLineEvent.LINE_DETECTED && ping_sensor_distance == 0)
		{	
			next_movement.clear();
			
			if(line_sensors_status[0])
			{
				next_movement.add(new SumoMovement(REVERSE, 10));
				next_movement.add(new SumoMovement(ROTATE_R, 500));
			}
				
			else if(line_sensors_status[1])
			{
				next_movement.add(new SumoMovement(REVERSE, 50));
				next_movement.add(new SumoMovement(ROTATE_L, 500));
			}
			else if(line_sensors_status[0] && line_sensors_status[1])
			{
				next_movement.add(new SumoMovement(REVERSE, 10));
				next_movement.add(new SumoMovement(ROTATE_L, 100));
			}
			else if(line_sensors_status[2])
			{
				next_movement.add(new SumoMovement(ROTATE_L, 500));
			}
			else if(line_sensors_status[3])
			{
				next_movement.add(new SumoMovement(ROTATE_L, 500));
			}
			else if(line_sensors_status[2] && line_sensors_status[3])
			{
				next_movement.add(new SumoMovement(REVERSE, 10));
				next_movement.add(new SumoMovement(ROTATE_L, 100));
			}
			else
			{
				next_movement.add(new SumoMovement(FORWARD, 1));
			}
			
		}
		
		else
		{

		}
		
	}
	
	private void look_at_prev_pos() 
	{
		if(detection_memory_empty())
			return;
		
		double angle = 0;
		
		angle = detection_memory; 
		
		if(angle > 0)
		{
			next_movement.add(new SumoMovement(ROTATE_L, 50));
			remove_pos_from_mem();
		}
		else
		{
			next_movement.add(new SumoMovement(ROTATE_R, 50));
			remove_pos_from_mem();
		}
	}

	private boolean opponent_is_at_left(double ping_angle) 
	{
		if(ping_angle <= -20)
			return true;
		
		return false;
	}

	private boolean opponent_is_at_right(double ping_angle) 
	{
		if(ping_angle >= 20)
		return true;
		
		return false;
	}

	private boolean opponent_is_at_front(double ping_angle) 
	{
		if(ping_angle < 20 && ping_angle > -20)
			return true;
		
		return false;
	}

	private void add_pos_to_mem(double angle) 
	{
		detection_memory = angle;
	}
	
	private void remove_pos_from_mem() 
	{
		detection_memory = 0;
	}

	private boolean any_prev_known_positions() 
	{
		if(detection_memory_empty()){return false;}
		else{return true;}
	}

	private boolean detection_memory_empty() 
	{
		if(detection_memory == 0)
			return true;
		else 
			return false;
	}

	private void act() 
	{
		if(!next_movement.isEmpty())
		{
			SumoMovement action = next_movement.get(0);
			
			switch(action.type)
			{
			case FORWARD:
				sumo.translate_in_direction(1);
				break;
				
			case REVERSE:
				sumo.translate_in_direction(-1);
				break;
				
			case ROTATE_L:
				sumo.rotate_around_center(-1);
				break;	
				
			case ROTATE_R:
				sumo.rotate_around_center(1);
				break;	
			}
			
			action.iterations--;
			
			if(action.iterations < 1)
				next_movement.remove(0);
		}
	}
	
	public SumoRobot getSumo()
	{
		return sumo;
	}

	private final class SumoLineEvent
	{
		public static final boolean LINE_DETECTED = true;
		public static final boolean LINE_NOT_DETECTED = false;
	}
	
	private final class SumoMovement
	{
		public int iterations = 0;
		public int type = 0;
		
		public SumoMovement(int type, int i)
		{
			iterations = i;
			this.type = type;
		}
	}
	
	private class SensorManeuver extends Thread
	{
		double angle = 0.0;
				
		@Override
		public void run() 
		{
			
			while(true)
			{
				angle = sumo.dir_servo.angular_distance_to(sumo.dir_robot);
				
				if(angle > 90)
					servo_direction = 1;
				
				else if(angle < -90)
					servo_direction = -1;
				
				sumo.sweep_sensor(0.08, servo_direction);	
				
				if(opponent == null)
					opponent = graphics.get_sumo(ai_id);
				
				ping_sensor_distance = sumo.ping(opponent);
				
				if(ping_sensor_distance > 0)
				{
					ping_detected = true;
					add_pos_to_mem(angle);
				}
				
				ping_performed = true;
				
				try {Thread.sleep(25);} 
				catch (InterruptedException e) {e.printStackTrace();}
				
				ping_performed = false;
				ping_detected = false;
			}
		}


	}
}
