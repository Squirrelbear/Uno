import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Uno
 *
 * TurnActionFactory class:
 * This class is responsible for constructing the sequence of actions that occur when
 * cards are drawn or played to manage decisions from the player as a dynamic state machine.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class TurnActionFactory {

    /**
     * Uno
     *
     * TurnAction class:
     * Defines a TurnAction that acts as a linked list of actions.
     * Takes in an action that is expected to be performed once before iterating to a next state.
     *
     * @author Peter Mitchell
     * @version 2021.1
     */
    public static class TurnAction {
        /**
         * Stores a map of data used for passing to the actions to keep stateful data about the action sequence.
         */
        protected final Map<String, Integer> storedData;
        /**
         * The action to be performed via performAction().
         */
        protected final Consumer<Map<String, Integer>> action;
        /**
         * Reference to the next TurnAction in the linked list sequence. This can be null to indicate the end.
         */
        protected final TurnAction next;
        /**
         * Text to be used to describe the current state in debug output.
         */
        protected final String actionDebugText;

        /**
         * Stores the properties specified ready to use.
         *
         * @param next Reference to the next TurnAction in the linked list sequence. This can be null to indicate the end.
         * @param storedData Stores a map of data used for passing to the actions to keep stateful data about the action sequence.
         * @param action The action to be performed via performAction().
         * @param actionDebugText Text to be used to describe the current state in debug output.
         */
        public TurnAction(TurnAction next, Map<String, Integer> storedData, Consumer<Map<String, Integer>> action, String actionDebugText) {
            this.next = next;
            this.storedData = storedData;
            this.action = action;
            this.actionDebugText = actionDebugText;
        }

        /**
         * Calls the specified action if it is not null by passing storedData to it.
         */
        public void performAction() {
            if(action != null) {
                action.accept(storedData);
            }
        }

        /**
         * Gets the next element in the linked list.
         *
         * @return The next TurnAction or null to indicate the end.
         */
        public TurnAction getNext() {
            return next;
        }

        /**
         * Stores the specified data into the storedData map to be used for future iterations.
         *
         * @param key Key to store at in the storedData map.
         * @param value Value to associate with the key.
         */
        public void injectProperty(String key, Integer value) {
            storedData.put(key, value);
        }
    }

    /**
     * Uno
     *
     * TurnDecisionAction class:
     * Defines a TurnDecisionAction that acts as a linked list of actions with a split to one
     * of two different options based on the value stored into a flagged variable.
     * Takes in an action that is expected to be performed once before iterating to a next state.
     *
     * @author Peter Mitchell
     * @version 2021.1
     */
    public static class TurnDecisionAction extends TurnAction {
        /**
         * When true, the TurnDecisionAction has a time limit to complete it.
         */
        protected final boolean timeOut;
        /**
         * The alternative TurnAction to move to if the flag variable is non-zero.
         */
        protected final TurnAction otherNext;
        /**
         * The flag variable used to determine when the decision has been met.
         */
        protected final String flagName;
        /**
         * A boolean to track whether the action has already been run.
         */
        protected boolean hasRunOnce;

        /**
         * Defines a TurnDecisionAction that chooses to use either the next or otherNext TurnAction
         * based on the value stored in flagName's mapped value stored in storedData. 0 will trigger next,
         * and 1 will trigger otherNext. getNext() will continue to return this current object until
         * the flagName has been set to a value.
         *
         * @param next Used when flagName's value is 0. Reference to the next TurnAction in the linked list sequence. This can be null to indicate the end.
         * @param otherNext Used when flagName's value is not 0. Reference to the alternative next TurnAction in the linked list sequence. This can be null to indicate the end.
         * @param timeOut When true, the TurnDecisionAction has a time limit to complete it.
         * @param flagName The flag variable used to determine when the decision has been met.
         * @param storedData Stores a map of data used for passing to the actions to keep stateful data about the action sequence.
         * @param action The action to be performed via performAction().
         * @param actionDebugText Text to be used to describe the current state in debug output.
         */
        public TurnDecisionAction(TurnAction next, TurnAction otherNext, boolean timeOut, String flagName,
                                  Map<String, Integer> storedData, Consumer<Map<String, Integer>> action, String actionDebugText) {
            super(next, storedData, action, actionDebugText);
            this.otherNext = otherNext;
            this.timeOut = timeOut;
            this.flagName = flagName;
            hasRunOnce = false;
        }

        /**
         * Checks if the flagName has been set in storedData. If it has been set the
         * value is evaluated such that 0 returns next, or any other value returns otherNext.
         * When it has not yet been set the method will continue to return a reference
         * to the current class.
         *
         * @return The current object or the next TurnAction to use.
         */
        @Override
        public TurnAction getNext() {
            if(storedData.containsKey(flagName)) {
                return (storedData.get(flagName) == 0) ? next : otherNext;
            }
            return this;
        }

        /**
         * Checks if the action has already been performed. Then performs
         * the action if it is not null based on the definition in TurnAction.
         */
        @Override
        public void performAction() {
            if(hasRunOnce) return;
            hasRunOnce = true;
            super.performAction();
        }

        /**
         * A shortcut method to storing a value directly into the flagName associated
         * with this TurnDecisionAction.
         *
         * @param value The value to set into the storedData using flagName.
         */
        public void injectFlagProperty(Integer value) {
            injectProperty(flagName, value);
        }
    }

    /**
     * Queues placing the specified card followed by the sequence of actions that result from the
     * type of card that was played from calling this method.
     *
     * @param playerID The player controlling the card.
     * @param cardID The unique ID associated with the card to be played.
     * @param faceValueID The reference to what is shown on the card to be played.
     * @param colourID The colour of the card to be played.
     * @return A sequence of actions based on the card that is being played.
     */
    public static TurnAction playCardAsAction(int playerID, int cardID, int faceValueID, int colourID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        storedData.put("cardID", cardID);
        storedData.put("faceValueID", faceValueID);
        storedData.put("colourID", colourID);
        TurnAction nextSequence = cardIDToTurnAction(faceValueID, storedData);
        return new TurnAction(nextSequence, storedData, TurnActionFactory::placeCard, "Place Card");
    }

    /**
     * Iterates over the provided TurnAction tree recursively and outputs it for debug purposes to the console.
     *
     * @param headNode Node to recursively generate a tree output from.
     */
    public static void debugOutputTurnActionTree(TurnAction headNode) {
        debugRecursiveNodeOutput(headNode, 0);
    }

    /**
     * Prints out the tree from the specified node downward splitting at any TurnDecisionAction.
     *
     * @param currentNode The node to print at this level of the iteration.
     * @param indentLevel Indicates how far to indent text for the output and for numbering.
     */
    private static void debugRecursiveNodeOutput(TurnAction currentNode, int indentLevel) {
        if(currentNode == null) return;
        if(currentNode instanceof TurnDecisionAction) {
            TurnDecisionAction currentSplitNode = (TurnDecisionAction) currentNode;
            System.out.println("\t".repeat(indentLevel) + "? " + (indentLevel+1) + ". " + currentSplitNode.flagName
                                + " Timeout: " + currentSplitNode.timeOut + " " + currentSplitNode.actionDebugText);
            debugRecursiveNodeOutput(currentSplitNode.next,indentLevel+1);
            if(currentSplitNode.next != currentSplitNode.otherNext) {
                debugRecursiveNodeOutput(currentSplitNode.otherNext, indentLevel + 1);
            }
        } else {
            System.out.println("\t".repeat(indentLevel) + "- " + (indentLevel+1) + ". " + currentNode.actionDebugText);
            debugRecursiveNodeOutput(currentNode.next,indentLevel+1);
        }
    }

    /**
     * This method should be used when the player is using their turn action to draw a card from the deck.
     * The decision tree generated by this method follows the sequence shown below. It is constructed in reverse.
     *     Draw Card -> cardPlayable? -> (true) -> keepOrPlay? -> Keep -> MoveToNextTurn
     *                                                         -> Play -> Begin Action Play Card
     *                                -> (false) -> drawTillCanPlay? -> (true) ->  Begin Action Draw Card
     *                                                               -> (false) -> MoveToNextTurn
     *
     * @param playerID The player who is performing the drawing action.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    public static TurnAction drawCardAsAction(int playerID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction playCard = new TurnAction(null, storedData, TurnActionFactory::playCardAsActionFromData, "Play the Drawn Card");
        TurnDecisionAction keepOrPlay = new TurnDecisionAction(moveToNextTurn, playCard, true,
                "keepOrPlay", storedData, TurnActionFactory::beginChoiceOverlay, "Keep Or Play Choice");
        TurnAction keepDrawing = new TurnAction(null, storedData, TurnActionFactory::drawCardAsActionFromData, "Draw Another Card (Recursive Tree)");
        TurnDecisionAction drawTillCanPlay = new TurnDecisionAction(moveToNextTurn,keepDrawing,false,
                "drawTillCanPlay?", storedData, TurnActionFactory::checkDrawTillCanPlayRule, "Check Draw Till Can Play Rule");
        TurnDecisionAction canPlayCard = new TurnDecisionAction(drawTillCanPlay, keepOrPlay, false,
                "cardPlayable", storedData, TurnActionFactory::isCardPlayable, "Check is the Card Playable");
        return new TurnAction(canPlayCard, storedData, TurnActionFactory::drawCard, "Draw a Card");
    }

    /**
     * Requires storedData contains (playerID, cardID, faceValueID, colourID)
     * If the drawCount was set it is carried over. All other properties are discarded.
     * The resulting TurnAction sequence is not returned, it is queued up directly
     * into the current game to start a new sequence of playing the card.
     * This method should be used to sequence playing of a card as part
     * of other actions from card effects.
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     */
    private static void playCardAsActionFromData(Map<String, Integer> storedData) {
        TurnAction playCard = playCardAsAction(storedData.get("playerID"), storedData.get("cardID"),
                storedData.get("faceValueID"), storedData.get("colourID"));
        playCard.injectProperty("drawCount", storedData.get("drawCount"));
        CurrentGameInterface.getCurrentGame().setCurrentTurnAction(playCard);
    }

    /**
     * Requires stored data contains a playerID. The resulting TurnAction sequence is
     * not returned, it is queued directly into the current game to start a sequence of
     * drawing the card. This should only be used for sequencing additional draws
     * when drawTillCanPlay? is true and triggers a recursive draw via drawCardAsAction().
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     */
    private static void drawCardAsActionFromData(Map<String, Integer> storedData) {
        TurnAction drawCardSequence = drawCardAsAction(storedData.get("playerID"));
        CurrentGameInterface.getCurrentGame().setCurrentTurnAction(drawCardSequence);
    }

    /**
     * Generates a sequence of TurnActions to handle the events required when a +2 card is played.
     * The following shows the sequence that can occur. It is constructed in reverse.
     *
     *   MoveToNextTurn -> Increase Draw Count +2 -> hasPlus2AndResponseAllowed?
     *                     -> (true) -> isStacking? -> (true) -> Begin Action Play Card
     *                                              -> (false) -> Draw Card * Draw Count + Reset Draw Count to 0 -> MoveToNextTurn
     *                     -> (false) -> Draw Card * Draw Count + Reset Draw Count to 0 -> MoveToNextTurn
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    private static TurnAction playPlus2Action(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction dealPenalty = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::drawNCards, "Draw N Number Cards");
        TurnAction playCard = new TurnAction(null, storedData, TurnActionFactory::playCardAsActionFromData, "Play another +2 (Recursive)");
        TurnDecisionAction waitForPlay2OrCancel = new TurnDecisionAction(dealPenalty,playCard, true,
                "isStacking", storedData, TurnActionFactory::beginChoiceOverlay, "Check for +2 or Cancel Choice");
        TurnDecisionAction checkCanRespond = new TurnDecisionAction(dealPenalty, waitForPlay2OrCancel, false,
                "hasPlus2AndResponseAllowed", storedData, TurnActionFactory::hasPlus2AndResponseAllowed, "Can Stack and has a +2");
        TurnAction increaseDrawCount = new TurnAction(checkCanRespond, storedData, TurnActionFactory::increaseDrawCountBy2, "Increase N (drawCount) by 2");
        return new TurnAction(increaseDrawCount, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
    }

    /**
     * Generates a sequence of TurnActions to handle the events required when a +4 card is played.
     * The following shows the sequence that can occur. It is constructed in reverse.
     *
     * couldPreviousPlayCard PreCheck -> WildColourSelection -> Set top pile colour -> MoveToNextTurn
     * 				-> isChallenging? -> (true) -> couldPreviousPlayCard? -> (true) -> MoveToPreviousTurn -> Draw 6 cards -> MoveToNextPlayer -> Draw * Draw Count + reset
     * 																	  -> (false) -> Increase drawCount by 4 -> Draw * Draw Count + reset draw count
     * 								  -> (false) -> isChaining? -> (true) -> Begin Action Play Card
     * 															-> (false) -> Increase drawCount by 4 -> Draw * Draw Count + reset draw count
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    private static TurnAction playPlus4Action(Map<String, Integer> storedData) {
        TurnAction moveToNextSkipDamagedPlayer = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction drawNCards = new TurnAction(moveToNextSkipDamagedPlayer, storedData, TurnActionFactory::drawNCards, "Draw N Number Cards");
        TurnAction increaseDrawBy4 = new TurnAction(drawNCards, storedData, TurnActionFactory::increaseDrawCountBy4, "Increase N (drawCount) by 4");
        TurnAction playCardAsResponse = new TurnAction(null, storedData, TurnActionFactory::playCardAsActionFromData, "Stack +4 on Previous (Recursive)");
        TurnDecisionAction isChainingCard = new TurnDecisionAction(increaseDrawBy4, playCardAsResponse,
                false, "isChaining", storedData, null, "No Action");
        TurnAction drawNCardsAndDoNothing = new TurnAction(null, storedData, TurnActionFactory::drawNCards, "Draw N Number Cards");
        TurnAction moveBackToNext = new TurnAction(drawNCardsAndDoNothing, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction applyPenalty = new TurnAction(moveBackToNext, storedData, TurnActionFactory::draw4ChallengeSuccess, "Apply penalty (+4) to Player");
        TurnAction moveToPreviousPlayer = new TurnAction(applyPenalty, storedData, TurnActionFactory::movePrevious, "Move to Previous Player");
        TurnAction increaseDrawBy2 = new TurnAction(increaseDrawBy4, storedData, TurnActionFactory::increaseDrawCountBy2, "Increase N (drawCount) by 2");
        TurnDecisionAction couldPreviousPlayCard = new TurnDecisionAction(increaseDrawBy2, moveToPreviousPlayer,
                false, "couldPreviousPlayCard", storedData, TurnActionFactory::showChallengeResult, "Could the Previous Player Have played a Card? (No Action)");
        TurnDecisionAction isChallenging = new TurnDecisionAction(isChainingCard, couldPreviousPlayCard, true,
                "isChallenging", storedData, TurnActionFactory::beginChoiceOverlay, "Ask if the player wants to Challenge, Stack, or Do Nothing");
        TurnAction moveToNextTurn = new TurnAction(isChallenging, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour, "Change the Colour on Top of Pile");
        TurnDecisionAction chooseWildColour = new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                true, "wildColour", storedData, TurnActionFactory::beginChoiceOverlay, "Ask player for a Colour Choice");
        return new TurnAction(chooseWildColour, storedData, TurnActionFactory::checkCouldPlayCard, "Check if a Card Could have been Played");
    }

    /**
     * Generates a sequence of TurnActions to handle the events required when a Wild card is played.
     * The following shows the sequence that can occur. It is constructed in reverse.
     *
     * WildColourSelection -> Set top pile colour -> MoveToNextTurn
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    private static TurnAction playWildAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour, "Change the Colour on Top of Pile");
        return new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                true, "wildColour", storedData, TurnActionFactory::beginChoiceOverlay, "Ask player for a Colour Choice");
    }

    /**
     * Generates a sequence of TurnActions to handle the events required when a Skip card is played.
     * The following shows the sequence that can occur. It is constructed in reverse.
     *
     * MoveToNextTurn -> Show Skip -> MoveToNextTurn
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    private static TurnAction playSkipAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurnAtEnd = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction showSkip = new TurnAction(moveToNextTurnAtEnd, storedData, TurnActionFactory::showSkip, "Show a Skip Icon Over Player");
        return new TurnAction(showSkip, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
    }

    /**
     * Generates a sequence of TurnActions to handle the events required when a Reverse card is played.
     * The following shows the sequence that can occur. It is constructed in reverse.
     *
     * Toggle Turn Direction Order -> MoveToNextTurn
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    private static TurnAction playReverseAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        return new TurnAction(moveToNextTurn, storedData, TurnActionFactory::togglePlayDirection, "Toggle Direction of Play");
    }

    /**
     * Generates a sequence of TurnActions to handle the events required when a Swap card is played.
     * This is for a different game mode with selecting a player to swap hands with.
     * The following shows the sequence that can occur. It is constructed in reverse.
     *
     * otherPlayer? Selection -> Swap Hands (current, selected) -> MoveToNextTurn
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    private static TurnAction playSwapAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction swapHands = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::swapHandWithOther, "Swap Hands with Selected Player");
        return new TurnDecisionAction(swapHands,swapHands,true,
                "otherPlayer",storedData,TurnActionFactory::beginChoiceOverlay, "Choose Other Player to Swap With");
    }

    /**
     * Generates a sequence of TurnActions to handle the events required when a Pass All card is played.
     * This is for a different game mode with shifting all hands around based on turn order.
     * The following shows the sequence that can occur. It is constructed in reverse.
     *
     * Pass All Cards -> MoveToNextTurn
     *
     * @param storedData Reference to the shared data for a sequence of actions.
     * @return The decision tree sequence of TurnActions as described ready for iteration.
     */
    private static TurnAction playPassAllAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        return new TurnAction(moveToNextTurn, storedData, TurnActionFactory::passAllHands, "Pass All Hands");
    }

    /**
     * Looks up a relevant action to apply based on the faceValue of the card. If there is no matching
     * associated action to generate a TurnAction sequence from then the default is to move to the next turn.
     *
     * @param faceValueID The face value of the card being played.
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     * @return A sequence of TurnActions based on the faceValue of the card being played.
     */
    private static TurnAction cardIDToTurnAction(int faceValueID, Map<String, Integer> storedData) {
        return switch (CurrentGameInterface.getCurrentGame().getRuleSet().getActionForCard(faceValueID)) {
            case Plus2 -> playPlus2Action(storedData);
            case Plus4 -> playPlus4Action(storedData);
            case Wild -> playWildAction(storedData);
            case Skip -> playSkipAction(storedData);
            case Reverse -> playReverseAction(storedData);
            case Swap -> playSwapAction(storedData);
            case PassAll -> playPassAllAction(storedData);
            case Nothing -> new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        };
    }

    /**
     * Draws a card from the deck, stores the (cardID, faceValueID, and colourID) in storedData,
     * and then adds the card to the current player's hand.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void drawCard(Map<String, Integer> storedData) {
        // Draw card from deck
        Deck deck = CurrentGameInterface.getCurrentGame().getDeck();
        Card drawnCard = deck.drawCard();
        // store ID into storedData
        storedData.put("cardID", drawnCard.getCardID());
        storedData.put("faceValueID", drawnCard.getFaceValueID());
        storedData.put("colourID", drawnCard.getColourID());
        // Add card to hand
        CurrentGameInterface.getCurrentGame().getCurrentPlayer().addCardToHand(drawnCard);
    }

    /**
     * Requires a cardID is set in storedData. Gets the card referenced by cardID in currentPlayer's hand,
     * then removes the card from their hand and adds the card to the pile of recently played cards.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void placeCard(Map<String, Integer> storedData) {
        // Get card from hand
        Player currentPlayer = CurrentGameInterface.getCurrentGame().getCurrentPlayer();
        Card cardToPlace = currentPlayer.getCardByID(storedData.get("cardID"));
        // Remove card from hand
        currentPlayer.removeCard(cardToPlace);
        // Add card to pile
        CurrentGameInterface.getCurrentGame().placeCard(cardToPlace);
    }

    /**
     * Moves to the next turn by moving one player in the current direction of play.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void moveNextTurn(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().moveToNextPlayer();
    }

    /**
     * Uses increaseDrawCountByN to increase the drawCount by 2.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void increaseDrawCountBy2(Map<String, Integer> storedData) {
        increaseDrawCountByN(2, storedData);
    }

    /**
     * Uses increaseDrawCountByN to increase the drawCount by 4.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void increaseDrawCountBy4(Map<String, Integer> storedData) {
        increaseDrawCountByN(4, storedData);
    }

    /**
     * Gets the current value stored in drawCount in storedData if it exists and adds the value to N
     * before storing the result back into drawCount.
     *
     * @param N The number to add to drawCount.
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void increaseDrawCountByN(int N, Map<String, Integer> storedData) {
        int result = N;
        if(storedData.containsKey("drawCount") && storedData.get("drawCount") != null) {
            result += storedData.get("drawCount");
        }
        storedData.put("drawCount", result);
    }

    /**
     * Requires drawCount is set in storedData. The value is taken and a loop is performed drawCount
     * number of times to call drawCard. After the cards have all been drawn the drawCount is removed
     * from storedData to clear ready for any future use.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void drawNCards(Map<String, Integer> storedData) {
        if(storedData.containsKey("drawCount") && storedData.get("drawCount") != null && storedData.get("drawCount") > 0) {
            int count = storedData.get("drawCount");
            for(int i = 0; i < count; i++) {
                drawCard(storedData);
            }
            CurrentGameInterface.getCurrentGame().showGeneralOverlay(
                    "DrawN"+CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID()
                                + ";" + count);
            storedData.remove("drawCount");
        }
    }

    /**
     * Requires storedData contains faceValueID, and colourID.
     * Gets the top card of the pile and checks if the card stored in storedData is playable.
     * The card is considered playable if it is either matching the faceValueID of the top card,
     * the colour of the top card, or the card is a wild or +4.
     * The result is stored into cardPlayable in storedData as a 1 if it is playable, or 0 if it is not.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void isCardPlayable(Map<String, Integer> storedData) {
        Card latestCard = CurrentGameInterface.getCurrentGame().getTopCard();
        boolean isPlayable = storedData.get("faceValueID") == latestCard.getFaceValueID()
                || storedData.get("colourID") == latestCard.getColourID()
                || storedData.get("faceValueID") >= 13;
        storedData.put("cardPlayable", isPlayable ? 1 : 0);
    }

    /**
     * Used to display a contextual choice overlay automatically based on the current TurnDecisionAction.
     * Calling this method assumes that the current TurnAction is a TurnDecisionAction and will
     * initialise any interface elements to wait for a required input.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void beginChoiceOverlay(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().showOverlayForTurnAction();
    }

    /**
     * Checks the RuleSet to verify if cards should continue to be drawn until one is playable.
     * The result is stored into drawTillCanPlay? as 1 if cards should continue to be drawn, or
     * 0 if cards should not be drawn until something can be played.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void checkDrawTillCanPlayRule(Map<String, Integer> storedData) {
        storedData.put("drawTillCanPlay?",
                CurrentGameInterface.getCurrentGame().getRuleSet().shouldDrawnTillCanPlay() ? 1 : 0);
    }

    /**
     * Checks the ruleset to verify if cards can be stacked. If they can be stacked, and the current player
     * has any +2 card in their hand. The result is stored into hasPlus2AndResponseAllowed in storedData.
     * If a response is allowed in this situation a 1 is stored, otherwise a 0.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void hasPlus2AndResponseAllowed(Map<String, Integer> storedData) {
        if(CurrentGameInterface.getCurrentGame().getRuleSet().canStackCards() &&
                CurrentGameInterface.getCurrentGame().getCurrentPlayer().getHand().stream().anyMatch(card -> card.getFaceValueID() == 10)) {
            storedData.put("hasPlus2AndResponseAllowed", 1);
        } else {
            storedData.put("hasPlus2AndResponseAllowed", 0);
        }
    }

    /**
     * Triggers a SkipVisual overlay over the current player.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void showSkip(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().showGeneralOverlay("SkipVisual"
                + CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID());
    }

    /**
     * Toggles the turn direction between clockwise to anti-clockwise and vice versa.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void togglePlayDirection(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().toggleTurnDirection();
    }

    /**
     * Requires colourID is set in storedData. The colourID is used to set the top card colour.
     * This method is assuming that the action is being applied as part of a Wild colour choice (not enforced).
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void setTopPileColour(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().setTopCardColour(storedData.get("colourID"));
    }

    /**
     * Gets the card played prior to the current top card and checks if there were any valid moves at the
     * time that could have been played instead as a colour card. If there was couldPreviousPlayCard is set to 1.
     * Otherwise couldPreviousPlayCard is set to 0.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void checkCouldPlayCard(Map<String, Integer> storedData) {
        List<Card> recentCards = CurrentGameInterface.getCurrentGame().getRecentCards();
        Card cardBeforeLast = recentCards.get(recentCards.size()-2);
        List<Card> validMoves = CurrentGameInterface.getCurrentGame().getCurrentPlayer().getValidMoves(
                cardBeforeLast.getFaceValueID(), cardBeforeLast.getColourID());
        for(Card card : validMoves) {
            if(card.getFaceValueID() < 13) {
                storedData.put("couldPreviousPlayCard",1);
                return;
            }
        }
        storedData.put("couldPreviousPlayCard",0);
    }

    /**
     * Draws 4 cards to the current player. Use for applying the penalty when a +4 challenge is succeeded.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void draw4ChallengeSuccess(Map<String, Integer> storedData) {
        for(int i = 0; i < 4; i++) {
            drawCard(storedData);
        }
        CurrentGameInterface.getCurrentGame().showGeneralOverlay(
                "DrawN"+CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID() + ";4");
    }

    /**
     * Moves to the previous player. This is accomplished by reversing the play direction,
     * then moving to the next player, and then moving the direction back.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void movePrevious(Map<String, Integer> storedData) {
        togglePlayDirection(storedData);
        moveNextTurn(storedData);
        togglePlayDirection(storedData);
    }

    /**
     * Requires otherPlayer is set in storedData. Gets the cards from the hands of otherPlayer,
     * and the current player. Removes the cards from both players, and then adds all the cards
     * to the opposite player's hand to complete the swap.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void swapHandWithOther(Map<String, Integer> storedData) {
        int targetPlayerID = storedData.get("otherPlayer");
        Player targetPlayer = CurrentGameInterface.getCurrentGame().getPlayerByID(targetPlayerID);
        Card[] targetPlayerHand = (Card[])targetPlayer.getHand().toArray();
        targetPlayer.emptyHand();
        Player currentPlayer = CurrentGameInterface.getCurrentGame().getCurrentPlayer();
        Card[] currentPlayerHand = (Card[])currentPlayer.getHand().toArray();
        currentPlayer.emptyHand();
        for(Card card : targetPlayerHand) {
            currentPlayer.addCardToHand(card);
        }
        for(Card card : currentPlayerHand) {
            targetPlayer.addCardToHand(card);
        }
    }

    /**
     * Empties the hands of all players into an array of hands. Then shifts the hands based on direction of play.
     * The hands are then stored back into players relative to the moved order.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void passAllHands(Map<String, Integer> storedData) {
        List<Card[]> hands = new ArrayList<>();
        List<Player> players = CurrentGameInterface.getCurrentGame().getAllPlayers();
        for(Player player : players) {
            hands.add((Card[])player.getHand().toArray());
            player.emptyHand();
        }

        // Shuffle the hands
        if(CurrentGameInterface.getCurrentGame().isIncreasing()) {
            Card[] movedHand = hands.get(0);
            hands.remove(0);
            hands.add(movedHand);
        } else {
            Card[] movedHand = hands.get(hands.size()-1);
            hands.remove(hands.size()-1);
            hands.add(0, movedHand);
        }

        // put all the cards into the hands again
        for(int playerID = 0; playerID < players.size(); playerID++) {
            for(Card card : hands.get(playerID)) {
                players.get(playerID).addCardToHand(card);
            }
        }
    }

    /**
     * Shows either a tick or cross overlay on the player who challenged.
     *
     * @param storedData Reference to the shared stored data to be used for passing on to all the TurnAction sequence.
     */
    private static void showChallengeResult(Map<String, Integer> storedData) {
        if(storedData.get("couldPreviousPlayCard") == 0) {
            CurrentGameInterface.getCurrentGame().showGeneralOverlay(
                    "ChallengeFailed"+CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID());
        } else {
            CurrentGameInterface.getCurrentGame().showGeneralOverlay(
                    "ChallengeSuccess"+CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID());
        }
    }
}
