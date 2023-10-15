package src;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Server {
    private static final String[] MAP = {
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

    private int player1X;
    private int player1Y;
    private int player2X;
    private int player2Y;
    private int diamondX;
    private int diamondY;
    private int currentPlayerTurn;

    private void initGame() {
        Random rand = new Random();

        do {
            player1X = rand.nextInt(8) + 1;
            player1Y = rand.nextInt(16) + 1;
        } while (MAP[player1X].charAt(player1Y) == '*');

        do {
            player2X = rand.nextInt(8) + 1;
            player2Y = rand.nextInt(16) + 1;
        } while (MAP[player2X].charAt(player2Y) == '*' || (player2X == player1X && player2Y == player1Y));

        do {
            diamondX = rand.nextInt(8) + 1;
            diamondY = rand.nextInt(16) + 1;
        } while (MAP[diamondX].charAt(diamondY) == '*' || (diamondX == player1X && diamondY == player1Y) || (diamondX == player2X && diamondY == player2Y));
        currentPlayerTurn = 1;
    }

    public Server() {
        initGame();
    }

    private static ObjectOutputStream player1Out ;
    private static ObjectOutputStream player2Out ;
    private static ObjectInputStream player1In;
    private static ObjectInputStream player2In;

    public static void main(String[] args) {
        Server server = new Server();

        try {
            ServerSocket serverSocket = new ServerSocket(30000);

            System.out.println("Waiting for Player 1...");
            Socket player1Socket = serverSocket.accept();
            player1Out = new ObjectOutputStream(player1Socket.getOutputStream());
            player1Out.writeObject(new GameData(server.player1X, server.player1Y, server.player2X ,server.player2Y, server.diamondX, server.diamondY, MAP));
            System.out.println("Player 1 connected.");

            System.out.println("Waiting for Player 2...");
            Socket player2Socket = serverSocket.accept();
            player2Out = new ObjectOutputStream(player2Socket.getOutputStream());
            player2Out.writeObject(new GameData(server.player2X, server.player2Y, server.player1X, server.player1Y,server.diamondX, server.diamondY, MAP));
            System.out.println("Player 2 connected.");

            player1In = new ObjectInputStream(player1Socket.getInputStream());
            player2In = new ObjectInputStream(player2Socket.getInputStream());
    
            while(true)
            {
                server.run(player1Socket, player2Socket);
            }
            /*player1Socket.close();
            player2Socket.close();
            serverSocket.close();*/
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    


    private void run(Socket client1 , Socket client2) throws IOException 
    {
        // Create input and output streams for clients
        DataInputStream in1 = new DataInputStream(client1.getInputStream());
        DataOutputStream out1 = new DataOutputStream(client1.getOutputStream());

        DataInputStream in2 = new DataInputStream(client2.getInputStream());
        DataOutputStream out2 = new DataOutputStream(client2.getOutputStream());

        // Receive integers from clients
        int num1 = in1.readInt();
        int num3 = in1.readInt();

        int num2 = in2.readInt();
        int num4 = in2.readInt();

        // Send integers to the opposite clients
        out2.writeInt(num1);
        out2.writeInt(num3);

        out1.writeInt(num2);
        out1.writeInt(num4);

        if(checkWin())
        {
            System.exit(0);
        }
        else if(checkLose())
        {
            System.exit(0);
        }


    }

    private boolean checkWin() {
        return player1X == diamondX && player1Y == diamondY;
    }

    private boolean checkLose() {
        return player2X == diamondX && player2Y == diamondY;
    }


    // Create a data class to send game information to clients
    // static class GameData implements Serializable {
    //     int player1X;
    //     int player1Y;
    //     int player2X;
    //     int player2Y;
    //     int diamondX;
    //     int diamondY;
    //     String[] map;

    //     GameData(int player1X, int player1Y,int player2X, int player2Y, int diamondX, int diamondY, String[] map) {
    //         this.player1X = player1X;
    //         this.player1Y = player1Y;
    //         this.player2X = player2X;
    //         this.player2Y = player2Y;
    //         this.diamondX = diamondX;
    //         this.diamondY = diamondY;
    //         this.map = map;
    //     }
    // }
}
