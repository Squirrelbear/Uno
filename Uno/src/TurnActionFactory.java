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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TurnActionFactory {

    public static class TurnAction {
        protected Map<String, Integer> storedData;
        protected Consumer<Map<String, Integer>> action;
        protected TurnAction next;

        public TurnAction(TurnAction next, Map<String, Integer> storedData, Consumer<Map<String, Integer>> action) {
            this.next = next;
            this.storedData = storedData;
            this.action = action;
        }

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
        private int timeOut;
        private TurnAction otherNext;
        private String flagName;

        public TurnDecisionAction(TurnAction next, TurnAction otherNext, int timeOut, String flagName,
                                  Map<String, Integer> storedData, Consumer<Map<String, Integer>> action) {
            super(next, storedData, action);
            this.otherNext = otherNext;
            this.timeOut = timeOut;
            this.flagName = flagName;
        }

        @Override
        public TurnAction getNext() {
            if(storedData.containsKey(flagName)) {
                return (storedData.get(flagName) == 0) ? next : otherNext;
            }
            return this;
        }

        public void injectFlagProperty(Integer value) {
            injectProperty(flagName, value);
        }
    }

    public static TurnAction playCardAsAction(int playerID, int cardID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        storedData.put("cardID", cardID);
        TurnAction nextSequence = cardIDToTurnAction(cardID, storedData);
        return new TurnAction(nextSequence, storedData, TurnActionFactory::placeCard);
    }

    public static TurnAction drawCardAsAction(int playerID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn);
        TurnAction playCard = playCardAsActionFromData(storedData);
        TurnDecisionAction keepOrPlay = new TurnDecisionAction(moveToNextTurn, playCard, 25,
                "keepOrPlay", storedData, TurnActionFactory::keepOrPlayChoice);
        TurnAction keepDrawing = new TurnAction(null, storedData, TurnActionFactory::drawCardAsActionFromData);
        TurnDecisionAction drawTillCanPlay = new TurnDecisionAction(moveToNextTurn,keepDrawing,-1,
                "drawTillCanPlay?", storedData, TurnActionFactory::checkDrawTillCanPlayRule);
        TurnDecisionAction canPlayCard = new TurnDecisionAction(drawTillCanPlay, keepOrPlay, -1,
                "cardPlayable", storedData, TurnActionFactory::isCardPlayable);
        TurnAction drawCard = new TurnAction(canPlayCard, storedData, TurnActionFactory::drawCard);
        return drawCard;
    }

    private static TurnAction playCardAsActionFromData(Map<String, Integer> storedData) {
        TurnAction playCard = playCardAsAction(storedData.get("playerID"), storedData.get("cardID"));
        playCard.injectProperty("drawCount", storedData.get("drawCount"));
        return playCard;
    }

    private static TurnAction drawCardAsActionFromData(Map<String, Integer> storedData) {
        return drawCardAsAction(storedData.get("playerID"));
    }

    private static TurnAction playPlus2Action(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn);
        TurnAction dealPenalty = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::drawNCards);
        TurnAction playCard = playCardAsActionFromData(storedData);
        TurnDecisionAction waitForPlay2OrCancel = new TurnDecisionAction(dealPenalty,playCard, 25,
                "playCard", storedData, TurnActionFactory::checkForPlay2OrCancel);
        TurnDecisionAction checkCanRespond = new TurnDecisionAction(dealPenalty, waitForPlay2OrCancel, -1,
                "hasPlus2AndResponseAllowed", storedData, TurnActionFactory::hasPlus2AndResponseAllowed);
        TurnAction increaseDrawCount = new TurnAction(checkCanRespond, storedData, TurnActionFactory::increaseDrawCountBy2);
        return new TurnAction(increaseDrawCount, storedData, TurnActionFactory::moveNextTurn);
    }

    private static TurnAction playPlus4Action(Map<String, Integer> storedData) {
        TurnAction drawNCards = new TurnAction(null, storedData, TurnActionFactory::drawNCards);
        TurnAction increaseDrawBy4 = new TurnAction(drawNCards, storedData, TurnActionFactory::increaseDrawCountBy4);
        TurnAction playCardAsResponse = new TurnAction(null, storedData, TurnActionFactory::drawCardAsActionFromData);
        TurnDecisionAction isChainingCard = new TurnDecisionAction(increaseDrawBy4, playCardAsResponse,
                -1, "isChaining", storedData, null);
        TurnAction moveBackToNext = new TurnAction(drawNCards, storedData, TurnActionFactory::moveNextTurn);
        TurnAction applyPenalty = new TurnAction(moveBackToNext, storedData, TurnActionFactory::drawPenalty);
        TurnAction moveToPreviousPlayer = new TurnAction(applyPenalty, storedData, TurnActionFactory::movePrevious);
        TurnDecisionAction couldPreviousPlayCard = new TurnDecisionAction(increaseDrawBy4, moveToPreviousPlayer,
                -1, "couldPreviousPlayCard", storedData, null);
        TurnDecisionAction isChallenging = new TurnDecisionAction(isChainingCard, couldPreviousPlayCard, 25,
                "", storedData, TurnActionFactory::beginChallengeChoice);
        TurnAction moveToNextTurn = new TurnAction(isChallenging, storedData, TurnActionFactory::moveNextTurn);
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour);
        TurnDecisionAction chooseWildColour = new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                25, "wildColour", storedData, TurnActionFactory::beginWildSelection);
        TurnAction checkCouldPlayCard = new TurnAction(chooseWildColour, storedData, TurnActionFactory::checkCouldPlayCard);
        return checkCouldPlayCard;
    }

    private static TurnAction playWildAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn);
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour);
        TurnDecisionAction chooseWildColour = new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                25, "wildColour", storedData, TurnActionFactory::beginWildSelection);
        return chooseWildColour;
    }

    private static TurnAction playSkipAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurnAtEnd = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn);
        TurnAction showSkip = new TurnAction(moveToNextTurnAtEnd, storedData, TurnActionFactory::showSkip);
        TurnAction moveToNextTurnAtStart = new TurnAction(showSkip, storedData, TurnActionFactory::moveNextTurn);
        return moveToNextTurnAtStart;
    }

    private static TurnAction playReverseAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn);
        TurnAction swapDirection = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::togglePlayDirection);
        return swapDirection;
    }

    private static TurnAction playSwapAction(Map<String, Integer> storedData) {
        // TODO

        return null;
    }

    private static TurnAction playPassAllAction(Map<String, Integer> storedData) {


        return null;
    }

    private static TurnAction cardIDToTurnAction(int cardID, Map<String, Integer> storedData) {
        return switch (CurrentGameInterface.getCurrentGame().getRuleSet().getActionForCard(cardID)) {
            case Plus2 -> playPlus2Action(storedData);
            case Plus4 -> playPlus4Action(storedData);
            case Wild -> playWildAction(storedData);
            case Skip -> playSkipAction(storedData);
            case Reverse -> playReverseAction(storedData);
            case Swap -> playSwapAction(storedData);
            case PassAll -> playPassAllAction(storedData);
            default -> new TurnAction(null, storedData, TurnActionFactory::moveNextTurn);
        };
    }

    private static void drawCard(Map<String, Integer> storedData) {
        // TODO
        // Draw card from deck
        // store ID into storedData
        // Add card to hand
    }

    private static void placeCard(Map<String, Integer> storedData) {
        // TODO
        // Get card from hand
        // Remove card from hand
        // Add card to pile
    }

    private static void moveNextTurn(Map<String, Integer> storedData) {
        // TODO
        // increment player number
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
        // TODO
        // Use card ID to check if it is playable given the current top card
        storedData.put("cardPlayable", 1);
    }

    private static void keepOrPlayChoice(Map<String, Integer> storedData) {

    }

    private static void checkDrawTillCanPlayRule(Map<String, Integer> storedData) {
        // TODO
        storedData.put("drawTillCanPlay?", 1);
    }

    private static void hasPlus2AndResponseAllowed(Map<String, Integer> storedData) {
        // TODO
    }

    private static void checkForPlay2OrCancel(Map<String, Integer> storedData) {
        // TODO
    }

    private static void showSkip(Map<String, Integer> storedData) {
        // TODO
    }

    private static void togglePlayDirection(Map<String, Integer> storedData) {
        // TODO
    }

    private static void beginWildSelection(Map<String, Integer> storedData) {
        // TODO
    }

    private static void setTopPileColour(Map<String, Integer> storedData) {
        // TODO
    }

    private static void checkCouldPlayCard(Map<String, Integer> storedData) {
        // TODO
        // couldPreviousPlayCard
    }

    private static void drawPenalty(Map<String, Integer> storedData) {
        for(int i = 0; i < 6; i++) {
            drawCard(storedData);
        }
    }

    private static void movePrevious(Map<String, Integer> storedData) {
        togglePlayDirection(storedData);
        moveNextTurn(storedData);
        togglePlayDirection(storedData);
    }

    private static void beginChallengeChoice(Map<String, Integer> storedData) {

    }
}
