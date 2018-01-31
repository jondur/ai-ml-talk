package engine_pkg;

public class AIBlock 
{	
	private double reinforcement_value;
	private int map_array_index;
	
	private AIBlock left;
	private AIBlock right;
	private AIBlock up;
	private AIBlock down;
	private Boolean visited;
	
	public AIBlock()
	{
		initBlocK();
	}
	
	private void initBlocK() 
	{
		setReinforcement(0);
		setMapArrayIndex(0);
		setLeft(null);
		setRight(null);
		setUp(null);
		setDown(null);
		setVisited(false);
	}

	public double getReinforcement() {
		return reinforcement_value;
	}

	public void setReinforcement(double val) {
		reinforcement_value = val;
	}

	public int getMapArrayIndex() {
		return map_array_index;
	}

	public void setMapArrayIndex(int val) {
		map_array_index = val;
	}

	public AIBlock getLeft() {
		return left;
	}

	public void setLeft(AIBlock block) {
		left = block;
	}

	public AIBlock getRight() {
		return right;
	}

	public void setRight(AIBlock block) {
		right = block;
	}

	public AIBlock getUp() {
		return up;
	}

	public void setUp(AIBlock block) {
		up = block;
	}

	public AIBlock getDown() {
		return down;
	}

	public void setDown(AIBlock block) {
		down = block;
	}

	public Boolean getVisited() {
		return visited;
	}

	public void setVisited(Boolean b) {
		visited = b;
	}
}
