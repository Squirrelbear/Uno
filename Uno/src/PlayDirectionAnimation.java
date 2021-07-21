import java.awt.*;

/**
 * Uno
 *
 * PlayDirectionAnimation class:
 * Represents a pair for orbs that circle clockwise or anti-clockwise to
 * show the direction of play for turn order.
 */
public class PlayDirectionAnimation {
    /**
     * Centre of the orbit.
     */
    private final Position centre;
    /**
     * Distance the orbs are spaced out from the centre.
     */
    private final int radiusFromCentre;
    /**
     * The width and height of the ovals.
     */
    private final int indicatorSize;
    /**
     * The two orbs positioned 180 degrees around the orbit from each other.
     */
    private final Position movingObject1, movingObject2;
    /**
     * The current angle of the orbit.
     */
    private double currentAngle;
    /**
     * Direction of the orbit.
     */
    private boolean isIncreasing;

    /**
     * Initialises the ovals to represent showing play direction.
     *
     * @param centre Centre of the orbit.
     * @param radiusFromCentre Distance the orbs are spaced out from the centre.
     * @param indicatorSize The width and height of the ovals.
     */
    public PlayDirectionAnimation(Position centre, int radiusFromCentre, int indicatorSize) {
        this.centre = centre;
        this.radiusFromCentre = radiusFromCentre;
        this.indicatorSize = indicatorSize;
        currentAngle = 0;
        movingObject1 = new Position(centre.x, centre.y + radiusFromCentre);
        movingObject2 = new Position(centre.x, centre.y - radiusFromCentre);
        isIncreasing = true;
    }

    /**
     * Moves the two ovals around in a circle motion around the centre.
     *
     * @param deltaTime Time since last update.
     */
    public void update(int deltaTime) {
        if(isIncreasing) currentAngle += deltaTime / 1000.0;
        else currentAngle -= deltaTime / 1000.0;
        if(currentAngle > Math.PI * 2) currentAngle = 0;

        movingObject1.setPosition(centre.x + (int)(radiusFromCentre * Math.cos(currentAngle)), centre.y + (int)(radiusFromCentre * Math.sin(currentAngle)));
        movingObject2.setPosition(centre.x - (int)(radiusFromCentre * Math.cos(currentAngle)), centre.y - (int)(radiusFromCentre * Math.sin(currentAngle)));
    }

    /**
     * Draws the two ovals representing play direction.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(movingObject1.x, movingObject1.y, indicatorSize, indicatorSize);
        g.fillOval(movingObject2.x, movingObject2.y, indicatorSize, indicatorSize);
    }

    /**
     * Changes the direction of the visual.
     *
     * @param isIncreasing When true the ovals move clockwise, when false they move anti-clockwise.
     */
    public void setIsIncreasing(boolean isIncreasing) {
        this.isIncreasing = isIncreasing;
    }

}
