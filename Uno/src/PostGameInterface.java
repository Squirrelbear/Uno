import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * PostGameInterface class:
 * Defines a simple interface that shows the scores from the just completed round.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class PostGameInterface extends WndInterface {
    /**
     * List of buttons visible on the interface.
     */
    private final List<Button> buttonList;
    /**
     * Reference to the players that are being shown for scores.
     */
    private final List<Player> players;
    /**
     * Reference to the Ruleset to check for score limits.
     */
    private final RuleSet ruleSet;
    /**
     * Reference to the GamePanel for exiting the interface.
     */
    private final GamePanel gamePanel;
    private List<String> playerStrings;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds Bounds of the interface.
     */
    public PostGameInterface(Rectangle bounds, List<Player> playerList, RuleSet ruleSet, GamePanel gamePanel) {
        super(bounds);
        this.players = playerList;
        this.ruleSet = ruleSet;
        this.gamePanel = gamePanel;

        buttonList = new ArrayList<>();
        buttonList.add(new Button(new Position(bounds.width/2-125-250-20,620), 250, 40,
                "Return to Lobby",1));
        buttonList.add(new Button(new Position(bounds.width/2-125,620), 250, 40,
                "Continue Next Round",2));
        buttonList.add(new Button(new Position(bounds.width/2+125+20,620), 250, 40,
                "New Game Same Settings",3));

        playerStrings = new ArrayList<>();
        for(Player player : playerList) {
            playerStrings.add((player.getPlayerType() == Player.PlayerType.ThisPlayer ? "You: " : "AI: ")
                    + player.getPlayerName());
        }
    }

    /**
     * Does nothing.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Draws all the elements of the interface.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        drawBackground(g);
        gamePanel.paintUnoTitle(g, bounds);
        drawPlayers(g);

        buttonList.forEach(button -> button.paint(g));

        // Pause overlay
        if(!isEnabled()) {
            g.setColor(new Color(144, 143, 143, 204));
            g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        }
    }

    /**
     * Draws the background and game title.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawBackground(Graphics g) {
        g.setColor(new Color(205, 138, 78, 128));
        g.fillRect(bounds.width/4, 80, bounds.width/2, 500);
        g.setColor(Color.BLACK);
        g.drawRect(bounds.width/4, 80, bounds.width/2, 500);
    }

    private void drawPlayers(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        for(int i = 0; i < players.size(); i++) {
            g.drawString(playerStrings.get(i), bounds.width/4 + 20, 120+i*50);
        }
    }

    /**
     * Does nothing if not enabled. Checks all the buttons for presses and handles actions if necessary.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;
        buttonList.forEach(button -> {
            if(button.isPositionInside(mousePosition))
                handleButtonPress(button.getActionID());
        });
    }

    /**
     * Does nothing if not enabled. Updates the hover state of all buttons.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;
        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
    }

    /**
     * Looks up the action to apply based on an actionID for a button.
     *
     * @param actionID The actionID for a button that was pressed.
     */
    private void handleButtonPress(int actionID) {
        switch(actionID) {
            case 1 -> gamePanel.showLobby();
            case 2 -> gamePanel.startNextRound(players, ruleSet);
            case 3 -> startNewGameWithSameSettings();
        }
    }

    /**
     * Wipes the score of all players and then starts a new game.
     */
    private void startNewGameWithSameSettings() {
        players.forEach(player -> player.resetScore());
        gamePanel.startNextRound(players, ruleSet);
    }
}
