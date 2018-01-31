package SumoMath;

public final class Algebra 
{	
	public static double base_angle_in_rad = 0.005;
    public static double deg_to_rad = Math.PI/180;
    
    private Algebra()
    {

    }
        		
	public static Point_2D translate_point(Point_2D initial_point, Vector2D direction, double step)
	{
		Point_2D newPoint = new Point_2D(0,0);
		
		newPoint.x = initial_point.x + direction.x * step;
		newPoint.y = initial_point.y + direction.y * step;
		return newPoint;	
	}
		
	public static double angle_between_vectors(Vector2D a, Vector2D b) 
	{
		double y_component = a.x * b.y - b.x * a.y;  
	    double x_component = a.x * b.x + a.y * b.y;

	    return Math.atan2(y_component, x_component);
	}
}
