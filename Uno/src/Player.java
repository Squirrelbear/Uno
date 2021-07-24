import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Uno
 *
 * Player class:
 * Defines a player with all the information about a single player.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class Player {
    /**
     * Types of players include:
     * ThisPlayer: Only one allowed, this is the player who is playing this game.
     * AIPlayer: Controlled by an AI (should be using an AIPlayer class).
     * NetworkPlayer: Not implemented yet. // TODO
     */
    public enum PlayerType { ThisPlayer, AIPlayer, NetworkPlayer}

    /**
     * The unique ID for this player.
     */
    private final int playerID;
    /**
     * The name for this player.
     */
    private final String playerName;
    /**
     * The type of player. (ThisPlayer, AIPlayer, or NetworkPlayer).
     */
    private final PlayerType playerType;
    /**
     * The region for drawing the player's cards.
     */
    private final Rectangle bounds;

    /**
     * The collection of cards contained in the player's hand.
     */
    private final List<Card> hand;
    /**
     * The card that the player is currently hovering their mouse over.
     */
    private Card hoveredCard;
    /**
     * When true the cards for this player are revealed face-up.
     */
    private boolean showCards;
    /**
     * The total score between multiple rounds for this player.
     */
    private int totalScore;
    /**
     * The score for a single round for this player.
     */
    private int currentRoundScore;
    /**
     * When true this player won the current round.
     * Necessary to store this because a score could be 0 is all other players only have 0s in their hands.
     */
    private boolean wonRound;
    /**
     * When true, the player's name is centred to the left side of the bounds, otherwise it is centred on the top.
     */
    private boolean showPlayerNameLeft;

    /**
     * Initialises the player with an empty hand and defaults to showing cards if
     * the player is defined with the type ThisPlayer.
     *
     * @param playerID The unique ID for this player.
     * @param playerName The name for this player.
     * @param playerType The type of player. (ThisPlayer, AIPlayer, or NetworkPlayer).
     * @param bounds The region for drawing the player's cards.
     * @param showPlayerNameLeft When true, the player's name is centred to the left side of the bounds, otherwise it is centred on the top.
     */
    public Player(int playerID, String playerName, PlayerType playerType, Rectangle bounds, boolean showPlayerNameLeft) {
        this.playerName = playerName;
        this.playerID = playerID;
        this.playerType = playerType;
        this.bounds = bounds;
        this.showPlayerNameLeft = showPlayerNameLeft;
        hand = new ArrayList<>();
        showCards = playerType == PlayerType.ThisPlayer;
        wonRound = false;
        totalScore = currentRoundScore = 0;
    }

    /**
     * Does nothing.
     *
     * @param deltaTime Time since last update.
     */
    public void update(int deltaTime) {

    }

    /**
     * Draws the player's cards with either card backs or fronts. Then draws the player's name nearby.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        if(showCards) {
            hand.forEach(card -> card.paint(g));
        } else {
            hand.forEach(card -> Card.paintCardBack(g, card));
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int strWidth = g.getFontMetrics().stringWidth(playerName);
        g.setColor(new Color(1,1,1, 204));
        int nameXOffset = bounds.position.x + (showPlayerNameLeft ? -(strWidth-50) : (bounds.width/2-(strWidth+30)/2));
        int nameYOffset = bounds.position.y + (showPlayerNameLeft ? (bounds.height/2-20) : -10);
        g.fillRect(nameXOffset, nameYOffset, strWidth+30, 40);
        g.setColor(CurrentGameInterface.getCurrentGame().getCurrentPlayer().getPlayerID() == getPlayerID()
                ? Color.ORANGE : Color.WHITE);
        g.drawString(playerName, nameXOffset+15, nameYOffset+25);
    }

    /**
     * Adds the card to the hand and recalculates the positions where all cards should be positioned.
     *
     * @param card The card to add to the hand.
     */
    public void addCardToHand(Card card) {
        hand.add(card);
        recalculateCardPositions();
    }

    /**
     * Empties the hand.
     */
    public void emptyHand() {
        hand.clear();
    }

    /**
     * Changes the visibility of the Player's cards.
     *
     * @param reveal True makes the card fronts show, false makes card backs show.
     */
    public void revealHand(boolean reveal) {
        showCards = reveal;
    }

    /**
     * Gets the type of Player.
     *
     * @return The type of player.
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Gets the unique player ID.
     *
     * @return The unique playerID.
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Takes in a possible faceValue and colourValue that would normally be the
     * top of pile colours. And checks every card in the hand to find a list
     * of all cards that can be played and returns it.
     *
     * @param curFaceValue The faceValue to check against.
     * @param curColourValue The colourID to check against.
     * @return A list of cards that are valid to be played in this context.
     */
    public List<Card> getValidMoves(int curFaceValue, int curColourValue) {
        List<Card> result = new ArrayList<>();
        for(Card card : hand) {
            if(card.getFaceValueID() == curFaceValue || card.getColourID() == curColourValue
            || card.getFaceValueID() == 13 || card.getFaceValueID() == 14) {
                result.add(card);
            }
        }
        return result;
    }

    /**
     * Sorts the hand and recalculates the positions of all cards.
     * Cards are sorted first by colour and then by face values.
     */
    public void sortHand() {
        Comparator<Card> compareByCard = Comparator
                .comparing(Card::getColourID)
                .thenComparing(Card::getFaceValueID);
        hand.sort(compareByCard);
        recalculateCardPositions();
    }

    /**
     * Updates the hover to check which card is hovered and then updates the
     * positions of all cards to offset based on hovering.
     *
     * @param mousePosition Position of the mouse cursor.
     */
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

    /**
     * Removes the card from the hand and recalculates position of all cards.
     *
     * @param card Card to be removed.
     */
    public void removeCard(Card card) {
        hand.remove(card);
        recalculateCardPositions();
    }

    /**
     * Searches to find the cardID.
     *
     * @param cardID cardID to search for.
     * @return The Card with cardID or null.
     */
    public Card getCardByID(int cardID) {
        for(Card card : hand) {
            if(card.getCardID() == cardID) {
                return card;
            }
        }
        return null;
    }

    /**
     * Updates the hovering position. Then returns any currently hovered card.
     *
     * @param mousePosition Position of the mouse.
     * @return The currently hovered card (can be null if none).
     */
    public Card chooseCardFromClick(Position mousePosition) {
        updateHover(mousePosition);
        return hoveredCard;
    }

    /**
     * Gets all the cards in the player's hand.
     *
     * @return The list of cards in this player's hand.
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Gets the player name.
     *
     * @return The player name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Adds up the score of all cards in the current hand.
     *
     * @return A total score for all the cards in the hand.
     */
    public int getHandTotalScore() {
        int score = 0;
        for (Card card : hand) {
            score += card.getScoreValue();
        }
        return score;
    }

    /**
     * Gets the centre of the player's region.
     *
     * @return Centre of the bounds where cards are drawn.
     */
    public Position getCentreOfBounds() {
        return bounds.getCentre();
    }

    /**
     * Recalculates positions for all cards by calculating numbers of
     * rows and columns then centring inside the region and applying
     * positions to all cards in the hand.
     */
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

        // True when there is only one not-full row (used to centre in that row).
        if(remainingElements < elementsPerRow) {
            rowXOffset = bounds.width/2-(int)(remainingElements*(Card.CARD_WIDTH+paddingX)/2.0);
        }
        for(Card card : hand) {
            // Apply a visual offset to the hovered card
            int hoverOffset = (card == hoveredCard) ? -10 : 0;
            card.position.setPosition(bounds.position.x + rowXOffset + x*(Card.CARD_WIDTH+paddingX),
                                     startY + y*(Card.CARD_HEIGHT+paddingY) + hoverOffset);
            x++;
            remainingElements--;
            // Check for iterating to the next row.
            if(x >= elementsPerRow) {
                x = 0;
                y++;
                rowXOffset = bounds.width/2-(int)(elementsPerRow*(Card.CARD_WIDTH+paddingX)/2.0);
                // Once a not full row has been found (used to centre in that row).
                if(remainingElements < elementsPerRow) {
                    rowXOffset = bounds.width/2-(int)(remainingElements*(Card.CARD_WIDTH+paddingX)/2.0);
                }
            }
        }
    }

    /**
     * Sets the currentRoundScore and increases the totalScore by this amount.
     *
     * @param newCurrentRoundScore New score for this player.
     */
    public void setCurrentRoundScore(int newCurrentRoundScore) {
        this.currentRoundScore = newCurrentRoundScore;
        totalScore += currentRoundScore;
    }

    /**
     * Sets the won state to true.
     *
     * @return When true, this player won the game.
     */
    public void setWon() {
        wonRound = true;
    }

    /**
     * This returns true when this player has won.
     *
     * @return The current won state.
     */
    public boolean getWon() {
        return wonRound;
    }

    /**
     * The total score between multiple rounds.
     *
     * @return The current total score for this player.
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Gets the current round score for this player.
     *
     * @return The current score for this player for the current round.
     */
    public int getCurrentRoundScore() {
        return currentRoundScore;
    }

    /**
     * Resets the score back to nothing.
     */
    public void resetScore() {
        totalScore = 0;
        currentRoundScore = 0;
        wonRound = false;
    }
}
