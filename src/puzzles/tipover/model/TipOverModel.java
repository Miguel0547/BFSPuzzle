package puzzles.tipover.model;

import solver.Configuration;
import solver.Solver;
import util.Coordinates;
import util.Grid;
import util.Observer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Definition for the model of a Tip Over game.
 *
 * @author Miguel Reyes
 * November 2021
 */
public class TipOverModel {


    /**
     * Those objects that are watching this object's every move
     */
    private final List<Observer<TipOverModel, Object>> observers;

    /**
     * Keeps track of the latest configuration created after a player makes a move.
     */
    private TipOverConfig currentConfig;

    /**
     * Keeps track of the latest configuration that is loaded in. For example, the first reload config will be the
     * configuration created when initializing the constructor. After this it will be the latest configuration used
     * when loading in a new file.
     */
    private TipOverConfig reloadConfig;

    /**
     * If true indicates that our solver has come up with the next step, otherwise the board is not solvable
     */
    private boolean resultHint;

    /**
     * If true indicates we have made a valid move, otherwise the move is invalid
     */
    private boolean resultMove;

    /**
     * Construct a TipOverModel; there is only one model.
     */
    public TipOverModel(Grid<String> board, Coordinates tippersPos, Coordinates goalCratePos) {
        this.observers = new LinkedList<>();
        this.currentConfig = new TipOverConfig(board, tippersPos, goalCratePos, false);
        this.reloadConfig = this.currentConfig;
    }

    /**
     * Returns the current configuration
     * @return the current configuration
     */
    public TipOverConfig getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Returns resultHint
     * @return resultHint
     */
    public boolean getResultHint() {
        return !resultHint;
    }

    /**
     * Returns resultMove
     * @return resultMove
     */
    public boolean getResultMove() {
        return !resultMove;
    }

    /**
     * Will check if it is ok to move one step in the direction provided as well as check if this one step has tipped
     * over a tower.
     * @param direction - String that represents an ENUM type Coordinate.Directions
     */
    public void move(String direction) {
        this.resultMove = false;
        int height = Integer.parseInt(this.currentConfig.getBoard().get(this.currentConfig.getTippersPos()));
        // If it's a crate we want to check all Cardinal Neighbor directions and see if it's a legal coord. pos and not
        // empty for out tipper to move one space.
        if (this.currentConfig.isCrate(height)) {
            Coordinates.Direction d = Coordinates.Direction.valueOf(direction);
            Coordinates current = this.currentConfig.getTippersPos().sum(d.coords.multiply(1));
            if (this.currentConfig.getBoard().legalCoords(current) && !this.currentConfig.getBoard().get(current).equals("0")) {
                this.currentConfig = new TipOverConfig(this.currentConfig.getBoard(), current, this.currentConfig.getGoalCratePos(), false);
                this.resultMove = true;
            }
            announce(null);
        } else {
            // Get our map for our tower
            HashMap<Coordinates.Direction, Boolean> towerTipDir = this.currentConfig.mapOfTippableTower(height);
            // Check if the tower is tippable and in what direction
            Coordinates.Direction d = Coordinates.Direction.valueOf(direction);
            if (towerTipDir.get(d)) {
                // Here we are tipping over the tower so our board changes, so we make a deep copy of the current
                // board and update the copy with the tower being tipped.
                Grid<String> newBoard = new Grid<>(this.currentConfig.getBoard());
                // The tower is now an empty cell.
                newBoard.set("0", this.currentConfig.getTippersPos());
                for (int i = 1; i <= height; i++) {
                    Coordinates current = this.currentConfig.getTippersPos().sum(d.coords.multiply(i));
                    newBoard.set("1", current);
                }
                Coordinates current = this.currentConfig.getTippersPos().sum(d.coords.multiply(1));
                this.currentConfig = new TipOverConfig(newBoard, current, this.currentConfig.getGoalCratePos(), true);
                this.resultMove = true;
                announce("tower");

            } else {
                // Not all towers will be tippable, so we treat as if it were a crate.
                Coordinates current = this.currentConfig.getTippersPos().sum(d.coords.multiply(1));
                if (this.currentConfig.getBoard().legalCoords(current) && !this.currentConfig.getBoard().get(current).equals("0")) {
                    this.currentConfig = new TipOverConfig(this.currentConfig.getBoard(), current, this.currentConfig.getGoalCratePos(), false);
                    this.resultMove = true;
                }
                announce(null);
            }
        }
    }

    /**
     * Will give the player the correct next move or will tell the player the board in its current conditions is not
     * solvable.
     */
    public void hint() {
        //Create an instance of a solver and store its shortestPath List into a collection.
        Solver solver = new Solver();
        Collection<Configuration> shortestPath = solver.getShortestPath(this.currentConfig);
        List<Configuration> shortestPathList = new LinkedList<>(shortestPath);
        //If the List constructed at the end of BFS is empty it means we did not find a match
        if (shortestPath.size() == 0) {
            this.resultHint = false;
            announce("hint");
        } else {
            int height = Integer.parseInt(this.currentConfig.getBoard().get(this.currentConfig.getTippersPos()));
            // List is size 1 means that we have reached the solution, so we want the first value of the list.
            if (shortestPath.size() == 1) {
                this.currentConfig = ((TipOverConfig) shortestPathList.get(0));
            } else {
                // Otherwise, give us the next step
                this.currentConfig = ((TipOverConfig) shortestPathList.get(1));
            }
            // Check if the current configuration was the result of a tipped tower if so we want to announce tower.
            if (height > 1 && this.currentConfig.isTipped()) {
                this.resultHint = true;
                announce("tower");
            } else {
                // Otherwise, for all other valid cases do the following
                this.resultHint = true;
                announce("hint");
            }
        }
    }

    /**
     * Will handle reading in a file and updating our current configuration as well as to the "reload" configuration.
     *
     * @param arg - File name
     * @throws Exception - File selection may be incorrect for the program.
     */
    public void load(String arg) throws Exception {
        try (BufferedReader inputReader = new BufferedReader(new FileReader(arg))) {
            String line = inputReader.readLine();
            String[] fields = line.split("\\s+");
            int boardsRow = Integer.parseInt(fields[0]);
            int boardsCol = Integer.parseInt(fields[1]);
            Grid<String> board = new Grid<>("", boardsRow, boardsCol);
            Coordinates tipperPos = new Coordinates(Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
            Coordinates goalCratePos = new Coordinates(Integer.parseInt(fields[4]), Integer.parseInt(fields[5]));
            int row = 0;
            while ((line = inputReader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                fields = line.split("\\s+");
                for (int col = 0; col < fields.length; col++) {
                    board.set(fields[col], row, col);
                }
                row++;
            }
            this.currentConfig = new TipOverConfig(board, tipperPos, goalCratePos, false);
            this.reloadConfig = this.currentConfig;
            announce(null);
        }
    }


    /**
     * Will set the currentConfig to point back at the reloadConfig. Allows player to start over using the most recent
     * board loaded in.
     */
    public void reload() {
        this.currentConfig = this.reloadConfig;
        announce(null);
    }

    /**
     * Add a new observer to the list for this model
     *
     * @param obs an object that wants an
     *            {@link Observer#update(Object, Object)}
     *            when something changes here
     */
    public void addObserver(Observer<TipOverModel, Object> obs) {
        this.observers.add(obs);
    }

    /**
     * Announce to observers the model has changed;
     */
    private void announce(String arg) {
        for (var obs : this.observers) {
            obs.update(this, arg);
        }
    }
}
