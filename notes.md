# Conventions

This document contains the decisions and design for Crossfire.

## Game play overview
The game is divided into rounds in which each active player takes a turn.  Players my become inactive by losing.
Once only one player is active, the game is over.

A turn consists of a looop the following steps until the game is over:

1. Send a `take-turn` message to the current active player.
2. Player sends the `take-turn-response` to the engine.
3. Engine updates world state
    * Validate the turn response
    * Update the opponent board
    * Update opponent status
    * Assign current player
    * Test for end of game
4. Engine sends `update-state` to all players

## Actors

* Player: both player and opponent
* Engine

## Messages

* `take-turn`
* `take-turn-response`
* `update-state`
* `request-state`

### Message Passing

In order to accomodate messages in both Clojure and ClojureScript, atoms will be used to pass messages.  To send a message
use `ames-send`.  The first argument is the name of the message function for the recipient to execute as either a string or a symbol.
The rest of the arguments are the arguments to pass to the message function.  These should be standard Clojure objects only as
they need to be able to be executed in either Clojure or ClojureScript.

### Message Functions

A message is simply the name of a message function to call and the arguments to pass to that function.  The message function
is a standard function that exists in the recipient namespace and accepts one more argument than the arguments passed.  The first
argument to a message function will be the result of calling the `message-sentinal` function passed to the `ames-create` method.  
This allows message functions to be protocols.

### AMES - Synchronous Message 

AMES allows the system to send asynchonous messages.

*  `ames-create`  Creates an ames mailbox.  Returns a `ames`. Arguments:
    *  `message-sentinal`: A function that will be called and the result passed as the first argument to the message function
*  `ames-send` Sends a message to a mailbox
    * `ames`: The mailbox creted with `ames-create`
    * `message-function`: The name of the fucntion to send as either a String or a Symbol
    * `args`: The optional arguments to pass

See 

* http://stackoverflow.com/questions/12545792/is-there-a-single-publish-subscribe-that-will-work-in-both-clojure-and-clojuresc
* http://www.pauldee.org/blog/2012/clojurescript-and-node-js-an-experience-report/

## UI

### Swing

https://github.com/quil/quil

## Data

* `board`: a map of the state of a board including:
   * `:coods` :  a sparse map of `cood` to `Peg`, can be nil
* `cood`: a vector [x y] that represents a position in the world
* `delta-cood`: a vector [delta-x delta-y] that represent an offset from a `cood`
* `piece`: 
    * `:coods-map` : A map of `cood` to a `peg-state`
* `peg-state`: representing the state of a peg
    * `:pegged?` : nil or true
* `player`:
    * `:boardid` : the id of this player's board
    * `:name` : display name for the player
    * `:playerid` : the id of this player
    * `:status` : one of :active, :paused, :defeated
* `prototype`:
    * `:delta-coods` : seq of `delta-cood`
* `world`: a map that represents the current state of the game includes:
    * `:board-1` : `board` for player 1
    * `:board-2` : `board` for player 2
    * `:dim` : the size of the world as a vector [width height].  The upper left is the first location and has a cood of [0 0].
* `worlds`: an atom that holds a map of `worldid` to `world`


   