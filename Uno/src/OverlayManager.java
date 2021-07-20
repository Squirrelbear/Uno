import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class OverlayManager extends WndInterface {
    private Map<String, TurnDecisionOverlayInterface> decisionOverlays;
    private Map<String, GeneralOverlayInterface> generalOverlays;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public OverlayManager(Rectangle bounds, List<Player> playerList) {
        super(bounds);
        setEnabled(true);
        decisionOverlays = new HashMap<>();
        WildColourSelectorOverlay wildColourSelectorOverlay = new WildColourSelectorOverlay(new Position(bounds.width/2-100,bounds.height/2-100),200,200);
        KeepOrPlayOverlay keepOrPlayOverlay = new KeepOrPlayOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height));
        PlayerSelectionOverlay playerSelectionOverlay = new PlayerSelectionOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height), playerList);
        StatusOverlay statusOverlay = new StatusOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height));
        ChallengeOverlay challengeOverlay = new ChallengeOverlay(bounds);
        StackChoiceOverlay stackChoiceOverlay = new StackChoiceOverlay(bounds);
        decisionOverlays.put("wildColour", wildColourSelectorOverlay);
        decisionOverlays.put("keepOrPlay", keepOrPlayOverlay);
        decisionOverlays.put("otherPlayer", playerSelectionOverlay);
        decisionOverlays.put("statusOverlay", statusOverlay);
        decisionOverlays.put("isChallenging", challengeOverlay);
        decisionOverlays.put("isStacking", stackChoiceOverlay);

        generalOverlays = new HashMap<>();
        UnoButton unoButton = new UnoButton(new Position(bounds.position.x + bounds.width - UnoButton.WIDTH-40,
                bounds.position.y + bounds.height - UnoButton.HEIGHT-40));
        AntiUnoButton antiUnoButton = new AntiUnoButton(new Rectangle(new Position(bounds.position.x + bounds.width - UnoButton.WIDTH-40,
                bounds.position.y + bounds.height - UnoButton.HEIGHT-40),40,20));
        for(int i = 0; i < playerList.size(); i++) {
            SkipVisualOverlay skipVisualOverlay = new SkipVisualOverlay(playerList.get(i).getCentreOfBounds());
            generalOverlays.put("SkipVisual"+(i+1),skipVisualOverlay);
        }
        generalOverlays.put("UnoButton", unoButton);
        generalOverlays.put("antiUnoButton", antiUnoButton);

    }

    public void showDecisionOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        if(currentAction.timeOut) {
            setEnabled(true);
            if(CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerType() == Player.PlayerType.ThisPlayer) {
                TurnDecisionOverlayInterface overlayToShow = decisionOverlays.get(currentAction.flagName);
                if (overlayToShow != null) {
                    overlayToShow.showOverlay(currentAction);
                }
            }
            decisionOverlays.get("statusOverlay").showOverlay(currentAction);
        }
    }

    public void showGeneralOverlay(String overlayName) {
        GeneralOverlayInterface overlayToShow = generalOverlays.get(overlayName);
        if(overlayToShow != null) {
            overlayToShow.showOverlay();
        }
    }

    public void hideOverlay() {

    }

    @Override
    public void update(int deltaTime) {
        decisionOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled()) {
                overlay.update(deltaTime);
            }
        });
        generalOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled()) {
                overlay.update(deltaTime);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        decisionOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled()) {
                overlay.paint(g);
            }
        });
        generalOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled()) {
                overlay.paint(g);
            }
        });
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        decisionOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled() && overlay instanceof WndInterface) {
                ((WndInterface)overlay).handleMousePress(mousePosition, isLeft);
            }
        });
        generalOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled() && overlay instanceof WndInterface) {
                ((WndInterface)overlay).handleMousePress(mousePosition, isLeft);
            }
        });
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        decisionOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled() && overlay instanceof WndInterface) {
                ((WndInterface)overlay).handleMouseMove(mousePosition);
            }
        });
        generalOverlays.forEach((key,overlay) -> {
            if(overlay.isEnabled() && overlay instanceof WndInterface) {
                ((WndInterface)overlay).handleMouseMove(mousePosition);
            }
        });
    }
}
