import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * LobbyInterface class:
 * Shows a lobby to setup the players and ruleset ready to start a game.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class LobbyInterface extends WndInterface {
    /**
     * List of the players. Always contains 4. Those that are enabled are considered active.
     */
    private final List<LobbyPlayer> playerList;
    /**
     * List of buttons visible on the lobby interface.
     */
    private final List<Button> buttonList;
    /**
     * Reference to the GamePanel for callbacks.
     */
    private final GamePanel gamePanel;
    /**
     * The RuleSet being configured.
     */
    private final RuleSet ruleSet;

    /**
     * String showing the stack rule state.
     */
    private String stackRuleStateStr;
    /**
     * String showing the draw rule state.
     */
    private String drawTillPlayableRuleStateStr;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds Area to display the lobby in.
     */
    public LobbyInterface(Rectangle bounds, GamePanel gamePanel) {
        super(bounds);
        this.gamePanel = gamePanel;
        playerList = new ArrayList<>();
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.ThisPlayer,
                new Rectangle(new Position(20,100),bounds.width/2, 100)));
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.AIPlayer,
                new Rectangle(new Position(20,100+120),bounds.width/2, 100)));
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.AIPlayer,
                new Rectangle(new Position(20,100+120*2),bounds.width/2, 100)));
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.AIPlayer,
                new Rectangle(new Position(20,100+120*3),bounds.width/2, 100)));

        buttonList = new ArrayList<>();
        buttonList.add(new Button(new Position(bounds.width/4-150, bounds.height-100),300,60,
                "Toggle Number of Players", 1));
        buttonList.add(new Button(new Position(bounds.width*3/4-150, bounds.height-100),300,60,
                "Start Game", 2));
        ruleSet = new RuleSet();
        toggleStackRule();
        toggleDrawTillCanPlayRule();
        buttonList.add(new Button(new Position(bounds.width/2+120, 150),150,40, "Toggle Rule", 3));
        buttonList.add(new Button(new Position(bounds.width/2+120, 200),150,40, "Toggle Rule", 4));
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
     * Draws all the elements required for the LobbyInterface.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        // Draw backgrounds
        g.setColor(new Color(205, 138, 78, 128));
        g.fillRect(10, 80, bounds.width/2+20, 500);
        g.fillRect(bounds.width/2+40, 80, bounds.width/2-60, 500);
        g.setColor(Color.BLACK);
        g.drawRect(10, 80, bounds.width/2+20, 500);
        g.drawRect(bounds.width/2+40, 80, bounds.width/2-60, 500);

        // Draw interaction elements
        buttonList.forEach(button -> button.paint(g));
        playerList.forEach(lobbyPlayer -> lobbyPlayer.paint(g));

        // Draw rule status elements
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Rules", bounds.width/2+280, 120);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(stackRuleStateStr, bounds.width/2+300, 180);
        g.drawString(drawTillPlayableRuleStateStr, bounds.width/2+300, 230);

        // Pause overlay
        if(!isEnabled()) {
            g.setColor(new Color(144, 143, 143, 204));
            g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        }
    }

    /**
     * updates the hover status of elements. Does nothing if not enabled.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
        playerList.forEach(lobbyPlayer -> lobbyPlayer.updateHoverState(mousePosition));
    }

    /**
     * Does nothing if not enabled. Checks for presses on the buttons and players
     * with methods to handle the interactions as necessary.
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
        playerList.forEach(lobbyPlayer -> {
            if (lobbyPlayer.isPositionInside(mousePosition))
                lobbyPlayer.iterateStrategy();
        });
    }

    /**
     * Handles the button actions by mapping the IDs to actions.
     *
     * @param actionID ID mapped to an action relevant to the button.
     */
    private void handleButtonPress(int actionID) {
        switch (actionID) {
            case 1 -> toggleNumberOfPlayers();
            case 2 -> gamePanel.startGame(playerList, ruleSet);
            case 3 -> toggleStackRule();
            case 4 -> toggleDrawTillCanPlayRule();
        }
    }

    /**
     * Toggles player 2 and 3 between enabled and disabled states.
     */
    private void toggleNumberOfPlayers() {
        playerList.get(2).setEnabled(!playerList.get(2).isEnabled());
        playerList.get(3).setEnabled(!playerList.get(3).isEnabled());
    }

    /**
     * Toggles the stacking rule and updates the message.
     */
    private void toggleStackRule() {
        ruleSet.setCanStackCards(!ruleSet.canStackCards());
        stackRuleStateStr = "Stacking +2/+4: "
                + (ruleSet.canStackCards() ? "On" : "Off");
    }

    /**
     * Toggles the draw rule and updates the message.
     */
    private void toggleDrawTillCanPlayRule() {
        ruleSet.setDrawnTillCanPlay(!ruleSet.shouldDrawnTillCanPlay());
        drawTillPlayableRuleStateStr = "Draw Till Can Play: "
                + (ruleSet.shouldDrawnTillCanPlay() ? "On" : "Off");
    }
}
