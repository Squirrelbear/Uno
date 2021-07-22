import java.awt.*;
import java.util.Random;

public class LobbyPlayer extends Rectangle {
    private final int playerID;
    private String playerName;
    private final Player.PlayerType playerType;
    private AIPlayer.AIStrategy aiStrategy;
    private String strategyStr;
    private boolean isEnabled;
    private boolean isHovered;

    public LobbyPlayer(int playerID, String playerName, Player.PlayerType playerType, Rectangle bounds) {
        super(bounds.position, bounds.width, bounds.height);
        this.playerID = playerID;
        this.playerName = playerName;
        this.playerType = playerType;
        aiStrategy = AIPlayer.AIStrategy.Random;
        strategyStr = aiStrategy.toString();
        isEnabled = true;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void iterateStrategy() {
        switch (aiStrategy) {
            case Random -> aiStrategy = AIPlayer.AIStrategy.Offensive;
            case Offensive -> aiStrategy = AIPlayer.AIStrategy.Defensive;
            case Defensive -> aiStrategy = AIPlayer.AIStrategy.Chaotic;
            case Chaotic -> aiStrategy = AIPlayer.AIStrategy.Random;
        }
        strategyStr = aiStrategy.toString();
    }

    public void paint(Graphics g) {
        if(!isEnabled) return;

        if(isHovered) {
            g.setColor(new Color(115, 156, 58, 204));
        } else {
            g.setColor(new Color(68, 97, 28, 204));
        }
        g.fillRect(position.x, position.y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(position.x, position.y, width, height);

        g.drawString(playerName, position.x + 20, position.y+40);

        if(playerType == Player.PlayerType.AIPlayer) {
            g.drawString(strategyStr, position.x+200, position.y+40);
        }
    }

    public void updateHoverState(Position mousePosition) {
        isHovered = isPositionInside(mousePosition);
    }
}
