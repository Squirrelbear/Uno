import java.awt.*;

public class PlayerFlashOverlay extends WndInterface implements GeneralOverlayInterface {

    /**
     * Timer till the overlay is hidden again.
     */
    protected double displayTimer;
    /**
     * Message to display.
     */
    protected String message;
    /**
     * Colour to show it with.
     */
    private final Color colour;
    /**
     * Size of the message.
     */
    protected final int fontSize;

    /**
     * Sets up the overlay ready to show.
     *
     * @param position Position where to place this overlay.
     * @param message Message to display.
     * @param colour Colour to show message with.
     */
    public PlayerFlashOverlay(Position position, String message, Color colour, int fontSize) {
        super(new Rectangle(position, 40,40));
        setEnabled(false);
        this.message = message;
        this.colour = colour;
        this.fontSize = fontSize;
    }

    /**
     * Sets the message to the new value.
     *
     * @param message The message to display.
     */
    public void setMessage(String message) {
        this.message = message;
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
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            int strWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, bounds.position.x - strWidth / 2 - 2, bounds.position.y - 2);
            g.setColor(colour);
            g.drawString(message, bounds.position.x - strWidth / 2, bounds.position.y);
        }
    }
}