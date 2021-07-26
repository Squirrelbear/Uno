import java.awt.*;

/**
 * Uno
 *
 * AntiUnoOverlay class:
 * Shows the ! when a player is called out for not calling UNO!
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class AntiUnoOverlay extends WndInterface implements GeneralOverlayInterface {
    /**
     * Timer till the overlay is hidden again.
     */
    private double displayTimer;

    /**
     * Sets up the overlay ready to show.
     *
     * @param position Position where to place this overlay.
     */
    public AntiUnoOverlay(Position position) {
        super(new Rectangle(position, 40,40));
        setEnabled(false);
    }

    /**
     * Shows the overlay and sets a timer for how long it will appear.
     */
    @Override
    public void showOverlay() {
        setEnabled(true);
        displayTimer = 2000;
    }

    /**
     * Updates the timer to hide the overlay and hides it when it hits 0.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {
        displayTimer -= deltaTime;
        if(displayTimer <= 0) {
            setEnabled(false);
        }
    }

    /**
     * Draws the SKIPPED text flashing with showing 75% of the time.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(displayTimer % 200 < 150) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("!", bounds.position.x, bounds.position.y);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(new Color(226, 173, 67));
            g.drawString("!", bounds.position.x+2, bounds.position.y-2);
        }
    }
}