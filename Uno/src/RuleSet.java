/*
    // Flag as unsafe at start of turn with 2 cards
    Call Uno Action: Make player safe
    Catch Player Action: MoveToPreviousPlayer -> Alert Caught -> Draw Card * 2 -> MoveToNextPlayer



    Two Player: Reverse becomes skip

    Progressive Uno: Response Allowed for +2 and +4 enabled

    Seven-O: 7 (Swap Action) 0 (all pass hand to left)
 */

public class RuleSet {
    public enum CardAction { Nothing, Plus2, Plus4, Wild, Skip, Reverse, Swap, PassAll}

    // 0 to 14 are face values of cards
    private CardAction[] faceValueToActionMap;
    private boolean canStackCards;
    private boolean drawnTillCanPlay;
    private int defaultTimeOut;

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

    public CardAction getActionForCard(int cardID) {
        return faceValueToActionMap[cardID];
    }

    public boolean canStackCards() {
        return canStackCards;
    }

    public boolean shouldDrawnTillCanPlay() {
        return drawnTillCanPlay;
    }

    public int getDefaultTimeOut() {
        return defaultTimeOut;
    }
}
