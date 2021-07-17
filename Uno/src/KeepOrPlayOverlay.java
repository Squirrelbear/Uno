import java.awt.*;

public class KeepOrPlayOverlay extends WndInterface implements TurnActionOverlay {
    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public KeepOrPlayOverlay(Rectangle bounds) {
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
        System.out.println("Visible choice");
    }

    @Override
    public void hideOverlay() {

    }
}
