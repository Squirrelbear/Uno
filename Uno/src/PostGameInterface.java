import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PostGameInterface extends WndInterface {
    /**
     * List of buttons visible on the interface.
     */
    private final List<Button> buttonList;
    private List<Player> players;
    private RuleSet ruleSet;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds Bounds of the interface.
     */
    public PostGameInterface(Rectangle bounds, List<Player> playerList, RuleSet ruleSet) {
        super(bounds);
        this.players = playerList;
        this.ruleSet = ruleSet;

        buttonList = new ArrayList<>();
    }

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));

        // Pause overlay
        if(!isEnabled()) {
            g.setColor(new Color(144, 143, 143, 204));
            g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        }
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;
        buttonList.forEach(button -> {
            if(button.isPositionInside(mousePosition))
                handleButtonPress(button.getActionID());
        });
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;
        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
    }

    private void handleButtonPress(int actionID) {
        switch(actionID) {

        }
    }
}
