import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
public class DiamondGameClient extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int CELL_SIZE = 40;

    private static final Color WALL_COLOR = Color.BLACK;
    private static final Color PLAYER_COLOR = Color.BLUE;
    private static final Color BOT_COLOR = Color.RED;
    private static final Color DIAMOND_COLOR = Color.YELLOW;

    private static final Font FONT = new Font("Arial", Font.BOLD, 24);

    private int player1X;
    private int player1Y;
    private int player2X;
    private int player2Y;
    private int diamondX;
    private int diamondY;
    private String[] map;
    private static Socket socket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Queue<Character> keyQueue;

    public DiamondGameClient(Socket socket) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        keyQueue = new LinkedList<Character>();
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            GameData gameData = (GameData) in.readObject();
            player1X = gameData.player1X;
            player1Y = gameData.player1Y;
            player2X = gameData.player2X;
            player2Y = gameData.player2Y;
            diamondX = gameData.diamondX;
            diamondY = gameData.diamondY;
            map = gameData.map;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 17; j++) {
                if (map[i].charAt(j) == '*') {
                    g.setColor(WALL_COLOR);
                } else {
                    g.setColor(Color.WHITE);
                }

                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        if (player1X == player2X && player1Y == player2Y) {
            g.setColor(Color.MAGENTA);
            g.fillOval(player1Y * CELL_SIZE, player1X * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        else
        {

            g.setColor(PLAYER_COLOR);
            g.fillOval(player1Y * CELL_SIZE, player1X * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            g.setColor(BOT_COLOR);
            g.fillOval(player2Y * CELL_SIZE, player2X * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        g.setColor(DIAMOND_COLOR);
        g.fillOval(diamondY * CELL_SIZE, diamondX * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        g.setColor(Color.BLACK);
        g.setFont(FONT);
        g.drawString("Use WASD to move, Q to quit", 10, HEIGHT - 10);
    }

    private void movePlayer(char move) {
        int newX = player1X;
        int newY = player1Y;

        switch (move) {
            case 'w':
                newX--;
                break;
            case 's':
                newX++;
                break;
            case 'a':
                newY--;
                break;
            case 'd':
                newY++;
                break;
            default:
                return;
        }

        if (map[newX].charAt(newY) != '*') {
            player1X = newX;
            player1Y = newY;
        }
    }


    public static void main(String[] args) {
        try {
            socket = new Socket("tanel.ddns.net", 30000);
            JFrame frame = new JFrame("Diamond Game Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            DiamondGameClient gameClient = new DiamondGameClient(socket);
            frame.add(gameClient);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            while(true)
            {
                gameClient.run(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(Socket socket) throws IOException 
    {    
            if(!keyQueue.isEmpty())
            {
                char move = keyQueue.remove();
                movePlayer(move);
            }
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Send the integers to the server
            out.writeInt(player1X);
            out.writeInt(player1Y);

            // Receive the exchanged integers from the server
            player2X= in.readInt();
            player2Y = in.readInt();

            if(checkWin())
            {
                System.out.println("You win!");
                System.exit(0);
            }
            else if(checkLose())
            {
                System.out.println("You lose!");
                System.exit(0);
            }

            repaint();
    }

    private boolean checkWin() {
        return player1X == diamondX && player1Y == diamondY;
    }

    private boolean checkLose() {
        return player2X == diamondX && player2Y == diamondY;
    }

    // Define a data class to send move information
    static class MoveData implements Serializable {
        char move;

        MoveData(char move) {
            this.move = move;
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();

        if (key == 'q') {
            System.exit(0);
        }

        keyQueue.add(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
