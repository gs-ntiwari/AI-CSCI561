package com.HW1;

public class Coordinate{
    int x;
    int y;

    Coordinate(int x, int y)
    {
        this.x=x;
        this.y=y;
    }

    public String toString()
    {
        return this.x+","+this.y;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Coordinate))
            return false;
        if (obj == this)
            return true;
        Coordinate coordinate = (Coordinate)obj;
        return (this.x==coordinate.x && this.y==coordinate.y);
    }

    public int hashCode(){
        return x*31+y;
    }
}
