import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Uno
 *
 * GamePanel class:
 * Manages the primary game with passing off actions from the mouse, keys, and
 * any timer events to the different parts of the game.
 *
 * @author Peter Mitchell
 * @version 2021.1
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

    /**
     * Reference to the window that appears when the game is paused.
     */
    private final PauseInterface pauseWnd;
    /**
     * Reference to the active interface.
     */
    private WndInterface activeInterface;

    /**
     * Configures the game ready to be played including selection of playing against either
     * AI or another player.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(93, 141, 74));

        pauseWnd = new PauseInterface(new Rectangle(PANEL_WIDTH/2-100,PANEL_HEIGHT/2-100,200,200), this);
        pauseWnd.setEnabled(false);

        showLobby();

        Timer updateTimer = new Timer(20, this);
        updateTimer.start();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Sets the currently active interface to the lobby removing any existing interface.
     * If this is triggered from the pause interface it just resumes the current interface.
     */
    public void showLobby() {
        if(!(activeInterface instanceof LobbyInterface)) {
            activeInterface = new LobbyInterface(new Rectangle(0, 0, PANEL_WIDTH, PANEL_HEIGHT), this);
        }
        setPauseState(false);
    }

    /**
     * Sets the currently active interface to the post-game interface after a game has ended.
     *
     * @param playerList List of players who were playing in the round.
     * @param ruleSet Rules applied during the round.
     */
    public void showPostGame(List<Player> playerList, RuleSet ruleSet) {
        activeInterface = new PostGameInterface(new Rectangle(0,0,PANEL_WIDTH, PANEL_HEIGHT),
                                                playerList, ruleSet, this);
    }

    /**
     * Creates a new game with the specified list of players. Use this for coming from the lobby.
     *
     * @param playerList The player list to start a game with.
     * @param ruleSet Definition of how the game is to be played.
     */
    public void startGame(List<LobbyPlayer> playerList, RuleSet ruleSet) {
        activeInterface = new CurrentGameInterface(new Rectangle(0,0,PANEL_WIDTH,PANEL_HEIGHT),
                                                    ruleSet, playerList, this);
    }

    /**
     * Creates a new game with the specified list of players. Use this for coming from post-game.
     *
     * @param playerList The player list to start a new round with.
     * @param ruleSet Definition of how the game is to be played.
     */
    public void startNextRound(List<Player> playerList, RuleSet ruleSet) {
        activeInterface = new CurrentGameInterface(new Rectangle(0,0,PANEL_WIDTH,PANEL_HEIGHT),
                playerList, ruleSet, this);
    }

    /**
     * Draws the game grid and draws the message at the bottom showing a string representing the game state.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        super.paint(g);
        if(activeInterface != null) {
            activeInterface.paint(g);
        }
        if(pauseWnd.isEnabled()) {
            pauseWnd.paint(g);
        }
    }

    /**
     * Pauses or unpauses the game.
     *
     * @param isPaused When true the game is paused and pause window is shown.
     */
    public void setPauseState(boolean isPaused) {
        if(activeInterface != null) {
            activeInterface.setEnabled(!isPaused);
        }
        pauseWnd.setEnabled(isPaused);
    }

    /**
     * Quits the game immediately.
     */
    public void quitGame() {
        System.exit(0);
    }

    /**
     * Handles the key input to have Escape open the pause menu.
     *
     * @param keyCode The key that was pressed.
     */
    public void handleInput(int keyCode) {
        if(keyCode == KeyEvent.VK_ESCAPE) {
            setPauseState(!pauseWnd.isEnabled());
        } else {
            activeInterface.handleInput(keyCode);
        }
        repaint();
    }

    /**
     * Passes the mouse event on to all the windows.
     *
     * @param e Information about the mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Position mousePosition = new Position(e.getX(), e.getY());
        pauseWnd.handleMousePress(mousePosition, e.getButton() == 1);
        if(activeInterface != null) {
            activeInterface.handleMousePress(mousePosition, e.getButton() == 1);
        }
        repaint();
    }

    /**
     * Draws a title with UNO! and text below for credits.
     *
     * @param g Reference to the Graphics object for rendering.
     * @param bounds Bounds of the game area.
     */
    public void paintUnoTitle(Graphics g, Rectangle bounds) {
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("UNO!", bounds.width/2-40, 50);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("Developed by Peter Mitchell (2021)", bounds.width/2-70, 65);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(Card.getColourByID(0));
        g.drawString("U", bounds.width/2-40+2, 48);
        g.setColor(Card.getColourByID(1));
        g.drawString("N", bounds.width/2-40+2+30, 48);
        g.setColor(Card.getColourByID(2));
        g.drawString("O", bounds.width/2-40+2+60, 48);
        g.setColor(Card.getColourByID(3));
        g.drawString("!", bounds.width/2-40+2+90, 48);
    }

    /**
     * Passes the mouse event on to all the windows.
     *
     * @param e Information about the mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Position mousePosition = new Position(e.getX(), e.getY());
        pauseWnd.handleMouseMove(mousePosition);
        if(activeInterface != null) {
            activeInterface.handleMouseMove(mousePosition);
        }
        repaint();
    }

    /**
     * Forces the active game to update and forces a repaint.
     *
     * @param e Information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(activeInterface != null) {
            activeInterface.update(20);
        }
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
