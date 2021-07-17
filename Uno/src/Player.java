import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Player {
    public enum PlayerType { ThisPlayer, AIPlayer, NetworkPlayer}
    private final int playerNumber;
    private final String playerName;
    private PlayerType playerType;
    private final Rectangle bounds;

    private final List<Card> hand;
    private Card hoveredCard;
    private boolean showCards;

    public Player(int playerNumber, String playerName, PlayerType playerType, Rectangle bounds) {
        this.playerName = playerName;
        this.playerNumber = playerNumber;
        this.playerType = playerType;
        this.bounds = bounds;
        hand = new ArrayList<>();
        showCards = playerType == PlayerType.ThisPlayer;
    }

    public void paint(Graphics g) {
        if(showCards) {
            hand.forEach(card -> card.paint(g));
        } else {
            hand.forEach(card -> Card.paintCardBack(g, card));
        }
    }

    public void addCardToHand(Card card) {
        hand.add(card);
        recalculateCardPositions();
    }

    public void emptyHand() {
        hand.clear();
    }

    public void revealHand(boolean reveal) {
        showCards = reveal;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public List<Card> getValidMoves(int curFaceValue, int curColourValue) {
        List<Card> result = new ArrayList<>();
        for(Card card : hand) {
            if(//curFaceValue == 13 || curFaceValue == 14
                     card.getFaceValueID() == curFaceValue
                    || card.getColourID() == curColourValue
            || card.getFaceValueID() == 13 || card.getFaceValueID() == 14) {
                result.add(card);
            }
        }
        return result;
    }

    public void sortHand() {
        Comparator<Card> compareByCard = Comparator
                .comparing(Card::getColourID)
                .thenComparing(Card::getFaceValueID);
        hand.sort(compareByCard);
        recalculateCardPositions();
    }

    public void updateHover(Position mousePosition) {
        if(hoveredCard != null && !hoveredCard.isPositionInside(mousePosition)) {
            hoveredCard = null;
        }
        for(Card card : hand) {
            if(card.isPositionInside(mousePosition)) {
                hoveredCard = card;
                break;
            }
        }
        recalculateCardPositions();
    }

    public void removeCard(Card card) {
        hand.remove(card);
        recalculateCardPositions();
    }

    public Card getCardByID(int cardID) {
        for(Card card : hand) {
            if(card.getCardID() == cardID) {
                return card;
            }
        }
        return null;
    }

    public Card chooseCardFromClick(Position mousePosition) {
        updateHover(mousePosition);
        return hoveredCard;
    }

    public List<Card> getHand() {
        return hand;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getHandTotalScore() {
        int score = 0;
        for (Card card : hand) {
            score += card.getScoreValue();
        }
        return score;
    }

    private void recalculateCardPositions() {
        int paddingX = -15;
        int paddingY = (playerType == PlayerType.ThisPlayer) ? 10 : -Card.CARD_HEIGHT/2-10;
        int elementsPerRow = (bounds.width+paddingX)/Card.CARD_WIDTH;
        int rows = (int)Math.ceil(hand.size()/(double)elementsPerRow);
        int startY = bounds.position.y+bounds.height/2-rows*(Card.CARD_HEIGHT+paddingY)/2;
        int x = 0;
        int y = 0;
        int remainingElements = hand.size();
        int rowXOffset = bounds.width/2-(int)(elementsPerRow*(Card.CARD_WIDTH+paddingX)/2.0);

        if(remainingElements < elementsPerRow) {
            rowXOffset = bounds.width/2-(int)(remainingElements*(Card.CARD_WIDTH+paddingX)/2.0);
        }
        for(Card card : hand) {
            int hoverOffset = (card == hoveredCard) ? -10 : 0;
            card.position.setPosition(bounds.position.x + rowXOffset + x*(Card.CARD_WIDTH+paddingX),
                                     startY + y*(Card.CARD_HEIGHT+paddingY) + hoverOffset);
            x++;
            if(x >= elementsPerRow) {
                x = 0;
                y++;
                rowXOffset = bounds.width/2-(int)(elementsPerRow*(Card.CARD_WIDTH+paddingX)/2.0);;
                if(remainingElements < elementsPerRow) {
                    rowXOffset = bounds.width/2-(int)(remainingElements*(Card.CARD_WIDTH+paddingX)/2.0);
                }
            }
            remainingElements--;
        }
    }
}
