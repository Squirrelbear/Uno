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
        faceValueToActionMap[13] = CardAction.Wild;
        faceValueToActionMap[14] = CardAction.Plus4;
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
