package SumoObjects;
import SumoMath.Point_2D;

public class SumoRing 
{
	public Point_2D center = new Point_2D();
	public int diameter_safe;
	public int diameter_unsafe;
	
	public SumoRing(Point_2D c, int diameter)
	{
		diameter_unsafe = diameter;
		diameter_safe = diameter_unsafe - 20;
		center.x = c.x + diameter_unsafe/2;
		center.y = c.y + diameter_unsafe/2;
	}
}

