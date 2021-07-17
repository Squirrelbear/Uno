import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TurnActionOverlayManager extends WndInterface implements TurnActionOverlay {
    private WildColourSelectorOverlay wildColourSelectorOverlay;
    private KeepOrPlayOverlay keepOrPlayOverlay;
    private PlayerSelectionOverlay playerSelectionOverlay;
    private StatusOverlay statusOverlay;
    private List<TurnActionOverlay> overlays;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public TurnActionOverlayManager(Rectangle bounds) {
        super(bounds);
        setEnabled(false);
        overlays = new ArrayList<>();
        wildColourSelectorOverlay = new WildColourSelectorOverlay(new Position(bounds.width/2-100,bounds.height/2-100),200,200);
        keepOrPlayOverlay = new KeepOrPlayOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height));
        playerSelectionOverlay = new PlayerSelectionOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height));
        statusOverlay = new StatusOverlay(new Rectangle(new Position(0,0), bounds.width, bounds.height));
        overlays.add(wildColourSelectorOverlay);
        overlays.add(keepOrPlayOverlay);
        overlays.add(playerSelectionOverlay);
        overlays.add(statusOverlay);
    }

    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        setEnabled(false);
        if(currentAction.timeOut) {
            switch(currentAction.flagName) {
                case "keepOrPlay" -> keepOrPlayOverlay.showOverlay(currentAction);
                case "wildColour" -> wildColourSelectorOverlay.showOverlay(currentAction);
                //case "isChallenging" -> result = "choosing Response to +4."; // TODO
                case "otherPlayer" -> playerSelectionOverlay.showOverlay(currentAction);
                // TODO Need to handle Stacking +2 Overlay too
            }
        } else {
            statusOverlay.showOverlay(currentAction);
        }
    }

    @Override
    public void hideOverlay() {

    }

    @Override
    public void update(int deltaTime) {
        for(TurnActionOverlay overlay : overlays) {
            if(overlay.isEnabled()) {
                overlay.update(deltaTime);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        for(TurnActionOverlay overlay : overlays) {
            if(overlay.isEnabled()) {
                overlay.paint(g);
            }
        }
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(wildColourSelectorOverlay.isEnabled()) {
            int colourID = wildColourSelectorOverlay.getColourFromClick(mousePosition);
            if(colourID != -1) {
                wildColourSelectorOverlay.setEnabled(false);
                // TODO
            }
        } else if(keepOrPlayOverlay.isEnabled()) {


        } else if(playerSelectionOverlay.isEnabled()) {


        }
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        if(wildColourSelectorOverlay.isEnabled()) {
            wildColourSelectorOverlay.handleMouseMove(mousePosition);
        }
    }
}
