package puzzles.lunarlanding.ptui;

import puzzles.lunarlanding.model.LunarLandingModel;
import util.Coordinates;
import util.Grid;
import util.Observer;

import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * The textual view and controller for Lunar Landing.
 * @author Bill Stephen
 * November 2021
 */
public class LunarLandingPTUI implements Observer<LunarLandingModel, Object > {
    /**
     * Last piece that was selected
     */
    Coordinates tempCoord;
    /**
     * Name of last piece moved
     */
    String name;
    /**
     * Name of last board file used
     */
    String[] filename = new String[]{""};
    /**
     * Last command entered
     */
    String lastCommand;

    /**
     * The model for the view and controller.
     */
    private LunarLandingModel model;

    /**
     * Construct the PTUI
     */
    public LunarLandingPTUI(String arg) {
        filename[0] = arg;
        this.model = new LunarLandingModel(filename[0]);
        initializeView();
    }

    /**
     * Read a command and execute loop.
     */
    private void run() {
        Scanner in = new Scanner(System.in);
        for (; ; ) {
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if ( words.length > 0 ) {
                lastCommand = words[0];
                switch (words[0]) {
                    //Loads new puzzle from file
                    case "load" -> {
                        try {
                            String[] tempFileName = words[1].split("\\.");
                            if(tempFileName[1].equals("txt")) {
                                filename[0] = words[1];
                                this.model = new LunarLandingModel(filename[0]);
                                initializeView();
                            } else {
                                System.out.println("File could not be opened");
                            }
                        } catch (Exception e) {
                            System.out.println("ERROR: Incorrect file name/extension or input file may not exist. Try again with correct file input.");
                        }
                    }
                    //Restarts current puzzle
                    case "reload" -> {
                        this.model = new LunarLandingModel(filename[0]);
                        initializeView();
                    }
                    //Choose piece from Y X coords
                    case "choose" -> {
                        try {
                            int row = Integer.parseInt(words[1]);
                            int col = Integer.parseInt(words[2]);

                            tempCoord = new Coordinates(row,col);
                            if(this.model.getAllPieces().containsValue(tempCoord)){
                                for(Map.Entry<String, Coordinates> entry : this.model.getAllPieces().entrySet()){
                                    if (Objects.equals(entry.getValue(), tempCoord)) {
                                        name = entry.getKey();
                                    }
                                }
                            } else {
                                System.out.println("No figure at that position");
                            }
                        } catch (NumberFormatException e){
                            System.out.println("ERROR: Inputs for 'choose' must be integers. Try Again");
                        }
                    }
                    //Move piece in direction
                    case "go" -> {
                        try {
                            if (name != null && tempCoord != null) {
                                boolean legalMove = this.model.go(words[1], tempCoord, name);
                                if (!legalMove) {
                                    System.out.println("Illegal move");
                                }
                            } else {
                                if (tempCoord == null) {
                                    System.out.println("Choose a character to move first");
                                }
                            }
                            name = null;
                            tempCoord = null;
                        } catch (ArrayIndexOutOfBoundsException e){
                            System.out.println("ERROR: Input for 'go' must include a direction [NORTH, EAST, SOUTH, WEST]. Try Again.");
                        }
                    }
                    //Uses LunarLanding BFS to find next move
                    case "hint" -> {
                        int hint = this.model.hint();
                        if (hint == 0) {
                            System.out.println("Unsolvable board");
                        }
                        if(hint == 1){
                            System.out.println("Current board is already solved");
                        }
                    }
                    //Displays board
                    case "show" -> System.out.println(this.show());
                    //Command menu
                    case "help" -> displayHelp();
                    //Quit puzzle
                    case "quit" -> System.exit(0);
                    default -> {
                        System.out.println("Illegal command");
                        displayHelp();
                    }
                }
            }
        }
    }

    /**
     * Initialize the view
     */
    public void initializeView() {
        this.model.addObserver( this );
        update( this.model, null );
    }

    /**
     * Print on standard out help for the game.
     */
    private void displayHelp() {
        System.out.println("Legal commands are...");
        System.out.println("\t > help : Show all commands.");
        System.out.println("\t > reload filename: Load the most recent file again.");
        System.out.println("\t > load filename: Load a new game board file. (1 argument)");
        System.out.println("\t > hint : Make the next move for me.");
        System.out.println("\t > show : Display the board.");
        System.out.println("\t > go {north|south|east|west}: Tell chosen character where to go. (1 argument)");
        System.out.println("\t > choose row column: Choose which character moves next. (2 arguments)");
        System.out.println("\t > quit");
    }

    /**
     *Displays board after move & checks to see if the board was solved by the BFS or User
     */
    @Override
    public void update( LunarLandingModel o, Object arg ) {
        System.out.println(this.show());
        if((this.model.getGoalLunar().row() == this.model.getExplorer().row()) &&
                (this.model.getGoalLunar().col() == this.model.getExplorer().col())){
            if(!lastCommand.equals("hint")) {
                System.out.println("YOU WON!");
            }else{
                System.out.println("I WON!");
            }
        }
    }

    /**
     * PTUI Driver
     * @param args - Input file
     */
    public static void main(String[] args ) {
        LunarLandingPTUI ptui = new LunarLandingPTUI(args[0]);
        System.out.println();
        System.out.println("File loaded");
        ptui.run();
    }

    /**
     * @return Board's formatted output to the terminal
     */
    public String show() {
        Grid<String> newBoard = new Grid<>(this.model.getBoard());
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
                    if (currentCoord.equals(this.model.getGoalLunar()) && !newBoard.get(currentCoord).equals("_")) {
                        newBoard.set("!" + newBoard.get(currentCoord), currentCoord);
                    } else {
                        if (currentCoord.equals(this.model.getGoalLunar())) {
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
}
