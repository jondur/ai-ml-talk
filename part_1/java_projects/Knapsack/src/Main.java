

import java.util.Scanner;

/**
 * The program solves the "Knapsack-problem", using (Depth-First) DFS- or (breath-First) BFS search algorithm. 
 * The algorithms has not been optimized in any way.
 * @author Stefan Danielsson. 
 * @since  2017-11-15
 * */

public class Main {
	
	public static void main(String[] args) {
	
		System.out.println("***********************************************");
		System.out.println("* AI SEARCH ALGORITHMS - THE KNAPSACK PROBLEM *");
		System.out.println("***********************************************");
		
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("Select search algorithm. (0) for DFS, or (other) for BFS.");
		String input_str = keyboard.next();
		char input_char = input_str.charAt(0);
		int user_input = Character.getNumericValue(input_char);
		
		if(user_input == Knapsack_construct.DFS){
			System.out.println("DFS selected!\n");
		}
		else{
			user_input = Knapsack_construct.BFS;
			System.out.println("BFS selected!\n");
		}

		@SuppressWarnings("unused")
		Knapsack_construct a = new Knapsack_construct(user_input);
	}
}