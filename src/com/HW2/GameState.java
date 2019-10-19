package com.HW2;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    private CellType color;
    private GameState parentState;
    private MoveType moveType;
    private List<GameState> branches;
    private Set<Coordinate> whitePositions;
    private Set<Coordinate> blackPositions;
    private int evalValue = 0;
    private List<Path> path;
    private Coordinate fromCoordinate;
    private Coordinate toCoordinate;
    int alpha=Integer.MIN_VALUE;
    int beta=Integer.MAX_VALUE;
    int bestNode=-1;
    Map<Integer, List<Integer>> tieBreaker= new HashMap<>();

    public List<Path> getPath() {
        return path;
    }

    public void setPath(List<Path> path) {
        this.path = path;
    }

    public int getBestNode() {
        return bestNode;
    }

    public void setBestNode(int bestNode) {
        this.bestNode = bestNode;
    }

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


    public GameState(Set<Coordinate> whitepositions, Set<Coordinate> blackpositions, CellType color, GameState parentState, Coordinate from, Coordinate to, MoveType moveType) {
        this.blackPositions = blackpositions;
        this.whitePositions = whitepositions;
        this.color = color;
        this.parentState = parentState;
        this.fromCoordinate = from;
        this.toCoordinate = to;
        this.moveType=moveType;
        this.path=new ArrayList<>();
        this.path.add(new Path(moveType, from, to));
    }

    public GameState(Set<Coordinate> whitepositions, Set<Coordinate> blackpositions, CellType color, GameState parentState, Coordinate from, Coordinate to, MoveType moveType, List<Path> pathUntilNow) {
        this.blackPositions = blackpositions;
        this.whitePositions = whitepositions;
        this.color = color;
        this.parentState = parentState;
        this.fromCoordinate = from;
        this.toCoordinate = to;
        this.moveType=moveType;
        this.path=new ArrayList<>(pathUntilNow);
        this.path.add(new Path(moveType, from, to));
    }

    public GameState(Set<Coordinate> whitepositions, Set<Coordinate> blackpositions, CellType color, GameState parentState) {
        this.blackPositions = blackpositions;
        this.whitePositions = whitepositions;
        this.color = color;
        this.parentState = parentState;
        this.fromCoordinate = new Coordinate(-1, -1);
        this.toCoordinate = new Coordinate(-1, -1);
        this.path= new ArrayList<>();
    }


    public int generateBranches(int depth, CellType myColor, Map<Coordinate, Coordinate> gamepath) {
        if (depth == 0) {
            evalTerminalNode(myColor);
            return this.evalValue;
        }
        List<GameState> branches = new ArrayList<>();
        if (this.color == CellType.White) {
            branches.addAll(generateAllPossibleBranches(this.whitePositions));
        } else {
            branches.addAll(generateAllPossibleBranches(this.blackPositions));
        }
        this.branches = branches;

        int v;
        if (this.color == myColor)
        {
            v=Integer.MIN_VALUE;
        }
        else
        {
            v=Integer.MAX_VALUE;
        }
        for (int i=0;i<branches.size();i++) {
            GameState gameState=branches.get(i);
            if (gameState != null) {
                int action_value = gameState.generateBranches(depth - 1, myColor, gamepath);
                if(this.tieBreaker.containsKey(action_value)){
                    List<Integer> indices= this.tieBreaker.get(action_value);
                    indices.add(i);
                    this.tieBreaker.put(action_value, indices);
                }else{
                    ArrayList<Integer> indices = new ArrayList<>();
                    indices.add(i);
                    this.tieBreaker.put(action_value, indices);
                }
                if (this.color == myColor) {
                    v = Math.max(v, action_value);
                    if (v >= this.beta)
                        return v;
                    if(this.alpha<v) {
                        this.alpha = v;
                        this.bestNode=i;
                    }
                } else {
                    v = Math.min(v, action_value);
                    if (v <= this.alpha)
                        return v;
                    if(this.beta>v) {
                        this.beta = v;
                        this.bestNode=i;
                    }
                }
            }
        }
        return v;
    }

    private void evalTerminalNode(CellType mycolor) {
        Set<Coordinate> originalWhitePositions=Utility.getInitialPositions(CellType.White);
        Set<Coordinate> originalBlackPositions=Utility.getInitialPositions(CellType.Black);
        if (mycolor == CellType.White)
            evaluateFunction(mycolor, this.whitePositions, this.blackPositions, originalWhitePositions, originalBlackPositions);
        else
            evaluateFunction(mycolor, this.blackPositions, this.whitePositions, originalBlackPositions, originalWhitePositions);
    }

    private void evaluateFunction(CellType color, Set<Coordinate> myPositions, Set<Coordinate> opponentPositions, Set<Coordinate> originalMyPositions, Set<Coordinate> originalOpponentPositions) {
        int eval = 0;
        int maxWhenNotInOpponentCamp = 22;
        int maxWhenInOpponentCamp = 2 * maxWhenNotInOpponentCamp;

        for (Coordinate position : myPositions) {
            int distance=getDistanceFromCurrentPosition(position, color);
            if (originalOpponentPositions.contains(position))
                eval += maxWhenInOpponentCamp - distance;
            else {

                int v;
                if(distance<=7)
                    v=3;
                else if(distance<=14)
                    v=2;
                else
                    v=1;

                eval += maxWhenNotInOpponentCamp - distance;
            }
        }
        for (Coordinate position : opponentPositions) {
            int distance=getDistanceFromCurrentPosition(position, Utility.flipColor(color));
            if (originalMyPositions.contains(position))
                eval -= maxWhenInOpponentCamp - distance;
            else {
                int v;
                if(distance<=7)
                    v=3;
                else if(distance<=14)
                    v=2;
                else
                    v=1;

                eval -= maxWhenNotInOpponentCamp - distance;
            }
        }
        this.evalValue = eval;
    }



    private int getDistanceFromCurrentPosition(Coordinate position, CellType color) {
         return (int)Math.ceil(calculateEuclideanDistance(color==CellType.Black?new Coordinate(15,15): new Coordinate(0,0), position));

    }

    private double calculateEuclideanDistance(Coordinate currPosition, Coordinate position) {
        return Math.sqrt((currPosition.x - position.x) ^ 2 + (currPosition.y - position.y) ^ 2);
    }

    private List<GameState> generateAllPossibleBranches(Set<Coordinate> positions) {
        List<GameState> nextMoves = new ArrayList<>();
        Set<Coordinate> initialPositions = Utility.getInitialPositions(this.color);
        //there are moves left at home
        boolean needToMoveFromInitialState=true;

        //there are no moves left at home, however do not go back there
        if(Collections.disjoint(initialPositions, positions))
            needToMoveFromInitialState=false;

        for (Coordinate coordinate : positions) {
            if(needToMoveFromInitialState && (!initialPositions.contains(coordinate)))
                continue;
            int fromX = coordinate.x;
            int fromY = coordinate.y;
            //Adjacent Moves
            Coordinate from = new Coordinate(fromX, fromY);
            if (Utility.isValidMove('A', fromX, fromY, fromX + 1, fromY, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX + 1, fromY, positions), getBlackpositions(fromX, fromY, fromX + 1, fromY, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX + 1, fromY), MoveType.Adjacent));
            if (Utility.isValidMove('A', fromX, fromY, fromX + 1, fromY + 1, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX + 1, fromY + 1, positions), getBlackpositions(fromX, fromY, fromX + 1, fromY + 1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX + 1, fromY + 1), MoveType.Adjacent));
            if (Utility.isValidMove('A', fromX, fromY, fromX + 1, fromY - 1, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX + 1, fromY - 1, positions), getBlackpositions(fromX, fromY, fromX + 1, fromY - 1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX + 1, fromY - 1), MoveType.Adjacent));
            if (Utility.isValidMove('A', fromX, fromY, fromX, fromY + 1, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX, fromY + 1, positions), getBlackpositions(fromX, fromY, fromX, fromY + 1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX, fromY + 1), MoveType.Adjacent));
            if (Utility.isValidMove('A', fromX, fromY, fromX - 1, fromY + 1, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX - 1, fromY + 1, positions), getBlackpositions(fromX, fromY, fromX - 1, fromY + 1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX - 1, fromY + 1), MoveType.Adjacent));
            if (Utility.isValidMove('A', fromX, fromY, fromX - 1, fromY, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX - 1, fromY, positions), getBlackpositions(fromX, fromY, fromX - 1, fromY, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX - 1, fromY), MoveType.Adjacent));
            if (Utility.isValidMove('A', fromX, fromY, fromX - 1, fromY - 1, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX - 1, fromY - 1, positions), getBlackpositions(fromX, fromY, fromX - 1, fromY - 1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX - 1, fromY - 1), MoveType.Adjacent));
            if (Utility.isValidMove('A', fromX, fromY, fromX, fromY - 1, whitePositions, blackPositions, this.color))
                nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX, fromY - 1, positions), getBlackpositions(fromX, fromY, fromX, fromY - 1, positions), Utility.flipColor(this.color), this, from, new Coordinate(fromX, fromY - 1), MoveType.Adjacent));
            //Jump Moves
            nextMoves.addAll(generateAllJumpMoves(fromX, fromY, positions));
        }

        nextMoves=filterOutInvalidMoves(nextMoves, initialPositions);

        if(needToMoveFromInitialState)
            nextMoves= filterMovesForWhenPiecesInCamp(nextMoves, initialPositions);

        //TODO If nextMoves IS EMPTY, DO SOMETHING.
        return nextMoves;
    }

    private List<GameState> filterOutInvalidMoves(List<GameState> nextMoves, Set<Coordinate> initialPositions) {
        Set<Coordinate> opponentsInitialPositions= Utility.getInitialPositions(Utility.flipColor(this.color));
        // A piece cannot move back to its camp.
        // Once a piece enters opponent's camp, it cannot move out.
        List<GameState> filtered = nextMoves.stream()
                .filter(b -> (!(initialPositions.contains(b.toCoordinate) && !initialPositions.contains(b.fromCoordinate)))&&!(opponentsInitialPositions.contains(b.fromCoordinate) && !opponentsInitialPositions.contains(b.toCoordinate)))
                .collect(Collectors.toList());

        return filtered;
    }

    private List<GameState> filterMovesForWhenPiecesInCamp(List<GameState> nextMoves, Set<Coordinate> initialPositions) {
        List<GameState> leftOut = new ArrayList<>();

        for(int i=0;i<nextMoves.size();i++) {
            // First, try to select only moves that make the pieces leave the camp.
            if (!initialPositions.contains(nextMoves.get(i).toCoordinate)) {
                leftOut.add(nextMoves.get(i));
            }
        }
        if(leftOut.isEmpty())
        {
            // No moves exist to make the pieces leave the camp.
            // Select moves that make pieces in the camp move farther away from their camp origin.
            for(int i=0;i<nextMoves.size();i++)
            {
                if(CellType.Black==this.color&&isGreaterThan(nextMoves.get(i).toCoordinate,nextMoves.get(i).path.get(0).from))
                {
                    leftOut.add(nextMoves.get(i));
                }
                else if(CellType.White==this.color&&isLessThan(nextMoves.get(i).toCoordinate,nextMoves.get(i).path.get(0).from))
                {
                    leftOut.add(nextMoves.get(i));
                }
            }
        }
        if(leftOut.isEmpty())
        {
            // No valid moves exist for pieces present in camp. Allow valid moves for other pieces.
            for(int i=0;i<nextMoves.size();i++) {
                if (!initialPositions.contains(nextMoves.get(i).fromCoordinate)) {
                    leftOut.add(nextMoves.get(i));
                }
            }

        }
        if(leftOut.isEmpty()){
            return nextMoves;
        }
        return leftOut;
    }

    private boolean isLessThan(Coordinate to, Coordinate from) {
        return to.x<=from.x && to.y<=from.y;
    }

    private boolean isGreaterThan(Coordinate to, Coordinate from) {
        return to.x>=from.x && to.y>=from.y;
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
        if (!Utility.isValidMove('J', fromX, fromY, toX, toY, whitePositions, blackPositions, this.color))
            return result;
        GameState currNode = new GameState(getWhitepositions(fromX, fromY, toX, toY, positions), getBlackpositions(fromX, fromY, toX, toY, positions), this.color, this, new Coordinate(fromX, fromY), new Coordinate(toX, toY), MoveType.Jump);
        Queue<GameState> queue = new LinkedList<>();
        queue.add(currNode);
        Set<Coordinate> close = new HashSet<>();
        close.add(currNode.fromCoordinate);
        while (!queue.isEmpty()) {
            GameState currState = queue.poll();
            //close.add(currNode.toCoordinate);
            List<GameState> children = expandNode(currState, currState.path);
            for (GameState node : children) {
                if (!close.contains(node.toCoordinate)) {
                    queue.add(node);
                }
            }
            currState.color=Utility.flipColor(currState.color);
            close.add(currState.toCoordinate);
            result.add(currState);
        }
        return result;
    }

    private Set<Coordinate> getWhitepositions(int fromX, int fromY, int toX, int toY, Set<Coordinate> positions) {
        return color == CellType.White ? Utility.createBoardCopy(positions, fromX, fromY, toX, toY) : this.whitePositions;
    }


    private Set<Coordinate> getBlackpositions(int fromX, int fromY, int toX, int toY, Set<Coordinate> positions) {
        return color == CellType.Black ? Utility.createBoardCopy(positions, fromX, fromY, toX, toY) : this.blackPositions;
    }


    private List<GameState> expandNode(GameState currState, List<Path> pathUntilNow) {
        List<GameState> nextMoves = new ArrayList<>();
        Set<Coordinate> positions;
        if (currState.color == CellType.White)
            positions = currState.whitePositions;
        else
            positions = currState.blackPositions;
        int fromX = currState.toCoordinate.x;
        int fromY = currState.toCoordinate.y;
        Coordinate from = new Coordinate(fromX, fromY);
        if (Utility.isValidMove('J', fromX, fromY, fromX + 2, fromY, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX + 2, fromY, positions), getBlackpositions(fromX, fromY, fromX + 2, fromY, positions), this.color, currState, from, new Coordinate(fromX + 2, fromY), MoveType.Jump, pathUntilNow));
        if (Utility.isValidMove('J', fromX, fromY, fromX + 2, fromY + 2, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX + 2, fromY + 2, positions), getBlackpositions(fromX, fromY, fromX + 2, fromY + 2, positions), this.color, currState, from, new Coordinate(fromX + 2, fromY + 2), MoveType.Jump, pathUntilNow));
        if (Utility.isValidMove('J', fromX, fromY, fromX + 2, fromY - 2, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX + 2, fromY - 2, positions), getBlackpositions(fromX, fromY, fromX + 2, fromY - 2, positions), this.color, currState, from, new Coordinate(fromX + 2, fromY - 2), MoveType.Jump, pathUntilNow));
        if (Utility.isValidMove('J', fromX, fromY, fromX, fromY + 2, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX, fromY + 2, positions), getBlackpositions(fromX, fromY, fromX, fromY + 2, positions), this.color, currState, from, new Coordinate(fromX, fromY + 2), MoveType.Jump, pathUntilNow));
        if (Utility.isValidMove('J', fromX, fromY, fromX - 2, fromY + 2, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX - 2, fromY + 2, positions), getBlackpositions(fromX, fromY, fromX - 2, fromY + 2, positions), this.color, currState, from, new Coordinate(fromX - 2, fromY + 2), MoveType.Jump, pathUntilNow));
        if (Utility.isValidMove('J', fromX, fromY, fromX - 2, fromY, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX - 2, fromY, positions), getBlackpositions(fromX, fromY, fromX - 2, fromY, positions),this.color, currState, from, new Coordinate(fromX - 2, fromY), MoveType.Jump, pathUntilNow));
        if (Utility.isValidMove('J', fromX, fromY, fromX - 2, fromY - 2, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX - 2, fromY - 2, positions), getBlackpositions(fromX, fromY, fromX - 2, fromY - 2, positions), this.color, currState, from, new Coordinate(fromX - 2, fromY - 2), MoveType.Jump, pathUntilNow));
        if (Utility.isValidMove('J', fromX, fromY, fromX, fromY - 2, currState.whitePositions, currState.blackPositions, this.color))
            nextMoves.add(new GameState(getWhitepositions(fromX, fromY, fromX, fromY + 2, positions), getBlackpositions(fromX, fromY, fromX, fromY + 2, positions), this.color, currState, from, new Coordinate(fromX, fromY - 2), MoveType.Jump, pathUntilNow));
        return nextMoves;

    }

}
