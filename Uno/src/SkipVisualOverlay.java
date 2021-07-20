import java.awt.*;

public class SkipVisualOverlay extends WndInterface implements GeneralOverlayInterface {

    private double displayTimer;

    public SkipVisualOverlay(Position position) {
        super(new Rectangle(position, 40,40));
        setEnabled(false);
    }

    @Override
    public void showOverlay() {
        setEnabled(true);
        displayTimer = 2000;
    }

    @Override
    public void hideOverlay() {
        setEnabled(false);
    }

    @Override
    public void update(int deltaTime) {
        displayTimer -= deltaTime;
        if(displayTimer <= 0) {
            hideOverlay();
        }
    }

    @Override
    public void paint(Graphics g) {
        if(displayTimer % 200 < 150) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            int strWidth = g.getFontMetrics().stringWidth("SKIPPED");
            g.drawString("SKIPPED", bounds.position.x - strWidth / 2 - 2, bounds.position.y - 2);
            g.setColor(Color.RED);
            g.drawString("SKIPPED", bounds.position.x - strWidth / 2, bounds.position.y);
        }
    }
}
