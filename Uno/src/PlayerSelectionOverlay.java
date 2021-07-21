import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * PlayerSelectionOverlay class:
 * Defines the overlay used for choosing to choosing a player to swap with.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class PlayerSelectionOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * List of buttons that can be used in the overlay. Includes a Challenge and Decline button.
     */
    private final List<Button> buttonList;
    /**
     * Reference to the TurnAction that triggered the display of this overlay.
     */
    private TurnActionFactory.TurnDecisionAction currentAction;

    /**
     * Initialises the overlay with a button for each of the other players.
     * The buttons are centred inside the player's regions.
     *
     * @param bounds The bounds of the entire game area. The buttons are offset from the centre.
     */
    public PlayerSelectionOverlay(Rectangle bounds, List<Player> playerList) {
        super(bounds);
        setEnabled(false);
        buttonList = new ArrayList<>();
        for(int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i).getPlayerType() != Player.PlayerType.ThisPlayer) {
                Position centre = playerList.get(i).getCentreOfBounds();
                buttonList.add(new Button(new Position(centre.x-100,centre.y-20), 200, 40, "Choose Player",i+1));
            }
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
     * Draws all the buttons.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));
    }

    /**
     * Makes the overlay visible.
     *
     * @param currentAction The action used to trigger this interface.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        setEnabled(true);
    }

    /**
     * Does nothing if not enabled. Updates the hover state for all buttons.
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
     * Does nothing if not enabled. Checks for a click in the buttons.
     * If a button has been clicked the action is registered to close the overlay.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        for (Button button : buttonList) {
            if(button.isPositionInside(mousePosition)) {
                setEnabled(false);
                currentAction.injectFlagProperty(button.getActionID());
                break;
            }
        }
    }
}
