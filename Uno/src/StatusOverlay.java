import java.awt.*;

public class StatusOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    private TurnActionFactory.TurnDecisionAction currentAction;
    private String statusText;
    private Font statusFont = new Font("Arial", Font.BOLD, 20);
    private Position centre;
    private double timeOut;
    private String timeOutStr;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds
     */
    public StatusOverlay(Rectangle bounds) {
        super(bounds);
        setEnabled(false);
        centre = bounds.getCentre();
        timeOutStr = "";
    }

    @Override
    public void update(int deltaTime) {
        // TODO show timeout
        timeOut -= deltaTime / 1000.0;
        if(timeOut < 0) timeOut = 0;
        timeOutStr = (int)timeOut + "s";
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(statusFont);
        int strWidth = g.getFontMetrics().stringWidth(statusText);
        g.drawString(statusText, centre.x-strWidth/2, centre.y-20);
        g.setColor(timeOut < 6 ? Color.RED : Color.YELLOW);
        strWidth = g.getFontMetrics().stringWidth(timeOutStr);
        g.drawString(timeOutStr, centre.x-strWidth/2, centre.y-40);
    }

    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        setEnabled(true);
        statusText = createContextString(currentAction);
        timeOut = CurrentGameInterface.getCurrentGame().getRuleSet().getDefaultTimeOut();
        timeOutStr = (int)timeOut + "s";
    }

    @Override
    public void hideOverlay() {
        setEnabled(false);
    }

    private String createContextString(TurnActionFactory.TurnDecisionAction currentAction) {
        String playerName = CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerName();
        String result;
        switch(currentAction.flagName) {
            case "keepOrPlay" -> result = "choosing to Keep or Play.";
            case "wildColour" -> result = "choosing Wild Colour.";
            case "isChallenging" -> result = "choosing Response to +4.";
            case "otherPlayer" -> result = "choosing Other Player to Swap With.";
            default -> result = "thinking...";
        }
        if(CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerType() == Player.PlayerType.ThisPlayer) {
            return "You are " + result;
        }
        return playerName + " is " + result;
    }
}
