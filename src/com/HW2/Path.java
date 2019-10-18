package com.HW2;

public class Path {
    MoveType moveType;
    Coordinate from;
    Coordinate to;

    Path(MoveType moveType, Coordinate from, Coordinate to)
    {
        this.from=from;
        this.to=to;
        this.moveType=moveType;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public Coordinate getFrom() {
        return from;
    }

    public void setFrom(Coordinate from) {
        this.from = from;
    }

    public Coordinate getTo() {
        return to;
    }

    public void setTo(Coordinate to) {
        this.to = to;
    }
}
