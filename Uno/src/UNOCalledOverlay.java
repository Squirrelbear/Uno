import java.awt.*;

/**
 * Uno
 *
 * UNOCalledOverlay class:
 * Shows UNO! for a player when they called UNO!
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class UNOCalledOverlay extends WndInterface implements GeneralOverlayInterface {
    /**
     * Timer till the overlay is hidden again.
     */
    private double displayTimer;

    /**
     * Sets up the overlay ready to show.
     *
     * @param position Position where to place this overlay.
     */
    public UNOCalledOverlay(Position position) {
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
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("UNO!", bounds.position.x, bounds.position.y);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Card.getColourByID(0));
            g.drawString("U", bounds.position.x+2, bounds.position.y-2);
            g.setColor(Card.getColourByID(1));
            g.drawString("N", bounds.position.x+30, bounds.position.y-2);
            g.setColor(Card.getColourByID(2));
            g.drawString("O", bounds.position.x+2+60, bounds.position.y-2);
            g.setColor(Card.getColourByID(3));
            g.drawString("!", bounds.position.x+2+90, bounds.position.y-2);
        }
    }
}
