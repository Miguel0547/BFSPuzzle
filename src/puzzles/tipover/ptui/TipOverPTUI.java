package puzzles.tipover.ptui;

import puzzles.tipover.model.TipOverModel;
import util.Coordinates;
import util.Grid;
import util.Observer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * The textual view and controller for TipOver.
 *
 * @author Miguel Reyes
 * November 2021
 */
public class TipOverPTUI implements Observer<TipOverModel, Object> {

    /**
     * String displayed whenever a new configuration is loaded or we reload the current configuration
     */
    private final String fileLoaded = "New file loaded.";


    /**
     * The model for the view and controller.
     */
    private final TipOverModel model;

    /**
     * Construct the PTUI
     */
    public TipOverPTUI(Grid<String> board, Coordinates tippersPos, Coordinates goalCratePos) {
        this.model = new TipOverModel(board, tippersPos, goalCratePos);
        initializeView();
    }


    /**
     * Will handle reading in the initial file through command line for our game. Will construct our PTUI and run the
     * game.
     * @param args - File we are reading in.
     */
    public static void main(String[] args) {
        try (BufferedReader inputReader = new BufferedReader(new FileReader(args[0]))) {
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
            TipOverPTUI ptui = new TipOverPTUI(board, tipperPos, goalCratePos);
            ptui.run();
        } catch (IOException e) {
            System.out.println("""
                    ERROR: Program has ended due to incorrect file name/extension or input file may not exist.

                    Try again with correct file input.""");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("""
                    ERROR: Program has ended due to no command line arguments.

                    Try again with correct file input.""");
        }
    }

    // CONTROLLER

    /**
     * Read a command and execute loop.
     */
    private void run() {
        // Help String that's displayed when input command is "help"
        String help = """
                Legal commands are...
                \t> help : Show all commands.
                \t> move {north|south|east|west}: Go in given direction, possibly tipping a tower. (1 argument)
                \t> reload filename: Load the most recent file again.
                \t> load {board-file-name}: Load a new game board file. (1 argument)
                \t> hint Make the next move for me.
                \t> show Display the board.
                \t> quit""";

        // Get input from console
        Scanner in = new Scanner(System.in);
        // Run game forever until "quit" is entered as the input command
        for (; ; ) {
            System.out.print("Game command: ");
            String line = in.nextLine();
            String[] words = line.split("\\s+");
            if (words.length > 0) {
                System.out.print(line + "\n");
                switch (words[0]) {
                    case "move":
                        // direction may throw an exception if user does not enter a word after "move" so it may be null
                        try {
                            String direction = words[1].toUpperCase();
                            if (Coordinates.Direction.isDirection(direction)) {
                                this.model.move(direction);
                                // If it's an invalid move print the following
                                if (this.model.getResultMove()) {
                                    System.out.println("""

                                            Invalid Move: Must move within the board and on a crate/tower.
                                            Try Again.""");
                                }
                            }
                            // This will handle the user entering "move" and an invalid direction
                            else {
                                System.out.println("""

                                        Legal directions are
                                        [NORTH, EAST, SOUTH, WEST]""");
                            }
                        } catch (Exception e) {
                            System.out.println("\nILLEGAL COMMAND -> " + help);
                        }
                        break;
                    case "help":
                        System.out.println(help);
                        break;
                    case "load":
                        try {
                            // Load may throw an exception if incorrect file used.
                            this.model.load(words[1]);
                            System.out.println(this.fileLoaded);
                        } catch (Exception e) {
                            System.out.println("""
                                                                            
                                    ERROR: Incorrect file name/extension or input file may not exist.

                                    Try again with correct file input.""");
                        }
                        break;
                    case "reload":
                        System.out.println(this.fileLoaded);
                        this.model.reload();
                        break;
                    case "show":
                        displayBoard();
                        break;
                    case "hint":
                        this.model.hint();
                        if (this.model.getResultHint()) {
                            System.out.println("\nUnsolvable Board");
                        }
                        break;
                    case "quit":
                        System.out.println();
                        System.exit(0);
                    default:
                        System.out.println("\nILLEGAL COMMAND -> " + help);
                        break;
                }
            }
        }
    }

    // VIEW

    /**
     * Initialize the view
     */
    public void initializeView() {
        this.model.addObserver(this);
        System.out.println(this.fileLoaded);
        update(this.model, null);
    }

    /**
     * Print on standard out the board using the current configs toString
     */
    private void displayBoard() {
        System.out.println(this.model.getCurrentConfig().toString());
    }

    @Override
    public void update(TipOverModel tipOverModel, Object o) {
        // Will display a tower has been tipped - works for both the user's and the solver's move
        if (o != null && o.equals("tower")) {
            System.out.print("\nA tower has been tipped over.");
        }
        // If current config reaches solution but using hint then prints the following
        if (o != null && o.equals("hint")) {
            if (this.model.getCurrentConfig().isSolution()) {
                System.out.println("\nI Won !!");
            }
        }
        // Player reaches solution
        if (this.model.getCurrentConfig().isSolution() && o == null) {
            System.out.println("\nYou Won!!");
        }
        // Prints the updated configuration
        displayBoard();
    }
}