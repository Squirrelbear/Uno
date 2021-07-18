import java.awt.*;

public interface TurnDecisionOverlayInterface {
    void showOverlay(TurnActionFactory.TurnDecisionAction currentAction);
    void hideOverlay();
    void setEnabled(boolean isEnabled);
    boolean isEnabled();
    void update(int deltaTime);
    void paint(Graphics g);
}
