package ga_package;


import java.util.Comparator;

public class ComparatorByFitness implements Comparator<Offspring>
{
	@Override
	public int compare(Offspring a, Offspring b) 
	{
		 if(a.fitness > b.fitness){return -1;}
		 else{return 1;}
	}
}
