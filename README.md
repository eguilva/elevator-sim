# Pseudocode

### CLASS Task
    origin: int 
    destination: int 
    direction: enum {UP, DOWN} 
    timestamp: int   # task creation time

### CLASS Scheduler
    up_heap = MinHeap()   # tasks going UP; sorted by origin level
    down_heap = MaxHeap()   # tasks going DOWN; sorted by origin level
    fifo_list = LinkedList()   # preserves "first-come-first-served" order
    active_task = null
    active_direction = null
    current_level = 1
    
    addTask(task)
        fifo_list.append(task)
        IF task.direction == UP
            # Add task to "up" heap
            up_heap.push(task.origin, task)
        ELSE
            # Add task to "down" heap
            down_heap.push(task.origin, task)

    select_next_task()
        # Case 1: No active task
        IF active_task == null
            IF fifo_list.is_empty()
                RETURN null
            active_task = fifo_list.pop_front()
            active_direction = active_task.direction
            RETURN active_task

    process_movement()
        # "Runs" every 1 second, handling movement, pickups and drop-offs
        IF active_task == null
            RETURN # idle

        MOVE elevator toward active_task.destination (1 level per second)

        current_level = updated elevator position

        # Drop off passengers that have requested this level
        drop_off_all(current_level)

        # Pick up waiting passenders whose origin matches this level
        pick_up_all(current_level)

        # Check if active destination reached
        IF current_level == active_task.destination
            mark_completed(active_task)
            active_task = null

            # If no more tasks in this direction, reset direction
            IF active_direction == UP AND up_heap.is_empty()
                active_direction == null
            IF active_direction == DOWN AND down_heap.is_empty()
                active_direction == null

            select_next_task()

    promote_to_active_batch(task)
        # Adds task to the group of currently served passenders
        active_batch.add(task)
        fifo_list.remove(task)

    drop_off_all(level)
        FOR task IN active_batch
            IF task.destination == level
                mark_completed(task)
                active_batch.remove(task)

    pick_up_all(level)
        # Handles new tasks that were queued with this level as origin
        FOR task IN fifo_list
            IF task.origin == level AND task.direction == active_direction
                promote_to_active_batch

    