import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LobbyInterface extends WndInterface {
    private List<LobbyPlayer> playerList;
    private List<Button> buttonList;
    private GamePanel gamePanel;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds Area to display the lobby in.
     */
    public LobbyInterface(Rectangle bounds, GamePanel gamePanel) {
        super(bounds);
        this.gamePanel = gamePanel;
        playerList = new ArrayList<>();
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.ThisPlayer,
                new Rectangle(new Position(20,100),bounds.width/2, 100)));
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.AIPlayer,
                new Rectangle(new Position(20,100+120),bounds.width/2, 100)));
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.AIPlayer,
                new Rectangle(new Position(20,100+120*2),bounds.width/2, 100)));
        playerList.add(new LobbyPlayer(0,"Player", Player.PlayerType.AIPlayer,
                new Rectangle(new Position(20,100+120*3),bounds.width/2, 100)));

        buttonList = new ArrayList<>();
        buttonList.add(new Button(new Position(bounds.width/4-150, bounds.height-100),300,60,
                "Toggle Number of Players", 1));
        buttonList.add(new Button(new Position(bounds.width*3/4-150, bounds.height-100),300,60,
                "Start Game", 2));
    }

    @Override
    public void update(int deltaTime) {

    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(205, 138, 78, 128));
        g.fillRect(10, 80, bounds.width/2+20, 500);
        g.setColor(Color.BLACK);
        g.drawRect(10, 80, bounds.width/2+20, 500);

        buttonList.forEach(button -> button.paint(g));
        playerList.forEach(lobbyPlayer -> lobbyPlayer.paint(g));

        if(!isEnabled()) {
            g.setColor(new Color(144, 143, 143, 204));
            g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        }
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
        playerList.forEach(lobbyPlayer -> lobbyPlayer.updateHoverState(mousePosition));
    }

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        buttonList.forEach(button -> {
            if(button.isPositionInside(mousePosition))
                handleButtonPress(button.getActionID());
        });
        playerList.forEach(lobbyPlayer -> {
            if (lobbyPlayer.isPositionInside(mousePosition))
                lobbyPlayer.iterateStrategy();
        });
    }

    private void handleButtonPress(int actionID) {
        switch (actionID) {
            case 1 -> toggleNumberOfPlayers();
            case 2 -> gamePanel.startGame(playerList);
        }
    }

    private void toggleNumberOfPlayers() {
        playerList.get(2).setEnabled(!playerList.get(2).isEnabled());
        playerList.get(3).setEnabled(!playerList.get(3).isEnabled());
    }
}
