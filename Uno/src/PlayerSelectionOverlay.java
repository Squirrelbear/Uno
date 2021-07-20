import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionOverlay extends WndInterface implements TurnDecisionOverlayInterface {

    private List<Button> buttonList;
    private TurnActionFactory.TurnDecisionAction currentAction;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
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
