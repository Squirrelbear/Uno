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
     * Reference to the active game.
     */
    private CurrentGameInterface activeGame;
    /**
     * Reference to the lobby to set up games.
     */
    private LobbyInterface lobbyInterface;

    /**
     * Configures the game ready to be played including selection of playing against either
     * AI or another player.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(93, 141, 74));

        pauseWnd = new PauseInterface(new Rectangle(PANEL_WIDTH/2-100,PANEL_HEIGHT/2-100,200,200), this);
        pauseWnd.setEnabled(false);

        lobbyInterface = new LobbyInterface(new Rectangle(0,0,PANEL_WIDTH, PANEL_HEIGHT), this);

        Timer updateTimer = new Timer(20, this);
        updateTimer.start();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Creates a new game with the specified list of players and then disables the lobby.
     *
     * @param playerList The player list to start a game with.
     * @param ruleSet Definition of how the game is to be played.
     */
    public void startGame(List<LobbyPlayer> playerList, RuleSet ruleSet) {
        activeGame = new CurrentGameInterface(new Rectangle(0,0,PANEL_WIDTH,PANEL_HEIGHT), playerList, ruleSet);
        lobbyInterface = null;
    }

    /**
     * Draws the game grid and draws the message at the bottom showing a string representing the game state.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        super.paint(g);
        if(lobbyInterface != null) {
            lobbyInterface.paint(g);
        }
        if(activeGame != null) {
            activeGame.paint(g);
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
        if(activeGame != null) {
            activeGame.setEnabled(!isPaused);
        }
        if(lobbyInterface != null) {
            lobbyInterface.setEnabled(!isPaused);
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
        } else if(keyCode == KeyEvent.VK_D) {
            // TODO
            TurnActionFactory.TurnAction turnAction = TurnActionFactory.playCardAsAction(1, 1,14,1);
            TurnActionFactory.debugOutputTurnActionTree(turnAction);
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
        if(activeGame != null) {
            activeGame.handleMousePress(mousePosition, e.getButton() == 1);
        }
        if(lobbyInterface != null) {
            lobbyInterface.handleMousePress(mousePosition, e.getButton() == 1);
        }
        repaint();
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
        if(activeGame != null) {
            activeGame.handleMouseMove(mousePosition);
        }
        if(lobbyInterface != null) {
            lobbyInterface.handleMouseMove(mousePosition);
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
        if(activeGame != null) {
            activeGame.update(20);
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
