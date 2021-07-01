import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Uno
 * Author: Peter Mitchell (2021)
 *
 */
public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    /**
     * Height of the panel.
     */
    private static final int PANEL_HEIGHT = 720;
    /**
     * Width of the panel.
     */
    private static final int PANEL_WIDTH = 1280;

    private PauseInterface pauseWnd;
    private CurrentGameInterface activeGame;

    /**
     * Configures the game ready to be played including selection of playing against either
     * AI or another player.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(93, 141, 74));

        pauseWnd = new PauseInterface(new Rectangle(PANEL_WIDTH/2-100,PANEL_HEIGHT/2-100,200,200), this);
        pauseWnd.setEnabled(false);

        activeGame = new CurrentGameInterface(new Rectangle(0,0,PANEL_WIDTH,PANEL_HEIGHT));

        Timer updateTimer = new Timer(20, this);
        updateTimer.start();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Draws the game grid and draws the message at the bottom showing a string representing the game state.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        super.paint(g);
        activeGame.paint(g);
        if(pauseWnd.isEnabled()) {
            pauseWnd.paint(g);
        }
    }

    public void setPauseState(boolean isPaused) {
        activeGame.setEnabled(!isPaused);
        pauseWnd.setEnabled(isPaused);
    }

    public void quitGame() {
        System.exit(0);
    }

    /**
     * Handles the key input to have Escape exit the game.
     *
     * @param keyCode The key that was pressed.
     */
    public void handleInput(int keyCode) {
        if(keyCode == KeyEvent.VK_ESCAPE) {
            setPauseState(!pauseWnd.isEnabled());
        } else if(keyCode == KeyEvent.VK_S) {
            activeGame.sortHand();
        } else if(keyCode == KeyEvent.VK_R) {
            activeGame.revealHands();
        } else if(keyCode == KeyEvent.VK_T) {
            activeGame.toggleTurnDirection();
        }
        repaint();
    }

    /**
     *
     *
     * @param e Information about the mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Position mousePosition = new Position(e.getX(), e.getY());
        pauseWnd.handleMousePress(mousePosition, e.getButton() == 1);
        activeGame.handleMousePress(mousePosition, e.getButton() == 1);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Position mousePosition = new Position(e.getX(), e.getY());
        pauseWnd.handleMouseMove(mousePosition);
        activeGame.handleMouseMove(mousePosition);
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        activeGame.update(20);
        repaint();
    }

    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseClicked(MouseEvent e) {}
    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseReleased(MouseEvent e) {}
    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseEntered(MouseEvent e) {}
    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseExited(MouseEvent e) {}
    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseDragged(MouseEvent e) {}
}
