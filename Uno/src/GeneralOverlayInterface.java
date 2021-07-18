import java.awt.*;

public interface GeneralOverlayInterface {
    void showOverlay();
    void hideOverlay();
    void setEnabled(boolean isEnabled);
    boolean isEnabled();
    void update(int deltaTime);
    void paint(Graphics g);
}
