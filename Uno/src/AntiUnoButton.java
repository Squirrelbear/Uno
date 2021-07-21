import java.awt.*;

/**
 * Uno
 *
 * AntiUnoButton class:
 * A special button used to pair with the Uno button for calling out
 * players who have not called their Uno.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class AntiUnoButton extends WndInterface implements GeneralOverlayInterface {
    /**
     * Initialise the interface with bounds.
     *
     * @param bounds Bounds of the button.
     */
    public AntiUnoButton(Rectangle bounds) {
        super(bounds);
    }

    /**
     * Shows the overlay.
     */
    @Override
    public void showOverlay() {
        setEnabled(true);
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
     * Draws the AntiUno button.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        // TODO
    }
}
