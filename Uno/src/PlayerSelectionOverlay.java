import java.awt.*;

public class PlayerSelectionOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public PlayerSelectionOverlay(Rectangle bounds) {
        super(bounds);
        setEnabled(false);
    }

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {

    }

    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {

    }

    @Override
    public void hideOverlay() {

    }
}
