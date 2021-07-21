import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AIPlayer extends Player {
    public enum AIStrategy { Offensive, Defensive, Chaotic, Random }

    private AIStrategy strategy;
    private double delayTimer;

    public AIPlayer(int playerNumber, String playerName, Rectangle bounds, AIStrategy strategy) {
        super(playerNumber, playerName, PlayerType.AIPlayer, bounds);
        if(strategy == AIStrategy.Random) {
            selectRandomStrategy();
        } else {
            this.strategy = strategy;
        }
    }

    private void selectRandomStrategy() {
        switch((int)(Math.random()*3)) {
            case 0 -> strategy = AIStrategy.Offensive;
            case 1 -> strategy = AIStrategy.Defensive;
            case 2 -> strategy = AIStrategy.Chaotic;
        }
    }

    @Override
    public void update(int deltaTime) {
        if(CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID() != getPlayerID()) {
            return;
        }

        // TODO Need to add Calling of Uno and AntiUno

        delayTimer -= deltaTime;
        if(delayTimer <= 0) {
            resetDelayTimer();
        } else {
            return;
        }

        if(CurrentGameInterface.getCurrentGame().getCurrentTurnAction() == null) {
           Card topCard = CurrentGameInterface.getCurrentGame().getTopCard();
           List<Card> validMoves = getValidMoves(topCard.getFaceValueID(), topCard.getColourID());
           if(validMoves.isEmpty()) {
               CurrentGameInterface.getCurrentGame().setCurrentTurnAction(TurnActionFactory.drawCardAsAction(getPlayerID()));
           } else {
               Card cardToPlay = chooseCard(validMoves);
               CurrentGameInterface.getCurrentGame().setCurrentTurnAction(TurnActionFactory.playCardAsAction(
                       getPlayerID(), cardToPlay.getCardID(), cardToPlay.getFaceValueID(), cardToPlay.getColourID()));
           }
        } else {
            TurnActionFactory.TurnAction currentAction = CurrentGameInterface.getCurrentGame().getCurrentTurnAction();
            if(currentAction != null && currentAction instanceof TurnActionFactory.TurnDecisionAction) {
                TurnActionFactory.TurnDecisionAction decisionAction = (TurnActionFactory.TurnDecisionAction) currentAction;
                if(decisionAction.timeOut) {
                    handleTurnDecision(decisionAction);
                }
            }
        }
    }

    private void resetDelayTimer() {
        delayTimer = 1500;
    }

    private Card chooseCard(List<Card> validCards) {
        if(strategy == AIStrategy.Chaotic) {
            return validCards.get((int)(Math.random()*validCards.size()));
        }

        validCards.sort(Comparator.comparingInt(Card::getScoreValue));

        if(strategy == AIStrategy.Defensive) {
            return validCards.get(validCards.size()-1);
        } else { // Offensive
            return validCards.get(0);
        }
    }

    private void handleTurnDecision(TurnActionFactory.TurnDecisionAction decisionAction) {
        switch (decisionAction.flagName) {
            case "wildColour" -> chooseWildColour(decisionAction);
            case "keepOrPlay" -> chooseKeepOrPlay(decisionAction);
            case "otherPlayer" -> choosePlayerToSwapWith(decisionAction);
            case "isChallenging" -> chooseChallengeOrDecline(decisionAction);
            case "isStacking" -> chooseStackPlus2(decisionAction);
        }
    }

    private void chooseWildColour(TurnActionFactory.TurnDecisionAction decisionAction) {
        List<Card> colouredHandCards = getHand().stream().filter(card -> card.getColourID() != 4).collect(Collectors.toList());

        // No cards, or only wilds, or rare 10% chance: randomly choose colour
        if(colouredHandCards.isEmpty() || Math.random() * 100 > 90) {
            decisionAction.injectProperty("colourID", (int)(Math.random()*4));
        } else { // Use first coloured card
            decisionAction.injectProperty("colourID", colouredHandCards.get(0).getColourID());
        }
        decisionAction.injectFlagProperty(1);
    }

    private void chooseKeepOrPlay(TurnActionFactory.TurnDecisionAction decisionAction) {
        decisionAction.injectFlagProperty(1);
    }

    private void choosePlayerToSwapWith(TurnActionFactory.TurnDecisionAction decisionAction) {
        Player chosenPlayer = null;
        int cardCount = 999;
        for(Player player : CurrentGameInterface.getCurrentGame().getAllPlayers()) {
            if(player.getHand().size() < cardCount && player != this) {
                chosenPlayer = player;
                cardCount = chosenPlayer.getHand().size();
            }
        }
        decisionAction.injectFlagProperty(chosenPlayer.getPlayerID());
    }

    private void chooseChallengeOrDecline(TurnActionFactory.TurnDecisionAction decisionAction) {
        // Always stack a card if it is allowed and available.
        if(CurrentGameInterface.getCurrentGame().getRuleSet().canStackCards()) {
            Card validCard = getHand().stream().filter(card -> card.getFaceValueID() == 13).findFirst().orElse(null);
            if(validCard != null) {
                decisionAction.injectProperty("faceValueID", validCard.getFaceValueID());
                decisionAction.injectProperty("colourID", validCard.getColourID());
                decisionAction.injectProperty("cardID", validCard.getCardID());
                decisionAction.injectProperty("isChaining", 1);
                decisionAction.injectFlagProperty(0);
                return;
            }
        }
        decisionAction.injectProperty("isChaining", 0);
        // Randomly choose 50-50 whether to challenge or decline
        decisionAction.injectFlagProperty((int)(Math.random()*2));
    }

    private void chooseStackPlus2(TurnActionFactory.TurnDecisionAction decisionAction) {
        if(CurrentGameInterface.getCurrentGame().getRuleSet().canStackCards()) {
            Card validCard = getHand().stream().filter(card -> card.getFaceValueID() == 10).findFirst().orElse(null);
            if(validCard != null) {
                decisionAction.injectProperty("faceValueID", validCard.getFaceValueID());
                decisionAction.injectProperty("colourID", validCard.getColourID());
                decisionAction.injectProperty("cardID", validCard.getCardID());
                decisionAction.injectFlagProperty(1);
                return;
            }
        }
        decisionAction.injectFlagProperty(0);
    }
}
