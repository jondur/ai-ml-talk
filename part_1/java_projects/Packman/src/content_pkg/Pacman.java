package content_pkg;

public class Pacman extends MoveableObject{
	
	public static final String ANIMATION_TYPE = new String(Animation.PACMAN_ANIMATION);
	public int stock, score;
	
	public Pacman() {
		super(ANIMATION_TYPE);
	}
}
