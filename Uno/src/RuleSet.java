public class RuleSet {
    public enum CardAction { Nothing, Plus2, Plus4, Wild, Skip, Reverse, Swap, PassAll}

    // 0 to 14 are face values of cards
    private CardAction[] faceValueToActionMap;

    public RuleSet() {
        faceValueToActionMap = new CardAction[15];
        for(int i = 0; i <= 9; i++) {
            faceValueToActionMap[i] = CardAction.Nothing;
        }

    }

    public CardAction getActionForCard(int cardID) {
        return faceValueToActionMap[cardID];
    }

}
