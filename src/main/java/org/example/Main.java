package org.example;

public class Main {

    public static void main(String[] args) {
        simulate();
    }

    public static void simulate() {
        ElevatorScheduler scheduler = new ElevatorScheduler();

        // tasks created at different times
        scheduler.addTask(new Task(2, 6, System.currentTimeMillis()));
        scheduler.addTask(new Task(8, 4, System.currentTimeMillis()));
        scheduler.addTask(new Task(3, 1, System.currentTimeMillis()));

        // Run simulation for 30 ticks (1 tick = 1 sec, 1m/s, 3m per floor)
        for (int i = 0; i < 30; i++) {
            scheduler.processTick();
            try {
                Thread.sleep(1000); // simulate 1 second per tick
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

