package puzzles.lunarlanding.model;

import solver.Configuration;
import solver.Solver;
import util.Coordinates;
import util.Grid;
import util.Observer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Model for Lunar Landing, used by LunarLandingPTUI & LunarLandingGUI
 * @author Bill Stephen
 * November 2021
 */
public class LunarLandingModel{

    /**
     * The current board of a LunarLandingConfig.
     */
    private Grid<String> board= new Grid<>("", 0, 0);
    /**
     * The explorers current position on the board of a LunarLandingConfig.
     */
    private Coordinates explorer;
    /**
     * The goal position on board of a LunarLandingConfig - never changes.
     */
    private  Coordinates goalLunar;
    /**
     * The all pieces on the current board of a LunarLandingConfig.
     */
    private Map<String, Coordinates> allPiecePos = new HashMap<>();
    /**
     * Boolean that indicates if a move was valid
     */
    private boolean legalMove = true;

    /**
     * Those objects that are watching this object's every move
     */
    private final List<Observer< LunarLandingModel, Object >> observers;

    /**
     * Construct a LunarLandingModel; there is only one configuration.
     */
    public LunarLandingModel(String file) {
        this.observers = new LinkedList<>();
        try (BufferedReader inputReader = new BufferedReader(new FileReader(file))) {
            String line = inputReader.readLine();
            String[] fields = line.split("\\s+");
            int boardsRow = Integer.parseInt(fields[0]);
            int boardsCol = Integer.parseInt(fields[1]);
            this.board = new Grid<>("", boardsRow, boardsCol);
            this.goalLunar = new Coordinates(Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
            Coordinates pieceCoord;
            while ((line = inputReader.readLine()) != null) {
                if (line.isEmpty()) {break;}
                fields = line.split("\\s+");
                if(fields[0].equals("E")){
                    this.explorer = new Coordinates(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                }
                this.board.set(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                pieceCoord = new Coordinates(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                this.allPiecePos.put(fields[0],pieceCoord);
            }
        } catch (IOException e) {
        }
        announce();
    }

     /**
     * Gets explorer pos from model's board
     * @return Current explorer
     */
    public Coordinates getExplorer(){return this.explorer;}

    /**
     * Gets a HashMap off all pieces on board
     * @return HashMap off all pieces on board
     */
    public Map<String, Coordinates> getAllPieces(){return this.allPiecePos;}

    /**
     * Gets goals pos from model's board
     * @return Current explorer
     */
    public Coordinates getGoalLunar(){return this.goalLunar;}

    /**
     * Gets the model's board
     * @return Current explorer
     */
    public Grid<String> getBoard() {
        return this.board;
    }

    /**
     * Check to see if "go" was a valid move
     * @return Current explorer
     */
    public boolean getLegalMove() {
        return !legalMove;
    }

    /**
     * Moves the selected piece. Moves the piece north, east, south or west.
     */
    public boolean go(String direction, Coordinates piece, String name) {
        Coordinates tempExplorer = this.explorer;
        int boardRows = this.board.getNRows();
        int boardCols = this.board.getNCols();
        int tempPieceX;
        int tempPieceY;
        legalMove = false;
        //Check north for piece collision / border collision
        if(direction.equals("north")) {
            tempPieceX = piece.col();
            tempPieceY = piece.row();
            boolean foundNorth = false;
            while (0 < tempPieceY && !foundNorth) {
                tempPieceY--;
                Coordinates tempPiece = new Coordinates(tempPieceY, tempPieceX);
                if (!this.board.get(tempPiece).equals("")) {
                    if (name.equals("E")) {
                        tempExplorer = new Coordinates(tempPieceY + 1, tempPieceX);
                    }
                    Coordinates returnPiece = new Coordinates(tempPieceY + 1, tempPieceX);
                    foundNorth = true;
                    if (!returnPiece.equals(piece)) {
                        Grid<String> tempBoard = new Grid<>(this.board);
                        tempBoard.set("", piece.row(), piece.col());
                        tempBoard.set(name, tempPieceY + 1, tempPieceX);
                        Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                        tempAllPiecePOS.remove(name);
                        tempAllPiecePOS.put(name, returnPiece);
                        legalMove = true;
                        this.board = tempBoard;
                        this.explorer = tempExplorer;
                        this.allPiecePos = tempAllPiecePOS;
                        announce();
                    }
                }
            }
        }

        //Check east for piece collision / border collision
        if(direction.equals("east")) {
            tempPieceX = piece.col();
            tempPieceY = piece.row();
            boolean foundEast = false;
            while (tempPieceX < boardCols - 1 && !foundEast) {
                tempPieceX++;
                Coordinates tempPiece = new Coordinates(tempPieceY, tempPieceX);
                if (!this.board.get(tempPiece).equals("")) {
                    if (name.equals("E")) {
                        tempExplorer = new Coordinates(tempPieceY, tempPieceX - 1);
                    }
                    Coordinates returnPiece = new Coordinates(tempPieceY, tempPieceX - 1);
                    foundEast = true;
                    if (!returnPiece.equals(piece)) {
                        Grid<String> tempBoard = new Grid<>(this.board);
                        tempBoard.set("", piece.row(), piece.col());
                        tempBoard.set(name, tempPieceY, tempPieceX - 1);
                        Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                        tempAllPiecePOS.remove(name);
                        tempAllPiecePOS.put(name, returnPiece);
                        legalMove = true;
                        this.board = tempBoard;
                        this.explorer = tempExplorer;
                        this.allPiecePos = tempAllPiecePOS;
                        announce();
                    }
                }
            }
        }

        //Check south for piece collision / border collision
        if(direction.equals("south")) {
            tempPieceX = piece.col();
            tempPieceY = piece.row();
            boolean foundSouth = false;
            while (tempPieceY < boardRows - 1 && !foundSouth) {
                tempPieceY++;
                Coordinates tempPiece = new Coordinates(tempPieceY, tempPieceX);
                if (!this.board.get(tempPiece).equals("")) {
                    if (name.equals("E")) {
                        tempExplorer = new Coordinates(tempPieceY - 1, tempPieceX);
                    }
                    Coordinates returnPiece = new Coordinates(tempPieceY - 1, tempPieceX);
                    foundSouth = true;
                    if (!returnPiece.equals(piece)) {
                        Grid<String> tempBoard = new Grid<>(this.board);
                        tempBoard.set("", piece.row(), piece.col());
                        tempBoard.set(name, tempPieceY - 1, tempPieceX);
                        Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                        tempAllPiecePOS.remove(name);
                        tempAllPiecePOS.put(name, returnPiece);
                        legalMove = true;
                        this.board = tempBoard;
                        this.explorer = tempExplorer;
                        this.allPiecePos = tempAllPiecePOS;
                        announce();
                    }
                }
            }
        }

        //Check west for piece collision / border collision
        if(direction.equals("west")) {
            tempPieceX = piece.col();
            tempPieceY = piece.row();
            boolean foundWest = false;
            while (0 < tempPieceX && !foundWest) {
                tempPieceX--;
                Coordinates tempPiece = new Coordinates(tempPieceY, tempPieceX);
                if (!this.board.get(tempPiece).equals("")) {
                    if (name.equals("E")) {
                        tempExplorer = new Coordinates(tempPieceY, tempPieceX + 1);
                    }
                    Coordinates returnPiece = new Coordinates(tempPieceY, tempPieceX + 1);
                    foundWest = true;
                    if (!returnPiece.equals(piece)) {
                        Grid<String> tempBoard = new Grid<>(this.board);
                        tempBoard.set("", piece.row(), piece.col());
                        tempBoard.set(name, tempPieceY, tempPieceX + 1);
                        Map<String, Coordinates> tempAllPiecePOS = new HashMap<>(this.allPiecePos);
                        tempAllPiecePOS.remove(name);
                        tempAllPiecePOS.put(name, returnPiece);
                        legalMove = true;
                        this.board = tempBoard;
                        this.explorer = tempExplorer;
                        this.allPiecePos = tempAllPiecePOS;
                        announce();
                    }
                }
            }
        }
        return legalMove;
    }

    /**
     * Takes in current boards config and uses LunarLanding BFS to find next move towards solution.
     */
    public int hint() {
        Configuration lunarLanding = new LunarLandingConfig(this.board, this.allPiecePos, this.explorer, this.goalLunar);
        //Create an instance of a Solver and store its shortestPath List into a collection.
        Solver solver = new Solver();
        Collection<Configuration> shortestPath = solver.getShortestPath(lunarLanding);
        LinkedList <Configuration> solutionList = new LinkedList<>(shortestPath);
        if(solutionList.size() > 1) {
            this.board = ((LunarLandingConfig) solutionList.get(1)).getBoard();
            this.explorer = ((LunarLandingConfig) solutionList.get(1)).getExplorerPOS();
            this.allPiecePos = ((LunarLandingConfig) solutionList.get(1)).getAllPiecePos();
            announce();
        }
        return solutionList.size();
    }


    /**
     * Add a new observer to the list for this model
     * @param obs an object that wants an
     *            {@link Observer#update(Object, Object)}
     *            when something changes here
     */
    public void addObserver( Observer< LunarLandingModel, Object > obs ) {
        this.observers.add( obs );
    }

    /**
     * Announce to observers the model has changed;
     */
    private void announce() {
        for ( var obs : this.observers ) {
            obs.update( this, null);
        }
    }

}
