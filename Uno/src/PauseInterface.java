import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PauseInterface extends WndInterface {
    private List<Button> buttonList;
    private GamePanel gamePanel;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public PauseInterface(Rectangle bounds, GamePanel gamePanel) {
        super(bounds);
        this.gamePanel = gamePanel;
        buttonList = new ArrayList<>();
        buttonList.add(new Button(new Position(bounds.position.x+6, bounds.position.y+6),
                bounds.width-12, 30, "Resume", 1));
        buttonList.add(new Button(new Position(bounds.position.x+6, bounds.position.y+6+30+6),
                bounds.width-12, 30, "Quit", 2));
    }

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(165, 177, 94, 205));
        g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.setColor(Color.BLACK);
        g.drawRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        buttonList.forEach(button -> button.paint(g));
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        for (Button button : buttonList) {
            button.setHovering(button.isPositionInside(mousePosition));
        }
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        for (Button button : buttonList) {
            if(button.isPositionInside(mousePosition)) {
                handleButtonAction(button.getActionID());
                break;
            }
        }
    }

    private void handleButtonAction(int actionID) {
        switch(actionID) {
            case 1 -> gamePanel.setPauseState(false);
            case 2 -> gamePanel.quitGame();
        }
    }
}
