

/**The object that is being put into the knapsack.*/
public class PackableItem {
	
	/**Identification number for this item. */
	private int id = 0;
	
	/**Contributing weight of this item. */
	private int weight = 0;
	
	/**Contributing benefit of this item. */
	private int benefit = 0;

	/**@param i Identity @param b Benefit @param w Weight*/
	public PackableItem(int i, int b, int w)
	{
		id = i;
		weight = w;
		benefit = b;
	}
	
	public int id(){
		return id;
	}
	
	public int weight(){
		return weight;
	}

	public int benefit(){
		return benefit;
	}
}