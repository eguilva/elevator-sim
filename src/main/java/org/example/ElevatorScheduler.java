package org.example;

import java.util.*;

public class ElevatorScheduler {
    private final PriorityQueue<Task> upHeap;
    private final PriorityQueue<Task> downHeap;
    private final LinkedList<Task> fifoList;

    private Task activeTask;
    private Task.Direction activeDirection;
    private final List<Task> activeBatch;
    private int currentLevel;

    public ElevatorScheduler() {
        this.upHeap = new PriorityQueue<>(Comparator.comparingInt(Task::getOrigin));
        this.downHeap = new PriorityQueue<>((a,b) -> Integer.compare(b.getOrigin(), a.getOrigin()));
        this.fifoList = new LinkedList<>();
        this.activeBatch = new ArrayList<>();
        this.currentLevel = 1;
    }

    public void addTask(Task task){
        fifoList.add(task);
        if (task.getDirection() == Task.Direction.UP) {
            upHeap.add(task);
        } else {
            downHeap.add(task);
        }
    }

    public void processTick() {
        if (activeTask == null) {
            selectNextTask();
            if (activeTask == null) {
                System.out.println("Elevator idle at level " + currentLevel);
                return;
            }
        }

        // Move one level toward destination
        if (currentLevel < activeTask.getDestination()) {
            currentLevel++;
        } else if (currentLevel > activeTask.getDestination()) {
            currentLevel--;
        }

        System.out.println("Elevator at level " + currentLevel);

        // Drop off
        dropOffAll(currentLevel);
        // Pick up
        pickUpAll(currentLevel);

        // Check if active task is finished
        if (currentLevel == activeTask.getDestination()) {
            completeTask(activeTask);
            activeTask = null;
            if (activeDirection == Task.Direction.UP && upHeap.isEmpty()) {
                activeDirection = null;
            }
            if (activeDirection == Task.Direction.DOWN && downHeap.isEmpty()) {
                activeDirection = null;
            }
            selectNextTask();
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
            while (!upHeap.isEmpty() && upHeap.peek().getOrigin() >= currentLevel) {
                Task t = upHeap.poll();
                promoteToActiveBatch(t);
            }
        } else if (activeDirection == Task.Direction.DOWN) {
            while (!downHeap.isEmpty() && downHeap.peek().getOrigin() <= currentLevel) {
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
        Iterator<Task> i = activeBatch.iterator();
        while (i.hasNext()) {
            Task t = i.next();
            if (t.getDestination() == level) {
                System.out.println("Dropped off: " + t);
                i.remove();
            }
        }
    }

    private void pickUpAll(int level) {
        Iterator<Task> i = fifoList.iterator();
        while (i.hasNext()) {
            Task t = i.next();
            if (t.getOrigin() == level && t.getDirection() == activeDirection) {
                System.out.println("Picked up: " + t);
                promoteToActiveBatch(t);
                i.remove();
            }
        }
    }

    private void completeTask(Task task) {
        System.out.println("Completed active task: " + task);
    }

}
