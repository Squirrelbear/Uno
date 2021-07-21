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
    private CardAction[] faceValueToActionMap;
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
    private int defaultTimeOut;

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
     * Checks whether cards must be drawn till one can be played.
     *
     * @return True if when drawing from the deck for turn cards must continue to be drawn till a playable card is found.
     */
    public boolean shouldDrawnTillCanPlay() {
        return drawnTillCanPlay;
    }

    /**
     * Gets the time in seconds that can be spent maximum for any individual action.
     *
     * @return The time player's have to make their choice during actions.
     */
    public int getDefaultTimeOut() {
        return defaultTimeOut;
    }
}
