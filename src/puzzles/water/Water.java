package puzzles.water;

import solver.Configuration;
import solver.Solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Main class for the water buckets puzzle.
 *
 * @author Miguel Reyes
 */
public class Water {

    /**
     * Run an instance of the water buckets puzzle.
     *
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(
                    ("Usage: java Water amount bucket1 bucket2 ...")
            );
        } else {

            List<Integer> buckets = new ArrayList<>();
            List<Integer> current = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                buckets.add(Integer.parseInt(args[i]));
                current.add(0);
            }
            int amount = Integer.parseInt(args[0]);
            System.out.println("Amount: " + amount + ", Buckets: " + buckets);

            Configuration water = new WaterConfiguration(buckets, current, amount);

            //Create an instance of a ClockSolver and store its shortestPath List into a collection.
            Solver solver = new Solver();
            Collection<Configuration> shortestPath = solver.getShortestPath(water);

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
