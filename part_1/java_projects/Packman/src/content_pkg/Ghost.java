package content_pkg;

import java.awt.Color;
import java.util.ArrayList;

public class Ghost extends MoveableObject
{
	public String PREV_ACTION;
	public Color COLOR;
    public ArrayList<Integer> CURR_PATH = new ArrayList<Integer>();
    public ArrayList<Integer> FINAL_PATH = new ArrayList<Integer>();
    	
    public Ghost() {
    	
    	super();
    	COLOR = new Color(255, 0, 0);
    }
    
	public Ghost(String type){
		
		super(type);
		
		switch(type)
		{
		case "HUNT":
			COLOR = new Color(255, 0, 0);
			break;
			
		case "INTECEPT":
			COLOR = new Color(0, 255, 0);
			break;
			
		case "RANDOM":
			COLOR = new Color(0, 0, 255);
			break;
			
		default:
			COLOR = new Color(100, 100,100);
		}
	}
}
