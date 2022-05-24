package puzzles.water;

import solver.Configuration;

import java.util.LinkedList;
import java.util.List;


/**
 * Class represents a WaterConfiguration. The configuration is made up of the bucket amount we want, the bucket that contains
 * all max capacity, each current bucket size which always starts at 0 and every current bucket's neighbor.
 *
 * @author Miguel Reyes
 */
public class WaterConfiguration implements Configuration {
    private int goal;
    private List<Integer> buckets;
    private List<Integer> current;
    private List<Configuration> neighbors;

    /**
     *
     * @param buckets - Containts info on desired amount of water we want, and the capacities of the N available buckets.
     * @param current - Current state of N buckets - each bucket starts at 0.
     * @param goal - Desired amount of water we want in a bucket
     */
    public WaterConfiguration(List<Integer> buckets, List<Integer> current, int goal) {
        this.current = current;
        this.goal = goal;
        this.buckets = buckets;
    }


    @Override
    public boolean isSolution() {
        boolean result = false;
        //Loop through every bucket in current state of buckets
        // and check if one of the buckets is the goal 
        for (Integer in : this.current) {
            if (in == goal) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public List<Configuration> getNeighbors() {
        this.neighbors = new LinkedList<>();
        List<Integer> currConf;
        Configuration c1;
        for (int i = 0; i < this.current.size(); ++i) {
            //Fills Bucket
            if (this.current.get(i) == 0) {
                currConf = new LinkedList<>(this.current);
                currConf.set(i, buckets.get(i));
                c1 = new WaterConfiguration(this.buckets, currConf, goal);
                this.neighbors.add(c1);
            }
            //Drains bucket
            else if (this.current.get(i).equals(this.buckets.get(i))) {
                currConf = new LinkedList<>(this.current);
                currConf.set(i, 0);
                c1 = new WaterConfiguration(this.buckets, currConf, goal);
                this.neighbors.add(c1);
            }
            //Transfers water from bucket to bucket under the conditions one is being completely filled or drained
            for (int j = 0; j < this.current.size(); ++j) {
                currConf = new LinkedList<>(this.current);
                int currentCapacity = buckets.get(j) - currConf.get(j);
                if (j != i) {
                    if (currConf.get(i) <= currentCapacity) {
                        currConf.set(j, currConf.get(j) + currConf.get(i));
                        currConf.set(i, 0);
                    } else {
                        currConf.set(j, buckets.get(j));
                        currConf.set(i, currConf.get(i) - currentCapacity);
                    }
                    c1 = new WaterConfiguration(this.buckets, currConf, goal);
                    this.neighbors.add(c1);
                }
            }
        }
        return this.neighbors;
    }

    @Override
    public String toString() {
        return this.current.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof WaterConfiguration) {
            WaterConfiguration otherWater = (WaterConfiguration) obj;
            result = this.current.equals(otherWater.current);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.current.hashCode();
    }
}



