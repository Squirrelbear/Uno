public class AIPlayer extends Player {
    private enum AIStrategy { Offensive, Defensive, Chaotic }

    private AIStrategy strategy;

    public AIPlayer(int playerNumber, String playerName, Rectangle bounds) {
        super(playerNumber, playerName, PlayerType.AIPlayer, bounds);
        selectRandomStrategy();
    }

    private void selectRandomStrategy() {
        switch((int)(Math.random()*3)) {
            case 0 -> strategy = AIStrategy.Offensive;
            case 1 -> strategy = AIStrategy.Defensive;
            case 2 -> strategy = AIStrategy.Chaotic;
        }
    }
}
