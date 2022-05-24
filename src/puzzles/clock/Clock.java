package puzzles.clock;

import solver.Configuration;
import solver.Solver;

import java.util.Collection;

/**
 * Main class for the "clock" puzzle.
 *
 * @author Miguel Reyes
 */
public class Clock {
    /**
     * Run an instance of the clock puzzle.
     *
     * @param args [0]: number of hours on the clock;
     *             [1]: starting time on the clock;
     *             [2]: goal time to which the clock should be set.
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Clock hours start end");
        } else {
            int numberOfHours = Integer.parseInt(args[0]);
            int startTime = Integer.parseInt(args[1]);
            int endTime = Integer.parseInt(args[2]);

            System.out.println("Hours: " + numberOfHours + ", Start: " + startTime + ", End: " + endTime);

            //Create an instance of a ClockConfiguration as Configuration type and then store its graph in a Map.
            Configuration clock = new ClockConfiguration(numberOfHours, startTime, endTime);
//            Map<Integer, Configuration> graph = clock.constructGraph();

            //Create an instance of a ClockSolver and store its shortestPath List into a collection.
            Solver solver = new Solver();
            Collection<Configuration> shortestPath = solver.getShortestPath(clock);

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
        }
    }
}
