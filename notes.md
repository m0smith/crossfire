# Conventions

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

   