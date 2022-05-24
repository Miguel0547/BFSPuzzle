package puzzles.tipover;

import puzzles.tipover.model.TipOverConfig;
import solver.Configuration;
import solver.Solver;
import util.Coordinates;
import util.Grid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

/**
 * Main class for Tip Over Puzzle
 *
 * @author Miguel Reyes
 * November 2021
 */
public class TipOver {

    /*
     * code to read the file name from the command line and
     * run the solver on the puzzle
     */

    public static void main( String[] args ) {

        try(BufferedReader inputReader = new BufferedReader( new FileReader(args[0] ) )){
            String line  = inputReader.readLine();
            String[] fields = line.split("\\s+");
            int boardsRow = Integer.parseInt(fields[0]);
            int boardsCol = Integer.parseInt(fields[1]);
            Grid<String> board = new Grid<>("", boardsRow, boardsCol);
            Coordinates tipper = new Coordinates(Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
            Coordinates goalCrane = new Coordinates(Integer.parseInt(fields[4]), Integer.parseInt(fields[5]));
            int row = 0;
            while ((line = inputReader.readLine()) != null) {
                if(line.isEmpty()){
                    break;
                }
                fields = line.split("\\s+");
                for(int col = 0; col < fields.length; col++){
                    board.set(fields[col], row, col);
                }
                row ++;
            }
            // Instance of tipOver config
            Configuration tipOver = new TipOverConfig(board, tipper, goalCrane, false);
            //Create an instance of a solver and store its shortestPath List into a collection.
            Solver solver = new Solver();
            Collection<Configuration> shortestPath = solver.getShortestPath(tipOver);

            //Display both total configurations and unique configurations
            System.out.println("Total configs: " + solver.getTotalConfigs());
            System.out.println("Unique configs: " + solver.getUniqueConfigs());

            //If the List constructed at the end of BFS is empty it means we did not find a match
            //Otherwise we want to print every step with its correlating TipOverConfig
            if (shortestPath.size() == 0) {
                System.out.println("No Solution");
            } else {
                int i = 0;
                for (Configuration in : shortestPath) {
                    System.out.println("Step " + i + ": " + in);
                    ++i;
                }
            }

        } catch (IOException e) {
            System.out.println("""
                    ERROR: Program has ended due to incorrect file name/extension or input file may not exist.

                    Try again with correct file input.""");
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("""
                    ERROR: Program has ended due to no command line arguments.

                    Try again with correct file input.""");
        }

    }
}
