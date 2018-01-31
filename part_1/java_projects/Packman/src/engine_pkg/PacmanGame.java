package engine_pkg;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class PacmanGame extends JFrame 
{

	public PacmanGame() 
	{
        initUI();
    }
    
    private void initUI() 
    {
        add(new Game2());
        
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 420);
        setLocationRelativeTo(null);
        setVisible(true);        
    }

    public static void main(String[] args) 
    {
        EventQueue.invokeLater(() -> 
        {
            PacmanGame ex = new PacmanGame();
            ex.setVisible(true);
        });
    }
}