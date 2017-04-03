package net.pilif0.open_desert.util;

/**
 * Handles calculation and keeping of the delta time - the time between two updates
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Delta {
    /** The number of units (ns) in a second */
    public static final long SECOND = 1_000_000_000;
    /** The number of units (ns) in a millisecond */
    public static final long MILLISECOND = 1_000_000;

    /** The time of the last update */
    private long last;
    /** The time of the last update (in ns) */
    private long delta;
    /** The time since the last UPS update */
    private long upsTime = 0;
    /** The number of updates since the last UPS update */
    private int upsCounter = 0;
    /** The current updates per second value (for the previous second) */
    private int ups;
    /** Whether the measuring has been started */
    private boolean started = false;

    /**
     * Starts the measuring
     */
    public void start(){
        //TODO: tone the length of an imaginary 0th update
        delta = MILLISECOND;
        started = true;
        last = System.nanoTime();
    }

    /**
     * Marks an update.
     * Calculates delta, updates last and upsTime values and calculates UPS if needed.
     */
    public void update(){
        //Update delta
        long now = System.nanoTime();
        delta = now - last;
        last = now;
        upsTime += delta;
        upsCounter++;

        //Update UPS
        if(upsTime >= SECOND){
            ups = upsCounter;
            upsTime %= SECOND;
            upsCounter = 0;
        }
    }

    /**
     * Returns the delta (in ns)
     *
     * @return The delta (in ns)
     */
    public long getDelta(){ return delta; }

    /**
     * Returns the delta (in ms)
     *
     * @return The delta (in ms)
     */
    public double getDeltaMillis(){ return (double) delta / MILLISECOND; }

    /**
     * Returns the delta (in s)
     *
     * @return The delta (in s)
     */
    public double getDeltaSeconds(){ return (double) delta / SECOND; }

    /**
     * Returns the ups (over the previous second)
     *
     * @return The ups
     */
    public int getUps(){ return ups; }

    /**
     * Returns whether the measuring has been started
     *
     * @return Whether the measuring has been started
     */
    public boolean isStarted(){ return started; }
}
