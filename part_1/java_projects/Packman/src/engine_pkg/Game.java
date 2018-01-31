package engine_pkg;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("deprecation")
public class Game extends JPanel implements ActionListener 
{
	Random rand_generator = new Random();

	private Dimension board_pixel_dimention;
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private final Color dotColor = new Color(192, 192, 0);
    private final Color ghostPathColor = new Color(100, 100,100);
    private final Color blackColor = new Color(0, 0, 0);
    private Color mazeColor;

    private boolean game_running = false;
    private boolean pacman_dead = false;

    private final int BLOCK_PIXEL_SIZE = 24;
    private final int NUM_OF_BLOCKS = 15;
    private final int SCREEN_PIXEL_SIZE = NUM_OF_BLOCKS * BLOCK_PIXEL_SIZE;
    private final int PACMAN_ANIMATION_DELAY = 2;
    private final int PACMAN_ANIMATION_COUNT = 4;
    private final int MAX_GHOSTS = 2;
    private final int PACMAN_SPEED = 6;
    private final int VALID_SPEEDS[] = {1, 2, 3, 4, 6, 8};
    private final int MAX_SPEED = 6;
    
    private final int GHOST_PATH = 99;
    private final int MAP_WALL_LEFT = 1;
    private final int MAP_WALL_RIGHT = 4;
    private final int MAP_WALL_UP = 2;
    private final int MAP_WALL_DOWN = 8;
    private final int GAME_POINT = 16;
    private final int EMPTY_SPACE = 15;
    private final int PIXEL_SIZE_OF_POINTS = 2;
    
    // The Pacman    
    private int pacman_animation_count = PACMAN_ANIMATION_DELAY;
    private int pacman_animation_dir = 1;
    private int pacman_animation_pos = 0;
    private int pacman_stock, pacman_score;

    private int pacman_posx, pacman_posy;
    private int pacman_dirx, pacmand_diry;
    private int requested_dirx, requested_diry;
    private int view_dx, view_dy;
    
    private Image pacman1_img;
    private Image pacman2up_img, pacman2left_img, pacman2right_img, pacman2down_img;
    private Image pacman3up_img, pacman3left_img, pacman3right_img, pacman3down_img;
    private Image pacman4up_img, pacman4left_img, pacman4right_img, pacman4down_img;
    
    // The ghosts
    private int[] ghost_posx, ghost_posy;
    private int[] ghost_dirx, ghost_diry;
    private int[] ghost_speed;
    private int next_action_index = 0;
    private String[] ghost_prev_action;
    private int N_GHOSTS = 1;
    private int[] ghost_collision_dirx, ghost_collision_diry;
    private Image ghost_img;
    
    private AIBlock[][] AI_map;
    private String best_path = new String();
    private ArrayList<Integer> current_ghost_path = null;
    private ArrayList<Integer> final_ghost_path = null;
    private int best_path_len = 0;
    private int num_of_solutions;
    /** An array holding data for how each sub-block of the map should be drawn.**/
    private final short map_content[] = {
    		
		19, 26, 22,  0,  0, 19, 26, 26, 26, 22,  0,  0, 19, 26, 22,
		21,  0, 17, 18, 26, 20,  0,  0,  0, 17, 26, 18, 20,  0, 21,
		25, 18, 16, 28,  0, 25, 22,  0, 19, 28,  0, 25, 16, 18, 28,
		 0, 17, 20,  0,  0,  0, 21,  0, 21,  0,  0,  0, 17, 20,  0,
		 0, 17, 20,  0,  0,  0, 17, 26, 20,  0,  0,  0, 17, 20,  0,
		19, 24, 16, 22,  0, 19, 20,  0, 17, 22,  0, 19, 16, 24, 22,
		21,  0, 25, 24, 18, 24, 20,  0, 17, 24, 18, 24, 28,  0, 21,
		21,  0,  0,  0, 21,  0, 17, 26, 20,  0, 21,  0,  0,  0, 21,
		21,  0, 19, 26, 16, 26, 20,  0, 17, 26, 16, 26, 22,  0, 21,
		17, 26, 20,  0, 21,  0, 21,  0, 21,  0, 21,  0, 17, 26, 20,
		21,  0, 21,  0, 21,  0, 17, 26, 20,  0, 21,  0, 21,  0, 21,
		17, 18, 20,  0, 21,  0, 21,  0, 21,  0, 21,  0, 17, 18, 20,
		17, 24, 16, 26, 24, 18, 28,  0, 25, 18, 24, 26, 16, 24, 20,
		21,  0, 21,  0,  0, 21,  0,  0,  0, 21,  0,  0, 21,  0, 21,
		25, 26, 28,  0,  0, 25, 26, 26, 26, 28,  0, 0, 25, 26, 28
         
    };

    private int current_speed = 3;
    
    /** An array holding information about each sub-block of the map, at any given time.**/
    private short[] map_data;
    
    private Timer timer;

    public Game() 
    {
        loadImages();
        initVariables();
        initBoard();
    }
        
    private void initBoard() {
        
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);
        setDoubleBuffered(true);        
    }

    private void initVariables() {

        map_data = new short[NUM_OF_BLOCKS * NUM_OF_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        board_pixel_dimention = new Dimension(400, 400);
        ghost_posx = new int[MAX_GHOSTS];
        ghost_dirx = new int[MAX_GHOSTS];
        ghost_posy = new int[MAX_GHOSTS];
        ghost_diry = new int[MAX_GHOSTS];
        ghost_speed = new int[MAX_GHOSTS];
        ghost_collision_dirx = new int[4];
        ghost_collision_diry = new int[4];
        AI_map = new AIBlock[NUM_OF_BLOCKS][NUM_OF_BLOCKS];
        initAIMap();
        current_ghost_path = new ArrayList<Integer>();
        final_ghost_path = new ArrayList<Integer>();
        ghost_prev_action = new String[2];
        ghost_prev_action[0] = new String("");
        ghost_prev_action[1] = new String("");
        
        timer = new Timer(40, this);
        timer.start();
    }

    private void initAIMap() 
    {
        int row = 0;
        int column = 0;
        
        for(column = 0; column < NUM_OF_BLOCKS; column++)
        {
        	for(row = 0; row < NUM_OF_BLOCKS; row++)
        	{
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
        
        for(row = 0; row < NUM_OF_BLOCKS; row++)
        {
        	for(column = 0; column < NUM_OF_BLOCKS; column++)
        	{
        		AI_map[row][column].setMapArrayIndex(index);
        		
        		if((map_data[index] == 0))
        		{
        			AI_map[row][column].setLeft(null);
        			AI_map[row][column].setRight(null);
        			AI_map[row][column].setUp(null);
        			AI_map[row][column].setDown(null);
        			
        			index ++;
        			continue;
        		}
        		
        		if((map_data[index] & MAP_WALL_LEFT) != 0)
        		{
        			AI_map[row][column].setLeft(null);
        		}
        		else if(column > 0)
        		{
        			AI_map[row][column].setLeft(AI_map[row][column - 1]);
        		}
        		
        		if((map_data[index] & MAP_WALL_RIGHT) != 0)
        		{
        			AI_map[row][column].setRight(null);
        		}
        		else if(column < 14)
        		{
        			AI_map[row][column].setRight(AI_map[row][column + 1]);
        		}
        		
        		if((map_data[index] & MAP_WALL_UP) != 0)
        		{
        			AI_map[row][column].setUp(null);
        		}
        		else if(row > 0)
        		{
        			AI_map[row][column].setUp(AI_map[row - 1][column]);
        		}
        		
        		if((map_data[index] & MAP_WALL_DOWN) != 0)
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
        pacman_animation_count--;

        if (pacman_animation_count <= 0) 
        {
            pacman_animation_count = PACMAN_ANIMATION_DELAY;
            pacman_animation_pos = pacman_animation_pos + pacman_animation_dir;

            if (pacman_animation_pos == (PACMAN_ANIMATION_COUNT - 1) || pacman_animation_pos == 0) 
            {
                pacman_animation_dir = -pacman_animation_dir;
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
            checkForLevelCompletion();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_PIXEL_SIZE / 2 - 30, SCREEN_PIXEL_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_PIXEL_SIZE / 2 - 30, SCREEN_PIXEL_SIZE - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (SCREEN_PIXEL_SIZE - metr.stringWidth(s)) / 2, SCREEN_PIXEL_SIZE / 2);
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + pacman_score;
        g.drawString(s, SCREEN_PIXEL_SIZE / 2 + 96, SCREEN_PIXEL_SIZE + 16);

        for (i = 0; i < pacman_stock; i++) {
            g.drawImage(pacman3left_img, i * 28 + 8, SCREEN_PIXEL_SIZE + 1, this);
        }
    }

    private void checkForLevelCompletion() 
    {
        int current_map_block = 0;
        boolean allPointsCollected = true;

        while (current_map_block < NUM_OF_BLOCKS * NUM_OF_BLOCKS && allPointsCollected) 
        {

            if ((map_data[current_map_block] & 48) != 0) 
            {
                allPointsCollected = false;
                break;
            }

            current_map_block++;
        }

        if (allPointsCollected) 
        {
            pacman_score += 50;

            if (N_GHOSTS < MAX_GHOSTS) 
            {
                N_GHOSTS++;
            }

            if (current_speed < MAX_SPEED) 
            {
                current_speed++;
            }

            initiateNextLevel();
        }
    }

    private void death() 
    {

        pacman_stock--;

        if (pacman_stock == 0) 
        {
            game_running = false;
        }

        resetGhostsAndPacman();
    }

    private void moveGhosts(Graphics2D g2d) 
    {
        int ghost_id;
        int map_block_id;
        
        for (ghost_id = 0; ghost_id < N_GHOSTS; ghost_id++) 
        {
        	
        	map_block_id = ghost_posx[ghost_id] / BLOCK_PIXEL_SIZE + NUM_OF_BLOCKS * (int) (ghost_posy[ghost_id] / BLOCK_PIXEL_SIZE);
        	
        	// The ghost has reached the center of a map-block.
            if (ghost_posx[ghost_id] % BLOCK_PIXEL_SIZE == 0 && ghost_posy[ghost_id] % BLOCK_PIXEL_SIZE == 0) 
            {
            	best_path = "";
            	best_path_len = 0;
            	num_of_solutions = 0;
            	
                calculateNextMove(map_block_id);
                
//                if (next_action_index == 0)
//            	{
//                	next_action_index = 1;	
//            	}
//                else
//                {
//                	next_action_index = 0;
//                }
                
                
                if(best_path.length() != 0)
                {
	                if(best_path.charAt(0) == 'l')
	                {
	                  ghost_dirx[ghost_id] = -1;
	                  ghost_diry[ghost_id] = 0;
	                  ghost_prev_action[next_action_index] = new String("l");
	                }
	                else if(best_path.charAt(0) == 'r')
	                {
	                  ghost_dirx[ghost_id] = 1;
	                  ghost_diry[ghost_id] = 0;
	                  ghost_prev_action[next_action_index]  =  new String("r");
	                }
	                else if(best_path.charAt(0) == 'u')
	                {
	                  ghost_dirx[ghost_id] = 0;
	                  ghost_diry[ghost_id] = -1;
	                  ghost_prev_action[next_action_index]  =  new String("u");
	                }
	                else
	                {
	                    ghost_dirx[ghost_id] = 0;
	                    ghost_diry[ghost_id] = 1;
	                    ghost_prev_action[next_action_index]  =  new String("d");
	                }
                }
            }
//            else
//            {
//                ghost_collision_count = 0;
//
//                // Check for collision with walls for the ghost.
//                if ((map_data[map_block_id] & MAP_WALL_LEFT) == 0 && ghost_dirx[ghost_id] != 1) 
//                {
//                	ghost_collision_dirx[ghost_collision_count] = -1;
//                    ghost_collision_diry[ghost_collision_count] = 0;
//                    ghost_collision_count++;
//                }
//
//                if ((map_data[map_block_id] & MAP_WALL_UP) == 0 && ghost_diry[ghost_id] != 1) {
//                	ghost_collision_dirx[ghost_collision_count] = 0;
//                    ghost_collision_diry[ghost_collision_count] = -1;
//                    ghost_collision_count++;
//                }
//
//                if ((map_data[map_block_id] & MAP_WALL_RIGHT) == 0 && ghost_dirx[ghost_id] != -1) {
//                	ghost_collision_dirx[ghost_collision_count] = 1;
//                    ghost_collision_diry[ghost_collision_count] = 0;
//                    ghost_collision_count++;
//                }
//
//                if ((map_data[map_block_id] & MAP_WALL_DOWN) == 0 && ghost_diry[ghost_id] != -1) {
//                	ghost_collision_dirx[ghost_collision_count] = 0;
//                    ghost_collision_diry[ghost_collision_count] = 1;
//                    ghost_collision_count++;
//                }
//
//                // No collision detected around ghost.
//                if (ghost_collision_count == 0) 
//                {
//                    if ((map_data[map_block_id] & EMPTY_SPACE) == 15) 
//                    {
//                        ghost_dirx[ghost_id] = 0;
//                        ghost_diry[ghost_id] = 0;
//                    } 
//                    else 
//                    {
//                        ghost_dirx[ghost_id] = -ghost_dirx[ghost_id];
//                        ghost_diry[ghost_id] = -ghost_diry[ghost_id];
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
//                    ghost_dirx[ghost_id] = ghost_collision_dirx[ghost_collision_count];
//                    ghost_diry[ghost_id] = ghost_collision_diry[ghost_collision_count];
//                }
//        	} 
            
	        // Update the ghost position, and redraw the ghost.
	        ghost_posx[ghost_id] = ghost_posx[ghost_id] + (ghost_dirx[ghost_id] * ghost_speed[ghost_id]);
	        ghost_posy[ghost_id] = ghost_posy[ghost_id] + (ghost_diry[ghost_id] * ghost_speed[ghost_id]);
	        drawGhost(g2d, ghost_posx[ghost_id] + 1, ghost_posy[ghost_id] + 1);
		        
	        // The ghost killed Pacman.
	        if (pacman_posx > (ghost_posx[ghost_id] - 12) && pacman_posx < (ghost_posx[ghost_id] + 12)
	                && pacman_posy > (ghost_posy[ghost_id] - 12) && pacman_posy < (ghost_posy[ghost_id] + 12)
	                && game_running) 
	        {
	            pacman_dead = true;
	        } 
        }
    }

    private void calculateNextMove(int ghost_block_id) 
    {
    	int curr_row, curr_column;
    	
    	int pacman_row = -1;
    	int pacman_column = -1;
    	int pacman_block_id;
    	
    	int ghost_row = -1; 
    	int ghost_column = -1;
    	
    	int max_row, max_col;
    	int min_row, min_col;
    	int map_index = 0;
    	
    	pacman_block_id = pacman_posx / BLOCK_PIXEL_SIZE + NUM_OF_BLOCKS * (int) (pacman_posy / BLOCK_PIXEL_SIZE);
		
    	// After this loop, we know the AI-map positions for the ghost, and for pacman.
        for(curr_row = 0; curr_row < NUM_OF_BLOCKS; curr_row++)
        {
        	if ((pacman_column != -1 || pacman_row != -1 ) && (ghost_column != -1 || ghost_row != -1 )){break;}
        	
        	for(curr_column = 0; curr_column < NUM_OF_BLOCKS; curr_column++)
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
        
        max_row  = Math.max(pacman_row, ghost_row);
        min_row = Math.min(pacman_row, ghost_row);
        
        max_col  = Math.max(pacman_column, ghost_column);
        min_col = Math.min(pacman_column, ghost_column);
        
        double a, b, c, d;
        int e;
        
        // Now we set the reinforcement values for the AI-map.
        for(curr_row = 0; curr_row < NUM_OF_BLOCKS; curr_row++)
        {       	
        	for(curr_column = 0; curr_column < NUM_OF_BLOCKS; curr_column++)
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
        		map_index ++;
        	}
        } 
        
        findOptimalPath(AI_map[ghost_row][ghost_column], "", 0);
	}
    
    private void findOptimalPath(AIBlock current_block, String current_path, double accu_val)
    {    	    	
    	if(num_of_solutions > 0) {return;}
    	if(current_block == null) {return;}
    	if(current_path.length() > 30) {return;}
    	
    	if(current_path.length() == 1 & ghost_prev_action[0] != null && current_block.getReinforcement() > 4) 
    	{	
    		if (ghost_prev_action[0].equals("r") && current_path.substring(0, 1).equals("l"))  
	    	{
    			return;
	    	}
	    	if(ghost_prev_action[0].equals("l") && current_path.substring(0, 1).equals("r")) 
	    	{
	    		return;
	    	}
	    	if(ghost_prev_action[0].equals("d")  && current_path.substring(0, 1).equals( "u")) 
	    	{
	    		return;
	    	}
	    	if (ghost_prev_action[0].equals("u") && current_path.substring(0, 1).equals("d")) 
	    	{
	    		return;
	    	}
		}
    	
//    	if(current_path.length() == 2 & ghost_prev_action[1] != null) 
//    	{	
//    		if (ghost_prev_action[1].equals("r") && current_path.substring(0, 1).equals("l"))  
//	    	{
//    			return;
//	    	}
//	    	if(ghost_prev_action[1].equals("l") && current_path.substring(0, 1).equals("r")) 
//	    	{
//	    		return;
//	    	}
//	    	if(ghost_prev_action[1].equals("d") && current_path.substring(0, 1).equals( "u")) 
//	    	{
//	    		return;
//	    	}
//	    	if (ghost_prev_action[1].equals("u") && current_path.substring(0, 1).equals("d")) 
//	    	{
//	    		return;
//	    	}
//		}
    	
    	current_ghost_path.add(current_block.getMapArrayIndex());
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
    		final_ghost_path = new ArrayList<Integer>(current_ghost_path);
            current_ghost_path.clear();
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
				findOptimalPath(current_block.getLeft(), current_path + "l", accu_val +  current_block.getReinforcement());
		    }
			if(routing[path] == RIGHT && routing[path] != 999.0 && current_block.getRight() != null && !current_block.getRight().getVisited())
	    	{
				findOptimalPath(current_block.getRight(), current_path + "r", accu_val +  current_block.getReinforcement());
	    	}
			if(routing[path] == UP && routing[path] != 999.0 && current_block.getUp() != null && !current_block.getUp().getVisited())
	    	{
				findOptimalPath(current_block.getUp(), current_path + "u", accu_val +  current_block.getReinforcement());
	    	}
			if(routing[path] == DOWN && routing[path] != 999.0 && current_block.getDown() != null && !current_block.getDown().getVisited())
	    	{
				findOptimalPath(current_block.getDown(), current_path + "d", accu_val +  current_block.getReinforcement());
	    	}
    	}
		current_block.setVisited(false);
		if (current_ghost_path.size() > 0)
		{
			current_ghost_path.remove(current_ghost_path.size() - 1);
    	}
		return;
    }

	private void drawGhost(Graphics2D g2d, int new_ghost_xpos, int new_ghost_ypos) 
    {
        g2d.drawImage(ghost_img, new_ghost_xpos, new_ghost_ypos, this);
    }

    private void movePacman() 
    {
        int map_block_id;
        short current_map_block_data;
        
        // The player requests to change Pacmans direction.
        if (requested_dirx == -pacman_dirx && requested_diry == -pacmand_diry) 
        {
            pacman_dirx = requested_dirx;
            pacmand_diry = requested_diry;
            view_dx = pacman_dirx;
            view_dy = pacmand_diry;
        }

        // Pacman has reached the center of a map-block.
        if (pacman_posx % BLOCK_PIXEL_SIZE == 0 && pacman_posy % BLOCK_PIXEL_SIZE == 0) 
        {
            map_block_id = pacman_posx / BLOCK_PIXEL_SIZE + NUM_OF_BLOCKS * (int) (pacman_posy / BLOCK_PIXEL_SIZE);
            current_map_block_data = map_data[map_block_id];

            if ((current_map_block_data & GAME_POINT) != 0) 
            {
                map_data[map_block_id] = (short) (current_map_block_data & EMPTY_SPACE);
                pacman_score++;
            }
            
            // Check for collision with walls, for the new direction of Pacman.
            if (requested_dirx != 0 || requested_diry != 0) 
            {
                if (!((requested_dirx == -1 && requested_diry == 0 && (current_map_block_data & MAP_WALL_LEFT) != 0)
                        || (requested_dirx == 1 && requested_diry == 0 && (current_map_block_data & MAP_WALL_RIGHT) != 0)
                        || (requested_dirx == 0 && requested_diry == -1 && (current_map_block_data & MAP_WALL_UP) != 0)
                        || (requested_dirx == 0 && requested_diry == 1 && (current_map_block_data & MAP_WALL_DOWN) != 0))) 
                {
                    pacman_dirx = requested_dirx;
                    pacmand_diry = requested_diry;
                    view_dx = pacman_dirx;
                    view_dy = pacmand_diry;
                }
            }

            // Check for collision with walls, for the same and current direction of Pacman.
            if ((pacman_dirx == -1 && pacmand_diry == 0 && (current_map_block_data & MAP_WALL_LEFT) != 0)
                    || (pacman_dirx == 1 && pacmand_diry == 0 && (current_map_block_data & MAP_WALL_RIGHT) != 0)
                    || (pacman_dirx == 0 && pacmand_diry == -1 && (current_map_block_data & MAP_WALL_UP) != 0)
                    || (pacman_dirx == 0 && pacmand_diry == 1 && (current_map_block_data & MAP_WALL_DOWN) != 0)) 
            {
                pacman_dirx = 0;
                pacmand_diry = 0;
            }
        }

        
        pacman_posx = pacman_posx + PACMAN_SPEED * pacman_dirx;
        pacman_posy = pacman_posy + PACMAN_SPEED * pacmand_diry;
        
    }
    
    private void drawPacman(Graphics2D g2d) 
    {
        if (view_dx == -1) 
        {
            drawPacnanLeft(g2d);
        } 
        else if (view_dx == 1) 
        {
            drawPacmanRight(g2d);
        } 
        else if (view_dy == -1) 
        {
            drawPacmanUp(g2d);
        } 
        else 
        {
            drawPacmanDown(g2d);
        }
    }

    private void drawPacmanUp(Graphics2D g2d) 
    {
        switch (pacman_animation_pos) 
        {
            case 1:
                g2d.drawImage(pacman2up_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            default:
                g2d.drawImage(pacman1_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) 
    {
        switch (pacman_animation_pos) 
        {
            case 1:
                g2d.drawImage(pacman2down_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            default:
                g2d.drawImage(pacman1_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) 
    {
        switch (pacman_animation_pos) 
        {
            case 1:
                g2d.drawImage(pacman2left_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            default:
                g2d.drawImage(pacman1_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) 
    {
        switch (pacman_animation_pos) 
        {
            case 1:
                g2d.drawImage(pacman2right_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
            default:
                g2d.drawImage(pacman1_img, pacman_posx + 1, pacman_posy + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) 
    {
        int  current_map_block = 0;
        int block_xpos, block_ypos;
        int next_move_number = 0;
        
        for (block_ypos = 0; block_ypos < SCREEN_PIXEL_SIZE; block_ypos += BLOCK_PIXEL_SIZE) 
        {
            for (block_xpos = 0; block_xpos < SCREEN_PIXEL_SIZE; block_xpos += BLOCK_PIXEL_SIZE) 
            {
            	g2d.setStroke(new BasicStroke(2));   
            	
                g2d.setColor(mazeColor);

                
                if ((map_data[current_map_block] & MAP_WALL_LEFT) != 0) 
                { 

                    g2d.drawLine(block_xpos, 
                    			 block_ypos, 
                    			 block_xpos, 
                    			 block_ypos + BLOCK_PIXEL_SIZE - 1);
                }

                if ((map_data[current_map_block] & MAP_WALL_UP) != 0) 
                { 
                    g2d.drawLine(block_xpos, 
                    			 block_ypos, 
                    			 block_xpos + BLOCK_PIXEL_SIZE - 1, 
                    			 block_ypos);
                }

                if ((map_data[current_map_block] & MAP_WALL_RIGHT) != 0) 
                { 
                    g2d.drawLine(block_xpos + BLOCK_PIXEL_SIZE - 1, 
                    			 block_ypos, 
                    			 block_xpos + BLOCK_PIXEL_SIZE - 1,
                    			 block_ypos + BLOCK_PIXEL_SIZE - 1);
                }

                if ((map_data[current_map_block] & MAP_WALL_DOWN) != 0) 
                { 
                    g2d.drawLine(block_xpos, 
                    			 block_ypos + BLOCK_PIXEL_SIZE - 1, 
                    			 block_xpos + BLOCK_PIXEL_SIZE - 1,
                    			 block_ypos + BLOCK_PIXEL_SIZE - 1);
                }

                if ((map_data[current_map_block] & GAME_POINT) != 0) 
                { 
                    g2d.setColor(dotColor);
                    g2d.fillRect(block_xpos + 11, 
                    			 block_ypos + 11, 
                    			 PIXEL_SIZE_OF_POINTS, 
                    			 PIXEL_SIZE_OF_POINTS);
                }
                
                if(final_ghost_path.contains(current_map_block))
                {	
               	 	g2d.setColor(ghostPathColor);
               	 
                    g2d.drawLine(block_xpos, 
               			 block_ypos, 
               			 block_xpos, 
               			 block_ypos + BLOCK_PIXEL_SIZE - 1);
                    
                    g2d.drawLine(block_xpos, 
               			 block_ypos, 
               			 block_xpos + BLOCK_PIXEL_SIZE - 1, 
               			 block_ypos);
                    
                    g2d.drawLine(block_xpos + BLOCK_PIXEL_SIZE - 1, 
               			 block_ypos, 
               			 block_xpos + BLOCK_PIXEL_SIZE - 1,
               			 block_ypos + BLOCK_PIXEL_SIZE - 1);
                    
                    g2d.drawLine(block_xpos, 
               			 block_ypos + BLOCK_PIXEL_SIZE - 1, 
               			 block_xpos + BLOCK_PIXEL_SIZE - 1,
               			 block_ypos + BLOCK_PIXEL_SIZE - 1);
                    
                    String s = Integer.toString(final_ghost_path.indexOf(current_map_block));
                    Font small = new Font("Helvetica", Font.BOLD, 18);

                    g2d.setColor(ghostPathColor);
                    g2d.setFont(small);
                    g2d.drawString(s, (block_xpos + 1), block_ypos + 20);
                    
                    next_move_number ++;
				}
                
                current_map_block++;
            }
        }
    }

    private void initGame() {

        pacman_stock = 3;
        pacman_score = 0;
        initiateNextLevel();
        linkAIMap();
        N_GHOSTS = 1;
        current_speed = 3;
    }

    private void initiateNextLevel() 
    {
        int current_map_block_id;
        
        for (current_map_block_id = 0; current_map_block_id < NUM_OF_BLOCKS * NUM_OF_BLOCKS; current_map_block_id++) 
        {
            map_data[current_map_block_id] = map_content[current_map_block_id];
        }

        linkAIMap();
        resetGhostsAndPacman();
    }

    private void resetGhostsAndPacman() 
    {
        int ghost_id;
        int dx = 1;
        int random;

        ghost_posy[0] = 0 * BLOCK_PIXEL_SIZE;
        ghost_posx[0] = 5 * BLOCK_PIXEL_SIZE;
        ghost_diry[0] = 1;
        ghost_dirx[0] = 0;
        dx = -dx;
        ghost_speed[0] = 4;
        		
        ghost_posy[1] = 0 * BLOCK_PIXEL_SIZE;
        ghost_posx[1] = 9 * BLOCK_PIXEL_SIZE;
        ghost_diry[1] = 1;
        ghost_dirx[1] = 0;
        dx = -dx;
        
        ghost_speed[1] = 6;
        		
//        for (ghost_id = 0; ghost_id < N_GHOSTS; ghost_id++) 
//        {
//            random = (int) (Math.random() * (current_speed + 1));
//
//            if (random > current_speed) 
//            {
//                random = current_speed;
//            }
//
//            ghost_speed[ghost_id] = VALID_SPEEDS[random];
//        }

        pacman_posx = 7 * BLOCK_PIXEL_SIZE;
        pacman_posy = 10 * BLOCK_PIXEL_SIZE;
        pacman_dirx = 0;
        pacmand_diry = 0;
        requested_dirx = 0;
        requested_diry = 0;
        view_dx = -1;
        view_dy = 0;
        pacman_dead = false;
    }

    private void loadImages() 
    {
        ghost_img = 		new ImageIcon("images/ghost.png").getImage();
        pacman1_img = 		new ImageIcon("images/pacman.png").getImage();
        pacman2up_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman3up_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman4up_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman2down_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman3down_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman4down_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman2left_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman3left_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman4left_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman2right_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman3right_img = 	new ImageIcon("images/pacman.png").getImage();
        pacman4right_img = 	new ImageIcon("images/pacman.png").getImage();
    }

    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) 
    {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, board_pixel_dimention.width, board_pixel_dimention.height);

        drawMaze(g2d);
        drawScore(g2d);
        animatePacman();

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
                    requested_dirx = -1;
                    requested_diry = 0;
                } 
                else if (key == KeyEvent.VK_RIGHT)
                {
                    requested_dirx = 1;
                    requested_diry = 0;
                } 
                else if (key == KeyEvent.VK_UP) 
                {
                    requested_dirx = 0;
                    requested_diry = -1;
                } 
                else if (key == KeyEvent.VK_DOWN) 
                {
                    requested_dirx = 0;
                    requested_diry = 1;
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
                requested_dirx = 0;
                requested_diry = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        repaint();
    }
}