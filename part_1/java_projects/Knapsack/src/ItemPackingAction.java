

/**A node that represents a state after a certain action has been performed, such as packing an item, 
 * or packing nothing at all*/
public class ItemPackingAction 
{	
	/** Parent node for this node. */
	private ItemPackingAction parentNode = null;
	
	/**The selection that lead to this state.*/
	private PackableItem selectedItem = null;
	
	private int accWeight = 0;
	private int accBenefit = 0;
	private int nodeDepth = 0;
	
	public ItemPackingAction(){}

	public ItemPackingAction(ItemPackingAction n) {
		parentNode = n.parent();
		selectedItem = n.item();
		nodeDepth = n.depth();
	}
	
	public int depth() {
		return nodeDepth;
	}
	
	public void setDepth(int d) {
		nodeDepth = d;
	}

	public PackableItem item() {
		return selectedItem;
	}
	
	public void setItem(PackableItem i) {
		selectedItem = i;
	}

	public void setParent(ItemPackingAction p) {
		parentNode = p;
	}

	public ItemPackingAction parent() {
		return parentNode;
	}

	public void setAccumulatedBenefit(int newBenefit) {
		accBenefit = newBenefit;
	}

	public void setAccumulatedWeight(int newWeight) {
		accWeight = newWeight;
	}

	public int accumulatedBenefit() {
		return accBenefit;
	}

	public int accumulatedWeight() {
		return accWeight;
	}
}