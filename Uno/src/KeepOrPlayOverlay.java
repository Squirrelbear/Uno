import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * KeepOrPlayOverlay class:
 * Used when the player has to choose to keep or play a card that has been drawn.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class KeepOrPlayOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * List of buttons consisting of the Keep and Play buttons.
     */
    private final List<Button> buttonList;
    /**
     * Reference to the TurnAction that triggered the display of this overlay.
     */
    private TurnActionFactory.TurnDecisionAction currentAction;
    /**
     * Reference to the dummy card that the overlay is making a decision about.
     */
    private Card cardForChoice;
    /**
     * Position where the card in question is to be placed.
     */
    private final Position cardPosition;

    /**
     * Initialise the Keep and Play buttons and the location where the dummy card has to be placed.
     *
     * @param bounds The bounds of the entire game area. The buttons are offset from the centre.
     */
    public KeepOrPlayOverlay(Rectangle bounds) {
        super(bounds);
        setEnabled(false);
        buttonList = new ArrayList<>();
        Position centre = bounds.getCentre();
        buttonList.add(new Button(new Position(centre.x-150,centre.y+100), 100, 40, "Keep", 0));
        buttonList.add(new Button(new Position(centre.x+50,centre.y+100), 100, 40, "Play", 1));

        cardPosition = new Position(centre.x-Card.CARD_WIDTH/2, centre.y+100+20-Card.CARD_HEIGHT/2);
    }

    /**
     * Not used.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Draws all the buttons and the card related to the choice.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));
        cardForChoice.paint(g);
    }

    /**
     * Creates a dummy version of the card to be shown as part of the overlay
     * and makes the overlay show.
     *
     * @param currentAction The action used to trigger this interface.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        cardForChoice = new Card(currentAction.storedData.get("faceValueID"),
                                currentAction.storedData.get("colourID"),
                                currentAction.storedData.get("cardID"));
        cardForChoice.position.setPosition(cardPosition.x, cardPosition.y);
        setEnabled(true);
    }

    /**
     * Does nothing when not enabled. Updates the hover state of all buttons.
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
     * Does nothing when not enabled. Checks for clicks in the buttons and
     * triggers the correct event when a button is interacted with.
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
