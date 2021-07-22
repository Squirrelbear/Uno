import java.awt.*;

/**
 * Uno
 *
 * LobbyPlayer class:
 * Defines a player in the Lobby menu with functions to modify their settings ready before a game starts.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class LobbyPlayer extends Rectangle {
    /**
     * The unique ID of the player.
     */
    private final int playerID;
    /**
     * The name shown for the player.
     */
    private String playerName;
    /**
     * The type of player (ThisPlayer, AIPlayer, or NetworkPlayer).
     */
    private final Player.PlayerType playerType;
    /**
     * The strategy to use for the AIPlayer type.
     */
    private AIPlayer.AIStrategy aiStrategy;
    /**
     * A String showing the text version of the strategy.
     */
    private String strategyStr;
    /**
     * Visible and included in the collection of players when true.
     */
    private boolean isEnabled;
    /**
     * True when the mouse is over the player.
     */
    private boolean isHovered;

    /**
     * Initialises the object ready to show information about the player.
     *
     * @param playerID The unique ID of the player.
     * @param playerName The name shown for the player.
     * @param playerType The type of player (ThisPlayer, AIPlayer, or NetworkPlayer).
     * @param bounds Region for interacting with this player object in the menu.
     */
    public LobbyPlayer(int playerID, String playerName, Player.PlayerType playerType, Rectangle bounds) {
        super(bounds.position, bounds.width, bounds.height);
        this.playerID = playerID;
        this.playerName = playerName;
        this.playerType = playerType;
        aiStrategy = AIPlayer.AIStrategy.Random;
        strategyStr = aiStrategy.toString();
        isEnabled = true;
    }

    /**
     * Changes the player's name.
     *
     * @param playerName Name to change the player to.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the unique ID for this player.
     *
     * @return The unique ID associated with this player.
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Gets the type of player. (ThisPlayer, AIPlayer, or NetworkPlayer).
     *
     * @return The type of player.
     */
    public Player.PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Sets the enabled state of the player.
     *
     * @param isEnabled When true, the player is included in the list of players for the game.
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Gets the current enabled status.
     *
     * @return When true the player should be visible and included as a player.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Gets the strategy for this player. This is only relevant for AIPlayer types.
     *
     * @return The strategy to be used for the AI.
     */
    public AIPlayer.AIStrategy getAIStrategy() {
        return aiStrategy;
    }

    /**
     * Iterates through the list of AI Strategies to the next one.
     */
    public void iterateStrategy() {
        switch (aiStrategy) {
            case Random -> aiStrategy = AIPlayer.AIStrategy.Offensive;
            case Offensive -> aiStrategy = AIPlayer.AIStrategy.Defensive;
            case Defensive -> aiStrategy = AIPlayer.AIStrategy.Chaotic;
            case Chaotic -> aiStrategy = AIPlayer.AIStrategy.Random;
        }
        strategyStr = aiStrategy.toString();
    }

    /**
     * Does nothing if not enabled. Draws the content showing this player's information.
     *
     * @param g Reference to the Graphics object for rendering.
     */
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

    /**
     * Updates the hovered state of the button object based on where the mouse is.
     *
     * @param mousePosition Position of the mouse.
     */
    public void updateHoverState(Position mousePosition) {
        isHovered = isPositionInside(mousePosition);
    }
}
