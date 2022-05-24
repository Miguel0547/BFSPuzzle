package puzzles.tipover.model;

import solver.Configuration;
import util.Coordinates;
import util.Grid;

import java.util.*;

/**
 * Class represents a TipOver configuration. The configuration is made up of the puzzles board , the tipper, and goal
 * crate. Every configuration will have methods to detect where and what the tipper is on and what moves are possible.
 *
 * @author Miguel Reyes
 * November 2021
 */
public class TipOverConfig implements Configuration {

    /**
     * The TipOverConfigs goal crate's position - it never changes.
     */
    private final Coordinates goalCratePos;
    /**
     * The current board of a TipOverConfig. It is current because the board may change if a tower is tipped over. If
     * solely dealing with crates then the board will remain the same.
     */
    private Grid<String> board;

    /**
     * Tells us if the current configuration is a result of tipping a tower.
     */
    private boolean tipped;

    /**
     * The tipper's current position on the board. This changes for every neighbor created.
     */
    private Coordinates tippersPos;

    /**
     * Constructor to initialize state of TipOverConfig
     *
     * @param board        The current board
     * @param tippersPos   The tipper's current position
     * @param goalCratePos The goal crate's position
     */
    public TipOverConfig(Grid<String> board, Coordinates tippersPos, Coordinates goalCratePos, boolean tipped) {
        this.board = board;
        this.tippersPos = tippersPos;
        this.goalCratePos = goalCratePos;
        this.tipped = tipped;
    }

    /**
     * Returns the goals position
     * @return The goals position
     */
    public Coordinates getGoalCratePos() {
        return goalCratePos;
    }
    /**
     * Returns the board of the configuration
     * @return The board of the configuration
     */
    public Grid<String> getBoard() {
        return board;
    }
    /**
     * Returns the tippers position
     * @return The tippers position
     */
    public Coordinates getTippersPos() {
        return tippersPos;
    }
    /**
     * Whether a configuration was a result of a tower being tipped
     * @return Tower tipped or not
     */
    public boolean isTipped() {
        return tipped;
    }

    /**
     * Here we check if the Tipper is on a crate or a tower if c is greater than 1.
     *
     * @param c height of what the tipper is on
     * @return True if it's a Crate. False if it's a Tower.
     */
    public boolean isCrate(int c) {
        return c == 1;
    }

    /**
     * Here we will build a HashMap that stores the four Cardinal directions as its keys and its values are True or
     * False. The function will check if the tower is tippable and if so in what direction is it tippable. Helper
     * function for the getNeighbors when our tipper is on a tower.
     *
     * @param height height of the tower
     * @return A HashMap whose keys are Directions(NORTH, SOUTH, WEST, EAST) and whose values are Boolean types.
     */
    public HashMap<Coordinates.Direction, Boolean> mapOfTippableTower(int height) {
        // Our Map
        HashMap<Coordinates.Direction, Boolean> towerTipDir = new HashMap<>();
        // List of the Cardinal Neighbors - this will be updated everytime a tower is not tippable in a direction at
        // a certain coordinate position. Gets updated to the directions that are still valid.
        ArrayList<Coordinates.Direction> allDir = new ArrayList<>(Arrays.asList(Coordinates.CARDINAL_NEIGHBORS));
        //We want to check every square <= height
        for (int i = 1; i <= height; i++) {
            //This is going to hold the updated directions that are then assigned to allDir
            ArrayList<Coordinates.Direction> updatedDir = new ArrayList<>();
            for (Coordinates.Direction dir : allDir) {
                //Check if at i this coordinate is legal and an empty space. If so add it to updated list and set its
                // key-value pair to true. Otherwise, do not add it to the updated list and set its key-value pair to
                // false.
                Coordinates current = tippersPos.sum(dir.coords.multiply(i));
                if (this.board.legalCoords(current) && this.board.get(current).equals("0")) {
                    updatedDir.add(dir);
                    towerTipDir.put(dir, true);
                } else {
                    towerTipDir.put(dir, false);
                }
            }
            //allDir is now up-to-date
            allDir = updatedDir;
        }
        return towerTipDir;
    }

    @Override
    public boolean isSolution() {
        return tippersPos.equals(goalCratePos);
    }

    @Override
    public List<Configuration> getNeighbors() {
        // Linked list to store neighbors of a TipOverConfig
        List<Configuration> neighbors = new LinkedList<>();

        int height = Integer.parseInt(this.board.get(tippersPos));
        // If it's a crate we want to check all Cardinal Neighbor directions and see if it's a legal coord. pos and not
        // empty for out tipper to move one space.
        if (isCrate(height)) {
            for (Coordinates.Direction d : Coordinates.CARDINAL_NEIGHBORS) {
                Coordinates current = tippersPos.sum(d.coords.multiply(1));
                if (this.board.legalCoords(current) && !this.board.get(current).equals("0")) {
                    neighbors.add(new TipOverConfig(this.board, current, this.goalCratePos, false));
                }
            }
        } else {
            // Get our map for our tower
            HashMap<Coordinates.Direction, Boolean> towerTipDir = mapOfTippableTower(height);
            // Check if the tower is tippable and in what direction
            for (Coordinates.Direction d : Coordinates.CARDINAL_NEIGHBORS) {
                if (towerTipDir.get(d)) {
                    // Here we are tipping over the tower so our board changes, so we make a deep copy of the current
                    // board and update the copy with the tower being tipped.
                    Grid<String> newBoard = new Grid<>(this.board);
                    // The tower is now an empty cell.
                    newBoard.set("0", tippersPos);
                    for (int i = 1; i <= height; i++) {
                        Coordinates current = tippersPos.sum(d.coords.multiply(i));
                        newBoard.set("1", current);
                    }
                    Coordinates current = tippersPos.sum(d.coords.multiply(1));
                    neighbors.add(new TipOverConfig(newBoard, current, this.goalCratePos, true));

                } else {
                    // Not all towers will be tippable, so we treat as if it were a crate.
                    Coordinates current = tippersPos.sum(d.coords.multiply(1));
                    if (this.board.legalCoords(current) && !this.board.get(current).equals("0")) {
                        neighbors.add(new TipOverConfig(this.board, current, this.goalCratePos, false));
                    }
                }
            }
        }

        return neighbors;
    }

    @Override
    public String toString() {
        // Copy of the current board
        Grid<String> newBoard = new Grid<>(this.board);
        //Will convert StringBuilder to String and return it
        StringBuilder result = new StringBuilder();
        // Every row we want to start at a new line. The first 2 rows will be indented a tab space and will be used for
        // placing the column numbers.
        for (int row = 0; row < newBoard.getNRows() + 2; ++row) {
            result.append("\n");
            if (row < 2) {
                result.append("\t");
                for (int col = 0; col < newBoard.getNCols(); ++col) {
                    if (row == 0 && col < newBoard.getNCols()) {
                        result.append("  ");
                        result.append(col);
                    } else if (col < newBoard.getNCols()) {
                        result.append("___");
                    }
                }
            } else {
                //The remaining rows will start with the row numbers separated by ( | ) following pieces of the board
                result.append(" ").append(row - 2).append(" |");
                for (int col = 0; col < newBoard.getNCols(); ++col) {
                    Coordinates currentCoord = new Coordinates(row - 2, col);
                    //If the board is a 0 == empty so replace it with "_"
                    if (newBoard.get(currentCoord).equals("0")) {
                        newBoard.set("_", currentCoord);
                    }
                    //Once the tipper's position is equal to the goal crates position we want to indicate the tipper has
                    //reached the goal with an asterisk
                    if (currentCoord.equals(this.tippersPos) && currentCoord.equals(this.goalCratePos)) {
                        newBoard.set("*" + newBoard.get(currentCoord), currentCoord);
                    } else {
                        //The Tippers current position on the board
                        if (currentCoord.equals(this.tippersPos)) {
                            newBoard.set("*" + newBoard.get(currentCoord), currentCoord);
                        }
                        //The goal crates position
                        if (currentCoord.equals(this.goalCratePos)) {
                            newBoard.set("!" + newBoard.get(currentCoord), currentCoord);
                        }
                    }
                    //For correct spacing on the board
                    if (newBoard.get(currentCoord).contains("!") || newBoard.get(currentCoord).contains("*")) {
                        result.append(" ");
                    } else {
                        result.append("  ");
                    }
                    //Our towers and crates
                    result.append(newBoard.get(currentCoord));
                }
            }
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TipOverConfig otherWater) {
            result = this.tippersPos.equals(otherWater.tippersPos) && this.board.equals(otherWater.board);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.tippersPos.hashCode() + this.board.hashCode();
    }
}
