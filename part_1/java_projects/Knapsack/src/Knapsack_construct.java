

import java.util.LinkedList;

/**Class creates and evaluates a tree-representation of the "Knapsack-problem".*/

public class Knapsack_construct {
	
	public static final int WEIGHT_LIMIT = 420;
	public static final int DFS = 0; 
	public static final int BFS = 1;
	
	/**List of available items, before any items has been placed in the knapsack.*/
	private LinkedList<PackableItem> allItems = new LinkedList<PackableItem>();
	
	/**A queue/stack for keeping track on expanded packing-actions (tree-nodes). If we are 
	 * running DFS, it acts like a stack. Else it acts like a queue.*/
	private LinkedList<ItemPackingAction> expanded_nodes = new LinkedList<ItemPackingAction>();
	
	/**List containing the best series of packing-actions.*/
	private LinkedList<ItemPackingAction> optimal_packing = new LinkedList<ItemPackingAction>(); 
	
	/**The final packing-action, that completes the most optimal sequence of packing-actions.*/
	private ItemPackingAction last_packing_action= new ItemPackingAction();
	
	/**Number of possible solutions to the problem (based on the number of items used).*/
	private int possible_solutions_counter = 1;
	
    /**Number of nodes created during a search.*/
    private int nodeCount = 1;
		
    /**Record of start-time in mSeconds.*/
    private long startTime = 0; 
    
    /**Record of stop-time in mSeconds.*/
    private long stopTime = 0;
    
    /**Record of the total elapsed time (start-stop) in mSeconds.*/
    private long elapsedTime = 0;
    
    /**Flag to indicate the search algorithm.*/
    private int algorithm = DFS;
    
    private int max_numof_expanded_nodes = 0;

	/**The constructor initializes the problem. @param input Algorithm to use (0 for DFS, or other for BFS).*/
	public Knapsack_construct(int selected_algorithm)
	{
		algorithm = selected_algorithm;
		
		initKnapsackItems();
		
		// Create the root-node.
		ItemPackingAction initial_solution = new ItemPackingAction();
		initial_solution.setParent(null);
		
		// The first solution in the tree, or the root, corresponds to the action of "not packing anything".
		initial_solution.setItem(allItems.get(0));
		initial_solution.setDepth(0);
				
		expanded_nodes.add(initial_solution);
		initSearch();
	}
	
	/**Runs the selected search algorithm and prints the result. @param initialState The root.*/
	private void initSearch() 
	{
		set_timer();
		
		System.out.println("");
		
		if(algorithm == 0){
			System.out.println("Starting a Depth First Search...");}
		else{
			System.out.println("Starting a Breadth First Search...");}
		
		performSearch(expanded_nodes.get(0));

		get_elapsed_time();
		
		printResults();
		

	}
	
	private void printResults()
	{
		System.out.println("The search completed in: " + elapsedTime + " ms.");
		System.out.println("Number of tree-nodes created: " + nodeCount);
		System.out.println("Possible solutions: " + possible_solutions_counter);
		System.out.println("");
		System.out.println("***********************");
		System.out.println("** The best solution **");
		System.out.println("***********************");
		System.out.println("");
		System.out.println("Benefit achieved: " + last_packing_action.accumulatedBenefit());
		System.out.println("Knapsack weight: " + last_packing_action.accumulatedWeight());
		System.out.println("Maximum number of expanded nodes at a given time:" + max_numof_expanded_nodes);	
		System.out.println("");
		System.out.println("Actions (item selection) in order:" );
		System.out.println("");
		
		collectFinalSolutíon(last_packing_action);
		
		int pick = 1;
		
		for(ItemPackingAction i:optimal_packing){
			System.out.println("Pick " + pick + 
					" was item [" + i.item().id() + "], " + 
					"with benefit = " + i.item().benefit() + 
					" and  weight = " + i.item().weight());
			
			pick ++;
		}
	}
	
	private void set_timer()
	{
		startTime = System.currentTimeMillis();
	}
	
	private long get_elapsed_time()
	{
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		return elapsedTime;
	}

	/**Gather all the items, as the tree is traversed backwards from the solution state, to the root. 
	 * @param n Reference to the suggested solution node.*/
	private void collectFinalSolutíon(ItemPackingAction n) 
	{
		if(n.parent() != null){
			if(n.item().id() != 0){
				optimal_packing.add(0, n);
			}
			
			collectFinalSolutíon(n.parent());
		}
	}
	/**Creates a tree representation of the knapsack problem, and uses the selected search algorithm
	 * in order to evaluate the optimal solution.
	 * @param current_action Current state that is being explored.*/
	private void performSearch(ItemPackingAction current_action) 
	{
		int newBenefit = 0;
		int newWeight = 0;
		
		int currBenefit = 0;
		int currWeight = 0;
		
		int prevBenefit = 0;
		int prevWeight = 0;
		
		// We continue as long as there are unexplored nodes in the action-tree.
		while(!expanded_nodes.isEmpty())
		{
			// We keep track of the highest number of expanded nodes in the queue/stack.
			if (expanded_nodes.size() > max_numof_expanded_nodes){
				max_numof_expanded_nodes = expanded_nodes.size();
			}
			
			// We notice that we have found a new possible solution to the problem, but we don't know if it's optimal.
			if (current_action.depth() == allItems.size()-1){
				possible_solutions_counter++;
			}
			
			// Pop next node from stack (explore node).
			current_action = expanded_nodes.get(0);
			expanded_nodes.remove(0);
			
			// If this node isn't the root-node, we update the accumulated benefit and weight for this action,
			// based on all the previous actions that lead to this action.
			
			if(current_action.parent() != null ){
				currBenefit = current_action.item().benefit();
				currWeight = current_action.item().weight();
				prevBenefit = current_action.parent().accumulatedBenefit();
				prevWeight = current_action.parent().accumulatedWeight();
				newBenefit = currBenefit + prevBenefit;
				newWeight = currWeight + prevWeight;
				
				current_action.setAccumulatedBenefit(newBenefit);
				current_action.setAccumulatedWeight(newWeight);
				
				if(newBenefit > last_packing_action.accumulatedBenefit() && newWeight <= WEIGHT_LIMIT){
					last_packing_action = current_action;
				}
			}
			
			// Make sure that we don't go deeper into the tree, then what's possible.
			
			if(current_action.depth() < allItems.size() - 1)
			{					
				// For each new explored node, we create two new ones. We keep track of the count here.
				nodeCount = nodeCount + 2;
				
				// Add new solution, corresponding to putting the next item in the sack.
				ItemPackingAction putItemInSack = new ItemPackingAction(current_action);
				putItemInSack.setItem(allItems.get(current_action.depth() + 1 ));
				putItemInSack.setParent(current_action);
				putItemInSack.setDepth(current_action.depth() + 1);
				
				// Add new solution, corresponding to not putting the next item in the sack.
				ItemPackingAction dontPutItemInSack = new ItemPackingAction(current_action);
				dontPutItemInSack.setParent(current_action);
				dontPutItemInSack.setItem(allItems.get(0));
				dontPutItemInSack.setDepth(current_action.depth() + 1);
				
				// We expand the tree here (in parallel to our actual search).
				
				if(algorithm == 0){
					// Push new nodes
					expanded_nodes.add(0, putItemInSack);
					expanded_nodes.add(1, dontPutItemInSack);
				}
				else{
					// Queue new nodes
					expanded_nodes.add(putItemInSack);
					expanded_nodes.add(dontPutItemInSack);
				}
			}
		}
	}
	
	/**Initiate all knapsack items. */
	private void initKnapsackItems()
	{
		System.out.println("Initializing items...");
		
		// An item initialized with zeroes is considered as doing nothing.
		// This is our root-solution, but not our final, or optimal solution
		allItems.add(new PackableItem(0, 0, 0));
		
		allItems.add(new PackableItem(1, 20, 15));
		allItems.add(new PackableItem(2, 40, 32));
		allItems.add(new PackableItem(3, 50, 60));
		allItems.add(new PackableItem(4, 36, 80));
		allItems.add(new PackableItem(5, 25, 43));
		allItems.add(new PackableItem(6, 64, 120));
		allItems.add(new PackableItem(7, 54, 77));
		allItems.add(new PackableItem(8, 46,  6));
		allItems.add(new PackableItem(9, 46, 93));
		allItems.add(new PackableItem(10 ,28 ,35));
		allItems.add(new PackableItem(11, 20, 15));
		allItems.add(new PackableItem(12, 40, 32));
		allItems.add(new PackableItem(13, 50, 60));
		allItems.add(new PackableItem(14, 36, 80));
		allItems.add(new PackableItem(15, 25, 43));
		allItems.add(new PackableItem(16, 64, 120));
		allItems.add(new PackableItem(17, 54, 77));
		allItems.add(new PackableItem(18, 18,  6));
		allItems.add(new PackableItem(19, 46, 93));
		allItems.add(new PackableItem(20 ,28 ,35));
		allItems.add(new PackableItem(21, 54, 77));
		allItems.add(new PackableItem(22, 18,  6));
		allItems.add(new PackableItem(23, 46, 93));
		allItems.add(new PackableItem(24 ,28 ,35));
		allItems.add(new PackableItem(25 ,28 ,35));
		
		System.out.println("Items successfully created!");
		System.out.println("Number of items created: " + allItems.size());
		
		int tempW = 0;
		int tempB = 0;
		
		for(PackableItem i:allItems){
			tempW = tempW + i.weight();
			tempB = tempB + i.benefit();
		}
		
		System.out.println("Total item benefit: " + tempB );
		System.out.println("Total item weight: " + tempW);
		System.out.println("Weight limit of knapsack: " + WEIGHT_LIMIT);
	}
}