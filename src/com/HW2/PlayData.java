package com.HW2;

import java.io.Serializable;

public class PlayData implements Serializable {
    int moveCount;

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void PlayData(int moveCount)
    {
        this.moveCount=moveCount;
    }
}
