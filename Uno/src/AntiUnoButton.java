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
public class AntiUnoButton extends UnoButton implements GeneralOverlayInterface {
    /**
     * Initialises the AntiUnoButton.
     *
     * @param position Position to place the Uno button.
     */
    public AntiUnoButton(Position position) {
        super(position);
    }

    @Override
    public void update(int deltaTime) {
        isActive = false;
        for(Player player : CurrentGameInterface.getCurrentGame().getAllPlayers()) {
            if(player != bottomPlayer && !player.isSafe() && player.getHand().size() == 1) {
                isActive = true;
            }
        }
    }

    /**
     * Draws the AntiUno button.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(!isActive) return;

        drawButtonBackground(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        int strWidth = g.getFontMetrics().stringWidth("!");
        g.drawString("!", bounds.position.x+bounds.width/2-strWidth/2-2, bounds.position.y+bounds.height/2+2+10+10);
        g.setColor(new Color(226, 173, 67));
        g.drawString("!", bounds.position.x+bounds.width/2-strWidth/2, bounds.position.y+bounds.height/2+10+10);
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(isActive && bounds.isPositionInside(mousePosition)) {
            for(Player player : CurrentGameInterface.getCurrentGame().getAllPlayers()) {
                if(player != bottomPlayer && !player.isSafe() && player.getHand().size() == 1) {
                    System.out.println("Calling out Player " + player.getPlayerName());
                }
            }
        }
    }
}
