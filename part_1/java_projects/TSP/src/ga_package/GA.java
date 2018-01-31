package ga_package;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javax.swing.JFrame;

import area_reader_package.AreaReader;

public class GA 
{
	private ArrayList<Offspring> next_generation = new ArrayList<Offspring>();
	private ArrayList<Area> all_areas = null;
	private Area start_area = null;
	private int num_of_loops = 0;
	
	private JFrame ga_display = null;
	private final Traveldisplay draw_panel = new Traveldisplay(this);
	
	public static final int NUM_OF_OFFSPRINGS = 5000;
	public static final int NUM_OF_SURVIVING_OFFSPRINGS = NUM_OF_OFFSPRINGS / 2;
	public static final int NUM_OF_DEAD_OFFSPRINGS = NUM_OF_OFFSPRINGS - NUM_OF_SURVIVING_OFFSPRINGS;
	public static final int STOP_CRITERIA = 5000;
	public static final long EVOLUTION_DELAY_MS = 0;
	public static final double MUTATION_RATE = 0.9;
	
	public static int GENE_COUNT = 52;
	
	public GA()
	{
		readAreas();		
		initPopulation();
		initDisplay();
	
		while(num_of_loops != STOP_CRITERIA)
		{
			evolve();
			num_of_loops++;
			visualizeEvolution();
		}
		
		visualizeEvolution();
		printResult();
	}

	private void visualizeEvolution() 
	{
		try 
		{
			Thread.sleep(EVOLUTION_DELAY_MS);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	
		ga_display.setTitle("Distance (fitness): " + 
		        Float.toString(next_generation.get(0).distance) + 
		        ", Generation: " + Integer.toString(num_of_loops) + "/" + 
		        STOP_CRITERIA + 
		        ", Population/generation: " + 
		        NUM_OF_OFFSPRINGS + 
		        ", Mutation rate: " + Double.toString(MUTATION_RATE * 100.0) + "%, Selection: Random (from top " + 
		        Integer.toString(NUM_OF_OFFSPRINGS - NUM_OF_SURVIVING_OFFSPRINGS) + 
		        " descendants)" );
				
				ga_display.repaint();	
	}

	private void initDisplay() 
	{
		ga_display = new JFrame();
        ga_display.setSize(1200, 750);
        ga_display.setLocationRelativeTo(null);
        ga_display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ga_display.setVisible(true);
        
		ga_display.add(draw_panel);
	}

	private void readAreas() 
	{
		AreaReader ar = new AreaReader();
		
		try 
		{ 
			all_areas = new ArrayList<Area>(ar.readAreas());
			GENE_COUNT = all_areas.size();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		start_area = new Area(all_areas.get(0));
	}
	
	private void printResult() 
	{
		Collections.sort(next_generation, new ComparatorByFitness());
        
		System.out.println("TSMP solution: ");
		
		int new_line_count = 0;
		
		System.out.println("");
		System.out.print("(START) ");
		
        for(Area a: next_generation.get(0).dna)
        {
        	if (new_line_count == 9)
        	{
        		new_line_count = 0;
        		System.out.println(a.id + " -> ");
        		new_line_count = 0;
        	}
        	else
        	{
        		System.out.print(a.id + " -> ");
        		new_line_count += 1;
        	}
        	
        	
        }
        
        System.out.print(Integer.toString(next_generation.get(0).dna[0].id) + "(HOME AGAIN)");
	}

	/**The start- and end area are not included!*/
	private void initPopulation() 
	{
		ArrayList<Area> curr_genetics = new ArrayList<Area>();
		Offspring curr_offspring = null;
		
		for(int child = 0; child < NUM_OF_OFFSPRINGS; child++)
		{
			ArrayList<Integer> random_unique_indexes = new ArrayList<Integer>();
			
			for(int i = 0; i < GENE_COUNT; i++)
			{
				random_unique_indexes.add(i);
			}
				
			//random genetics for next offspring
	        Collections.shuffle(random_unique_indexes);
	        
	        int rand_index;
	        
	        for (int k = 0; k < GENE_COUNT; k ++) 
	        {
	        	rand_index = random_unique_indexes.get(k);
	        	
	        	if(all_areas.get(rand_index).id == 1)
	        	{
	        		curr_genetics.add(0, all_areas.get(rand_index) );
	        	}
	        	else
	        	{
	        		curr_genetics.add(all_areas.get(rand_index));
	        	}	
	        }
	        
	        curr_offspring = new Offspring(curr_genetics);
	        curr_genetics.removeAll(curr_genetics);
	        next_generation.add(curr_offspring);
		}
		
		updateFitness();
		Collections.sort(next_generation, new ComparatorByFitness());
	}	
	
	private void evolve() 
	{		
		Random rand = new Random();
		
		killTheWeakest(NUM_OF_DEAD_OFFSPRINGS);
		
		for(int i = 0; i < NUM_OF_SURVIVING_OFFSPRINGS; i++)
		{
			mate(rand.nextInt(NUM_OF_SURVIVING_OFFSPRINGS), 
					rand.nextInt(NUM_OF_SURVIVING_OFFSPRINGS));
		}
		
		updateFitness();
		
		try
		{
			Collections.sort(next_generation, new ComparatorByFitness());
		}
		catch(IllegalArgumentException e){}
	}

	/**Remove the weakest. Only the strongest survives.**/
	private void killTheWeakest(int numOfDescendantsToRemove) 
	{
		while(numOfDescendantsToRemove != 0)
		{
			next_generation.remove(next_generation.size()-1);
			numOfDescendantsToRemove--;
		}
	}

	private void mate(int parent1_index, int parent2_index) 
	{
		Random rand = new Random();
		
		int cross_over_start_index = 0;
		int cross_over_length = 24;
		
		ArrayList<Area> parent1_dna = new ArrayList<Area>(Arrays.asList(next_generation.get(parent1_index).dna));
		ArrayList<Area> parent2_dna = new ArrayList<Area>(Arrays.asList(next_generation.get(parent2_index).dna));

		cross_over_start_index = rand.nextInt(parent1_dna.size()/2) + 1;
		
		//select the middle-section of parent1's genes, to become the genes parent1 pass on to it's descendant
		ArrayList<Area> resulting_dna = new ArrayList<Area>(parent1_dna.subList(cross_over_start_index, cross_over_start_index + cross_over_length));
		
		//remove start-area. it will be added again at index 0 when all is done. 
		//(remember! We need to start and stop in the same area)
		parent2_dna.remove(start_area);
		
		//exclude parent1's contribution of genes, in parent2's contribution of genes
		parent2_dna.removeAll(resulting_dna);
		
		//add parent2's genes after parent1's genes.
		for(Area a: parent2_dna)
		{
			resulting_dna.add(a);
		}
		
		//mutations				
		if(rand.nextDouble() <= MUTATION_RATE)
		{
			int rand_index1 = 0;
			int rand_index2 = 0;
		
			while(rand_index1 == rand_index2)
			{
				rand_index1 = rand.nextInt(resulting_dna.size());
				rand_index2 = rand.nextInt(resulting_dna.size());
			
				if (rand_index1 != rand_index2)
				{
					Collections.swap(resulting_dna, rand_index1, rand_index2);
				}
			}
		}
		
		//add start state
		resulting_dna.add(0, start_area);
		
		if(resulting_dna.size() == 53)
		{
			System.out.println("");
		}
		//create new descendant from cross-over
		Offspring descendant = new Offspring(resulting_dna);
		
		//add offspring to next generation
		next_generation.add(descendant);
	}
	
	public Offspring currBestSolution()
	{
		if(!next_generation.isEmpty())
		{
			return next_generation.get(0);
		}
		
		return null;
	}
	
	private void updateFitness()
	{
		float worstDist = next_generation.get(NUM_OF_OFFSPRINGS-1).distance;
		
		for(Offspring curr : next_generation)
		{
			if(curr.distance > worstDist)
			{
				worstDist = curr.distance;
			}
		}
		
		for(Offspring curr: next_generation)
		{
			curr.fitness = (worstDist - curr.distance)/worstDist; 
		}
	}
}
