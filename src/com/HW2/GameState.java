package com.HW2;

import java.util.*;
import java.util.List;


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


    public int generateBranches(int depth, CellType myColor) {
        if (depth == 0) {
            evalTerminalNode(myColor);
            return this.evalValue;
        }
        List<GameState> branches = new ArrayList<>();
        if (this.color == CellType.White) {
            branches.addAll(generateAllPossibleBranches(this.whitePositions, myColor));
        } else {
            branches.addAll(generateAllPossibleBranches(this.blackPositions, myColor));
        }

        this.branches = branches;

        int v = getV(myColor);

        //if(branches.size()>300)
        //depth=1;

        for (int i=0;i<branches.size();i++) {
            GameState gameState=branches.get(i);
            if (gameState != null) {
                int action_value= getAction_value(depth, myColor, gameState);
                if (this.color == myColor) {
                    v = Math.max(v, action_value);
                    if (v >= this.beta) {
                        return v;
                    }
                    updateTieBreakerMap(i, action_value);
                    if(this.alpha<v) {
                        this.alpha = v;
                        this.bestNode=i;
                    }
                } else {
                    v = Math.min(v, action_value);
                    if (v <= this.alpha) {
                        return v;
                    }
                    updateTieBreakerMap(i, action_value);
                    if(this.beta>v) {
                        this.beta = v;
                        this.bestNode=i;
                    }
                }
                //if(myColor==CellType.White)
                //this.bestNode=(new Random()).nextInt(this.branches.size());
            }
        }
        return v;
    }

    private int getAction_value(int depth, CellType myColor, GameState gameState) {
        int action_value;
        if(Utility.isTerminalState(this.color, this.color==CellType.Black?gameState.blackPositions:gameState.whitePositions))
            action_value=this.color==myColor?10000000*depth:-10000000*depth;
        else {
            action_value = gameState.generateBranches(depth - 1, myColor);
        }
        return action_value;
    }


    private int getV(CellType myColor) {
        int v;
        if (this.color == myColor)
        {
            v=Integer.MIN_VALUE;
        }
        else
        {
            v=Integer.MAX_VALUE;
        }
        return v;
    }

    private void updateTieBreakerMap(int i, int action_value) {
        if(this.tieBreaker.containsKey(action_value)){
            List<Integer> indices= this.tieBreaker.get(action_value);
            indices.add(i);
            this.tieBreaker.put(action_value, indices);
        }else{
            ArrayList<Integer> indices = new ArrayList<>();
            indices.add(i);
            this.tieBreaker.put(action_value, indices);
        }
    }

    public void evalTerminalNode(CellType mycolor) {
        Set<Coordinate> originalOpponentPositions=Utility.getInitialPositions(Utility.flipColor(mycolor));
        if (mycolor == CellType.White)
            evaluateFunction(mycolor, this.whitePositions, originalOpponentPositions);
        else
            evaluateFunction(mycolor, this.blackPositions, originalOpponentPositions);
    }

    private void evaluateFunction(CellType color, Set<Coordinate> myPositions, Set<Coordinate> originalOpponentPositions) {
        double float_eval = 0.0;
        double avgDistance = 0.0;
        final double maxWhenNotInOpponentCamp = Math.pow(21,2);
        int numPiecesInOpponentsCamp = 0;
        int avgDistanceFromDiagonal = 0;
        double wt_of_pieces_opponent_camp = 1.0;
        double wt_of_diagonal_distance =1.0;
        final double wt_of_avg_distance = 0.0;
        int extraAddOn=0;
        if(color==CellType.White)
        {
            extraAddOn=-1;
        }
        for (Coordinate position : myPositions) {
            if (originalOpponentPositions.contains(position)) {
                numPiecesInOpponentsCamp++;
                avgDistanceFromDiagonal += color == CellType.White ? Math.pow(position.x + position.y+extraAddOn, 2) : Math.pow((15 - position.x) +(15-position.y)+extraAddOn, 2);
            } else {
                    avgDistanceFromDiagonal += color == CellType.White ? Math.pow(position.x + position.y+extraAddOn , 2) : Math.pow((15 - position.x) + (15 - position.y)+extraAddOn, 2);
                    //avgDistance += getDistanceFromCurrentPosition(position, color);
            }

        }
        //avgDistance /= 19;
        avgDistanceFromDiagonal /= myPositions.size();
        float_eval = (wt_of_pieces_opponent_camp * maxWhenNotInOpponentCamp * numPiecesInOpponentsCamp / myPositions.size() ) + (wt_of_diagonal_distance * (-1*avgDistanceFromDiagonal)) ;
                //+(wt_of_avg_distance *-1*avgDistance);
        this.evalValue = (int) (Math.round(float_eval));

    }

    /*private void evaluateFunction(CellType color, Set<Coordinate> myPositions, Set<Coordinate> opponentPositions, Set<Coordinate> originalMyPositions, Set<Coordinate> originalOpponentPositions) {
        final int maxWhenNotInOpponentCamp = 23;
        final int maxWhenInOpponentCamp = 10;
        final int maxWhenCrossedDiagonal = 5;
        final int maxWhenCrossedSecondDiagonal = 7;
        int maxDistance=Integer.MIN_VALUE;
        int minDistance=Integer.MAX_VALUE;
        int numPiecesInOpponentsCamp = 0;
        int numPiecesCrossedDiagonal = 0;
        int numPiecesCrossedSecondDiagonal = 0;

        int avgDistance = 0;
        int avgDistanceFromDiagonal = 0;
        for (Coordinate position : myPositions) {
            int distance = getDistanceFromCurrentPosition(position, color, originalMyPositions, originalOpponentPositions);
            minDistance = Math.min(minDistance, distance);
            if (originalOpponentPositions.contains(position)){
                numPiecesInOpponentsCamp++;
            }else{
                avgDistanceFromDiagonal += position.x + position.y;

                if(hasPieceCrossedSecondOffDiagonal(position, color)){
                    numPiecesCrossedSecondDiagonal++;

                } else if(hasPieceCrossedOffDiagonal(position, color)){
                    numPiecesCrossedDiagonal++;
                    distance *= 10;
                }else{
                    distance *= 20;
                }
                maxDistance = Math.max(maxDistance, distance);
                avgDistance += distance;
            }

        }
        avgDistanceFromDiagonal = myPositions.size() == numPiecesInOpponentsCamp? 0 : avgDistanceFromDiagonal/(myPositions.size()-numPiecesInOpponentsCamp);
        avgDistance = myPositions.size() == numPiecesInOpponentsCamp? 0 : avgDistance/(myPositions.size()-numPiecesInOpponentsCamp);
        final double wt_of_max_min = 0.3;
        final double wt_of_pieces_opponent_camp = 0.4;
        final double wt_of_diagonal_distance = 0.3;
        double float_eval = 0.0;
        float_eval = (-wt_of_max_min*(maxDistance) - (1-wt_of_max_min-wt_of_pieces_opponent_camp-wt_of_diagonal_distance)*avgDistance - wt_of_diagonal_distance*avgDistanceFromDiagonal + wt_of_pieces_opponent_camp*(numPiecesInOpponentsCamp*maxWhenInOpponentCamp + numPiecesCrossedDiagonal*maxWhenCrossedDiagonal + numPiecesCrossedSecondDiagonal*maxWhenCrossedSecondDiagonal));

        maxDistance=Integer.MIN_VALUE;
        minDistance=Integer.MAX_VALUE;
        avgDistance = 0;
        numPiecesInOpponentsCamp = 0;
        // TODO: Make opponent computation consistent.
        for (Coordinate position : opponentPositions) {
            int distance=getDistanceFromCurrentPosition(position, Utility.flipColor(color), originalOpponentPositions, originalMyPositions);
            minDistance = Math.min(minDistance, distance);
            if (originalOpponentPositions.contains(position)){
                numPiecesInOpponentsCamp++;
            }else{
                maxDistance = Math.max(maxDistance, distance);
                avgDistance += distance;
            }
        }
        avgDistance = opponentPositions.size() == numPiecesInOpponentsCamp? 0 : avgDistance/(opponentPositions.size()-numPiecesInOpponentsCamp);
        final double opp_wt = 0;
        float_eval -=-(opp_wt* (-wt_of_max_min*(maxDistance) - (1-wt_of_max_min-wt_of_pieces_opponent_camp)*avgDistance + wt_of_pieces_opponent_camp*numPiecesInOpponentsCamp*maxWhenInOpponentCamp));
        this.evalValue = (int)(100*float_eval);
    }*/


    private boolean hasPieceCrossedOffset(Coordinate position, CellType color, int offset){
        Coordinate origin=color==CellType.Black?new Coordinate(0,0):new Coordinate(15,15);
        int pos_sign = position.x + position.y - offset;
        int origin_sign = origin.x + origin.y - offset;
        return  pos_sign*origin_sign < 0;
    }
    private boolean hasPieceCrossedOffDiagonal(Coordinate position, CellType color){
        return hasPieceCrossedOffset(position, color, 15);
    }
    private boolean hasPieceCrossedSecondOffDiagonal(Coordinate position, CellType color){
        return hasPieceCrossedOffset(position, color, (color==CellType.Black? 24 : 6));
    }

    private double getDistanceFromCurrentPosition(Coordinate position, CellType color, Set<Coordinate> originalMyPositions, Set<Coordinate> originalOpponentPositions) {
        double d=Integer.MIN_VALUE;
        //double d=0;
        for(Coordinate currentPosition:originalMyPositions) {
                d = Math.max(Utility.calculateEuclideanDistance(currentPosition, position), d);
            //d+=Utility.calculateEuclideanDistance(currentPosition, position);
        }
        return Math.pow(d, 2);
    }

    private double getDistanceFromCurrentPosition(Coordinate position, CellType color) {
        return Math.pow(Utility.calculateEuclideanDistance(position, color==CellType.White?new Coordinate(0,0): new Coordinate(15,15)),2);
    }

    private List<GameState> generateAllPossibleBranches(Set<Coordinate> positions, CellType myColor) {
        List<GameState> nextMoves = new ArrayList<>();
        Set<Coordinate> initialPositions = Utility.getInitialPositions(this.color);
        if(this.color!=myColor) {
            nextMoves.add(new GameState(this.whitePositions, this.blackPositions, Utility.flipColor(this.color), this, new Coordinate(-1, -1), new Coordinate(-1, -1), MoveType.Adjacent));
            return nextMoves;
        }
        //there are moves left at home
        boolean needToMoveFromInitialState=true;

        //there are no moves left at home, however do not go back there
        if(Collections.disjoint(initialPositions, positions))
            needToMoveFromInitialState=false;

        for (Coordinate coordinate : positions) {
            /*if(needToMoveFromInitialState && (!initialPositions.contains(coordinate)))
                continue;*/
            int fromX = coordinate.x;
            int fromY = coordinate.y;
            //Adjacent Moves
            //Jump Moves
            nextMoves.addAll(generateAllJumpMoves(fromX, fromY, positions));

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
        }

        nextMoves=filterOutInvalidMoves(nextMoves, initialPositions);

        //TODO If nextMoves IS EMPTY, DO SOMETHING.
        if(needToMoveFromInitialState)
            nextMoves= filterMovesForWhenPiecesInCamp(nextMoves, initialPositions);
        return nextMoves;
    }

    private List<GameState> filterOutInvalidMoves(List<GameState> nextMoves, Set<Coordinate> initialPositions) {
        Set<Coordinate> opponentsInitialPositions= Utility.getInitialPositions(Utility.flipColor(this.color));
        // A piece cannot move back to its camp.
        // Once a piece enters opponent's camp, it cannot move out.

        List<GameState> filtered = new ArrayList<>();
        for(GameState gameState:nextMoves)
        {
            if((!(initialPositions.contains(gameState.toCoordinate) && !initialPositions.contains(gameState.getPath().get(0).from))) && !(opponentsInitialPositions.contains(gameState.getPath().get(0).from) && !opponentsInitialPositions.contains(gameState.toCoordinate)))
            filtered.add(gameState);
        }

        /*List<GameState> filtered = nextMoves.stream()
                .filter(move -> (!(initialPositions.contains(move.toCoordinate) && !initialPositions.contains(move.getPath().get(0).from))) && !(opponentsInitialPositions.contains(move.getPath().get(0).from) && !opponentsInitialPositions.contains(move.toCoordinate)))
                .collect(Collectors.toList());*/

        return filtered;
    }

    private List<GameState> filterMovesForWhenPiecesInCamp(List<GameState> nextMoves, Set<Coordinate> initialPositions) {
        List<GameState> leftOut = new ArrayList<>();
        List<GameState> movesForPiecesOutsideCamp= new ArrayList<>();
        List<GameState> remainingInCamp= new ArrayList<>();

        for(int i=0;i<nextMoves.size();i++) {

            //When no moves possible inside the camp, then allow pieces outside camp to make a move.
            if(!initialPositions.contains(nextMoves.get(i).path.get(0).from))
            {
                movesForPiecesOutsideCamp.add(nextMoves.get(i));
            }
            // First, try to select only moves that make the pieces leave the camp.
            else if (!initialPositions.contains(nextMoves.get(i).toCoordinate)) {
                leftOut.add(nextMoves.get(i));
            }
            else
            {
                remainingInCamp.add(nextMoves.get(i));
            }

        }
        if(leftOut.isEmpty())
        {
            // No moves exist to make the pieces leave the camp.
            // Select moves that make pieces in the camp move farther away from their camp origin.
            for(int i=0;i<remainingInCamp.size();i++)
            {
                if(CellType.Black==this.color&&isGreaterThan(remainingInCamp.get(i).toCoordinate,remainingInCamp.get(i).path.get(0).from))
                {
                    leftOut.add(remainingInCamp.get(i));
                }
                else if(CellType.White==this.color&&isLessThan(remainingInCamp.get(i).toCoordinate,remainingInCamp.get(i).path.get(0).from))
                {
                    leftOut.add(remainingInCamp.get(i));
                }
            }
        }
        if(leftOut.isEmpty())
        {
            // No valid moves exist for pieces present in camp. Allow valid moves for other pieces.
            return movesForPiecesOutsideCamp;

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
