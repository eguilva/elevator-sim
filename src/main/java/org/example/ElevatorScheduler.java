package org.example;

import java.util.*;

public class ElevatorScheduler {
    private final PriorityQueue<Task> upHeap;
    private final PriorityQueue<Task> downHeap;
    private final LinkedList<Task> fifoList;

    private Task activeTask;
    private Task.Direction activeDirection;
    private final List<Task> activeBatch;

    private int currentPositionMeters; // elevator position in meters

    public ElevatorScheduler() {
        this.upHeap = new PriorityQueue<>(Comparator.comparingInt(Task::getOrigin));
        this.downHeap = new PriorityQueue<>((a, b) -> Integer.compare(b.getOrigin(), a.getOrigin()));
        this.fifoList = new LinkedList<>();
        this.activeBatch = new ArrayList<>();
        this.currentPositionMeters = 0; // start at level 1 (0m–2m)
    }

    public void addTask(Task task) {
        fifoList.add(task);
        if (task.getDirection() == Task.Direction.UP) {
            upHeap.add(task);
        } else {
            downHeap.add(task);
        }
    }

    private int getCurrentLevel() {
        return currentPositionMeters / 3 + 1;
    }

    public void processTick() {
        if (activeTask == null) {
            selectNextTask();
            if (activeTask == null) {
                System.out.println("Elevator idle at level " + getCurrentLevel());
                return;
            }
        }

        // Mark pickup if at the origin floor
        if (activeTask.getOrigin() == getCurrentLevel() && activeTask.getPickupTimestamp() == null) {
            activeTask.markPickedUp();
        }

        int targetLevel = activeTask.getDestination();
        int targetPosition = (targetLevel - 1) * 3;

        // move 1 meter per tick
        if (currentPositionMeters < targetPosition) {
            currentPositionMeters++;
        } else if (currentPositionMeters > targetPosition) {
            currentPositionMeters--;
        }

        // only log at exact floor boundaries
        if (currentPositionMeters % 3 == 0) {
            System.out.println("Elevator at level " + getCurrentLevel());

            dropOffAll(getCurrentLevel());
            pickUpAll(getCurrentLevel()); // keep for other waiting tasks
        }
    }


    private void selectNextTask() {
        if (activeTask == null) {
            if (fifoList.isEmpty()) return;
            activeTask = fifoList.pollFirst();
            activeDirection = activeTask.getDirection();
            promoteToActiveBatch(activeTask);
        }

        if (activeDirection == Task.Direction.UP) {
            while (!upHeap.isEmpty() && upHeap.peek().getOrigin() >= getCurrentLevel()) {
                Task t = upHeap.poll();
                promoteToActiveBatch(t);
            }
        } else if (activeDirection == Task.Direction.DOWN) {
            while (!downHeap.isEmpty() && downHeap.peek().getOrigin() <= getCurrentLevel()) {
                Task t = downHeap.poll();
                promoteToActiveBatch(t);
            }
        }
    }

    private void promoteToActiveBatch(Task task) {
        if (!activeBatch.contains(task)) {
            activeBatch.add(task);
            fifoList.remove(task);
        }
    }

    private void dropOffAll(int level) {
        List<Task> toRemove = new ArrayList<>();

        for (Task t : activeBatch) {
            if (t.getDestination() == level) {
                t.markDroppedOff();

                // If this is the active task, mark completed
                if (t == activeTask) {
                    t.markCompleted();
                    System.out.println("Dropped off & Completed: " + t);
                    activeTask = null;
                } else {
                    System.out.println("Dropped off: " + t);
                }

                toRemove.add(t);
            }
        }

        // Remove after iteration
        activeBatch.removeAll(toRemove);

        // Only promote next task after finishing current drop-offs
        if (activeTask == null) {
            if (activeDirection == Task.Direction.UP && upHeap.isEmpty()) {
                activeDirection = null;
            }
            if (activeDirection == Task.Direction.DOWN && downHeap.isEmpty()) {
                activeDirection = null;
            }
            selectNextTask();
        }
    }


    private void pickUpAll(int level) {
        Iterator<Task> i = fifoList.iterator();
        while (i.hasNext()) {
            Task t = i.next();
            if (t.getOrigin() == level && t.getDirection() == activeDirection) {
                t.markPickedUp();
                promoteToActiveBatch(t);
                i.remove();
            }
        }
    }
}


