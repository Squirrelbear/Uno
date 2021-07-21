import java.awt.*;

/**
 * Uno
 *
 * UnoButton class:
 * A special variation of button that appears differently for the Uno calling.
 * Pressing the button is intended when a player reaches 2 or less cards.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class UnoButton extends WndInterface implements GeneralOverlayInterface {
    /**
     * Width of the button.
     */
    public static final int WIDTH = 80;
    /**
     * Height of the button.
     */
    public static final int HEIGHT = 60;
    /**
     * Current hover status of the button.
     */
    private boolean isHovered;

    /**
     * Initialises the UnoButton.
     *
     * @param position Position to place the Uno button.
     */
    public UnoButton(Position position) {
        super(new Rectangle(position, WIDTH, HEIGHT));
        isHovered = false;
        setEnabled(true);
    }

    /**
     * Shows the overlay.
     */
    @Override
    public void showOverlay() {
        setEnabled(true);
    }

    /**
     * Hides the overlay.
     */
    @Override
    public void hideOverlay() {
        setEnabled(false);
    }

    /**
     * Does nothing.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Draws the Uno button with an expanding oval on hover with the UNO text in the middle.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(!isEnabled()) return;

        g.setColor(new Color(147, 44, 44));
        int expandAmount = isHovered ? 20 : 0;
        g.fillOval(bounds.position.x-expandAmount/2, bounds.position.y-expandAmount/2,
                bounds.width+expandAmount,bounds.height+expandAmount);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        int strWidth = g.getFontMetrics().stringWidth("UNO");
        g.drawString("UNO", bounds.position.x+bounds.width/2-strWidth/2-2, bounds.position.y+bounds.height/2+2+10);
        g.setColor(new Color(226, 173, 67));
        g.drawString("UNO", bounds.position.x+bounds.width/2-strWidth/2, bounds.position.y+bounds.height/2+10);
    }

    /**
     * Updates the hover state of the Uno button.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Position mousePosition) {
        isHovered = bounds.isPositionInside(mousePosition);
    }
}
