package engine_pkg;
import content_pkg.Animation;
import content_pkg.Ghost;
import content_pkg.Pacman;
import map_pkg.Map;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("deprecation")
public class Game2 extends JPanel implements ActionListener 
{
	private boolean game_running = false;
    private boolean pacman_dead = false;
    
    private Pacman pacman = null;
    private ArrayList<Ghost> ghosts = null;
    
    Random rand_generator = new Random();
    
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private final int MAX_SPEED = 6;
    private final int  MAX_GHOSTS = 4;
    private int new_x_direction, new_y_direction;
    
    private AIBlock[][] AI_map;
    private String best_path = new String();
    private int best_path_len = 0;
    private int num_of_solutions;
    private int current_ghost_speed = 3;    
    private Timer timer;

    public Game2() {
    	
        initVariables();
        initBoard();
    }
        
    private void initBoard(){
        
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);
        setDoubleBuffered(true);        
    }

    private void initVariables(){
        
        pacman = new Pacman();
        ghosts = new ArrayList<Ghost>();
        ghosts.add(new Ghost(Animation.HUNT));
        
        for (Ghost ghost:ghosts)
    	{
	        ghost.position.y = 0;
	        ghost.position.x = 0;
	        ghost.direction.x = 0;
	        ghost.direction.y = 0;
	        ghost.speed = 2;
	        ghost.PREV_ACTION = new String("");
    	}       
        
        AI_map = new AIBlock[Map.NUM_OF_SIDE_BLOCKS][Map.NUM_OF_SIDE_BLOCKS];
        initAIMap();
        
        timer = new Timer(20, this);
        timer.start();
    }

    private void initAIMap() 
    {
        int row = 0;
        int column = 0;
        
        for(column = 0; column < Map.NUM_OF_SIDE_BLOCKS; column++){
        	
        	for(row = 0; row < Map.NUM_OF_SIDE_BLOCKS; row++){
        		
        		AI_map[row][column] = new AIBlock();
        	}
        }
	}

	/**Links all blocks in AI-map based on the map structure (edges, walls etc.).**/
    private void linkAIMap() 
    {
    	int index = 0;
        int row = 0;
        int column = 0;
        
        for(row = 0; row < Map.NUM_OF_SIDE_BLOCKS; row++)
        {
        	for(column = 0; column < Map.NUM_OF_SIDE_BLOCKS; column++)
        	{
        		AI_map[row][column].setMapArrayIndex(index);
        		
        		if((Map.DATA[index] == 0))
        		{
        			AI_map[row][column].setLeft(null);
        			AI_map[row][column].setRight(null);
        			AI_map[row][column].setUp(null);
        			AI_map[row][column].setDown(null);
        			
        			index ++;
        			continue;
        		}
        		
        		if((Map.DATA[index] & Map.WALL_LEFT) != 0)
        		{
        			AI_map[row][column].setLeft(null);
        		}
        		else if(column > 0)
        		{
        			AI_map[row][column].setLeft(AI_map[row][column - 1]);
        		}
        		
        		if((Map.DATA[index] & Map.WALL_RIGHT) != 0)
        		{
        			AI_map[row][column].setRight(null);
        		}
        		else if(column < 14)
        		{
        			AI_map[row][column].setRight(AI_map[row][column + 1]);
        		}
        		
        		if((Map.DATA[index] & Map.WALL_UP) != 0)
        		{
        			AI_map[row][column].setUp(null);
        		}
        		else if(row > 0)
        		{
        			AI_map[row][column].setUp(AI_map[row - 1][column]);
        		}
        		
        		if((Map.DATA[index] & Map.WALL_DOWN) != 0)
        		{
        			AI_map[row][column].setDown(null);	
        		}
        		else if(row < 14)
        		{
        			AI_map[row][column].setDown(AI_map[row + 1][column]);
        		}
        		
        		index ++;
        	}
        }
	}

	@Override
    public void addNotify() 
	{
        super.addNotify();
        initGame();
    }

    private void animatePacman() 
    {
    	
        pacman.animation.count--;

        if (pacman.animation.count <= 0) 
        {
        	pacman.animation.count = Animation.ANIMATION_DELAY;
        	pacman.animation.position = pacman.animation.position + 1;

            if (pacman.animation.position > Animation.NUMOF_IMG) 
            {
            	pacman.animation.position = 1;
            }
        }
    }

    private void animateGhosts() 
    {
    	for (Ghost ghost : ghosts){
    		
    		ghost.animation.count--;
    		
            if (ghost.animation.count <= 0) {
            	
            	ghost.animation.count = Animation.ANIMATION_DELAY;
            	ghost.animation.position = ghost.animation.position + 1;

                if (ghost.animation.position > Animation.NUMOF_IMG) {
                	
                	ghost.animation.position = 1;
                }
            }
    	}
    }
    
    private void playGame(Graphics2D g2d) 
    {
        if (pacman_dead) 
        {
            death();
        } 
        else 
        {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            drawGhosts(g2d);
            checkForLevelCompletion();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, Map.SCREEN_PIXEL_SIZE / 2 - 30, Map.SCREEN_PIXEL_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, Map.SCREEN_PIXEL_SIZE / 2 - 30, Map.SCREEN_PIXEL_SIZE - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (Map.SCREEN_PIXEL_SIZE - metr.stringWidth(s)) / 2, Map.SCREEN_PIXEL_SIZE / 2);
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + pacman.score;
        g.drawString(s, Map.SCREEN_PIXEL_SIZE / 2 + 96, Map.SCREEN_PIXEL_SIZE + 16);

        for (i = 0; i < pacman.stock; i++) {
            g.drawImage(pacman.animation.base(), i * 28 + 8, Map.SCREEN_PIXEL_SIZE + 1, this);
        }
    }

    private void checkForLevelCompletion() 
    {
        int current_map_block = 0;
        boolean allPointsCollected = true;

        while (current_map_block < Map.NUM_OF_SIDE_BLOCKS * Map.NUM_OF_SIDE_BLOCKS && allPointsCollected) 
        {

            if ((Map.DATA[current_map_block] & 48) != 0 && Map.DATA[current_map_block] != -1) 
            {
                allPointsCollected = false;
                break;
            }

            current_map_block++;
        }

        if (allPointsCollected) 
        {
            pacman.score += 50;
            Ghost new_ghost = null;
            
            if (ghosts.size() < MAX_GHOSTS) 
            {
            	if (ghosts.size() == 0) 
            	{
            		new_ghost = new Ghost(Animation.HUNT);
            	}
            	else if (ghosts.size() == 2) 
            	{
            		new_ghost = new Ghost(Animation.INTERCEPT);
            	}
            	else {
        		
            		new_ghost = new Ghost(Animation.RANDOM);
            	}
            	
            	new_ghost.speed = ghosts.get(0).speed;
            	
            	ghosts.add(new_ghost);
            }

            if (ghosts.get(0).speed < MAX_SPEED && ghosts.size() > 3) 
            {
            	for(Ghost ghost:ghosts){
            		ghost.speed ++;
            	}
            	pacman.speed++;
            }

            initiateNextLevel();
        }
    }

    private void death() 
    {

        pacman.stock--;

        if (pacman.stock == 0) {
        	
            game_running = false;
        }

        resetGhostsAndPacman();
    }

    private void moveGhosts(Graphics2D g2d) 
    {
        int map_block_id;
        
        for (Ghost ghost: ghosts){
        	
        	map_block_id = ghost.position.x / Map.BLOCK_PIXEL_SIZE + Map.NUM_OF_SIDE_BLOCKS * (int) (ghost.position.y / Map.BLOCK_PIXEL_SIZE);
        	
        	// The ghost has reached the center of a map-block.
            if (ghost.position.x % Map.BLOCK_PIXEL_SIZE == 0 && ghost.position.y % Map.BLOCK_PIXEL_SIZE == 0) 
            {
            	best_path = "";
            	best_path_len = 0;
            	num_of_solutions = 0;
            	
                calculateNextMove(ghost, map_block_id);
                                 
                if(best_path.length() != 0)
                {
	                if(best_path.charAt(0) == 'l')
	                {
	                	ghost.direction.x = -1;
	                	ghost.direction.y = 0;
	                	ghost.PREV_ACTION = new String("l");
	                }
	                else if(best_path.charAt(0) == 'r')
	                {
	                	ghost.direction.x = 1;
	                	ghost.direction.y = 0;
	                	ghost.PREV_ACTION  =  new String("r");
	                }
	                else if(best_path.charAt(0) == 'u')
	                {
	                	ghost.direction.x = 0;
	                	ghost.direction.y = -1;
	                	ghost.PREV_ACTION  =  new String("u");
	                }
	                else
	                {
	                	ghost.direction.x = 0;
	                	ghost.direction.y = 1;
	                	ghost.PREV_ACTION  =  new String("d");
	                }
                }
            }
//            else
//            {
//                ghost_collision_count = 0;
//
//                // Check for collision with walls for the ghost.
//                if ((Map.DATA[map_block_id] & Map.MAP_WALL_LEFT) == 0 && ghosts[ghost_id].direction.x != 1) 
//                {
//                	ghost_collision_dirx[ghost_collision_count] = -1;
//                    ghost_collision_diry[ghost_collision_count] = 0;
//                    ghost_collision_count++;
//                }
//
//                if ((Map.DATA[map_block_id] & Map.MAP_WALL_UP) == 0 && ghosts[ghost_id].direction.y != 1) {
//                	ghost_collision_dirx[ghost_collision_count] = 0;
//                    ghost_collision_diry[ghost_collision_count] = -1;
//                    ghost_collision_count++;
//                }
//
//                if ((Map.DATA[map_block_id] & Map.MAP_WALL_LEFT) == 0 && ghosts[ghost_id].direction.x != -1) {
//                	ghost_collision_dirx[ghost_collision_count] = 1;
//                    ghost_collision_diry[ghost_collision_count] = 0;
//                    ghost_collision_count++;
//                }
//
//                if ((Map.DATA[map_block_id] & Map.MAP_WALL_DOWN) == 0 && ghosts[ghost_id].direction.y != -1) {
//                	ghost_collision_dirx[ghost_collision_count] = 0;
//                    ghost_collision_diry[ghost_collision_count] = 1;
//                    ghost_collision_count++;
//                }
//
//                // No collision detected around ghost.
//                if (ghost_collision_count == 0) 
//                {
//                    if ((Map.DATA[map_block_id] & Map.EMPTY_SPACE) == 15) 
//                    {
//                        ghosts[ghost_id].direction.x = 0;
//                        ghosts[ghost_id].direction.y = 0;
//                    } 
//                    else 
//                    {
//                        ghosts[ghost_id].direction.x = -ghosts[ghost_id].direction.x;
//                        ghosts[ghost_id].direction.y = -ghosts[ghost_id].direction.y;
//                    }
//
//                } 
//                // Collision detected. Generate random new direction for the ghost.
//                else 
//                {
//                    ghost_collision_count = (int) (Math.random() * ghost_collision_count);
//
//                    if (ghost_collision_count > 3) 
//                    {
//                        ghost_collision_count = 3;
//                    }
//
//                    ghosts[ghost_id].direction.x = ghost_collision_dirx[ghost_collision_count];
//                    ghosts[ghost_id].direction.y = ghost_collision_diry[ghost_collision_count];
//                }
//        	} 
            
	        // Update the ghost position, and redraw the ghost.
            ghost.position.x = ghost.position.x + (ghost.direction.x * ghost.speed);
            ghost.position.y = ghost.position.y + (ghost.direction.y * ghost.speed);
            
	        drawGhost(g2d, ghost, ghost.position.x + 1, ghost.position.y + 1);
		        
	        // The ghost killed Pacman.
	        if (pacman.position.x > (ghost.position.x - 12) && pacman.position.x < (ghost.position.x + 12)
	                && pacman.position.y > (ghost.position.y - 12) && pacman.position.y < (ghost.position.y + 12)
	                && game_running) 
	        {
	            pacman_dead = true;
	        } 
        }
    }

    private void calculateNextMove(Ghost ghost, int ghost_block_id) 
    {
    	int curr_row, curr_column;
    	
    	int pacman_row = -1;
    	int pacman_column = -1;
    	int pacman_block_id;
    	
    	int ghost_row = -1; 
    	int ghost_column = -1;
    	
    	pacman_block_id = pacman.position.x / Map.BLOCK_PIXEL_SIZE + Map.NUM_OF_SIDE_BLOCKS * (int) (pacman.position.y / Map.BLOCK_PIXEL_SIZE);
		
    	// After this loop, we know the AI-map positions for the ghost, and for pacman.
        for(curr_row = 0; curr_row < Map.NUM_OF_SIDE_BLOCKS; curr_row++)
        {
        	if ((pacman_column != -1 || pacman_row != -1 ) && (ghost_column != -1 || ghost_row != -1 )){break;}
        	
        	for(curr_column = 0; curr_column < Map.NUM_OF_SIDE_BLOCKS; curr_column++)
        	{
        		if(AI_map[curr_row][curr_column].getMapArrayIndex() == pacman_block_id)
        		{
        			pacman_row = curr_row;
        			pacman_column = curr_column;
        		}
        		
        		if(AI_map[curr_row][curr_column].getMapArrayIndex() == ghost_block_id)
        		{
        			ghost_row = curr_row;
        			ghost_column = curr_column;
        		}
        		
        		if ((pacman_column != -1 || pacman_row != -1 ) && (ghost_column != -1 || ghost_row != -1 )){break;}
        	}
        }
        
        double a, b, c, d;
        
        // Now we set the reinforcement values for the AI-map.
        for(curr_row = 0; curr_row < Map.NUM_OF_SIDE_BLOCKS; curr_row++)
        {       	
        	for(curr_column = 0; curr_column < Map.NUM_OF_SIDE_BLOCKS; curr_column++)
        	{
        		if(curr_column == pacman_column && curr_row == pacman_row)
        		{
        			c = 0;
        			AI_map[curr_row][curr_column].setReinforcement(c);
        		}
        		else
        		{
	        		a = Math.abs(curr_row-pacman_row);
	        		b = Math.abs(curr_column-pacman_column);
	        		c = Math.sqrt(Math.pow(2, a) + Math.pow(2, b));
	        		d = (rand_generator.nextInt(9) + 1) * 0.1;
	        		
	        		AI_map[curr_row][curr_column].setReinforcement(c + 	d);
        		}
        	}
        } 
        if(ghost_row == -1 || ghost_column == -1)
        {
        	ghost_row = 0;
        }
        
        findOptimalPath(ghost, AI_map[ghost_row][ghost_column], "", 0);
	}
    
    private void findOptimalPath(Ghost ghost, AIBlock current_block, String current_path, double accu_val)
    {    	    	
    	if(current_block.getLeft() == null && current_block.getRight() == null &&
    	   current_block.getUp() == null && current_block.getDown() == null) 
    	{return;}
    	
    	if(num_of_solutions > 0) {return;}
    	if(current_path.length() > 30) {return;}
    	
    	if(current_path.length() == 1 & ghost.PREV_ACTION != null && current_block.getReinforcement() > 4) 
    	{	
    		if (ghost.PREV_ACTION.equals("r") && current_path.substring(0, 1).equals("l"))  
	    	{
    			return;
	    	}
	    	if(ghost.PREV_ACTION.equals("l") && current_path.substring(0, 1).equals("r")) 
	    	{
	    		return;
	    	}
	    	if(ghost.PREV_ACTION.equals("d")  && current_path.substring(0, 1).equals( "u")) 
	    	{
	    		return;
	    	}
	    	if (ghost.PREV_ACTION.equals("u") && current_path.substring(0, 1).equals("d")) 
	    	{
	    		return;
	    	}
		}
    	
    	ghost.CURR_PATH.add(current_block.getMapArrayIndex());
    	current_block.setVisited(true);
    	
    	if(current_block.getReinforcement() == 0)
    	{    		
    		if(best_path_len == 0 || current_path.length() < best_path_len)
    		{
    			best_path_len = current_path.length();
    			best_path = current_path;
    		}
    		
    		num_of_solutions++;
    		current_block.setVisited(false);
    		ghost.FINAL_PATH = new ArrayList<Integer>(ghost.CURR_PATH);
    		ghost.CURR_PATH.clear();
    		return;
    	}
    	
    	double LEFT = 	999.0;
    	double RIGHT = 	999.0;
    	double UP = 	999.0;
    	double DOWN = 	999.0;
    	
    	double fn = 0.0;
    	double gn = 0;
    	double hn = 0.0;
    	
    	gn = accu_val;
    	
    	if(current_block.getLeft() != null)
	    {
    		hn = current_block.getLeft().getReinforcement();
    		fn = gn + hn;
	    	LEFT = fn;
	    }
		if(current_block.getRight() != null)
    	{
    		hn = current_block.getRight().getReinforcement();
    		fn = gn + hn;
			RIGHT = fn;
    	}
		if(current_block.getUp() != null)
    	{
    		hn = current_block.getUp().getReinforcement();
    		fn = gn + hn;
			UP = fn;
    	}
		if(current_block.getDown() != null)
    	{
    		hn = current_block.getDown().getReinforcement();
    		fn = gn + hn;
			DOWN = fn;
    	}
    	
    	double[] routing = {LEFT, RIGHT, UP, DOWN};
    	Arrays.sort(routing);
    	
    	for(int path = 0; path < routing.length; path++)
    	{
			if(routing[path] == LEFT && routing[path] != 999.0 && current_block.getLeft() != null && !current_block.getLeft().getVisited())
		    {
				findOptimalPath(ghost, current_block.getLeft(), current_path + "l", accu_val +  current_block.getReinforcement());
		    }
			if(routing[path] == RIGHT && routing[path] != 999.0 && current_block.getRight() != null && !current_block.getRight().getVisited())
	    	{
				findOptimalPath(ghost, current_block.getRight(), current_path + "r", accu_val +  current_block.getReinforcement());
	    	}
			if(routing[path] == UP && routing[path] != 999.0 && current_block.getUp() != null && !current_block.getUp().getVisited())
	    	{
				findOptimalPath(ghost, current_block.getUp(), current_path + "u", accu_val +  current_block.getReinforcement());
	    	}
			if(routing[path] == DOWN && routing[path] != 999.0 && current_block.getDown() != null && !current_block.getDown().getVisited())
	    	{
				findOptimalPath(ghost, current_block.getDown(), current_path + "d", accu_val +  current_block.getReinforcement());
	    	}
    	}
		current_block.setVisited(false);
		if (ghost.CURR_PATH.size() > 0)
		{
			ghost.CURR_PATH.remove(ghost.CURR_PATH.size() - 1);
    	}
		return;
    }

	private void drawGhost(Graphics2D g2d, Ghost ghost, int new_ghost_xpos, int new_ghost_ypos) 
    {
        g2d.drawImage(ghost.animation.base(), new_ghost_xpos, new_ghost_ypos, this);
    }

    private void movePacman() 
    {
        int map_block_id;
        short current_map_block_data;
        
        // The player requests to change Pacmans direction.
        if (new_x_direction == -pacman.direction.x && new_y_direction == -pacman.direction.y) 
        {
            pacman.direction.x = new_x_direction;
            pacman.direction.y = new_y_direction;
            pacman.view_direction.x = pacman.direction.x;
            pacman.view_direction.y = pacman.direction.y;
        }

        // Pacman has reached the center of a map-block.
        if (pacman.position.x % Map.BLOCK_PIXEL_SIZE == 0 && pacman.position.y % Map.BLOCK_PIXEL_SIZE == 0) 
        {
            map_block_id = pacman.position.x / Map.BLOCK_PIXEL_SIZE + Map.NUM_OF_SIDE_BLOCKS * (int) (pacman.position.y / Map.BLOCK_PIXEL_SIZE);
            current_map_block_data = Map.DATA[map_block_id];

            if ((current_map_block_data & Map.GAME_POINT) != 0) 
            {
            	Map.DATA[map_block_id] = (short) (current_map_block_data & Map.EMPTY_SPACE);
                pacman.score++;
            }
            
            // Check for collision with walls, for the new direction of Pacman.
            if (new_x_direction != 0 || new_y_direction != 0) 
            {
                if (!((new_x_direction == -1 && new_y_direction == 0 && (current_map_block_data & Map.WALL_LEFT) != 0)
                        || (new_x_direction == 1 && new_y_direction == 0 && (current_map_block_data & Map.WALL_RIGHT) != 0)
                        || (new_x_direction == 0 && new_y_direction == -1 && (current_map_block_data & Map.WALL_UP) != 0)
                        || (new_x_direction == 0 && new_y_direction == 1 && (current_map_block_data & Map.WALL_DOWN) != 0))) 
                {
                    pacman.direction.x = new_x_direction;
                    pacman.direction.y = new_y_direction;
                    pacman.view_direction.x = pacman.direction.x;
                    pacman.view_direction.y = pacman.direction.y;
                }
            }

            // Check for collision with walls, for the same and current direction of Pacman.
            if ((pacman.direction.x == -1 && pacman.direction.y == 0 && (current_map_block_data & Map.WALL_LEFT) != 0)
                    || (pacman.direction.x == 1 && pacman.direction.y == 0 && (current_map_block_data & Map.WALL_RIGHT) != 0)
                    || (pacman.direction.x == 0 && pacman.direction.y == -1 && (current_map_block_data & Map.WALL_UP) != 0)
                    || (pacman.direction.x == 0 && pacman.direction.y == 1 && (current_map_block_data & Map.WALL_DOWN) != 0)) 
            {
                pacman.direction.x = 0;
                pacman.direction.y = 0;
            }
        }

        
        pacman.position.x = pacman.position.x + pacman.speed * pacman.direction.x;
        pacman.position.y = pacman.position.y + pacman.speed * pacman.direction.y;
        
    }
    
    private void drawPacman(Graphics2D g2d) 
    {
        if (pacman.view_direction.x == -1){
        	
            drawPacmanLeft(g2d);
        } 
        else if (pacman.view_direction.x == 1){
        	
            drawPacmanRight(g2d);
        } 
        else if (pacman.view_direction.y == -1){
        	
            drawPacmanUp(g2d);
        } 
        else{
        	
            drawPacmanDown(g2d);
        }
    }
    
    private void drawGhosts(Graphics2D g2d) 
    {
    	for(Ghost ghost : ghosts){
    		
            if (ghost.view_direction.x == -1){
            	drawGhostLeft(g2d, ghost);
            } 
            else if (ghost.view_direction.x == 1){
            	drawGhostRight(g2d, ghost);
            } 
            else if (ghost.view_direction.y == -1){
            	drawGhostUp(g2d, ghost);
            } 
            else{
            	drawGhostDown(g2d, ghost);
            }
    	}
    }


    private void drawPacmanUp(Graphics2D g2d) 
    {
        switch (pacman.animation.position){
            case 1:
                g2d.drawImage(pacman.animation.up(0), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(pacman.animation.up(1), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(pacman.animation.up(2), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(pacman.animation.base(), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) 
    {
        switch (pacman.animation.position){
        
            case 1:
                g2d.drawImage(pacman.animation.down(0), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(pacman.animation.down(1), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(pacman.animation.down(2), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(pacman.animation.base(), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
        }
    }

    private void drawPacmanLeft(Graphics2D g2d) 
    {
        switch (pacman.animation.position){
        
            case 1:
                g2d.drawImage(pacman.animation.left(0), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(pacman.animation.left(1), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(pacman.animation.left(2), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(pacman.animation.base(), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) 
    {
        switch (pacman.animation.position){
        
            case 1:
                g2d.drawImage(pacman.animation.right(0), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(pacman.animation.right(1), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(pacman.animation.right(2), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(pacman.animation.base(), pacman.position.x + 1, pacman.position.y + 1, this);
                break;
        }
    }

    private void drawGhostUp(Graphics2D g2d, Ghost ghost) 
    {
        switch (pacman.animation.position){
        
            case 1:
                g2d.drawImage(ghost.animation.up(0), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(ghost.animation.up(1), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(ghost.animation.up(2), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(ghost.animation.base(), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
        }
    }

    private void drawGhostDown(Graphics2D g2d, Ghost ghost) 
    {
        switch (pacman.animation.position){
        
            case 1:
                g2d.drawImage(ghost.animation.down(0), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(ghost.animation.down(1), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(ghost.animation.down(2), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(ghost.animation.base(), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
        }
    }

    private void drawGhostLeft(Graphics2D g2d, Ghost ghost){
    	
        switch (pacman.animation.position){
        
            case 1:
                g2d.drawImage(ghost.animation.left(0), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(ghost.animation.left(1), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(ghost.animation.left(2), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(ghost.animation.base(), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
        }
    }

    private void drawGhostRight(Graphics2D g2d, Ghost ghost){
    	
        switch (pacman.animation.position){
        
            case 1:
                g2d.drawImage(ghost.animation.right(0), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 2:
                g2d.drawImage(ghost.animation.right(1), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            case 3:
                g2d.drawImage(ghost.animation.right(2), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
                
            default:
                g2d.drawImage(ghost.animation.base(), ghost.position.x + 1, ghost.position.y + 1, this);
                break;
        }
    }
    private void drawMaze(Graphics2D g2d) 
    {
        int  current_map_block = 0;
        int block_xpos, block_ypos;
        
        for (block_ypos = 0; block_ypos < Map.SCREEN_PIXEL_SIZE; block_ypos += Map.BLOCK_PIXEL_SIZE) 
        {
            for (block_xpos = 0; block_xpos < Map.SCREEN_PIXEL_SIZE; block_xpos += Map.BLOCK_PIXEL_SIZE) 
            {
            	g2d.setStroke(new BasicStroke(1));   
            	
                g2d.setColor(Map.MAZE_COLOR);

                
                if ((Map.DATA[current_map_block] & Map.WALL_LEFT) != 0) { 
                   
                	g2d.drawLine(block_xpos, 
                    			 block_ypos, 
                    			 block_xpos, 
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1);
                }

                if ((Map.DATA[current_map_block] & Map.WALL_UP) != 0) { 
                	
                    g2d.drawLine(block_xpos, 
                    			 block_ypos, 
                    			 block_xpos + Map.BLOCK_PIXEL_SIZE - 1, 
                    			 block_ypos);
                }

                if ((Map.DATA[current_map_block] & Map.WALL_RIGHT) != 0) { 
                	
                    g2d.drawLine(block_xpos + Map.BLOCK_PIXEL_SIZE - 1, 
                    			 block_ypos, 
                    			 block_xpos + Map.BLOCK_PIXEL_SIZE - 1,
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1);
                }

                if ((Map.DATA[current_map_block] & Map.WALL_DOWN) != 0){ 
                	
                    g2d.drawLine(block_xpos, 
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1, 
                    			 block_xpos + Map.BLOCK_PIXEL_SIZE - 1,
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1);
                }

                if ((Map.DATA[current_map_block] & Map.GAME_POINT) != 0) { 
                	
                    g2d.setColor(Map.POINT_COLOR);
                    g2d.fillRect(block_xpos + 11, 
                    			 block_ypos + 11, 
                    			 Map.POINTS_PIXEL_SIZE, 
                    			 Map.POINTS_PIXEL_SIZE);
                }
                
                if (Map.DATA[current_map_block] == -1 ) { 
                	
                	g2d.setColor(Map.MAZE_COLOR);
                    
                    g2d.fillRect(block_xpos, 
                    			 block_ypos, 
                    			 Map.BLOCK_PIXEL_SIZE, 
                    			 Map.BLOCK_PIXEL_SIZE);
                }
                
                g2d.setStroke(new BasicStroke(1)); 
                
                for (Ghost ghost:ghosts)
                {
                	
                	 if(ghost.FINAL_PATH.contains(current_map_block))
                     {	
                    	 	g2d.setColor(ghost.COLOR);
                    	 
                         g2d.drawLine(block_xpos, 
                    			 block_ypos, 
                    			 block_xpos, 
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1);
                         
                         g2d.drawLine(block_xpos, 
                    			 block_ypos, 
                    			 block_xpos + Map.BLOCK_PIXEL_SIZE - 1, 
                    			 block_ypos);
                         
                         g2d.drawLine(block_xpos + Map.BLOCK_PIXEL_SIZE - 1, 
                    			 block_ypos, 
                    			 block_xpos + Map.BLOCK_PIXEL_SIZE - 1,
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1);
                         
                         g2d.drawLine(block_xpos, 
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1, 
                    			 block_xpos + Map.BLOCK_PIXEL_SIZE - 1,
                    			 block_ypos + Map.BLOCK_PIXEL_SIZE - 1);
                         
                         String s = Integer.toString(ghost.FINAL_PATH.indexOf(current_map_block));
                         Font small = new Font("Helvetica", Font.PLAIN, 18);

                         g2d.setColor(Map.GHOST_PATH_COLOR);
                         g2d.setFont(small);
                         g2d.drawString(s, (block_xpos + 1), block_ypos + 20);
     				}
                }
                
                current_map_block++;
            }
        }
    }

    private void initGame() {

        pacman.stock = 3;
        pacman.score = 0;
        initiateNextLevel();
        linkAIMap();
    }

    private void initiateNextLevel(){
    	
        int current_map_block_id;
        
        for (current_map_block_id = 0; current_map_block_id < Map.NUM_OF_SIDE_BLOCKS * Map.NUM_OF_SIDE_BLOCKS; current_map_block_id++) {
            
        	Map.DATA[current_map_block_id] = Map.CONTENT[current_map_block_id];
        }

        linkAIMap();
        resetGhostsAndPacman();
    }

    private void resetGhostsAndPacman() {	
    	
        for (Ghost ghost : ghosts) 
        {
            ghost.position.y = 0 * Map.BLOCK_PIXEL_SIZE;
            ghost.position.x = (rand_generator.nextInt((9 - 5) + 1) + 5) * Map.BLOCK_PIXEL_SIZE;
            ghost.direction.y = 1;
            ghost.direction.x = 0;
        }

        pacman.position.x = 7 * Map.BLOCK_PIXEL_SIZE;
        pacman.position.y = 10 * Map.BLOCK_PIXEL_SIZE;
        pacman.direction.x = -1;
        pacman.direction.y = 0;
        new_x_direction = 0;
        new_y_direction = 0;
        pacman.view_direction.x = -1;
        pacman.view_direction.y = 0;
        pacman_dead = false;
    }

    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
    	
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, Map.BOARD_PIXEL_DIMENSION.width, Map.BOARD_PIXEL_DIMENSION.height);
        drawMaze(g2d);
        drawScore(g2d);
        animatePacman();
        animateGhosts();
        
        if (game_running) 
        {
        	playGame(g2d);
        } 
        else 
        {
        	showIntroScreen(g2d);
        }
        
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    class TAdapter extends KeyAdapter 
    {

        @Override
        public void keyPressed(KeyEvent e) 
        {

            int key = e.getKeyCode();

            if (game_running) 
            {
                if (key == KeyEvent.VK_LEFT) 
                {
                    new_x_direction = -1;
                    new_y_direction = 0;
                } 
                else if (key == KeyEvent.VK_RIGHT)
                {
                    new_x_direction = 1;
                    new_y_direction = 0;
                } 
                else if (key == KeyEvent.VK_UP) 
                {
                    new_x_direction = 0;
                    new_y_direction = -1;
                } 
                else if (key == KeyEvent.VK_DOWN) 
                {
                    new_x_direction = 0;
                    new_y_direction = 1;
                } 
                else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) 
                {
                    game_running = false;
                } 
                else if (key == KeyEvent.VK_PAUSE)
                {
                    if (timer.isRunning()) {timer.stop();} 
                    else {timer.start();}
                }
            } 
            else 
            {
                if (key == 's' || key == 'S')
                {
                    game_running = true;
                    initGame();
                }
            }
        }
        
        
        @Override
        public void keyReleased(KeyEvent e) 
        {
        	// Using Event from java.awt.Event seems to give better key-input response.
            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                new_x_direction = 0;
                new_y_direction = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        repaint();
    }
}