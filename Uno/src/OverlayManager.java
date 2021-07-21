import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Uno
 *
 * OverlayManager class:
 * Defines a manager to control overlays for the CurrentGameInterface.
 * These overlays include those that wait for the player to interface with them
 * and some that are just informational.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class OverlayManager extends WndInterface {
    /**
     * Interfaces mapped to unique strings.
     */
    private final Map<String, WndInterface> overlays;
    /**
     * Current action for an active TurnDecisionAction.
     */
    private TurnActionFactory.TurnDecisionAction overlayAction;

    /**
     * Initialise the interfaces all ready for any that needs to be made visible.
     *
     * @param bounds The bounds of the entire game area.
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
            overlays.put("SkipVisual"+i,skipVisualOverlay);
        }
        overlays.put("UnoButton", unoButton);
        overlays.put("antiUnoButton", antiUnoButton);

    }

    /**
     * Finds the matching overlay for a decision if necessary, and then shows it.
     * Then shows the statusOverlay in all situations even if it is not the current player's decision.
     *
     * @param currentAction Action to use for determining which overlay to show.
     */
    public void showDecisionOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        if(currentAction.timeOut) {
            setEnabled(true);
            if(CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerType() == Player.PlayerType.ThisPlayer) {
                WndInterface overlayToShow = overlays.get(currentAction.flagName);
                if (overlayToShow instanceof TurnDecisionOverlayInterface) {
                   ((TurnDecisionOverlayInterface)overlayToShow).showOverlay(currentAction);
                }
            }
            overlayAction = currentAction;
            ((TurnDecisionOverlayInterface)overlays.get("statusOverlay")).showOverlay(currentAction);
        }
    }

    /**
     * Finds the matching interface and makes it visible if possible.
     *
     * @param overlayName Name that maps to an interface.
     */
    public void showGeneralOverlay(String overlayName) {
        WndInterface overlayToShow = overlays.get(overlayName);
        if(overlayToShow instanceof GeneralOverlayInterface) {
            ((GeneralOverlayInterface)overlayToShow).showOverlay();
        }
    }

    /**
     * Hides all the decision overlays automatically called when the TurnAction changes in update().
     */
    public void hideAllDecisionOverlays() {
        overlays.forEach((key, overlay) -> {
            if(overlay instanceof TurnDecisionOverlayInterface) {
                overlay.setEnabled(false);
            }
        });
        setEnabled(false);
    }

    /**
     * Updates all the active overlays and hides all the decision overlays if the TurnAction changed.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {
        if(overlayAction != CurrentGameInterface.getCurrentGame().getCurrentTurnAction()) {
            overlayAction = null;
            hideAllDecisionOverlays();
        }

        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.update(deltaTime);
            }
        });
    }

    /**
     * Paints all enabled overlays.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.paint(g);
            }
        });
    }

    /**
     * Passes the mousePress event on to all enabled overlays.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.handleMousePress(mousePosition, isLeft);
            }
        });
    }

    /**
     * Passes the mouseMove event on to all enabled overlays.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Position mousePosition) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.handleMouseMove(mousePosition);
            }
        });
    }
}
