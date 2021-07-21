import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class OverlayManager extends WndInterface {
    private final Map<String, WndInterface> overlays;
    private TurnActionFactory.TurnDecisionAction overlayAction;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public OverlayManager(Rectangle bounds, List<Player> playerList) {
        super(bounds);
        setEnabled(true);
        overlays = new HashMap<>();
        WildColourSelectorOverlay wildColourSelectorOverlay = new WildColourSelectorOverlay(new Position(bounds.width/2-100,bounds.height/2-100),200,200);
        KeepOrPlayOverlay keepOrPlayOverlay = new KeepOrPlayOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height));
        PlayerSelectionOverlay playerSelectionOverlay = new PlayerSelectionOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height), playerList);
        StatusOverlay statusOverlay = new StatusOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height));
        ChallengeOverlay challengeOverlay = new ChallengeOverlay(bounds);
        StackChoiceOverlay stackChoiceOverlay = new StackChoiceOverlay(bounds);
        overlays.put("wildColour", wildColourSelectorOverlay);
        overlays.put("keepOrPlay", keepOrPlayOverlay);
        overlays.put("otherPlayer", playerSelectionOverlay);
        overlays.put("statusOverlay", statusOverlay);
        overlays.put("isChallenging", challengeOverlay);
        overlays.put("isStacking", stackChoiceOverlay);

        UnoButton unoButton = new UnoButton(new Position(bounds.position.x + bounds.width - UnoButton.WIDTH-40,
                bounds.position.y + bounds.height - UnoButton.HEIGHT-40));
        AntiUnoButton antiUnoButton = new AntiUnoButton(new Rectangle(new Position(bounds.position.x + bounds.width - UnoButton.WIDTH-40,
                bounds.position.y + bounds.height - UnoButton.HEIGHT-40),40,20));
        for(int i = 0; i < playerList.size(); i++) {
            SkipVisualOverlay skipVisualOverlay = new SkipVisualOverlay(playerList.get(i).getCentreOfBounds());
            overlays.put("SkipVisual"+(i+1),skipVisualOverlay);
        }
        overlays.put("UnoButton", unoButton);
        overlays.put("antiUnoButton", antiUnoButton);

    }

    public void showDecisionOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        if(currentAction.timeOut) {
            setEnabled(true);
            if(CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerType() == Player.PlayerType.ThisPlayer) {
                WndInterface overlayToShow = overlays.get(currentAction.flagName);
                if (overlayToShow != null && overlayToShow instanceof TurnDecisionOverlayInterface) {
                   ((TurnDecisionOverlayInterface)overlayToShow).showOverlay(currentAction);
                }
            }
            overlayAction = currentAction;
            ((TurnDecisionOverlayInterface)overlays.get("statusOverlay")).showOverlay(currentAction);
        }
    }

    public void showGeneralOverlay(String overlayName) {
        WndInterface overlayToShow = overlays.get(overlayName);
        if(overlayToShow != null && overlayToShow instanceof GeneralOverlayInterface) {
            ((GeneralOverlayInterface)overlayToShow).showOverlay();
        }
    }

    public void hideOverlay() {
        overlays.forEach((key, overlay) -> {
            if(overlay instanceof TurnDecisionOverlayInterface) {
                ((TurnDecisionOverlayInterface)overlay).hideOverlay();
            }
        });
        setEnabled(false);
    }

    @Override
    public void update(int deltaTime) {
        if(overlayAction != CurrentGameInterface.getCurrentGame().getCurrentTurnAction()) {
            overlayAction = null;
            hideOverlay();
        }

        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.update(deltaTime);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.paint(g);
            }
        });
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.handleMousePress(mousePosition, isLeft);
            }
        });
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.handleMouseMove(mousePosition);
            }
        });
    }
}
