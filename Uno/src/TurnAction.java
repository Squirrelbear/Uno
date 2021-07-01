/*
    Actions:
    Play Card -> Place Card -> Lookup Action and Execute Sequence

    Play Card No Effect: MoveToNextTurn

    Draw Card: Draw Card-> CanPlayCard? -> (true) -> Keep Or Play? -> Keep -> MoveToNextTurn
                                                                            -> Play -> Begin Action Play Card
                                                 -> (false) -> DrawTillCanPlay? -> (true) ->  Begin Action Draw Card
                                                                                -> (false) -> MoveToNextTurn

    +2 Action -> MoveToNextTurn -> Increase Draw Count +2 -> CheckCanRespond
                    -> CanRespond -> WaitForAction(Play +2 (new chain) or Cancel) -> Begin Action Play Card
                    -> Can't Respond or Cancel Option -> Draw Card * Draw Count + Reset Draw Count to 0 -> MoveToNextTurn

    Wild Action -> WildColourSelection -> Set top pile colour -> MoveToNextTurn
    +4 Action -> WildColourSelection -> Set top pile colour -> MoveToNextTurn -> TODO

    Skip Action: Place Card -> MoveToNextTurn -> Show Skip -> MoveToNextTurn
    Reverse Action: Place Card -> Toggle Turn Direction Order -> MoveToNextTurn

    Swap Action: Player Selection -> Swap Hands (current, selected) -> MoveToNextTurn

    PassAll Action: PassAllHands -> MoveToNextTurn

    WaitForValidAction: JumpInAction -> Set Player to Jumping Player -> Begin Action Play Card
                        Deck -> Begin Action Draw Card
                        Card Choice -> Begin Action Play Card

    // Flag as unsafe at start of turn with 2 cards
    Call Uno Action: Make player safe
    Catch Player Action: MoveToPreviousPlayer -> Alert Caught -> Draw Card * 2 -> MoveToNextPlayer



    Two Player: Reverse becomes skip

    Progressive Uno: Response Allowed for +2 and +4 enabled

    Seven-O: 7 (Swap Action) 0 (all pass hand to left)

 */

public class TurnAction {
}
