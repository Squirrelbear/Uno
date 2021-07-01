import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck extends Rectangle {
    private List<Card> deck;

    public Deck(Position position) {
        super(position, Card.CARD_WIDTH, Card.CARD_HEIGHT);
        deck = new ArrayList<>();
    }

    public void paint(Graphics g) {
        Card.paintCardBack(g, this);

        g.setColor(Color.BLACK);
        int strWidth = g.getFontMetrics().stringWidth("DECK");
        g.drawString("DECK", position.x+width/2-strWidth/2, position.y-4);
    }

    public Card drawCard() {
        if(deck.isEmpty()) {
            fillDeck();
        }
        Card drawnCard = deck.get(0);
        deck.remove(0);
        return drawnCard;
    }

    private void fillDeck() {
        deck.clear();
        // for each colour
        for(int colourID = 0; colourID < 4; colourID++) {
            // Only 1x"0"
            deck.add(new Card(new Position(position), 0, colourID));
            // Two of 1 to 9, Draw Two, Skip, and Reverse
            for(int faceValue = 1; faceValue <= 12; faceValue++) {
                deck.add(new Card(new Position(position), faceValue, colourID));
                deck.add(new Card(new Position(position), faceValue, colourID));
            }
        }
        // Four of each Wild and Draw 4 Wild.
        for(int i = 0; i < 4; i++) {
            deck.add(new Card(new Position(position), 13, 4));
            deck.add(new Card(new Position(position), 14, 4));
        }
        System.out.println(deck.size());
        // randomise order
        Collections.shuffle(deck);
    }
}
