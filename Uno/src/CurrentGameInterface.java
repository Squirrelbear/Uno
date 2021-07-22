import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Uno
 *
 * CurrentGameInterface class:
 * Defines the main game view controlling a list of players and
 * managing the state of all game elements.
 *
 * @author Peter Mitchell
 * @version 2021.1
 */
public class CurrentGameInterface extends WndInterface {

    /**
     * The deck of cards ready to have cards drawn from it.
     */
    private final Deck deck;
    /**
     * A history of cards that have been played.
     */
    private final List<Card> recentCards;
    /**
     * The maximum number of cards kept in recentCards.
     */
    private final int MAX_CARD_HISTORY = 10;

    /**
     * The centre of where to place recent cards.
     */
    private final Position centredCardPos;
    /**
     * Reference to the player who is playing the game.
     */
    private Player bottomPlayer;
    /**
     * A manager controlling the various overlays that are shown based on events during the game.
     */
    private final OverlayManager overlayManager;
    /**
     * TurnActions are triggered via playing cards or drawing cards. This acts as a
     * linked list that performs actions at each step including splitting between choices
     * for TurnDecisionAction objects.
     */
    private TurnActionFactory.TurnAction currentTurnAction;
    /**
     * An action queued up to start. This is to let the current action finish and
     * then after it is done this is started.
     */
    private TurnActionFactory.TurnAction queuedTurnAction;

    /**
     * All the players that are currently playing including their hands and other details.
     */
    private final List<Player> players;
    /**
     * The current player who is in control of actions.
     */
    private int currentPlayerID;
    /**
     * Animation to show the direction of turn order.
     */
    private final PlayDirectionAnimation playDirectionAnimation;
    /**
     * Turn order increasing (true) means clockwise, or false would be anti-clockwise.
     */
    private boolean isIncreasing;

    /**
     * The rules for what card actions are set and other specific changes to how the game is played.
     */
    private final RuleSet ruleSet;
    /**
     * Reference to the current instance of this class so that other classes can quickly access it directly.
     */
    private static CurrentGameInterface activeSingleton;

    /**
     * Gets the current single instance of CurrentGameInterface. This is not enforced, but
     * it can be null if not created yet, but it is expected that only one will be run at a time.
     *
     * @return Reference to the current instance of this class.
     */
    public static CurrentGameInterface getCurrentGame() {
        return activeSingleton;
    }

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds The bounds of the interface.
     * @param lobbyPlayers Players to create in the game.
     * @param ruleSet The rules definition for how the game is to be played.
     */
    public CurrentGameInterface(Rectangle bounds, List<LobbyPlayer> lobbyPlayers, RuleSet ruleSet) {
        super(bounds);
        activeSingleton = this;
        players = new ArrayList<>();
        this.ruleSet = ruleSet;
        deck = new Deck(new Position(100,100));
        recentCards = new ArrayList<>();
        centredCardPos = new Position(bounds.position.x+bounds.width/2-30,bounds.position.y+bounds.height/2-45);

        createPlayers(lobbyPlayers);
        for (Player player : players) {
            for(int i = 0; i < 7; i++) {
                player.addCardToHand(deck.drawCard());
            }
        }
        currentPlayerID = bottomPlayer.getPlayerID(); //(int) (Math.random()*players.size()); // TODO
        isIncreasing = true;
        playDirectionAnimation = new PlayDirectionAnimation(new Position(bounds.width/2,bounds.height/2), 120, 5);

        overlayManager = new OverlayManager(bounds, players);
        forcePlayCard(deck.drawCard());
        currentTurnAction = null;
    }

    /**
     * Updates all the game components that need to be updated on a timer.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {
        playDirectionAnimation.update(deltaTime);
        overlayManager.update(deltaTime);
        if(currentTurnAction != null) {
            if (currentTurnAction instanceof TurnActionFactory.TurnDecisionAction) {
                if (!((TurnActionFactory.TurnDecisionAction) currentTurnAction).hasRunOnce) {
                    System.out.println(currentTurnAction.actionDebugText);
                }
            } else {
                System.out.println(currentTurnAction.actionDebugText);
            }
            currentTurnAction.performAction();
            currentTurnAction = currentTurnAction.getNext();
            if(queuedTurnAction != null) {
                currentTurnAction = queuedTurnAction;
                queuedTurnAction = null;
            }
        }

        players.forEach(player -> player.update(deltaTime));

        // TODO
        if (bottomPlayer.getHand().size() == 0) {
            int totalScore = 0;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i) != bottomPlayer) {
                    totalScore += players.get(i).getHandTotalScore();
                    System.out.println("Player " + i + ": " + players.get(i).getHandTotalScore());
                }
            }
            System.out.println("Total score: " + totalScore);
        }
    }

    /**
     * Draws all the game elements that are available.
     * When not enabled it will overlay with a transparent layer.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        deck.paint(g);
        recentCards.forEach(card -> card.paint(g));
        players.forEach(player -> {if(player.getPlayerType() != Player.PlayerType.ThisPlayer) player.paint(g);});
        bottomPlayer.paint(g);
        overlayManager.paint(g);

        playDirectionAnimation.paint(g);

        if(!isEnabled()) {
            g.setColor(new Color(144, 143, 143, 204));
            g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        }
    }

    /**
     * Does nothing if not enabled. Passes the interaction to the overlay manager,
     * and allows the player to interact with the deck/their cards when it is their turn.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        if(overlayManager.isEnabled()) {
            overlayManager.handleMousePress(mousePosition, isLeft);
        }

        if(currentTurnAction == null && currentPlayerID == bottomPlayer.getPlayerID()) {
            if (deck.isPositionInside(mousePosition)) {
                currentTurnAction = TurnActionFactory.drawCardAsAction(currentPlayerID);
            } else {
                Card cardToPlay = bottomPlayer.chooseCardFromClick(mousePosition);
                Card topCard = getTopCard();
                if (bottomPlayer.getValidMoves(topCard.getFaceValueID(), topCard.getColourID()).contains(cardToPlay)) {
                    currentTurnAction = TurnActionFactory.playCardAsAction(currentPlayerID, cardToPlay.getCardID(), cardToPlay.getFaceValueID(), cardToPlay.getColourID());
                }
            }
        }
    }

    /**
     * Does nothing if not enabled. Passes the mouse movement to the overlay manager and bottom player.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        overlayManager.handleMouseMove(mousePosition);
        bottomPlayer.updateHover(mousePosition);
    }

    /**
     * Used to show an overlay based on a current decision.
     */
    public void showOverlayForTurnAction() {
        if(currentTurnAction instanceof TurnActionFactory.TurnDecisionAction) {
            overlayManager.showDecisionOverlay((TurnActionFactory.TurnDecisionAction) currentTurnAction);
        }
    }

    /**
     * Shows an overlay based on a String lookup in the overlay manager.
     *
     * @param overlayName Name that maps to a specific overlay.
     */
    public void showGeneralOverlay(String overlayName) {
        overlayManager.showGeneralOverlay(overlayName);
    }

    /**
     * Forces all hands to reveal and stay revealed.
     */
    public void revealHands() {
        players.forEach(player -> player.revealHand(true));
    }

    /**
     * Sorts the player's hand.
     */
    public void sortHand() {
        bottomPlayer.sortHand();
    }

    /**
     * Returns the player who is currently playing the game.
     *
     * @return The player who is playing the game.
     */
    public Player getBottomPlayer() {
        return bottomPlayer;
    }

    /**
     * Toggles the turn direction between clockwise and anti-clockwise.
     * Including updating the animation direction.
     */
    public void toggleTurnDirection() {
        isIncreasing = !isIncreasing;
        playDirectionAnimation.setIsIncreasing(isIncreasing);
    }

    /**
     * Moves to the next player depending on whether the direction is clockwise or anti-clockwise.
     */
    public void moveToNextPlayer() {
        if(isIncreasing) {
            currentPlayerID++;
            if (currentPlayerID >= players.size()) {
                currentPlayerID = 0;
            }
        } else {
            currentPlayerID--;
            if(currentPlayerID < 0) {
                currentPlayerID = players.size()-1;
            }
        }
    }

    /**
     * Gets the current direction of play.
     *
     * @return When true the play direction is clockwise.
     */
    public boolean isIncreasing() {
        return isIncreasing;
    }

    /**
     * Changes the top card colour. Used for changing the colour of the wild and +4 cards.
     *
     * @param colourID 0=Red, 1=Blue, 2=Green, 3=Yellow
     */
    public void setTopCardColour(int colourID) {
        recentCards.get(recentCards.size()-1).setColour(colourID);
    }

    /**
     * If there is a current action already active it will be queued to start asap.
     * Otherwise the action is set up immediately.
     *
     * @param turnAction The TurnAction to begin.
     */
    public void setCurrentTurnAction(TurnActionFactory.TurnAction turnAction) {
        if(currentTurnAction != null) {
            queuedTurnAction = turnAction;
        } else {
            currentTurnAction = turnAction;
        }
    }

    /**
     * Used to play the first card. This consists of simply placing the card
     * with no action, and if the card is a wild the colour is randomised.
     *
     * @param card Card to place on top of the card pile with no action.
     */
    public void forcePlayCard(Card card) {
        placeCard(card);

        if(card.getFaceValueID() >= 13) {
            setTopCardColour((int)(Math.random()*4));
        }
    }

    /**
     * Moves the card's position to the card pile with a random offset and adds it
     * to the collection of recentCards. If the number of recent cards is more
     * than the maximum allowed the oldest card is removed.
     *
     * @param card Card to place on top of the card pile.
     */
    public void placeCard(Card card) {
        card.position.setPosition(centredCardPos.x, centredCardPos.y);
        card.position.add(new Position((int)(Math.random()*24-12),(int)(Math.random()*24-12)));
        recentCards.add(card);
        if(recentCards.size() > MAX_CARD_HISTORY) {
            recentCards.remove(0);
        }
    }

    /**
     * Gets the current TurnAction if there is one.
     *
     * @return The current action or null.
     */
    public TurnActionFactory.TurnAction getCurrentTurnAction() {
        return currentTurnAction;
    }

    /**
     * Gets the ruleset to easily check and apply any rules.
     *
     * @return The ruleset definition.
     */
    public RuleSet getRuleSet() {
        return ruleSet;
    }

    /**
     * Gets the currently active player for turn order.
     *
     * @return The player identified by currentPlayerID.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerID);
    }

    /**
     * Gets a list of all players.
     *
     * @return A reference to all the players.
     */
    public List<Player> getAllPlayers() {
        return players;
    }

    /**
     * Looks up the player with the given ID.
     *
     * @param playerID ID to get from the players collection.
     * @return The player matching the given playerID.
     */
    public Player getPlayerByID(int playerID) {
        return players.get(playerID);
    }

    /**
     * Gets the deck to provide access to drawing cards.
     *
     * @return A reference to the Deck.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the collection of recently played cards.
     *
     * @return A reference to the recently played cards.
     */
    public List<Card> getRecentCards() {
        return recentCards;
    }

    /**
     * Gets the most recently played recentCard.
     *
     * @return The card that appears on top of the played pile.
     */
    public Card getTopCard() {
        return recentCards.get(recentCards.size()-1);
    }

    /**
     * Generates a list of players using the specified types. Requires a single ThisPlayer and 1 or 3 AIPlayer.
     *
     * @param playerList A list of player data to generate a collection.
     */
    private void createPlayers(List<LobbyPlayer> playerList) {
        List<LobbyPlayer> playersToAdd = playerList.stream().filter(lobbyPlayer -> lobbyPlayer.isEnabled()).collect(Collectors.toList());
        if(playersToAdd.size() != 2 && playersToAdd.size() != 4) {
            System.out.println("Critical Error. Only combinations of 2 or 4 players are allowed");
            return;
        }
        int thisPlayerIndex = -1;
        for(int i = 0; i < playersToAdd.size(); i++) {
            if(playersToAdd.get(i).getPlayerType() == Player.PlayerType.ThisPlayer) {
                if(thisPlayerIndex == -1) {
                    thisPlayerIndex = i;
                } else {
                    System.out.println("Critical Error. Only one ThisPlayer is allowed.");
                    return;
                }
            }
        }
        if(thisPlayerIndex == -1) {
            System.out.println("Critical Error. One ThisPlayer is required!");
            return;
        }

        for (int i = 0; i < playersToAdd.size(); i++) {
            Rectangle playerRegion;
            if(playersToAdd.size() == 4) {
                playerRegion = getPlayerRect((i + 4 - thisPlayerIndex) % 4);
            } else {
                playerRegion = getPlayerRect(playersToAdd.get(i).getPlayerType() == Player.PlayerType.ThisPlayer ? 0 : 2);
            }
            if(playersToAdd.get(i).getPlayerType() == Player.PlayerType.AIPlayer) {
                players.add(new AIPlayer(i, playersToAdd.get(i).getPlayerName(), playerRegion, playersToAdd.get(i).getAIStrategy()));
            } else {
                players.add(new Player(i, playersToAdd.get(i).getPlayerName(), playersToAdd.get(i).getPlayerType(), playerRegion));
            }
        }
        bottomPlayer = players.get(thisPlayerIndex);
    }

    /**
     * Generates bounds for where a player's cards should be placed.
     *
     * @param direction 0=bottom, 1=left, 2=top, 3=right
     * @return A Rectangle defining where the player should have their cards on the field.
     */
    private Rectangle getPlayerRect(int direction) {
        return switch (direction) {
            case 1 -> new Rectangle(bounds.position.x,//(Card.CARD_WIDTH + 4) * 6 / 2 + 50,
                    bounds.position.y + bounds.height / 2-150,
                    (Card.CARD_WIDTH + 4) * 6, bounds.height / 2 - 100 - 10);
            case 2 -> new Rectangle(bounds.position.x + bounds.width / 2 - (Card.CARD_WIDTH + 4) * 15 / 2,
                    bounds.position.y,
                    (Card.CARD_WIDTH + 4) * 15, bounds.height / 2 - 100 - 10);
            case 3 -> new Rectangle(bounds.position.x +bounds.width- ((Card.CARD_WIDTH + 4) * 6 + 50)+50,
                    bounds.position.y + bounds.height / 2-150,
                    (Card.CARD_WIDTH + 4) * 6, bounds.height / 2 - 100 - 10);
            default -> new Rectangle(bounds.position.x + bounds.width / 2 - (Card.CARD_WIDTH + 4) * 15 / 2,
                    bounds.position.y + bounds.height / 2 + 100,
                    (Card.CARD_WIDTH + 4) * 15, bounds.height / 2 - 100 - 10);
        };
    }
}
