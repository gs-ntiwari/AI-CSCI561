package com.HW2;

import java.util.*;

import static com.HW2.Utility.isValidMove;

public class GameState {
    private CellType color;
    private GameState parentState;
    private List<GameState> branches;
    private Set<Coordinate> whitePositions;
    private Set<Coordinate> blackPositions;
    private int evalValue = 0;
    private Coordinate fromCoordinate;
    private Coordinate toCoordinate;

    public Set<Coordinate> getWhitePositions() {
        return whitePositions;
    }

    public void setWhitePositions(Set<Coordinate> whitePositions) {
        this.whitePositions = whitePositions;
    }

    public Set<Coordinate> getBlackPositions() {
        return blackPositions;
    }

    public void setBlackPositions(Set<Coordinate> blackPositions) {
        this.blackPositions = blackPositions;
    }

    public Coordinate getFromCoordinate() {
        return fromCoordinate;
    }

    public void setFromCoordinate(Coordinate fromCoordinate) {
        this.fromCoordinate = fromCoordinate;
    }

    public Coordinate getToCoordinate() {
        return toCoordinate;
    }

    public void setToCoordinate(Coordinate toCoordinate) {
        this.toCoordinate = toCoordinate;
    }

    public CellType getColor() {
        return color;
    }

    public void setColor(CellType color) {
        this.color = color;
    }

    public GameState getParentState() {
        return parentState;
    }

    public void setParentState(GameState parentState) {
        this.parentState = parentState;
    }

    public List<GameState> getBranches() {
        return branches;
    }

    public void setBranches(List<GameState> branches) {
        this.branches = branches;
    }

    public int getEvalValue() {
        return evalValue;
    }

    public void setEvalValue(int evalValue) {
        this.evalValue = evalValue;
    }


    public GameState(Set<Coordinate> whitepositions, Set<Coordinate> blackpositions, CellType color, GameState parentState, Coordinate from, Coordinate to) {
        this.blackPositions = blackpositions;
        this.whitePositions = whitepositions;
        this.color = color;
        this.parentState = parentState;
        this.fromCoordinate = from;
        this.toCoordinate = to;
    }

    public GameState(Set<Coordinate> whitepositions, Set<Coordinate> blackpositions, CellType color, GameState parentState) {
        this.blackPositions = blackpositions;
        this.whitePositions = whitepositions;
        this.color = color;
        this.parentState = parentState;
        this.fromCoordinate= new Coordinate(-1,-1);
        this.toCoordinate = new Coordinate(-1,-1);
    }


    public void generateBranches(int depth, CellType myColor, Set<Coordinate> originalWhitePositions, Set<Coordinate> originalBlackPositions) {
        if (depth == 0)
        {
            evalTerminalNode(myColor, originalWhitePositions,originalBlackPositions);
            return;
        }
        List<GameState> branches = new ArrayList<>();
        if (color == CellType.White) {
            branches.addAll(generateAllPossibleBranches(this.whitePositions));
        } else {
            branches.addAll(generateAllPossibleBranches(this.blackPositions));
        }
        this.branches = branches;

        for (GameState gameState : branches) {
            if (gameState != null)
                gameState.generateBranches(depth - 1, myColor, originalWhitePositions,originalBlackPositions);
        }
    }

    private void evalTerminalNode(CellType mycolor, Set<Coordinate> originalWhitePositions, Set<Coordinate> originalBlackPositions) {
        if(mycolor==CellType.White)
            evaluateFunction(this.whitePositions, this.blackPositions, originalWhitePositions,originalBlackPositions);
        else
            evaluateFunction(this.blackPositions, this.whitePositions, originalBlackPositions,originalWhitePositions);
    }

    private void evaluateFunction(Set<Coordinate> myPositions, Set<Coordinate> opponentPositions, Set<Coordinate> originalMyPositions, Set<Coordinate> originalOpponentPositions) {
        int eval=0;
        int maximumDistance=21;
        for(Coordinate position:myPositions)
        {
            if(originalOpponentPositions.contains(position))
                eval+=maximumDistance;
            else
            {
                eval+=maximumDistance-getAverageDistanceFromCurrentPosition(originalOpponentPositions, position);
            }
        }

        for(Coordinate position:opponentPositions)
        {
            if(originalMyPositions.contains(position))
                eval-=maximumDistance;
            else
            {
                eval-=maximumDistance-getAverageDistanceFromCurrentPosition(originalMyPositions, position);
            }
        }
        this.evalValue=eval;
    }

    private int getAverageDistanceFromCurrentPosition(Set<Coordinate> originalPositions, Coordinate position) {
        double sum=0;
        for(Coordinate currPosition:originalPositions)
        {
            sum=Math.max(calculateEuclideanDistance(currPosition, position), sum);
        }
        return (int)sum;
    }

    private double calculateEuclideanDistance(Coordinate currPosition, Coordinate position) {
        return Math.sqrt((currPosition.x-position.x)^2+(currPosition.y-position.y)^2);
    }

    private List<GameState> generateAllPossibleBranches(Set<Coordinate> positions) {
        List<GameState> nextMoves = new ArrayList<>();
        for (Coordinate coordinate : positions) {
            int fromX = coordinate.x;
            int fromY = coordinate.y;
            //Adjacent Moves
            Coordinate from = new Coordinate(fromX, fromY);
            if (isValidMove('A', fromX, fromY, fromX + 1, fromY, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX + 1, fromY, positions), getBlackpositions(fromX, fromY,fromX + 1, fromY, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX + 1, fromY)));
            if (isValidMove('A', fromX, fromY, fromX + 1, fromY + 1, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX + 1, fromY+1, positions), getBlackpositions(fromX, fromY,fromX + 1, fromY+1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX + 1, fromY + 1)));
            if (isValidMove('A', fromX, fromY, fromX + 1, fromY - 1, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX + 1, fromY-1, positions), getBlackpositions(fromX, fromY,fromX + 1, fromY-1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX + 1, fromY - 1)));
            if (isValidMove('A', fromX, fromY, fromX, fromY + 1, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX , fromY+1, positions), getBlackpositions(fromX, fromY,fromX, fromY+1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX, fromY + 1)));
            if (isValidMove('A', fromX, fromY, fromX - 1, fromY + 1, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX -1, fromY+1, positions), getBlackpositions(fromX, fromY,fromX -1, fromY+1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX - 1, fromY + 1)));
            if (isValidMove('A', fromX, fromY, fromX - 1, fromY, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX - 1, fromY, positions), getBlackpositions(fromX, fromY,fromX - 1, fromY, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX - 1, fromY)));
            if (isValidMove('A', fromX, fromY, fromX - 1, fromY - 1, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX - 1, fromY-1, positions), getBlackpositions(fromX, fromY,fromX - 1, fromY-1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX - 1, fromY - 1)));
            if (isValidMove('A', fromX, fromY, fromX, fromY - 1, whitePositions, blackPositions))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX, fromY-1, positions), getBlackpositions(fromX, fromY,fromX, fromY-1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX, fromY - 1)));

            //Jump Moves
            nextMoves.addAll(generateAllJumpMoves(fromX, fromY, positions));
        }
        return nextMoves;
    }


    private Set<GameState> generateAllJumpMoves(int fromX, int fromY, Set<Coordinate> positions) {
        Set<GameState> nextMoves = new HashSet<>();
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX + 2, fromY, positions));
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX + 2, fromY + 2, positions));
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX + 2, fromY - 2, positions));
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX - 2, fromY + 2, positions));
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX, fromY + 2, positions));
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX - 2, fromY - 2, positions));
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX, fromY - 2, positions));
        nextMoves.addAll(generateBranchesForAJump(fromX, fromY, fromX - 2, fromY, positions));
        return nextMoves;
    }

    private List<GameState> generateBranchesForAJump(int fromX, int fromY, int toX, int toY, Set<Coordinate> positions) {
        List<GameState> result = new ArrayList<>();
        if (!isValidMove('J', fromX, fromY, toX, toY, whitePositions, blackPositions))
            return result;
        GameState currNode = new GameState(getWhitepositions(fromX, fromY, toX, toY, positions),getBlackpositions(fromX, fromY, toX, toY, positions), Utility.flipColor(this.color), this, new Coordinate(fromX, fromY), new Coordinate(toX, toY));
        Queue<GameState> queue = new LinkedList<>();
        queue.add(currNode);
        Set<Coordinate> close = new HashSet<>();
        close.add(new Coordinate(fromX, fromY));
        while (!queue.isEmpty()) {
            GameState currState = queue.poll();
            result.add(currState);
            close.add(currNode.toCoordinate);
            List<GameState> children = expandNode(currState);
            for (GameState node : children) {
                if (!close.contains(node.toCoordinate)) {
                    queue.add(node);
                    close.add(node.toCoordinate);
                }
            }
        }
        return result;
    }

    private Set<Coordinate> getWhitepositions(int fromX, int fromY, int toX, int toY, Set<Coordinate> positions) {
        return color==CellType.White?Utility.createBoardCopy(positions, fromX, fromY, toX, toY):this.whitePositions;
    }

    private Set<Coordinate> getBlackpositions(int fromX, int fromY, int toX, int toY, Set<Coordinate> positions) {
        return color==CellType.Black?Utility.createBoardCopy(positions, fromX, fromY, toX, toY):this.blackPositions;
    }

    private List<GameState> expandNode(GameState currState) {
        List<GameState> nextMoves = new ArrayList<>();
        Set<Coordinate> positions;
        if(currState.color==CellType.White)
            positions=currState.whitePositions;
        else
            positions=currState.blackPositions;
        int fromX=currState.toCoordinate.x;
        int fromY=currState.toCoordinate.y;
        Coordinate from = new Coordinate(fromX, fromY);
        if (isValidMove('J', fromX, fromY, fromX + 2, fromY, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX + 2, fromY, positions), getBlackpositions(fromX, fromY,fromX + 2, fromY, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX+2, fromY)));
        if (isValidMove('J', fromX, fromY, fromX + 2, fromY + 2, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX + 2, fromY+2, positions), getBlackpositions(fromX, fromY,fromX + 2, fromY+2, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX+2, fromY+2)));
        if (isValidMove('J', fromX, fromY, fromX + 2, fromY - 2, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX + 2, fromY-2, positions), getBlackpositions(fromX, fromY,fromX + 2, fromY-2, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX+2, fromY-2)));
        if (isValidMove('J', fromX, fromY, fromX, fromY + 2, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX, fromY+2, positions), getBlackpositions(fromX, fromY,fromX, fromY+2, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX, fromY+2)));
        if (isValidMove('J', fromX, fromY, fromX - 2, fromY + 2, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX -2, fromY+2, positions), getBlackpositions(fromX, fromY,fromX -2, fromY+2, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX-2, fromY+2)));
        if (isValidMove('J', fromX, fromY, fromX - 2, fromY, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX -2, fromY, positions), getBlackpositions(fromX, fromY,fromX -2, fromY, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX-2, fromY)));
        if (isValidMove('J', fromX, fromY, fromX - 2, fromY - 2, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX -2, fromY-2, positions), getBlackpositions(fromX, fromY,fromX -2, fromY-2, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX-2, fromY-2)));
        if (isValidMove('J', fromX, fromY, fromX, fromY - 2, currState.whitePositions, currState.blackPositions))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY,fromX, fromY+2, positions), getBlackpositions(fromX, fromY,fromX, fromY+2, positions), Utility.flipColor(this.color), currState, from, new Coordinate(fromX, fromY-2)));
        return nextMoves;

    }

}
