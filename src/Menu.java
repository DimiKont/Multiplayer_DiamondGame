package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame implements ActionListener
{
    private final JLabel title = new JLabel("Diamond Game");
    private final JButton singlePlayer = new JButton("Single Player");
    private final JButton multiPlayer = new JButton("Multi Player");
    private final JButton exitButton = new JButton("Exit");

    public Menu()
    {
        super("Diamond Game");
        setLayout(null);

        title.setBounds(280, 50, 400, 100);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        this.add(title);

        singlePlayer.setBounds(300, 200, 200, 50);
        singlePlayer.setFont(new Font("Arial", Font.BOLD, 16));
        singlePlayer.addActionListener(this);
        this.add(singlePlayer);

        multiPlayer.setBounds(300, 300, 200, 50);
        multiPlayer.setFont(new Font("Arial", Font.BOLD, 16));
        multiPlayer.addActionListener(this);
        this.add(multiPlayer);

        exitButton.setBounds(300, 400, 200, 50);
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(this);
        this.add(exitButton);

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setResizable(false);
        this.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == singlePlayer)
        {
            this.dispose(); // Close the GUI window
            DiamondGame.main(new String[0]); // Launch the Diamond Game
        }
        else if (e.getSource() == multiPlayer)
        {
            this.dispose();
        }
        else if (e.getSource() == exitButton)
        {
            System.exit(0);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Menu::new);
    }
}
