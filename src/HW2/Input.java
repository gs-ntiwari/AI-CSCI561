package HW2;

public class Input {
    boolean isSinglePlayer=false;
    double time;
    GameState initState;

    public Input(boolean isSinglePlayer, double time, GameState initState) {
        this.isSinglePlayer = isSinglePlayer;
        this.time = time;
        this.initState = initState;
    }

    public boolean isSinglePlayer() {
        return isSinglePlayer;
    }

    public void setSinglePlayer(boolean singlePlayer) {
        isSinglePlayer = singlePlayer;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public GameState getInitState() {
        return initState;
    }

    public void setInitState(GameState initState) {
        this.initState = initState;
    }
}
