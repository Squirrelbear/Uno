import java.awt.*;

/**
 * Uno
 *
 * ChallengeSuccessOverlay class:
 * Displays a short time flashing tick to show the challenge was successful.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class ChallengeSuccessOverlay extends WndInterface implements GeneralOverlayInterface {
    /**
     * Timer till the overlay is hidden again.
     */
    private double displayTimer;
    /**
     * X Coordinates to make the graphic appear.
     */
    private final int[] polyXCoords;
    /**
     * Y Coordinates to make the graphic appear.
     */
    private final int[] polyYCoords;

    /**
     * Initialise the interface with bounds and makes it ready to be enabled.
     *
     * @param bounds Region where the object is shown.
     */
    public ChallengeSuccessOverlay(Rectangle bounds) {
        super(bounds);
        setEnabled(false);

        int widthDiv6 = bounds.width / 6;
        int x = bounds.position.x;
        int y = bounds.position.y;
        int heightDiv6 = bounds.height / 6;

        polyXCoords = new int[] { x, x+widthDiv6, x+widthDiv6 * 2,
                x+widthDiv6*5, x+bounds.width, x+widthDiv6 * 2};
        polyYCoords = new int[] { y + heightDiv6 * 4, y + heightDiv6 * 3, y+heightDiv6*4,
                y+heightDiv6*2, y+heightDiv6*3, y+bounds.height};
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
     * Draws the tick flashing with showing 75% of the time.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(displayTimer % 200 < 150) {
            g.setColor(new Color(106, 163, 22));
            g.fillPolygon(polyXCoords,polyYCoords,polyXCoords.length);
            g.setColor(Color.BLACK);
            g.drawPolygon(polyXCoords,polyYCoords,polyXCoords.length);
        }
    }
}
