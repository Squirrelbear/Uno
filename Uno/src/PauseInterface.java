import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * PauseInterface class:
 * A simple pause interface that allows a couple of buttons to control
 * game flow or otherwise just pause for a moment.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class PauseInterface extends WndInterface {
    /**
     * A list of all the buttons in the interface.
     */
    private final List<Button> buttonList;
    /**
     * A reference to the GamePanel to call back to it.
     */
    private final GamePanel gamePanel;

    /**
     * Initialise the interface with bounds makes it ready to use.
     *
     * @param bounds The region to draw this interface in.
     */
    public PauseInterface(Rectangle bounds, GamePanel gamePanel) {
        super(bounds);
        this.gamePanel = gamePanel;
        buttonList = new ArrayList<>();
        buttonList.add(new Button(new Position(bounds.position.x+6, bounds.position.y+6),
                bounds.width-12, 30, "Resume", 1));
        buttonList.add(new Button(new Position(bounds.position.x+6, bounds.position.y+6+30+6),
                bounds.width-12, 30, "Quit", 2));
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
     * Draws a background and all the buttons.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(165, 177, 94, 205));
        g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.setColor(Color.BLACK);
        g.drawRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        buttonList.forEach(button -> button.paint(g));
    }

    /**
     * Does nothing if not enabled. Updates hover states of all buttons.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        for (Button button : buttonList) {
            button.setHovering(button.isPositionInside(mousePosition));
        }
    }

    /**
     * Does nothing if not enabled. Checks if a button has been clicked and responds to it.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        for (Button button : buttonList) {
            if(button.isPositionInside(mousePosition)) {
                handleButtonAction(button.getActionID());
                break;
            }
        }
    }

    /**
     * Handles the actionID by mapping each ID to an action related to the button.
     *
     * @param actionID The actionID to map to an action from the pause menu.
     */
    private void handleButtonAction(int actionID) {
        switch(actionID) {
            case 1 -> gamePanel.setPauseState(false);
            case 2 -> gamePanel.quitGame();
        }
    }
}
