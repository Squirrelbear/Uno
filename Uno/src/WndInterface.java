import java.awt.*;

/**
 * Uno
 * Author: Peter Mitchell
 *
 * WndInterface class:
 * Defines a generic abstraction to use for multiple interfaces.
 */
public abstract class WndInterface {
    /**
     * State of whether the object is enabled so it can be used for managing updates.
     */
    private boolean isEnabled;
    /**
     * Bounds of this interface.
     */
    protected Rectangle bounds;

    /**
     * Initialise the interface with bounds and make it enabled.
     */
    public WndInterface(Rectangle bounds) {
        isEnabled = true;
        this.bounds = bounds;
    }

    /**
     * Update the interface elements.
     *
     * @param deltaTime Time since last update.
     */
    public abstract void update(int deltaTime);

    /**
     * Draw all elements to the interface.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public abstract void paint(Graphics g);

    /**
     * Handle updates related to the mouse pressing at the specified position.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft If true, the mouse button is left, otherwise is right.
     */
    public void handleMousePress(Position mousePosition, boolean isLeft) {}

    /**
     * Handle updates related to the mouse being moved.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    public void handleMouseMove(Position mousePosition) {}

    /**
     * Change the enabled state of this object.
     *
     * @param enabled New state to set the enabled/disabled state of this object.
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    /**
     * Get the current enabled state of the object.
     *
     * @return True if the object is enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

}
