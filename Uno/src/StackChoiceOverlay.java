import java.awt.*;

/**
 * Uno
 *
 * StackChoiceOverlay class:
 * Defines the overlay used for choosing to Decline/Stack against a +2.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class StackChoiceOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * The decline button that simply accepts taking the card drawing.
     */
    private final Button declineButton;
    /**
     * Reference to the TurnAction that triggered the display of this overlay.
     */
    private TurnActionFactory.TurnDecisionAction currentAction;
    /**
     * Reference to the bottom player who is making the choice.
     */
    private final Player playerReference;

    /**
     * Initialise the decline button and reference to the player for tracking their cards.
     *
     * @param bounds The bounds of the entire game area. The buttons are offset from the centre.
     */
    public StackChoiceOverlay(Rectangle bounds) {
        super(bounds);
        setEnabled(false);
        Position centre = bounds.getCentre();
        declineButton = new Button(new Position(centre.x-50,centre.y+100), 100, 40, "Decline", 0);

        playerReference = CurrentGameInterface.getCurrentGame().getBottomPlayer();
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
     * Draws the Decline button.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        declineButton.paint(g);
    }

    /**
     * Shows the overlay.
     *
     * @param currentAction The action used to trigger this interface.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        setEnabled(true);
    }

    /**
     * Hides the overlay.
     */
    @Override
    public void hideOverlay() {
        setEnabled(false);
    }

    /**
     * Does nothing if not enabled. Updates the hover status of the decline button.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        declineButton.setHovering(declineButton.isPositionInside(mousePosition));
    }

    /**
     * Does nothing if not enabled. Checks for a click on the decline button to handle it.
     * And checks for the player clicking on their cards to allow stacking.
     * If this overlay is visible it is implied that stacking is allowed.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        if(declineButton.isPositionInside(mousePosition)) {
            currentAction.injectFlagProperty(0);
            hideOverlay();
            return;
        }

        Card clickedCard = playerReference.chooseCardFromClick(mousePosition);
        if(clickedCard != null && clickedCard.getFaceValueID() == 10) {
            currentAction.injectProperty("faceValueID", clickedCard.getFaceValueID());
            currentAction.injectProperty("colourID", clickedCard.getColourID());
            currentAction.injectProperty("cardID", clickedCard.getCardID());
            currentAction.injectFlagProperty(1);
            hideOverlay();
        }
    }
}
