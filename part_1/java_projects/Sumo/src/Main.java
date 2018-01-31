import java.awt.EventQueue;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Main extends JFrame 
{
    
    public Main() { initUI(); }
    
    private void initUI() 
    {
        add(new SumoGraphics());

        setTitle("Robot Sumo Simulator");
        setSize(811, 833);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);        
    }

    public static void main(String[] args) 
    {        
        
        EventQueue.invokeLater(new Runnable() 
        { 
            @Override
            public void run() 
            {
                Main ex = new Main();
                ex.setVisible(true);
            }
        });
    }
}