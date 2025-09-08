package org.example;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// Represents a single elevator task (pickup/drop-off request)
public class Task {
    enum Direction {UP, DOWN}
    private final int origin;
    private final int destination;
    private final Direction direction;
    private final long createdTimestamp;

    private Long pickupTimestamp = null;
    private Long dropOffTimestamp = null;
    private Long completedTimestamp = null;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public Task(int origin, int destination, long timestamp) {
        this.origin = origin;
        this.destination = destination;
        this.createdTimestamp = timestamp;
        this.direction = (destination > origin) ? Direction.UP : Direction.DOWN;
    }

    public int getOrigin() { return origin; }
    public int getDestination() { return destination; }
    public Long getPickupTimestamp() {return pickupTimestamp; }
    public Direction getDirection() { return direction; }

    public void markPickedUp() {
        if (pickupTimestamp == null) {
            pickupTimestamp = System.currentTimeMillis();
        }
    }

    public void markDroppedOff() {
        if (dropOffTimestamp == null) {
            dropOffTimestamp = System.currentTimeMillis();
        }
    }

    public void markCompleted() {
        if (completedTimestamp == null) {
            completedTimestamp = System.currentTimeMillis();
        }
    }

    private String formatTime(Long ts) {
        return ts == null ? "-" : FORMATTER.format(Instant.ofEpochMilli(ts));
    }

    @Override
    public String toString() {
        return "Task{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", direction=" + direction +
                ", created=" + formatTime(createdTimestamp) +
                ", pickedUp=" + formatTime(pickupTimestamp) +
                ", droppedOff=" + formatTime(dropOffTimestamp) +
                ", completed=" + formatTime(completedTimestamp) +
                '}';
    }
}



