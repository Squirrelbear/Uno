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

    /**
     * Updates to determine if there is a player vulnerable to being called out on not saying "UNO".
     * They are vulnerable if they only have one card, are not the bottom player (because that is the one controlling it),
     * and the player did not call UNO yet.
     *
     * @param deltaTime Time since last update.
     */
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

    /**
     * When the button is active it means there is at least one player that can be called out.
     * This method checks for the button being pressed and determines which player needs to be called out.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(isActive && bounds.isPositionInside(mousePosition)) {
            for(Player player : CurrentGameInterface.getCurrentGame().getAllPlayers()) {
                if(player != bottomPlayer && !player.isSafe() && player.getHand().size() == 1) {
                    CurrentGameInterface.getCurrentGame().applyAntiUno(player.getPlayerID());
                }
            }
        }
    }
}
