import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    private List<Button> buttonList;
    private TurnActionFactory.TurnDecisionAction currentAction;
    private Player playerReference;
    private boolean allowStacking;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public ChallengeOverlay(Rectangle bounds) {
        super(bounds);
        setEnabled(false);
        buttonList = new ArrayList<>();
        Position centre = bounds.getCentre();
        buttonList.add(new Button(new Position(centre.x-150,centre.y+100), 100, 40, "Challenge", 1));
        buttonList.add(new Button(new Position(centre.x+50,centre.y+100), 100, 40, "Decline", 0));

        allowStacking = CurrentGameInterface.getCurrentGame().getRuleSet().canStackCards();
        playerReference = CurrentGameInterface.getCurrentGame().getAllPlayers().stream()
                .filter(player -> player.getPlayerType() == Player.PlayerType.ThisPlayer)
                .findFirst().get();
    }

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));
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

        for (Button button : buttonList) {
            button.setHovering(button.isPositionInside(mousePosition));
        }
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        for (Button button : buttonList) {
            if(button.isPositionInside(mousePosition)) {
                currentAction.injectProperty("isChaining", 0);
                currentAction.injectFlagProperty(button.getActionID());
                hideOverlay();
                return;
            }
        }

        if(allowStacking) {
            Card clickedCard = playerReference.chooseCardFromClick(mousePosition);
            if(clickedCard != null && clickedCard.getFaceValueID() == 13) {
                currentAction.injectProperty("faceValueID", clickedCard.getFaceValueID());
                currentAction.injectProperty("colourID", clickedCard.getColourID());
                currentAction.injectProperty("cardID", clickedCard.getCardID());
                currentAction.injectProperty("isChaining", 1);
                currentAction.injectFlagProperty(0);
                hideOverlay();
            }
        }
    }
}
