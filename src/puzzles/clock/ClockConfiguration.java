package puzzles.clock;

import solver.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a ClockConfiguration. The configuration is made up of the total number of hours the clock has, each
 * individual hour, the hour we want to get to and each hour's neighbor.
 *
 * @author Miguel Reyes
 */
public class ClockConfiguration implements Configuration {
    private int hours;
    private int start;
    private int end;
    private List<Configuration> neighbors;

    /**
     * @param hours - Total amount of hours starting in a clock
     * @param start - The hour we start at for current configuration
     * @param end   - the hour we want to get to.
     */
    public ClockConfiguration(int hours, int start, int end) {
        this.hours = hours;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean isSolution() {
        return this.start == this.end;
    }

    @Override
    public List<Configuration> getNeighbors() {
        this.neighbors = new ArrayList<>(2);
        Configuration c1;
        //1 is the smallester hour on the clock so its neighbor will 2 and the largest hour
        if (this.start == 1) {
            c1 = new ClockConfiguration(this.hours, this.start + 1, this.end);
            neighbors.add(c1);
            c1 = new ClockConfiguration(this.hours, this.hours, this.end);
            neighbors.add(c1);

        }
        //At the largest hour is neighbor is hours - 1 and 1
        else if (this.start == this.hours) {
            c1 = new ClockConfiguration(this.hours, 1, this.end);
            neighbors.add(c1);
            c1 = new ClockConfiguration(this.hours, this.hours - 1, this.end);
            neighbors.add(c1);
        } else {
            c1 = new ClockConfiguration(this.hours, this.start + 1, this.end);
            neighbors.add(c1);
            c1 = new ClockConfiguration(this.hours, this.start - 1, this.end);
            neighbors.add(c1);
        }
        return this.neighbors;
    }

    @Override
    public String toString() {
        return "Hour " + this.start;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof ClockConfiguration) {
            ClockConfiguration otherClock = (ClockConfiguration) obj;
            result = this.start == otherClock.start;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.start;
    }
}
