package solver;

import java.util.*;

/**
 * This an abstract class that contains an abstract universal algorithm to find a path from a starting
 * configuration to a solution, if one exists - No implementation here.
 *
 * @author Miguel Reyes
 */
public class Solver {

    // This is the size of the predecessor map at the end of BFS in a Solver
    private int uniqueConfigs;

    // This is every configuration including the duplicates from the BFS in Solver
    private int totalConfigs = 1;


    /**
     * This will perform the BFS and check to see if a solution exists. It will also call the set both total and unique
     * configs after performing the search.
     *
     * @param conf - the start configuration
     * @return - a collection of configurations
     */
    public Collection<Configuration> getShortestPath(Configuration conf) {
        Configuration goal = null;
        // prime the queue with the starting configuration
        List<Configuration> queue = new LinkedList<>();
        queue.add(conf);

        // construct the predecessors data structure
        Map<Configuration, Configuration> predecessors = new HashMap<>();
        // put the starting configuration in, and just assign itself as predecessor
        predecessors.put(conf, conf);

        // loop until either the finish configuration is found, or the queue is empty (no path)
        while (!queue.isEmpty()) {
            // the next configuration to process is at the front of the queue
            Configuration current = queue.remove(0);
            if (current.isSolution()) {
                goal = current;
                break;
            }
            // loop over all neighbors of current
            for (Configuration nbr : current.getNeighbors()) {
                ++this.totalConfigs;
                // process unvisited neighbors
                if (!predecessors.containsKey(nbr)) {
                    predecessors.put(nbr, current);
                    queue.add(nbr);
                }
            }
        }
        this.uniqueConfigs = predecessors.size();

        // construct the path from the predecessor map and return the
        // sequence from start to finish configuration

        return constructPath(predecessors, conf, goal);
    }

    /**
     * Method to return a path from the starting to finishing configuration.
     *
     * @param predecessors Map used to reconstruct the path
     * @param conf         starting configuration
     * @param goal         finishing configuration
     * @return a list containing the sequence of configurations comprising the path.
     * An empty list if no path exists.
     */
    public List<Configuration> constructPath(Map<Configuration, Configuration> predecessors, Configuration conf, Configuration goal) {
        // use predecessors to work backwards from finish to start,
        // all the while dumping everything into a linked list
        List<Configuration> path = new LinkedList<>();

        if (predecessors.containsKey(goal)) {
            Configuration currConf = goal;
            while (currConf != conf) {
                path.add(0, currConf);
                currConf = predecessors.get(currConf);
            }
            path.add(0, conf);
        }
        return path;
    }


    /**
     * Returns the total number of unique configurations.
     *
     * @return Returns the total number of unique configurations.
     */
    public int getUniqueConfigs() {
        return uniqueConfigs;
    }


    /**
     * Returns the total number of configurations including duplicates.
     *
     * @return Returns the total number of configurations including duplicates.
     */
    public int getTotalConfigs() {
        return totalConfigs;
    }

}
