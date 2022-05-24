package solver;

import java.util.List;

/**
 * Configuration abstraction for the solver algorithm
 *
 * @author Miguel Reyes
 * November 2021
 */
public interface Configuration {

    /**
     * Checks to see if the current configuration is the configuration we're looking for. Returns True if
     * correct configuration. If not found returns False.
     *
     * @return True or False
     */
    boolean isSolution();


    /**
     * Returns a list of all the neighbors of a configuration
     *
     * @return A list of configuration(Clock or Water configurations)
     */
    List<Configuration> getNeighbors();
}
