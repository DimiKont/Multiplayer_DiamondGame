package src;

import java.io.Serializable;

class GameData implements Serializable {
        int player1X;
        int player1Y;
        int player2X;
        int player2Y;
        int diamondX;
        int diamondY;
        String[] map;

        GameData(int player1X, int player1Y,int player2X, int player2Y, int diamondX, int diamondY, String[] map) {
            this.player1X = player1X;
            this.player1Y = player1Y;
            this.player2X = player2X;
            this.player2Y = player2Y;
            this.diamondX = diamondX;
            this.diamondY = diamondY;
            this.map = map;
        }
    }