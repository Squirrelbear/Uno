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
public class UNOCalledOverlay extends PlayerFlashOverlay {
    /**
     * Sets up the overlay ready to show.
     *
     * @param position Position where to place this overlay.
     */
    public UNOCalledOverlay(Position position) {
        super(position, "UNO!", Color.RED, 40);
        setEnabled(false);
    }

    /**
     * Draws the UNO! text flashing with showing 75% of the time.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(displayTimer % 200 < 150) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            g.drawString(message, bounds.position.x, bounds.position.y);
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            for(int i = 0; i < message.length(); i++) {
                g.setColor(Card.getColourByID(i % 4));
                g.drawString(message.charAt(i)+"", bounds.position.x+2+i*30, bounds.position.y-2);
            }
        }
    }
}
