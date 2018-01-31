package content_pkg;

public class MoveableObject
{
	public Vector position = null;
    public Vector direction = null;
    public Vector view_direction = null;
    public int speed = 5;
    public Animation animation = null;
    
    public MoveableObject(){
    	init(Animation.HUNT);
    }
    
    public MoveableObject(String type){
    	init(type);
    }
    
	private void init(String type){
		position = new Vector(0, 0);
		direction = new Vector(0,0);
		view_direction = new Vector(0,0);
		speed = 3;
		animation = new Animation(type);
	}
}