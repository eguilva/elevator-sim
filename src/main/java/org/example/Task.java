package org.example;

// Represents a single elevator task (pickup/drop-off request)
public class Task {
    enum Direction {UP, DOWN}
    private final int origin;
    private final int destination;
    private final Direction direction;
    private final long timestamp;

    public Task(int origin, int destination, long timestamp) {
        this.origin = origin;
        this.destination = destination;
        this.timestamp = timestamp;
        this.direction = (destination > origin) ? Direction.UP : Direction.DOWN;
    }

    public int getOrigin() {return origin; }
    public int getDestination() {return destination; }
    public Direction getDirection() {return direction; }
    public long getTimestamp() {return timestamp; }

    @Override
    public String toString() {
        return "Task{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", direction=" + direction +
                ", timestamp=" + timestamp +
                '}';
    }

}
