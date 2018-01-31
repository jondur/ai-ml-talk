package SumoMath;

public class Vector2D
{
	public double x;
	public double y;
		
	public Vector2D()
	{
		x = 0;
		y = 0;
	}
	
	public Vector2D(double arg_x, double arg_y)
	{
		x = arg_x;
		y = arg_y;
	}
	
    public Vector2D rotate(double radians)
    {
        double x_component = x * Math.cos(radians) - y * Math.sin(radians);
        double y_component = x * Math.sin(radians) + y * Math.cos(radians);
        
        x = x_component;
        y = y_component;
        
        return this;
    }
    
	public double get_length()
	{
		return Math.sqrt(x*x + y*y);	
	}
    
	public Vector2D normalize()
	{
		return new Vector2D(x/get_length(), y/get_length());
	}
	
	public double radians_to_degrees(double radians)
	{
		return Math.toDegrees(radians);
	}
	
	static double normalizeDegrees(double degrees)
	{
	    degrees %= 360.0; 				// [0..360) if angle is positive, (-360..0] if negative
	    if (degrees > 180.0) 			// was positive
	        return degrees - 360.0; 	// was (180..360) => returning (-180..0)
	    if (degrees <= -180.0) 			// was negative
	        return degrees + 360.0; 	// was (-360..180] => returning (0..180]
	    return degrees; 				// (-180..180]
	}
	
	static double normalizeRadians(double radians)
	{
		radians %= 2*Math.PI; 				
	    if (radians > Math.PI) 			
	        return radians - 2*Math.PI; 	
	    if (radians <= -Math.PI) 			
	        return radians + 2*Math.PI; 	
	    return radians; 				
	}
	
	public double angular_distance_to(Vector2D vec)
	{
		Vector2D A = this;
		Vector2D B = vec;
		
		double alpha = 0;
		
		alpha = Math.acos( (A.x * B.x + A.y * B.y)/(A.get_length() * B.get_length()) );
		
		if(A.x*B.y - A.y*B.x < 0)
			alpha = -alpha;
		
		return radians_to_degrees(alpha);
	}
}
