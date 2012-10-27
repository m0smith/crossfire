# Conventions

This document contains the decisions and design for Crossfire.

## Game play overview
The game is divided into rounds in which each active player takes a turn.  Players my become inactive by losing.
Once only one player is active, the game is over.

A turn consists of a looop the following steps:


1 Send a `take-turn` message to the current active player.
2 Player sends the `take-turn-response` to the engine.
3 Engine updates world state
    * Validate the turn response
    * Update the opponent board
4 Engine sends `update-state` to all players

## Actors

* Player
* Engine

## Messages

* `take-turn`
* `take-turn-response`
* `update-state`
* `request-state`

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
* `worlds`: a map of 

   