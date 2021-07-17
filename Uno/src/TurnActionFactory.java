/*
    Actions:
    Play Card -> Place Card -> Lookup Action and Execute Sequence

    Play Card No Effect: MoveToNextTurn

    Draw Card: Draw Card-> CanPlayCard? -> (true) -> Keep Or Play? -> Keep -> MoveToNextTurn
                                                                            -> Play -> Begin Action Play Card
                                                 -> (false) -> DrawTillCanPlay? -> (true) ->  Begin Action Draw Card
                                                                                -> (false) -> MoveToNextTurn

    +2 Action -> MoveToNextTurn -> Increase Draw Count +2 -> CheckCanRespond
                    -> CanRespond -> WaitForAction(Play +2 (new chain) or Cancel) -> Begin Action Play Card
                    -> Can't Respond or Cancel Option -> Draw Card * Draw Count + Reset Draw Count to 0 -> MoveToNextTurn

    Wild Action -> WildColourSelection -> Set top pile colour -> MoveToNextTurn
    +4 Action -> CheckCanPlayStatus -> WildColourSelection -> Set top pile colour -> MoveToNextTurn
				-> Wait for Action Choice -> Is Challenging -> Yes -> Could previous player play a card?
																			-> Yes -> MoveToPreviousTurn -> Draw 6 cards
																										 -> MoveToNextPlayer
																										 -> Draw * Draw Count + reset
																			-> No -> Increase drawCount by 4 -> Draw * Draw Count + reset draw count
																-> No -> Is Chaining? -> Yes -> Begin Action Play Card
																					  -> No -> Increase drawCount by 4 -> Draw * Draw Count + reset draw count


    Skip Action: Place Card -> MoveToNextTurn -> Show Skip -> MoveToNextTurn
    Reverse Action: Place Card -> Toggle Turn Direction Order -> MoveToNextTurn

    Swap Action: Player Selection -> Swap Hands (current, selected) -> MoveToNextTurn

    PassAll Action: PassAllHands -> MoveToNextTurn

    WaitForValidAction: JumpInAction -> Set Player to Jumping Player -> Begin Action Play Card
                        Deck -> Begin Action Draw Card
                        Card Choice -> Begin Action Play Card

    // Flag as unsafe at start of turn with 2 cards
    Call Uno Action: Make player safe
    Catch Player Action: MoveToPreviousPlayer -> Alert Caught -> Draw Card * 2 -> MoveToNextPlayer



    Two Player: Reverse becomes skip

    Progressive Uno: Response Allowed for +2 and +4 enabled

    Seven-O: 7 (Swap Action) 0 (all pass hand to left)


    ACTIONS REQUIRED DATA:
    Play Card As Action (Prerequisite current player turn and Choice of Valid Card)
    Place Card (Data playerID, cardID) -> remove card from hand and place onto pile
    No Action (Requires nothing, does nothing)
    MoveToNextTurn (Requires nothing) -> iterate to next playerID
    Draw Card As Action (Prerequisite current player)
    Draw Card (Requires nothing) -> takes one card from deck and adds to hand of current player
    CanPlayCard? (Data playerID, cardID) -> true action 1, false action 2
    KeepOrPlay? Decision (Data playerID, cardID) -> Wait for result from current player
    KeepOrPlay? Result (Data playerID, cardID, true/false) -> true action 1, false action 2
    DrawTillCanPlay? -> true action 1, false action 2
    +2 Action (Requires nothing)
    IncreaseDrawCount (Data amount)
    CheckCanRespond? (Data +2 or +4, playerID), true action 1, false action 2
    WaitForPlayOrCancel? Decision (Data playerID, card type (+2/+4)
    WaitForPlayOrCancel? Result (Data playerID, card type (+2/+4), cardID, true/false) -> true action 1, false action 2
    DrawCountCards (Data playerID, numCards) -> loop calling Draw Card numCard times.
    ResetDrawCount Resets draw count to 0
    Wild Action (Requires nothing)
    WildColourSelection Decision (Data playerID) -> waits for choice
    WildColourSelection Result (Data playerID, colourID) -> set colour of top pile to colour ID
    Skip Action (Requires nothing)
    ShowSkip (Data playerID) show skip icon above player for X seconds.
    Reverse Action (Requires nothing)
    ToggleTurnDirection
    SetTurnDirection (Data direction)
    Swap Action (Requires nothing)
    ChooseSwapTargetPlayer? Decision (Data playerID) -> waits for choice
    ChooseSwapTargetPlayer? Result (Data playerID, choiceOfPlayer) -> swap hands
    PassAll Action (Requires nothing) Move all hands to the left





 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TurnActionFactory {

    public static class TurnAction {
        protected Map<String, Integer> storedData;
        protected Consumer<Map<String, Integer>> action;
        protected TurnAction next;
        protected String actionDebugText;

        public TurnAction(TurnAction next, Map<String, Integer> storedData, Consumer<Map<String, Integer>> action, String actionDebugText) {
            this.next = next;
            this.storedData = storedData;
            this.action = action;
            this.actionDebugText = actionDebugText;
        }

        /*public TurnAction(TurnAction next, Map<String, Integer> storedData, Consumer<Map<String, Integer>> action) {
            this(next, storedData, action, "Not Set");
        }*/

        public void performAction() {
            if(action != null) {
                action.accept(storedData);
            }
        }

        public TurnAction getNext() {
            return next;
        }

        public void injectProperty(String key, Integer value) {
            storedData.put(key, value);
        }
    }

    public static class TurnDecisionAction extends TurnAction {
        protected boolean timeOut;
        protected TurnAction otherNext;
        protected String flagName;
        protected boolean hasRunOnce;

        /*public TurnDecisionAction(TurnAction next, TurnAction otherNext, int timeOut, String flagName,
                                  Map<String, Integer> storedData, Consumer<Map<String, Integer>> action) {
            super(next, storedData, action);
            this.otherNext = otherNext;
            this.timeOut = timeOut;
            this.flagName = flagName;
        }*/

        public TurnDecisionAction(TurnAction next, TurnAction otherNext, boolean timeOut, String flagName,
                                  Map<String, Integer> storedData, Consumer<Map<String, Integer>> action, String actionDebugText) {
            super(next, storedData, action, actionDebugText);
            this.otherNext = otherNext;
            this.timeOut = timeOut;
            this.flagName = flagName;
            hasRunOnce = false;
        }

        @Override
        public TurnAction getNext() {
            if(storedData.containsKey(flagName)) {
                return (storedData.get(flagName) == 0) ? next : otherNext;
            }
            return this;
        }

        @Override
        public void performAction() {
            if(hasRunOnce) return;
            hasRunOnce = true;
            super.performAction();
        }

        public void injectFlagProperty(Integer value) {
            injectProperty(flagName, value);
        }
    }

    public static TurnAction playCardAsAction(int playerID, int cardID, int faceValueID, int colourID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        storedData.put("cardID", cardID);
        storedData.put("faceValueID", faceValueID);
        storedData.put("colourID", colourID);
        TurnAction nextSequence = cardIDToTurnAction(faceValueID, storedData);
        return new TurnAction(nextSequence, storedData, TurnActionFactory::placeCard, "Place Card");
    }

    public static void debugOutputTurnActionTree(TurnAction headNode) {
        debugRecursiveNodeOutput(headNode, 0);
    }

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

    public static TurnAction drawCardAsAction(int playerID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction playCard = new TurnAction(null, storedData, TurnActionFactory::playCardAsActionFromData, "Play the Drawn Card");
        TurnDecisionAction keepOrPlay = new TurnDecisionAction(moveToNextTurn, playCard, true,
                "keepOrPlay", storedData, TurnActionFactory::beginKeepOrPlayChoice, "Keep Or Play Choice");
        TurnAction keepDrawing = new TurnAction(null, storedData, TurnActionFactory::drawCardAsActionFromData, "Draw Another Card (Recursive Tree)");
        TurnDecisionAction drawTillCanPlay = new TurnDecisionAction(moveToNextTurn,keepDrawing,false,
                "drawTillCanPlay?", storedData, TurnActionFactory::checkDrawTillCanPlayRule, "Check Draw Till Can Play Rule");
        TurnDecisionAction canPlayCard = new TurnDecisionAction(drawTillCanPlay, keepOrPlay, false,
                "cardPlayable", storedData, TurnActionFactory::isCardPlayable, "Check is the Card Playable");
        TurnAction drawCard = new TurnAction(canPlayCard, storedData, TurnActionFactory::drawCard, "Draw a Card");
        return drawCard;
    }

    private static TurnAction playCardAsActionFromData(Map<String, Integer> storedData) {
        TurnAction playCard = playCardAsAction(storedData.get("playerID"), storedData.get("cardID"),
                storedData.get("faceValueID"), storedData.get("colourID"));
        playCard.injectProperty("drawCount", storedData.get("drawCount"));
        CurrentGameInterface.getCurrentGame().setCurrentTurnAction(playCard);
        return playCard;
    }

    private static TurnAction drawCardAsActionFromData(Map<String, Integer> storedData) {
        TurnAction drawCardSequence = drawCardAsAction(storedData.get("playerID"));
        CurrentGameInterface.getCurrentGame().setCurrentTurnAction(drawCardSequence);
        return drawCardSequence;
    }

    private static TurnAction playPlus2Action(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction dealPenalty = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::drawNCards, "Draw N Number Cards");
        TurnAction playCard = playCardAsActionFromData(storedData);
        TurnDecisionAction waitForPlay2OrCancel = new TurnDecisionAction(dealPenalty,playCard, true,
                "playCard", storedData, TurnActionFactory::checkForPlay2OrCancel, "Check for +2 or Cancel Choice");
        TurnDecisionAction checkCanRespond = new TurnDecisionAction(dealPenalty, waitForPlay2OrCancel, false,
                "hasPlus2AndResponseAllowed", storedData, TurnActionFactory::hasPlus2AndResponseAllowed, "Can Stack and has a +2");
        TurnAction increaseDrawCount = new TurnAction(checkCanRespond, storedData, TurnActionFactory::increaseDrawCountBy2, "Increase N (drawCount) by 2");
        return new TurnAction(increaseDrawCount, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
    }

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
                false, "couldPreviousPlayCard", storedData, null, "Could the Previous Player Have played a Card? (No Action)");
        TurnDecisionAction isChallenging = new TurnDecisionAction(isChainingCard, couldPreviousPlayCard, true,
                "isChallenging", storedData, TurnActionFactory::beginChallengeChoice, "Ask if the player wants to Challenge, Stack, or Do Nothing");
        TurnAction moveToNextTurn = new TurnAction(isChallenging, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour, "Change the Colour on Top of Pile");
        TurnDecisionAction chooseWildColour = new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                true, "wildColour", storedData, TurnActionFactory::beginWildSelection, "Ask player for a Colour Choice");
        TurnAction checkCouldPlayCard = new TurnAction(chooseWildColour, storedData, TurnActionFactory::checkCouldPlayCard, "Check if a Card Could have been Played");
        return checkCouldPlayCard;
    }

    private static TurnAction playWildAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour, "Change the Colour on Top of Pile");
        TurnDecisionAction chooseWildColour = new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                true, "wildColour", storedData, TurnActionFactory::beginWildSelection, "Ask player for a Colour Choice");
        return chooseWildColour;
    }

    private static TurnAction playSkipAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurnAtEnd = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction showSkip = new TurnAction(moveToNextTurnAtEnd, storedData, TurnActionFactory::showSkip, "Show a Skip Icon Over Player");
        TurnAction moveToNextTurnAtStart = new TurnAction(showSkip, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        return moveToNextTurnAtStart;
    }

    private static TurnAction playReverseAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction swapDirection = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::togglePlayDirection, "Toggle Direction of Play");
        return swapDirection;
    }

    private static TurnAction playSwapAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction swapHands = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::swapHandWithOther, "Swap Hands with Selected Player");
        TurnDecisionAction choosePlayerToSwapWith = new TurnDecisionAction(swapHands,swapHands,true,
                "otherPlayer",storedData,TurnActionFactory::beginChoosePlayerToSwapWith, "Choose Other Player to Swap With");
        return choosePlayerToSwapWith;
    }

    private static TurnAction playPassAllAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Move to Next Turn");
        TurnAction passAllHands = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::passAllHands, "Pass All Hands");
        return passAllHands;
    }

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

    private static void placeCard(Map<String, Integer> storedData) {
        // Get card from hand
        Player currentPlayer = CurrentGameInterface.getCurrentGame().getCurrentPlayer();
        Card cardToPlace = currentPlayer.getCardByID(storedData.get("cardID"));
        // Remove card from hand
        currentPlayer.removeCard(cardToPlace);
        // Add card to pile
        CurrentGameInterface.getCurrentGame().placeCard(cardToPlace);
    }

    private static void moveNextTurn(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().moveToNextPlayer();
    }

    private static void increaseDrawCountBy2(Map<String, Integer> storedData) {
        increaseDrawCountByN(2, storedData);
    }

    private static void increaseDrawCountBy4(Map<String, Integer> storedData) {
        increaseDrawCountByN(4, storedData);
    }

    private static void increaseDrawCountByN(int N, Map<String, Integer> storedData) {
        int result = N;
        if(storedData.containsKey("drawCount")) {
            result += storedData.get("drawCount");
        }
        storedData.put("drawCount", result);
    }

    private static void drawNCards(Map<String, Integer> storedData) {
        if(storedData.containsKey("drawCount")) {
            int count = storedData.get("drawCount");
            for(int i = 0; i < count; i++) {
                drawCard(storedData);
            }
            storedData.remove("drawCount");
        }
    }

    private static void isCardPlayable(Map<String, Integer> storedData) {
        List<Card> recentCards = CurrentGameInterface.getCurrentGame().getRecentCards();
        Card latestCard = recentCards.get(recentCards.size()-1);
        boolean isPlayable = storedData.get("faceValueID") == latestCard.getFaceValueID()
                || storedData.get("colourID") == latestCard.getColourID()
                || storedData.get("faceValueID") >= 13;
        storedData.put("cardPlayable", isPlayable ? 1 : 0);
    }

    private static void beginKeepOrPlayChoice(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().showOverlayForTurnAction();
    }

    private static void checkDrawTillCanPlayRule(Map<String, Integer> storedData) {
        storedData.put("drawTillCanPlay?",
                CurrentGameInterface.getCurrentGame().getRuleSet().shouldDrawnTillCanPlay() ? 1 : 0);
    }

    private static void hasPlus2AndResponseAllowed(Map<String, Integer> storedData) {
        // TODO
    }

    private static void checkForPlay2OrCancel(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().showOverlayForTurnAction();
    }

    private static void showSkip(Map<String, Integer> storedData) {
        // TODO
    }

    private static void togglePlayDirection(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().toggleTurnDirection();
    }

    private static void beginWildSelection(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().showOverlayForTurnAction();
    }

    private static void setTopPileColour(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().setTopCardColour(storedData.get("colourID"));
    }

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

    private static void draw4ChallengeSuccess(Map<String, Integer> storedData) {
        for(int i = 0; i < 4; i++) {
            drawCard(storedData);
        }
    }

    private static void movePrevious(Map<String, Integer> storedData) {
        togglePlayDirection(storedData);
        moveNextTurn(storedData);
        togglePlayDirection(storedData);
    }

    private static void beginChallengeChoice(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().showOverlayForTurnAction();
    }

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

    private static void beginChoosePlayerToSwapWith(Map<String, Integer> storedData) {
        CurrentGameInterface.getCurrentGame().showOverlayForTurnAction();
    }

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
}
