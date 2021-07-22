/*
    // Flag as unsafe at start of turn with 2 cards
    Call Uno Action: Make player safe
    Catch Player Action: MoveToPreviousPlayer -> Alert Caught -> Draw Card * 2 -> MoveToNextPlayer



    Two Player: Reverse becomes skip

    Progressive Uno: Response Allowed for +2 and +4 enabled

    Seven-O: 7 (Swap Action) 0 (all pass hand to left)

    TODO
 */

/**
 * Uno
 *
 * RuleSet class:
 * Refines the class that stores the active rules used to determine how cards are mapped to actions
 * and what actions are allowed.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class RuleSet {
    /**
     * Defines the different unique CardActions that can be mapped to faceValueIDs.
     */
    public enum CardAction { Nothing, Plus2, Plus4, Wild, Skip, Reverse, Swap, PassAll}

    /**
     * 0 to 14 mapped with CardActions to represent each of the different Uno cards.
     */
    private final CardAction[] faceValueToActionMap;
    /**
     * True if +2 and +4 cards can be stacked in response.
     */
    private boolean canStackCards;
    /**
     * True if when drawing from the deck for turn cards must continue to be drawn till a playable card is found.
     */
    private boolean drawnTillCanPlay;
    /**
     * The time player's have to make their choice during actions.
     */
    private final int defaultTimeOut;
    /**
     * Used to determine if Reverse becomes a skip when true.
     */
    private boolean onlyTwoPlayers;


    /**
     * Initialises a default RuleSet.
     */
    public RuleSet() {
        faceValueToActionMap = new CardAction[15];
        for(int i = 0; i <= 9; i++) {
            faceValueToActionMap[i] = CardAction.Nothing;
        }
        faceValueToActionMap[10] = CardAction.Plus2;
        faceValueToActionMap[11] = CardAction.Skip;
        faceValueToActionMap[12] = CardAction.Reverse;
        faceValueToActionMap[13] = CardAction.Plus4;
        faceValueToActionMap[14] = CardAction.Wild;
        canStackCards = true;
        drawnTillCanPlay = true;
        onlyTwoPlayers = false;
        defaultTimeOut = 25;
    }

    /**
     * Looks up the CardAction that should be activated in relation to a played card.
     *
     * @param faceValueID The faceValue to look up in the action map.
     * @return The mapped CardAction associated with the specified faceValueID.
     */
    public CardAction getActionForCard(int faceValueID) {
        return faceValueToActionMap[faceValueID];
    }

    /**
     * Checks whether +2 and +4 cards can be played in response to other +2 and +4 cards.
     *
     * @return True if +2 and +4 cards can be stacked in response.
     */
    public boolean canStackCards() {
        return canStackCards;
    }

    /**
     * Changes the state of card stacking.
     *
     * @param canStackCards When true +2 and +4 cards can be stacked in response.
     */
    public void setCanStackCards(boolean canStackCards) {
        this.canStackCards = canStackCards;
    }

    /**
     * Checks whether cards must be drawn till one can be played.
     *
     * @return True if when drawing from the deck for turn cards must continue to be drawn till a playable card is found.
     */
    public boolean shouldDrawnTillCanPlay() {
        return drawnTillCanPlay;
    }

    /**
     * Changes the state of drawing to a card can be played.
     *
     * @param drawnTillCanPlay When true cards must be drawn until one can be played.
     */
    public void setDrawnTillCanPlay(boolean drawnTillCanPlay) {
        this.drawnTillCanPlay = drawnTillCanPlay;
    }

    /**
     * Gets the time in seconds that can be spent maximum for any individual action.
     *
     * @return The time player's have to make their choice during actions.
     */
    public int getDefaultTimeOut() {
        return defaultTimeOut;
    }

    /**
     * Sets the state of whether there are only two players.
     *
     * @param onlyTwoPlayers When true Reverse becomes a skip.
     */
    public void setTwoPlayers(boolean onlyTwoPlayers) {
        this.onlyTwoPlayers = onlyTwoPlayers;
        faceValueToActionMap[12] = onlyTwoPlayers ? CardAction.Skip : CardAction.Reverse;
    }

    /**
     * Gets the current state of the two players.
     *
     * @return True if the only two player rules are active with Reverse set to a skip.
     */
    public boolean getOnlyTwoPlayers() {
        return onlyTwoPlayers;
    }
}
