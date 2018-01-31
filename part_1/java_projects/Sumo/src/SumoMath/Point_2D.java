package SumoMath;
public class Point_2D 
{
	public double x;
	public double y;
	
	public Point_2D()
	{
		x = 0;
		y = 0;
	}
	
	public Point_2D(double arg_x, double arg_y)
	{
		x = arg_x;
		y = arg_y;
	}
	
	public boolean exceeds_distance(Point_2D other_point, double dist)
	{		
		if(get_distance(other_point) >= dist)
			return true;
		else
			return false;
	}
	
	public double get_distance(Point_2D other_point)
	{
		double a = Math.pow(this.x - other_point.x, 2);
		double b = Math.pow(this.y - other_point.y, 2);
		double c = Math.sqrt(a + b);
		
		return c;
	}
}
