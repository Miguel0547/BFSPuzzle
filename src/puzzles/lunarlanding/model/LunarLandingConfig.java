package puzzles.lunarlanding.model;

import solver.Configuration;
import util.Coordinates;
import util.Grid;
import java.util.*;


/**
 * Class represents a LunarLandingConfig. The configuration is made up of the puzzles board, all pieces positions on
 * the board, the position of the explorer on the board and the position of the goal on the board.
 * @author Bill Stephen
 * November 2021
 */
public class LunarLandingConfig implements Configuration {

    /**
     * The current board of a LunarLandingConfig.
     */
    private Grid<String> board;
    /**
     * The all pieces on the current board of a LunarLandingConfig.
     */
    private Map<String, Coordinates> allPiecePos;
    /**
     * The explorers current position on the board of a LunarLandingConfig.
     */
    private Coordinates explorerPOS;
    /**
     * The goal position on board of a LunarLandingConfig - never changes.
     */
    private final Coordinates goalPos;

    /**
     * @param board - Board that contains all pieces on it
     * @param allPiecePos - Position of all Robots and Explorer on board
     * @param explorerPOS - Position of Explorer on board
     * @param goalPos - Position of Lunar Landing on board
     */
    public LunarLandingConfig(Grid<String> board, Map<String, Coordinates> allPiecePos, Coordinates explorerPOS, Coordinates goalPos){
        this.board = board;
        this.allPiecePos = allPiecePos;
        this.explorerPOS = explorerPOS;
        this.goalPos = goalPos;
    }


    /**
     * Returns the board of the configuration
     * @return The board of the configuration
     */
    public Grid<String> getBoard() {
        return this.board;
    }
    /**
     * Returns all piece positions
     * @return The all piece positions
     */
    public Map<String, Coordinates> getAllPiecePos() {
        return this.allPiecePos;
    }
    /**
     * Returns the explorer position
     * @return The explorer position
     */
    public Coordinates getExplorerPOS() {
        return this.explorerPOS;
    }

    /**
     * @return True is explorer and goal have the same coord, false otherwise.
     */
    @Override
    public boolean isSolution() {return explorerPOS.equals(goalPos);}

    /**
     * Finds the North, South, East, West neighbors of current piece. Checks for boarder and piece collision
     * @return List of neighbors for current piece.
     */
    @Override
    public List<Configuration> getNeighbors() {
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        List<Configuration> neighbors = new LinkedList<>();

        Coordinates tempExplorer = this.explorerPOS;
        for (String letter : alphabet) {
            if (this.allPiecePos.get(letter) != null) {
                int boardRows = board.getNRows();
                int boardCols = board.getNCols();
                Coordinates pieceCord = this.allPiecePos.get(letter);
                int currentPieceX = pieceCord.col();
                int currentPieceY = pieceCord.row();
                //Check north for piece collision / border collision
                int tempX = currentPieceX;
                int tempY = currentPieceY;
                boolean foundNorth = false;
                while (0 < tempY && !foundNorth) {
                    tempY--;
                    Coordinates tempPiece = new Coordinates(tempY, tempX);
                    if (!this.board.get(tempPiece).equals("")) {
                        if (letter.equals("E")) {
                            tempExplorer = new Coordinates(tempY + 1, tempX);
                        }
                        Coordinates returnPiece = new Coordinates(tempY + 1, tempX);
                        foundNorth = true;
                        if (!returnPiece.equals(pieceCord)) {
                            Grid<String> tempBoard = new Grid<>(this.board);
                            tempBoard.set("", currentPieceY, currentPieceX);
                            tempBoard.set(letter, tempY + 1, tempX);
                            Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                            tempAllPiecePOS.remove(letter);
                            tempAllPiecePOS.put(letter, returnPiece);
                            neighbors.add(new LunarLandingConfig(tempBoard, tempAllPiecePOS, tempExplorer, this.goalPos));
                        }
                    }
                }

                //Check south for piece collision / border collision
                tempY = currentPieceY;
                boolean foundSouth = false;
                while (tempY < boardRows - 1 && !foundSouth) {
                    tempY++;
                    Coordinates tempPiece = new Coordinates(tempY, tempX);
                    if (!this.board.get(tempPiece).equals("")) {
                        if (letter.equals("E")) {
                            tempExplorer = new Coordinates(tempY - 1, tempX);
                        }
                        Coordinates returnPiece = new Coordinates(tempY - 1, tempX);
                        foundSouth = true;
                        if (!returnPiece.equals(pieceCord)) {
                            Grid<String> tempBoard = new Grid<>(this.board);
                            tempBoard.set("", currentPieceY, currentPieceX);
                            tempBoard.set(letter, tempY - 1, tempX);
                            Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                            tempAllPiecePOS.remove(letter);
                            tempAllPiecePOS.put(letter, returnPiece);
                            neighbors.add(new LunarLandingConfig(tempBoard, tempAllPiecePOS, tempExplorer, this.goalPos));
                        }
                    }
                }


                //Check east for piece collision / border collision
                tempY = currentPieceY;
                boolean foundEast = false;
                while (tempX < boardCols - 1 && !foundEast) {
                    tempX++;
                    Coordinates tempPiece = new Coordinates(tempY, tempX);
                    if (!this.board.get(tempPiece).equals("")) {
                        if (letter.equals("E")) {
                            tempExplorer = new Coordinates(tempY, tempX - 1);
                        }
                        Coordinates returnPiece = new Coordinates(tempY, tempX - 1);
                        foundEast = true;
                        if (!returnPiece.equals(pieceCord)) {
                            Grid<String> tempBoard = new Grid<>(this.board);
                            tempBoard.set("", currentPieceY, currentPieceX);
                            tempBoard.set(letter, tempY, tempX - 1);
                            Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                            tempAllPiecePOS.remove(letter);
                            tempAllPiecePOS.put(letter, returnPiece);
                            neighbors.add(new LunarLandingConfig(tempBoard, tempAllPiecePOS, tempExplorer, this.goalPos));
                        }
                    }
                }


                //Check west for piece collision / border collision
                tempX = currentPieceX;
                boolean foundWest = false;
                while ( 0 < tempX && !foundWest) {
                    tempX--;
                    Coordinates tempPiece = new Coordinates(tempY, tempX);
                    if (!this.board.get(tempPiece).equals("")) {
                        if (letter.equals("E")) {
                            tempExplorer = new Coordinates(tempY, tempX + 1);
                        }
                        Coordinates returnPiece = new Coordinates(tempY, tempX + 1);
                        foundWest = true;
                        if (!returnPiece.equals(pieceCord)) {
                            Grid<String> tempBoard = new Grid<>(this.board);
                            tempBoard.set("", currentPieceY, currentPieceX);
                            tempBoard.set(letter, tempY, tempX + 1);
                            Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                            tempAllPiecePOS.remove(letter);
                            tempAllPiecePOS.put(letter, returnPiece);
                            neighbors.add(new LunarLandingConfig(tempBoard, tempAllPiecePOS, tempExplorer, this.goalPos));
                        }
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * @return Board's formatted output to the terminal
     */
    @Override
    public String toString() {
        Grid<String> newBoard = new Grid<>(this.board);
        StringBuilder result = new StringBuilder();
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
                result.append(" ").append(row - 2).append(" |");
                for (int col = 0; col < newBoard.getNCols(); ++col) {
                    Coordinates currentCoord = new Coordinates(row - 2, col);
                    if (newBoard.get(currentCoord).equals("")) {
                        newBoard.set("_", currentCoord);
                    }
                    if (currentCoord.equals(this.goalPos) && !newBoard.get(currentCoord).equals("_")) {
                        newBoard.set("!" + newBoard.get(currentCoord), currentCoord);
                    } else {
                        if (currentCoord.equals(this.goalPos)) {
                            newBoard.set(" !", currentCoord);
                        }
                    }
                    if (newBoard.get(currentCoord).contains("!")) {
                        result.append(" ");
                    } else {
                        result.append("  ");
                    }
                    result.append(newBoard.get(currentCoord));
                }
            }
        }
        return result.toString();
    }

    /**
     * @param obj - other instance of LunarLandingConfig
     * @return - True if explorerPOS are the same and board is the same between the two instances, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LunarLandingConfig otherLunar) {
            result = this.explorerPOS.equals(otherLunar.explorerPOS) && this.board.equals(otherLunar.board);
        }
        return result;
    }

    /**
     * @return - Hashcode
     */
    @Override
    public int hashCode() {
        return this.explorerPOS.hashCode() + this.board.hashCode();
    }
}