import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Uno
 * Author: Peter Mitchell (2021)
 *
 * Game class:
 * Defines the entry point for the game by creating the frame,
 * and populating it with a GamePanel.
 */
public class Game implements KeyListener {
    /**
     * Entry point for the application to create an instance of the Game class.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Game game = new Game();
    }

    /**
     * Reference to the GamePanel object to pass key events to.
     */
    private GamePanel gamePanel;

    /**
     * Creates the JFrame with a GamePanel inside it, attaches a key listener,
     * and makes everything visible.
     */
    public Game() {
        JFrame frame = new JFrame("Uno");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        gamePanel = new GamePanel();
        frame.getContentPane().add(gamePanel);

        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Called when the key is pressed down. Passes the key press on to the GamePanel.
     *
     * @param e Information about what key was pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        gamePanel.handleInput(e.getKeyCode());
    }

    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void keyTyped(KeyEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void keyReleased(KeyEvent e) {}
}
