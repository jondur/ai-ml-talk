package ga_package;


public class Area 
{
	public int id = 0; 
	public float x = 0;
	public float y = 0;
	
	public Area(){}
	
	public Area(Area a)
	{
		id = a.id;
		x = a.x;
		y = a.y;
	}
	
	@Override
    public boolean equals(Object obj)
    {  
	    if (obj instanceof Area) 
	    {
	    	Area a = (Area)obj;
	    	
	    	if(a.id == this.id)
	    	{
	    		return true;
	    	}
	    }
	    
	    return false;
    }
}
