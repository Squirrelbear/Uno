import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrentGameInterface extends WndInterface {

    private final Deck deck;
    private final List<Card> recentCards;
    private final int MAX_CARD_HISTORY = 10;

    private final Position centredCardPos;
    private int curFaceValue;
    private int curColourID;
    private Player bottomPlayer;
    private TurnActionOverlayManager overlayManager;
    private TurnActionFactory.TurnAction currentTurnAction;
    private TurnActionFactory.TurnAction queuedTurnAction;

    private final List<Player> players;
    private int currentPlayerID;
    private PlayDirectionAnimation playDirectionAnimation;
    private boolean isIncreasing;

    private RuleSet ruleSet;
    private static CurrentGameInterface activeSingleton;
    public static CurrentGameInterface getCurrentGame() {
        return activeSingleton;
    }

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds The bounds of the interface.
     */
    public CurrentGameInterface(Rectangle bounds) {
        super(bounds);
        players = new ArrayList<>();
        deck = new Deck(new Position(100,100));
        recentCards = new ArrayList<>();
        centredCardPos = new Position(bounds.position.x+bounds.width/2-30,bounds.position.y+bounds.height/2-45);
        overlayManager = new TurnActionOverlayManager(bounds);
        forcePlayCard(deck.drawCard());
        //createPlayers(Player.PlayerType.ThisPlayer, Player.PlayerType.AIPlayer);
        createPlayers(Player.PlayerType.ThisPlayer, Player.PlayerType.AIPlayer, Player.PlayerType.AIPlayer, Player.PlayerType.AIPlayer);
        bottomPlayer = players.stream().filter(player -> player.getPlayerType() == Player.PlayerType.ThisPlayer).findFirst().get();
        for (Player player : players) {
            for(int i = 0; i < 7; i++) {
                player.addCardToHand(deck.drawCard());
            }
        }
        //player = new Player(0, "Player", Player.PlayerType.AIPlayer, getPlayerRect(1));
        currentPlayerID = bottomPlayer.getPlayerID(); //(int) (Math.random()*players.size());
        isIncreasing = true;
        playDirectionAnimation = new PlayDirectionAnimation(new Position(bounds.width/2,bounds.height/2), 120, 5);

        ruleSet = new RuleSet();
        activeSingleton = this;
        currentTurnAction = null;
    }

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
    }

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

    @Override
    public void handleMousePress(Position mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        if(overlayManager.isEnabled()) {
            overlayManager.handleMousePress(mousePosition, isLeft);
        } else if(deck.isPositionInside(mousePosition)) {
            currentTurnAction = TurnActionFactory.drawCardAsAction(currentPlayerID);
            /*if(isLeft) {
                players.get(currentPlayerID).addCardToHand(deck.drawCard());
                System.out.println(Arrays.toString(bottomPlayer.getValidMoves(curFaceValue, curColourID).stream().map(card -> card.getFaceValueID() + " " + card.getColourID()).toArray()));
                moveToNextPlayer();
            } else {
                forcePlayCard(deck.drawCard());
            }*/
        } else {
            Card cardToPlay = bottomPlayer.chooseCardFromClick(mousePosition);
            if(bottomPlayer.getValidMoves(curFaceValue, curColourID).contains(cardToPlay)) {
                playCard(cardToPlay);
                bottomPlayer.removeCard(cardToPlay);
                if(bottomPlayer.getHand().size() == 0) {
                    int totalScore = 0;
                    for(int i = 0; i < players.size(); i++) {
                        if(players.get(i) != bottomPlayer) {
                            totalScore += players.get(i).getHandTotalScore();
                            System.out.println("Player " + i + ": " + players.get(i).getHandTotalScore());
                        }
                    }
                    System.out.println("Total score: " + totalScore);
                }
            }
        }
    }

    @Override
    public void handleMouseMove(Position mousePosition) {
        if(!isEnabled()) return;

        overlayManager.handleMouseMove(mousePosition);
        bottomPlayer.updateHover(mousePosition);
    }

    public void showOverlayForTurnAction() {
        if(currentTurnAction instanceof TurnActionFactory.TurnDecisionAction) {
            overlayManager.showOverlay((TurnActionFactory.TurnDecisionAction) currentTurnAction);
        }
    }

    public void revealHands() {
        players.forEach(player -> player.revealHand(true));
    }

    public void sortHand() {
        bottomPlayer.sortHand();
    }

    public void toggleTurnDirection() {
        isIncreasing = !isIncreasing;
        playDirectionAnimation.setIsIncreasing(isIncreasing);
    }

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

    public boolean isIncreasing() {
        return isIncreasing;
    }

    public void setTopCardColour(int colourID) {
        curColourID = colourID;
        recentCards.get(recentCards.size()-1).setColour(colourID);
    }

    public void setCurrentTurnAction(TurnActionFactory.TurnAction turnAction) {
        if(currentTurnAction != null) {
            queuedTurnAction = turnAction;
        } else {
            currentTurnAction = turnAction;
        }
    }

    public void forcePlayCard(Card card) {
        if(card.getFaceValueID() >= 13) {
            playWildCard(card, (int)(Math.random()*4));
        } else {
            playCard(card);
        }
    }

    public void playCard(Card card) {
        placeCard(card);
        if(curColourID == 4) {
            //wildColourSelectorOverlay.setEnabled(true); // TODO
        }
    }

    public void placeCard(Card card) {
        card.position.setPosition(centredCardPos.x, centredCardPos.y);
        card.position.add(new Position((int)(Math.random()*24-12),(int)(Math.random()*24-12)));
        recentCards.add(card);
        if(recentCards.size() > MAX_CARD_HISTORY) {
            recentCards.remove(0);
        }
        curColourID = card.getColourID();
        curFaceValue = card.getFaceValueID();
    }

    public void playWildCard(Card card, int choice) {
        playCard(card);
        curColourID = choice;
        card.setColour(choice);
        //wildColourSelectorOverlay.setEnabled(false); // TODO
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerID);
    }

    public List<Player> getAllPlayers() {
        return players;
    }

    public Player getPlayerByID(int playerID) {
        return players.get(playerID);
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getRecentCards() {
        return recentCards;
    }

    private void createPlayers(Player.PlayerType ... playerTypes) {
        if(playerTypes.length != 2 && playerTypes.length != 4) {
            System.out.println("Critical Error. Only combinations of 2 or 4 players are allowed");
            return;
        }

        int thisPlayerIndex = -1;
        for(int i = 0; i < playerTypes.length; i++) {
            if(playerTypes[i] == Player.PlayerType.ThisPlayer) {
                if(thisPlayerIndex == -1) {
                    thisPlayerIndex = i;
                } else {
                    System.out.println("Critical Error. Only one ThisPlayer is allowed.");
                    return;
                }
            }
        }

        for (int i = 0; i < playerTypes.length; i++) {
            if(playerTypes.length == 4) {
                players.add(new Player(i, "Player", playerTypes[i],
                        getPlayerRect((i + 4 - thisPlayerIndex) % 4)));
            } else {
                players.add(new Player(i, "Player", playerTypes[i],
                        getPlayerRect(playerTypes[i] == Player.PlayerType.ThisPlayer ? 0 : 2)));
            }
        }


    }

    // direction: 0=bottom, 1=left, 2=top, 3=right
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
