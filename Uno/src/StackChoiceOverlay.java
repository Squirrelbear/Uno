import java.awt.*;

public class StackChoiceOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    private Button declineButton;
    private TurnActionFactory.TurnDecisionAction currentAction;
    private Player playerReference;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public StackChoiceOverlay(Rectangle bounds) {
        super(bounds);
        setEnabled(true);
        Position centre = bounds.getCentre();
        declineButton = new Button(new Position(centre.x-50,centre.y+100), 100, 40, "Decline", 0);

        playerReference = CurrentGameInterface.getCurrentGame().getAllPlayers().stream()
                .filter(player -> player.getPlayerType() == Player.PlayerType.ThisPlayer)
                .findFirst().get();
    }

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {
        declineButton.paint(g);
    }

    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        setEnabled(true);
    }

    @Override
    public void hideOverlay() {
        setEnabled(false);
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        declineButton.setHovering(declineButton.isPositionInside(mousePosition));
    }

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
