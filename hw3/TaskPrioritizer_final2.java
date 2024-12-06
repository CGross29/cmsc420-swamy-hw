import java.util.ArrayList;
import java.util.List;

/**
 * TaskPrioritizer class that returns the most urgent
 * available task
 *
 * @author <Corey Gross>
 */
public class TaskPrioritizer {
    /**
     * Constructor to initialize the TaskPrioritizer
     */

    private class MyHashMap {
        private static final int INITIAL_CAPACITY = 65536;
        private Entry[] table;

        public MyHashMap() {
            table = new Entry[INITIAL_CAPACITY];
        }

        class Entry {
            String key;
            Task value;
            Entry next;

            Entry(String key, Task value, Entry next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        private int hash(String key) {
            int hashCode = 0;
            for (char c : key.toCharArray()) {
                hashCode = 31 * hashCode + c;
            }
            return Math.abs(hashCode) % table.length;
        }

        public void addTask(String key, Task value) {
            int index = hash(key);
            Entry current = table[index];
            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            table[index] = new Entry(key, value, table[index]);
        }

        public Task getTask(String key) {
            int index = hash(key);
            Entry curr = table[index];
            while (curr != null) {
                if (curr.key.equals(key)) {
                    return curr.value;
                }
                curr = curr.next;
            }
            return null;
        }

        public boolean hasTask(String key) {
            return getTask(key) != null;
        }
    }

    private class Task {
        String taskId;
        int urgencyLevel;
        List<Task> dependents;
        int order;
        boolean resolved;
        int unresolvedCount;
        int heapIndex;

        public Task(String taskId, int urgencyLevel, int order, String[] dependencies) {
            this.taskId = taskId;
            this.urgencyLevel = urgencyLevel;
            this.dependents = new ArrayList<>();
            this.order = order;
            this.resolved = false;
            this.heapIndex = -1;
            this.unresolvedCount = (dependencies == null) ? 0 : dependencies.length;
        }
    }

    private class MaxHeap {
        private Task[] heap;
        private int size;

        public MaxHeap(int capacity) {
            heap = new Task[capacity + 1];
            size = 0;
        }

        private int compare(Task a, Task b) {
            int urgencyComparison = Integer.compare(a.urgencyLevel, b.urgencyLevel);
            if (urgencyComparison != 0) {
                return urgencyComparison;
            }
            return Integer.compare(b.order, a.order);
        }

        private void swap(int i, int j) {
            Task temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
            heap[i].heapIndex = i;
            heap[j].heapIndex = j;
        }

        private void floatUp(int k) {
            while (k > 1 && compare(heap[k], heap[k / 2]) > 0) {
                swap(k, k / 2);
                k = k / 2;
            }
        }

        private void sinkDown(int n) {
            while (n * 2 <= size) {
                int child = n * 2;
                
                if (child + 1 <= size && compare(heap[child], heap[child + 1]) < 0) {
                    child++;
                }
                if (compare(heap[n], heap[child]) >= 0) {
                    break;
                }
                swap(n, child);
                n = child;
            }
        }

        public void addTask(Task task) {
            size++;
            heap[size] = task;
            task.heapIndex = size;
            floatUp(size);
        }

        public Task removeHighestPriority() {
            Task max = heap[1];
            swap(1, size);
            heap[size] = null;
            size--;
            sinkDown(1);
            max.heapIndex = -1;
            return max;
        }

        public void updatePriority(Task task) {
            int index = task.heapIndex;
            floatUp(index);
            sinkDown(index);
        }

        public boolean hasTasks() {
            return size == 0;
        }
    }



    private MyHashMap taskMap;
    private MaxHeap heap;
    private int addOrderCounter;

    public TaskPrioritizer() {
        taskMap = new MyHashMap();
        heap = new MaxHeap(500000);
        addOrderCounter = 0;
    }

    /**
     * A method to add a new task
     *
     * @param taskId       The string taskId of the task we want to add
     * @param urgencyLevel The integer urgencyLevel of the task we want to add
     * @param dependencies The array of taskIds of tasks the added task depends on
     */

     public void add(String taskId, int urgencyLevel, String[] dependencies) {
        if (taskMap.hasTask(taskId)) {
            return;
        }
    
        Task newTask = new Task(taskId, urgencyLevel, addOrderCounter++, dependencies);
        taskMap.addTask(taskId, newTask);
        if (dependencies != null) {
            for (String dependencyId : dependencies) {
                Task dependencyTask = taskMap.getTask(dependencyId);
                if (dependencyTask == null) {
                    dependencyTask = new Task(dependencyId, 0, -1, null);
                    taskMap.addTask(dependencyId, dependencyTask);
                }
                dependencyTask.dependents.add(newTask);
                if (dependencyTask.resolved) {
                    newTask.unresolvedCount--;
                }
            }
        }
        if (newTask.unresolvedCount == 0) {
            heap.addTask(newTask);
        }
    }

    /**
     * A method to change the urgency of a task
     *
     * @param taskId       The string taskId of the task we want to change the
     *                     urgency of
     * @param urgencyLevel The new integer urgencyLevel of the task
     */

    public void update(String taskId, int newUrgencyLevel) {
        Task task = taskMap.getTask(taskId);
        if (task == null || task.resolved) {
            return;
        }
        task.urgencyLevel = newUrgencyLevel;
        if (task.heapIndex > 0) {
            heap.updatePriority(task);
        }
    }

    /**
     * A method to resolve the greatest urgency task which has had all of its
     * dependencies satisfied
     *
     * @return The taskId of the resolved task
     * @return null if there are no unresolved tasks left
     */

    public String resolve() {
        if (heap.hasTasks()) {
            return null;
        }
    
        Task highestPriorityTask = heap.removeHighestPriority();
        highestPriorityTask.resolved = true;

        for (Task dependent : highestPriorityTask.dependents) {
            dependent.unresolvedCount--;

            if (dependent.unresolvedCount == 0) {
                heap.addTask(dependent);
            }
        }
    
        return highestPriorityTask.taskId;
    }
}