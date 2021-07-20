import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KeepOrPlayOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    private List<Button> buttonList;
    private TurnActionFactory.TurnDecisionAction currentAction;
    private Card cardForChoice;
    private Position cardPosition;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
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

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));
        cardForChoice.paint(g);
    }

    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        cardForChoice = new Card(currentAction.storedData.get("faceValueID"),
                                currentAction.storedData.get("colourID"),
                                currentAction.storedData.get("cardID"));
        cardForChoice.position.setPosition(cardPosition.x, cardPosition.y);
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
                hideOverlay();
                currentAction.injectFlagProperty(button.getActionID());
                break;
            }
        }
    }
}
