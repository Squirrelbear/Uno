import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Uno
 *
 * AIPlayer class:
 * Defines a specific variation of the Player that is handled
 * automatically by AI choosing actions to take during updates
 * with randomised delays to give players time to watch actions as they occur.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class AIPlayer extends Player {
    /**
     * Defines the different types of Strategies that an AI can have.
     * Random: Selects one of the other three randomly.
     * Offensive: Uses low value cards first.
     * Defensive: Uses high value cards first.
     * Chaotic: Uses random cards from any that can be played.
     */
    public enum AIStrategy { Offensive, Defensive, Chaotic, Random }

    /**
     * The Strategy to be used for selecting how cards are played.
     */
    private AIStrategy strategy;
    /**
     * Timer used for delaying between actions.
     */
    private double delayTimer;
    /**
     * ID of the player being considered for calling out.
     */
    private int consideringPlayerID;
    /**
     * Delay till a decision is made about calling out.
     */
    private double consideringDelayTimer;
    /**
     * When true the current situation allows for a jump in.
     * The transition from false to true is used to evaluate considerJumpIn.
     */
    private boolean canJumpIn;
    /**
     * When true the AIPlayer has chosen to jump in after the period consideringJumpInTimer.
     */
    private boolean consideringJumpIn;
    /**
     * Timer till a jump in is executed if still allowed.
     */
    private double consideringJumpInTimer;

    /**
     * Defines an AI on top of a basic player ready to perform actions
     * during update().
     *
     * @param playerNumber The playerID associated with this player.
     * @param playerName The name shown for this player.
     * @param bounds The region for placing cards within.
     * @param strategy The strategy the AI will use to play.
     * @param showPlayerNameLeft When true, the player's name is centred to the left side of the bounds, otherwise it is centred on the top.
     */
    public AIPlayer(int playerNumber, String playerName, Rectangle bounds, AIStrategy strategy, boolean showPlayerNameLeft) {
        super(playerNumber, playerName, PlayerType.AIPlayer, bounds, showPlayerNameLeft);
        if(strategy == AIStrategy.Random) {
            selectRandomStrategy();
        } else {
            this.strategy = strategy;
        }
        resetDelayTimer();
        consideringDelayTimer = -1;
    }

    /**
     * Chooses a random Strategy.
     */
    private void selectRandomStrategy() {
        switch((int)(Math.random()*3)) {
            case 0 -> strategy = AIStrategy.Offensive;
            case 1 -> strategy = AIStrategy.Defensive;
            case 2 -> strategy = AIStrategy.Chaotic;
        }
    }

    /**
     * Checks for valid actions that can be taken by this player and
     * performs them if there is the ability to.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {
        updateAntiUnoCheck(deltaTime);
        updateJumpInCheck(deltaTime);

        // Do nothing more if this is not the current player.
        if(CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID() != getPlayerID()) {
            return;
        }

        // Delay until
        delayTimer -= deltaTime;
        if(delayTimer <= 0) {
            resetDelayTimer();
        } else {
            return;
        }

        // If there is no turn action to deal with it means that the player is performing their regular turn
        if(CurrentGameInterface.getCurrentGame().getCurrentTurnAction() == null) {
           performTurn();
        } else {
            // Handle the turn action if it is necessary
            TurnActionFactory.TurnAction currentAction = CurrentGameInterface.getCurrentGame().getCurrentTurnAction();
            if(currentAction instanceof TurnActionFactory.TurnDecisionAction) {
                TurnActionFactory.TurnDecisionAction decisionAction = (TurnActionFactory.TurnDecisionAction) currentAction;
                if(decisionAction.timeOut) {
                    handleTurnDecision(decisionAction);
                }
            }
        }
    }

    /**
     * Checks the current status of any available anti-uno calls and makes a decision whether to call them out.
     *
     * @param deltaTime Time since last update.
     */
    private void updateAntiUnoCheck(int deltaTime) {
        for(Player player : CurrentGameInterface.getCurrentGame().getAllPlayers()) {
            if(player != this && !player.isSafe() && player.getHand().size() == 1) {
                if(consideringPlayerID != player.getPlayerID()) {
                    consideringDelayTimer = Math.random() * 800 + 200;
                }
                consideringPlayerID = player.getPlayerID();
            }
        }
        if(consideringPlayerID == -1 || CurrentGameInterface.getCurrentGame().getPlayerByID(consideringPlayerID).isSafe()) {
            consideringPlayerID = -1;
        } else {
            consideringDelayTimer -= deltaTime;
            if(consideringDelayTimer <= 0) {
                consideringDelayTimer = Math.random() * 1200 + 300;
                if(Math.random() * 100 < 30) {
                    CurrentGameInterface.getCurrentGame().applyAntiUno(consideringPlayerID);
                }
            }
        }
    }

    /**
     * Updates the state of jumping in if it is allowed and possible for this player.
     *
     * @param deltaTime Time since last update.
     */
    private void updateJumpInCheck(int deltaTime) {
        if(CurrentGameInterface.getCurrentGame().getRuleSet().allowJumpInRule()
                && CurrentGameInterface.getCurrentGame().getCurrentTurnAction() == null
                && CurrentGameInterface.getCurrentGame().getCurrentPlayer() != this) {
            Card topCard = CurrentGameInterface.getCurrentGame().getTopCard();
            List<Card> validCards = getHand().stream()
                    .filter(card -> card.getFaceValueID() == topCard.getFaceValueID()
                                    && card.getColourID() == topCard.getColourID())
                    .collect(Collectors.toList());
            if(!validCards.isEmpty()) {
                if(!canJumpIn) {
                    consideringJumpIn = Math.random() * 100 < 80;
                    consideringDelayTimer = Math.random() * 200 + 100;
                }
                canJumpIn = true;
            } else {
                canJumpIn = false;
                consideringJumpIn = false;
            }
        } else {
            canJumpIn = false;
            consideringJumpIn = false;
        }

        if(consideringJumpIn) {
            consideringDelayTimer -= deltaTime;
            if(consideringDelayTimer <= 0) {
                Card topCard = CurrentGameInterface.getCurrentGame().getTopCard();
                List<Card> validCards = getHand().stream()
                        .filter(card -> card.getFaceValueID() == topCard.getFaceValueID()
                                && card.getColourID() == topCard.getColourID())
                        .collect(Collectors.toList());
                if(!validCards.isEmpty()) {
                    CurrentGameInterface.getCurrentGame().jumpIn(getPlayerID(), validCards.get(0));
                }
            }
        }
    }

    /**
     * Performs the turn by checking if there are any valid moves to be played.
     * If there is no valid move, a card is drawn from the deck.
     * Otherwise a card is chosen from the valid moves and played by initialising a TurnAction.
     */
    private void performTurn() {
        Card topCard = CurrentGameInterface.getCurrentGame().getTopCard();
        List<Card> validMoves = getValidMoves(topCard.getFaceValueID(), topCard.getColourID());
        if(validMoves.isEmpty()) {
            CurrentGameInterface.getCurrentGame().setCurrentTurnAction(TurnActionFactory.drawCardAsAction(getPlayerID()));
        } else {
            Card cardToPlay = chooseCard(validMoves);
            checkCallUNO();
            CurrentGameInterface.getCurrentGame().setCurrentTurnAction(TurnActionFactory.playCardAsAction(
                    getPlayerID(), cardToPlay.getCardID(), cardToPlay.getFaceValueID(), cardToPlay.getColourID()));
        }
    }

    /**
     * Resets the delay timer back to default.
     */
    private void resetDelayTimer() {
        delayTimer = 1500;
    }

    /**
     * Takes a list of cards that can be played and chooses the card
     * based on the selected strategy for the AI.
     *
     * @param validCards A collection of cards that are all valid to be played.
     * @return A single valid card selected to be played.
     */
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

    /**
     * Checks the flagName of the decisionAction to determine an
     * appropriate response based on other methods in this class.
     *
     * @param decisionAction Reference to the current action requiring a decision.
     */
    private void handleTurnDecision(TurnActionFactory.TurnDecisionAction decisionAction) {
        switch (decisionAction.flagName) {
            case "wildColour" -> chooseWildColour(decisionAction);
            case "keepOrPlay" -> chooseKeepOrPlay(decisionAction);
            case "otherPlayer" -> choosePlayerToSwapWith(decisionAction);
            case "isChallenging" -> chooseChallengeOrDecline(decisionAction);
            case "isStacking" -> chooseStackPlus2(decisionAction);
        }
    }

    /**
     * Gets a list of coloured cards in the AIPlayer's hand. If there are none, or on a random
     * chance the colour is chosen randomly. Otherwise the first card in the list is selected
     * as the colour to be applied.
     *
     * @param decisionAction Reference to the current action requiring a decision.
     */
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

    /**
     * Always chooses to play cards that have been drawn.
     *
     * @param decisionAction Reference to the current action requiring a decision.
     */
    private void chooseKeepOrPlay(TurnActionFactory.TurnDecisionAction decisionAction) {
        checkCallUNO();
        decisionAction.injectFlagProperty(1);
    }

    /**
     * Finds the hand with the smallest number of cards other than their own and
     * swaps indicates a preference to swap with that target.
     *
     * @param decisionAction Reference to the current action requiring a decision.
     */
    private void choosePlayerToSwapWith(TurnActionFactory.TurnDecisionAction decisionAction) {
        Player chosenPlayer = this;
        int cardCount = 9999;
        for(Player player : CurrentGameInterface.getCurrentGame().getAllPlayers()) {
            if(player.getHand().size() < cardCount && player != this) {
                chosenPlayer = player;
                cardCount = chosenPlayer.getHand().size();
            }
        }
        decisionAction.injectFlagProperty(chosenPlayer.getPlayerID());
    }

    /**
     * Checks if cards can be stacked and always chains if they can be with a valid card.
     * Otherwise will randomly decide whether to challenge or decline.
     *
     * @param decisionAction Reference to the current action requiring a decision.
     */
    private void chooseChallengeOrDecline(TurnActionFactory.TurnDecisionAction decisionAction) {
        // Always stack a card if it is allowed and available.
        if(CurrentGameInterface.getCurrentGame().getRuleSet().canStackCards()) {
            Card validCard = getHand().stream().filter(card -> card.getFaceValueID() == 13).findFirst().orElse(null);
            if(validCard != null) {
                checkCallUNO();
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
        // Don't need to check the no bluffing rule because this method is only called if a valid choice is available
        // And the AI will ALWAYS choose to stack a card meaning this will never run the random chance of challenge in those cases.
        decisionAction.injectFlagProperty((int)(Math.random()*2));
    }

    /**
     * Checks if cards can be stacked and then plays a valid +2 if it is available and allowed.
     * Otherwise indicates that it is not being done.
     *
     * @param decisionAction Reference to the current action requiring a decision.
     */
    private void chooseStackPlus2(TurnActionFactory.TurnDecisionAction decisionAction) {
        if(CurrentGameInterface.getCurrentGame().getRuleSet().canStackCards()) {
            Card validCard = getHand().stream().filter(card -> card.getFaceValueID() == 10).findFirst().orElse(null);
            if(validCard != null) {
                checkCallUNO();
                decisionAction.injectProperty("faceValueID", validCard.getFaceValueID());
                decisionAction.injectProperty("colourID", validCard.getColourID());
                decisionAction.injectProperty("cardID", validCard.getCardID());
                decisionAction.injectFlagProperty(1);
                return;
            }
        }
        decisionAction.injectFlagProperty(0);
    }

    /**
     * Evaluates whether to call UNO to make the AI safe.
     */
    private void checkCallUNO() {
        if(getHand().size() != 2) return;
        if(Math.random() * 100 < 70) {
            setUnoState(UNOState.Called);
            CurrentGameInterface.getCurrentGame().showGeneralOverlay("UNOCalled"+getPlayerID());
        }
    }
}
