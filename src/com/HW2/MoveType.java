package com.HW2;

public enum MoveType {
    Jump("J"),
    Adjacent("E");

    String val;

    MoveType(String s)
    {
        this.val=s;
    }

    String getVal()
    {
        return val;
    }
}
