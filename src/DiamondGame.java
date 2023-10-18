package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.Queue;

import javax.swing.*;

public class DiamondGame extends JPanel implements ActionListener, KeyListener
{
//    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int CELL_SIZE = 40;

    private static final Color WALL_COLOR = Color.BLACK;
    private static final Color PLAYER_COLOR = Color.BLUE;
    private static final Color BOT_COLOR = Color.RED;
    private static final Color DIAMOND_COLOR = Color.YELLOW;

    private static final Font FONT = new Font("Arial", Font.BOLD, 24);

    private static final String[] MAP = 
    {
        "*****************",
        "*...*.....*.*...*",
        "*....*.......*..*",
        "*...*...*..*....*",
        "*.*....*....*...*",
        "*...*....*.*....*",
        "*...****.*...*..*",
        "*...*.....*.....*",
        "*****************"
    };

    private int playerX;
    private int playerY;
    private int botX;
    private int botY;
    private int diamondX;
    private int diamondY;

    private JLabel winMessage;
    private JButton restartGameButton = new JButton("Restart");
    private JButton exitGame = new JButton("Exit");

    private Queue<Character> keyQueue;

    public DiamondGame()
    {
        setLayout(null);
        winMessage = new JLabel();
        winMessage.setBounds(300, 400, 200, 50);
        add(winMessage);

        restartGameButton.setBounds(550, 420, 150, 50);
        restartGameButton.addActionListener(e -> restartGame());
        add(restartGameButton);

        exitGame.setBounds(550, 500, 150, 50);
        exitGame.addActionListener(e -> System.exit(0));
        add(exitGame);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        keyQueue = new LinkedList<Character>();

        spawnObjects();
    }

    private void spawnObjects()
    {
        Random rand = new Random();

        // Spawn player
        do {
            playerX = rand.nextInt(8) + 1;
            playerY = rand.nextInt(16) + 1;
        } while (MAP[playerX].charAt(playerY) == '*');

        // Spawn bot
        do {
            botX = rand.nextInt(8) + 1;
            botY = rand.nextInt(16) + 1;
        } while (MAP[botX].charAt(botY) == '*' || (botX == playerX && botY == playerY));

        // Spawn diamond
        do {
            diamondX = rand.nextInt(8) + 1;
            diamondY = rand.nextInt(16) + 1;
        } while (MAP[diamondX].charAt(diamondY) == '*' || (diamondX == playerX && diamondY == playerY) || (diamondX == botX && diamondY == botY));
    }

    class Node
    {
        int x, y;
        int cost;
        Node parent;

        public Node(int x, int y, int cost, Node parent)
        {
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.parent = parent;
        }
    }

    private List<Node> findPath()
    {
        Node startNode = new Node(botX, botY, 0, null);
        Node targetNode = new Node(diamondX, diamondY, 0, null);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
        openSet.add(startNode);

        Set<Node> closedSet = new HashSet<>();

        while (!openSet.isEmpty())
        {
            Node current = openSet.poll();

            if (current.x == targetNode.x && current.y == targetNode.y)
            {
                List<Node> path = new ArrayList<>();

                while (current != null) 
                {
                    path.add(current);
                    current = current.parent;
                }

                Collections.reverse(path);
                return path;
            }

            closedSet.add(current);

            int[] dx = {0, 0, -1, 1};
            int[] dy = {-1, 1, 0, 0};

            for (int i = 0; i < 4; i++)
            {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];

                if (newX >= 0 && newX < MAP.length && newY >= 0 && newY < MAP[0].length() && MAP[newX].charAt(newY) != '*'
                        && !closedSet.contains(new Node(newX, newY, 0, null))) 
                {
                    int newCost = current.cost + 1;
                    Node neighbor = new Node(newX, newY, newCost, current);
                    openSet.add(neighbor);
                }
            }
        }

        return null;
    }

    private void movePlayer(char move)
    {
        int newX = playerX;
        int newY = playerY;

        switch (move)
        {
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

        if (MAP[newX].charAt(newY) != '*')
        {
            playerX = newX;
            playerY = newY;
        }

    }

    private void moveBot()
    {
        List<Node> path = findPath();

        if (path != null && path.size() > 1)
        {
            Node nextNode = path.get(1);
            botX = nextNode.x;
            botY = nextNode.y;
        }
    }

    private boolean playerWin() {return playerX == diamondX && playerY == diamondY;}

    private boolean botWin() {return botX == diamondX && botY == diamondY;}

    private void restartGame()
    {
        SwingUtilities.getWindowAncestor(this).dispose();

        this.spawnObjects();
        this.winMessage.setText("");
        this.keyQueue.clear(); // Clear the keyQueue
        this.repaint();
        this.drawGame();
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        // if(e.getSource() == restartGameButton)
        // {
        //     SwingUtilities.getWindowAncestor(this).dispose();

        //     this.spawnObjects();
        //     this.winMessage.setText("");
        //     this.repaint();
        //     this.drawGame();
        // }
        // else if (e.getSource() == exitGame)
        // {
        //     System.exit(0);
        // }
    }

    private void displayMap(Graphics g)
    {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 17; j++)
            {
                if (MAP[i].charAt(j) == '*')
                {
                    g.setColor(WALL_COLOR);
                }
                else
                {
                    g.setColor(Color.WHITE);
                }

                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        displayMap(g);

        if (playerX == botX && playerY == botY)
        {
            g.setColor(Color.MAGENTA);
            g.fillOval(botY * CELL_SIZE, botX * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        else
        {
            g.setColor(PLAYER_COLOR);
            g.fillOval(playerY * CELL_SIZE, playerX * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            g.setColor(BOT_COLOR);
            g.fillOval(botY * CELL_SIZE, botX * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        g.setColor(DIAMOND_COLOR);
        g.fillOval(diamondY * CELL_SIZE, diamondX * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        g.setColor(Color.BLACK);
        g.setFont(FONT);
        g.drawString("Use WASD to move, Q to quit", 10, HEIGHT - 10);
    }


    private boolean checkWinner()
    {
        winMessage.setFont(new Font("Arial", Font.BOLD, 24));

        if (playerWin())
        {
            winMessage.setText("You won!!");
            return true;
        }
        else if (botWin())
        {
            winMessage.setText("Bot won!!");
            return true;
        }

        return false;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        char key = e.getKeyChar();

        if (key == 'q')
        {
            System.exit(0);
        }

        if (key == 'w' || key == 'a' || key == 's' || key == 'd') {
            keyQueue.offer(key); // Add to keyQueue only if WASD keys are pressed
        }

//        keyQueue.offer(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void drawGame()
    {
        JFrame frame = new JFrame("Diamond Game");
        DiamondGame game = new DiamondGame();

        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Thread gameThread = new Thread(() -> game.run());
        gameThread.start();
    }

    public void run()
    {
        boolean isPlayerTurn = true;

        while (true)
        {
            if (!keyQueue.isEmpty() && isPlayerTurn)
            {
                char move = keyQueue.poll();
                movePlayer(move);
                isPlayerTurn = false;
            }

            if (!isPlayerTurn)
            {
                moveBot();
                isPlayerTurn = true;
            }

            checkWinner();
            // if(checkWinner())
            //     break;

            repaint();
            // SwingUtilities.invokeLater(this::repaint);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new DiamondGame().drawGame());
    }

}