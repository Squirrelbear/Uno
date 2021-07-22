import java.awt.*;

/**
 * Uno
 *
 * DrawNMessageOverlay class:
 * Displays a short time flashing "+N" to show the number of cards a player drew.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class DrawNMessageOverlay extends WndInterface implements GeneralOverlayInterface {

    /**
     * Timer till the overlay is hidden again.
     */
    private double displayTimer;

    /**
     * The message to display.
     */
    private String message;

    /**
     * Sets up the overlay ready to show.
     *
     * @param position Position where to place this overlay.
     */
    public DrawNMessageOverlay(Position position) {
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
     * Sets the message to be displayed to "+N".
     *
     * @param N The number to display.
     */
    public void setN(int N) {
        message = "+" + N;
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
     * Draws the +N text flashing with showing 75% of the time.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(displayTimer % 200 < 150) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            int strWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, bounds.position.x - strWidth / 2 - 2, bounds.position.y - 2);
            g.setColor(Color.RED);
            g.drawString(message, bounds.position.x - strWidth / 2, bounds.position.y);
        }
    }
}
