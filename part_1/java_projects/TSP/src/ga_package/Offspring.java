package ga_package;

import java.util.ArrayList;

public class Offspring 
{
	public Area dna[] = new Area[52];
	public float fitness = 0;
	public float distance = 0;
	
	public Offspring(){}
	
	public Offspring(ArrayList<Area> listOfAreas)
	{
		setGenes(listOfAreas);
	}
	
	public void setGenes(ArrayList<Area> listOfAreas) 
	{
		int i = 0;
		
		for(Area a: listOfAreas)
		{
			dna[i] = new Area(a);
			i++;
		}
		
		calculateDist();
	}

	private void calculateDist() 
	{
		float x1 = 0;
		float x2 = 0; 
		float y1 = 0;
		float y2 = 0;
		
		distance = 0;
		
		for(int i = 0; i < 51; i ++)
		{
			x1 = dna[i].x;
			y1 = dna[i].y;
			
			x2 = dna[i+1].x;
			y2 = dna[i+1].y;
			
			distance = distance + (float) Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
		}
		
		x1 = dna[0].x;
		x2 = dna[0].x;
		
		y1 = dna[51].y;
		y2 = dna[51].y;
		
		distance = distance + (float) Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
}