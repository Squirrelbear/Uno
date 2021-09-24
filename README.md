# 1.1 UNO Game Clone Overview

**Overview written by Peter Mitchell.**

## Contents

```
1.1 UNO Game Clone Overview 
1.2 Introduction
1.3 The Rules of UNO 
1.4 High Level Discussion About Implementation 
1.5 How the Core Game Works
1.6 Known Issues and Potential Improvements
```
# 1.2 Introduction

This document will cover the basic introduction showing off the UNO game. The entire game has been 
written with no sprites using draw calls in Java to render everything to the screen. You can start 
the application from Game.java if you are compiling the code yourself.

# 1.3 The Rules of UNO



# 1.4 High Level Discussion About Implementation

In this section the game content will be described briefly mostly by showing image examples of
gameplay. This readme can't show off everything, but should give a good idea of the types of 
features that have been included. Some of the graphics are static and others are gifs to show
some of the animation sequences that occur as part of the game.

The first image shown below demonstrates the entry point for the application. The user is
immediately shown a game creation screen with many options. 

<img src="./images/image01.JPG">

The options included all expected rules with some minor constraints. The rules you can change
include the following with the toggle buttons on the right:
- Stacking +2/+4: (On or Off), when on you can respond to a +2 or +4 with the same type of card. The penalty stacks onto the next person.
- Draw Till Can Play: (On or Off), when drawing from the deck as an action this forces drawing until a playable card is drawn.
- Two/Four Players: Reverse becomes a skip if playing in two player mode (as seen below too).
- Seven-0: (On or Off), when on the 7 becomes swap with a target player, 0 becomes all players pass their hands to the next player.
- Jump In: (On or Off), allows jump in actions during other players turns if you have a card that exactly matches the one in play.
- Forced Play: (On or Off), forces playing of the card drawn from the deck if it is possible during a draw turn action.
- No Bluffing: Disables challenging of the Draw 4.
- Score Limit: Cycles through the options (One Round, 200 Points, 300 Points, 500 Points, Unlimited) and controls what happens on the end screen.

The other buttons available in the screen include "Toggle Number of Players" to toggle between 2 and 4 players. This automatically changes the rules for the two/four player rule. For each of the players they can be clicked as well. Clicking on your player at the top allows you to change your name. The AI player names are all randomly generated from a list. Clicking on any of the AI will cycle through strategies including (Random, Offensive, Defensive, and Chaotic). Random plays any random action they can play as a valid move, Offensive plays to hold onto high value cards (particularly draw 4s) till the end of their hand, Defensive plays high value cards first to minimise the score awarded to a winner if they lose, and Chaotic changes between Offensive and Defensive strategies. The final two buttons on the screen are the "Reset To Default" that resets all the rules to the recommended options where only the stacking and draw till can play rules are on with a limit of one round, and the "Start Game" button to begin the round with the specified options.

The image below shows another view of the options with only two players, and all the options toggled to on states.

<img src="./images/image03.JPG">

When the game is started the below is a typical view of the game. Showing the four players with names near their associated hands. A yellow/orange name colour indicates the current active turn player. The other player hands are not interactable, but hovering over your own cards will raise the currently hovered card as can be seen with the red 4 in the player's hand. The dots seen around the cards in the middle rotate to show the order of player (this will be clearer in the upcoming gifs). You can see the deck to the left of the card pile. The card pile in the middle shows the top card and randomly scattered previously played cards below it.

<img src="./images/image04.JPG">

From this game state you can see the playing the skip card forces the player next in the turn order to be skipped. The player after Juno apparently did not have anything they could play, so you can see them rapidly draw many cards. Having to draw many cards is a result of the "Draw Till Can Play" rule that requires a player drawing from the deck continues to draw until something is playable. Then they can choose to keep or play the card. In this case, Chance decided to play the wild card and selected green as the colour. The messages that appear in the middle of the play space are clearly showing who is currently performing a required action in response to a card and what decision they are having to make. 

<img src="./images/image05_skip.gif">

Continuing from the same game state as was shown above, the player Denver decided to play a green 9. The player then selected to play a Draw Two card and was punished for doing so. You can see as the players think in sequence as apply the Stacking rule to chain +2 cards until it reaches the player again. If the player had another +2 card they could have continued the chain, but as there was no response the cycle was ended with the player drawing +8 (+2 * 4) cards.

<img src="./images/image06_draw2x4.gif">

Below can be seen the use of a Reverse card. You can see the dots circling the middle of the screen change in response for a moment. Then the player Juno who only has two cards remaining calls UNO! and plays their own Reverse card sending it back to the player again.

<img src="./images/image07_reverseUNO.gif">

Following after the previous gif the player Juno won on their next turn. You can see the result shown below with a Post-Game Summary showing the score for the round. In the case of playing with a score limit or unlimited the round score would be tallied against the total score and you would continue on to the next round. In this case because it was set to a One Round score limit the choices are only to either Return to Lobby and choose new settings to play with, or to start a New Game Same Settings that will create a fresh game with all the same configuration.

<img src="./images/image08_scorescreen.JPG">

Below you can see an example of what choosing a response to a Draw Four card looks like. As long as they No Bluffing rule is off, the player can respond by either choosing to Challenge or Decline. If the previous player could have played a card they have to draw four cards, alternatively if they could not they player then has to draw 6 cards as a penalty. Declining will always draw just the four cards. This does become a bit more complicated if there are any Draw Fours in the player's hand because they could also choose to respond by stacking a Draw Four of their own. 

<img src="./images/image09_challengedraw4.JPG">

Below is a gif showing what happens if you fail to call UNO! fast enough. You can call it any time during the turn when you would be playing the second last card in your hand. You can also call it for a moment after playing the card, but the AI will not give you very long to do it. They may choose to call you out on failing to call it in time. That is what can be seen happening below where the 7 is played and the player goes to call UNO! after already playing the card, but is called out for not doing it fast enough. This is shown visually with a flashing exclamation mark over the player and the drawing of 2 cards for penalty.

<img src="./images/image10_faileduno.gif">

The below image shows an example of the interface shown when choosing to keep or play a card after drawing from the deck. Selecting Keep would keep the card in the hand (already shown at the right side of the hand below). Otherwise pressing Play will play that the card and perform any necessary additional actions depending on the card.

<img src="./images/image11_keeporplayafterdrawing.JPG">

The below gif shows correctly pressing the UNO button prior to playing the second last card in the player's hand. You can see the flashing UNO! appear over the player and then the card from the player's hand is played successfully with no calling out happening as was shown previously.

<img src="./images/image12_successfulUNOcall.gif">

The below gif shows off an animation of the keep or play action with choosing to play a wild card. The wild card requires choosing of a colour that can be seen represented with the colour wheel. The region where the mouse is hovering over is moved out from the circle to indicate that is the current selection and clicking will confirm the colour.

<img src="./images/image13_playingwildcard.gif">

# 1.5 How the Core Game Works



# 1.6 Known Issues and Potential Improvements




