package puzzles.lunarlanding;

import puzzles.lunarlanding.model.*;
import solver.Configuration;
import solver.Solver;
import util.Coordinates;
import util.Grid;
import java.io.*;
import java.util.*;

/**
 *  Main class for Lunar Landing Puzzle
 * @author Bill Stephen
 * November 2021
 */
public class LunarLanding {

    /**
     * code to read the file name from the command line and
     * run the solver on the puzzle
     */

    public static void main(String[] args) throws FileNotFoundException {
        try (BufferedReader inputReader = new BufferedReader(new FileReader(args[0]))) {
            String line = inputReader.readLine();
            String[] fields = line.split("\\s+");
            int boardsRow = Integer.parseInt(fields[0]);
            int boardsCol = Integer.parseInt(fields[1]);
            Grid<String> board = new Grid<>("", boardsRow, boardsCol);
            Coordinates goalLunar = new Coordinates(Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
            Coordinates explorer = null;
            Coordinates pieceCoord;

            Map<String, Coordinates> allPiecePos = new HashMap<>();
            while ((line = inputReader.readLine()) != null) {
                if (line.isEmpty()) {break;}
                fields = line.split("\\s+");
                if(fields[0].equals("E")){
                    explorer = new Coordinates(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                }
                board.set(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                 pieceCoord = new Coordinates(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                allPiecePos.put(fields[0],pieceCoord);
            }
            // Instance of lunar landing config
            Configuration lunarLanding = new LunarLandingConfig(board, allPiecePos, explorer, goalLunar);
            //Create an instance of a Solver and store its shortestPath List into a collection.
            Solver solver = new Solver();
            Collection<Configuration> shortestPath = solver.getShortestPath(lunarLanding);

            //Display both total configurations and unique configurations
            System.out.println("Total configs: " + solver.getTotalConfigs());
            System.out.println("Unique configs: " + solver.getUniqueConfigs());

            //If the List constructed at the end of BFS is empty it means we did not find a match
            //Otherwise we want to print every step with its correlating ClockConfiguration
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

