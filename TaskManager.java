import java.util.Scanner;
import java.util.Stack;

// Class to represent a Task with a name and description
class Task {
    String name;
    String description;

    // Constructor to initialize task
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

// Class to represent a node in the BST that holds a Task
class TaskNode {
    Task task; // Task stored in this node
    TaskNode left, right; // Left and right children in the BST

    // Constructor to create a new TaskNode
    public TaskNode(Task task) {
        this.task = task;
        left = right = null;
    }
}

// Binary Search Tree to organize tasks by their names
class TaskSearchTree {
    private TaskNode root; // Root of the BST

    // Add a task to the BST
    public void addTask(Task task) {
        root = addRecursive(root, task); // Recursively add task
    }

    // Helper method to recursively add tasks into the BST
    private TaskNode addRecursive(TaskNode current, Task task) {
        if (current == null) { // If the current node is null, insert the task here
            return new TaskNode(task);
        }
        // Compare the task names in lexicographical order and decide whether to go left or right
        if (task.name.compareToIgnoreCase(current.task.name) < 0) {
            current.left = addRecursive(current.left, task); // Go to the left subtree
        } else if (task.name.compareToIgnoreCase(current.task.name) > 0) {
            current.right = addRecursive(current.right, task); // Go to the right subtree
        }
        return current; // Return the current node
    }

    // Search for a task in the BST by name
    public Task searchTask(String name) {
        return searchRecursive(root, name); // Recursively search task by name
    }

    // Helper method to recursively search tasks in the BST
    private Task searchRecursive(TaskNode current, String name) {
        if (current == null) {
            return null;
        }
        if (current.task.name.equalsIgnoreCase(name)) {
            return current.task;
        }
        // Decide to continue search in left or right subtree
        return name.compareToIgnoreCase(current.task.name) < 0
                ? searchRecursive(current.left, name)
                : searchRecursive(current.right, name);
    }

    // Remove a task from the BST
    public void removeTask(String name) {
        root = removeRecursive(root, name); // Recursively remove task
    }

    // Helper method to recursively remove a task
    private TaskNode removeRecursive(TaskNode current, String name) {
        if (current == null) {
            return null;
        }
        // Found the node to delete
        if (name.equalsIgnoreCase(current.task.name)) {
            // If the node has no children
            if (current.left == null && current.right == null) {
                return null;
            }
            // If the node has one child (right or left)
            if (current.right == null) {
                return current.left;
            }
            if (current.left == null) {
                return current.right;
            }
            // If the node has two children, find the next higher node in the right subtree to replace the current node
            Task smallestTask = findSmallestTask(current.right);
            current.task = smallestTask; // Replace the current task with the smallest one (the next higher node, not the highest)
            current.right = removeRecursive(current.right, smallestTask.name); // Recursively remove the smallest task
            return current;
        }
        // Recursively search in the left or right subtree
        if (name.compareToIgnoreCase(current.task.name) < 0) {
            current.left = removeRecursive(current.left, name);
        } else {
            current.right = removeRecursive(current.right, name);
        }
        return current;
    }

    // Helper method to find the smallest task in the right subtree
    private Task findSmallestTask(TaskNode root) {
        // If the left child of current node is null then current node is the smallest node in that subtree
        // If the left child isn't null, search the smallest node on the left child, continue until there's no left child
        return root.left == null ? root.task : findSmallestTask(root.left);
    }
}

// Singly linked list to maintain task order
class TaskList {
    private class Node {
        Task task; // Task stored in this node
        Node next; // Pointer to the next node

        // Constructor to create a new node
        Node(Task task) {
            this.task = task;
            this.next = null; // Next pointer initialized to null
        }
    }

    private Node head; // Head of the linked list

    // Add a task to the end of the linked list
    public void addTask(String name, String description) {
        Task newTask = new Task(name, description);
        Node newNode = new Node(newTask); // Create a new node with the task
        if (head == null) {
            head = newNode; // If the list is empty, set the new node as head
        } else {
            Node current = head;
            while (current.next != null) { // Traverse to the last node
                current = current.next;
            }
            current.next = newNode; // Insert the new node at the end
        }
    }

    // Remove a task from the linked list
    public void removeTask(String name) {
        Node current = head;
        Node prev = null;
        while (current != null) {
            if (current.task.name.equalsIgnoreCase(name)) {
                if (prev != null) {
                    prev.next = current.next; // If the Task is Found and Not the Head, skip over the current node
                } else {
                    head = current.next; // If the head is removed, update the head to the next node
                }
                return;
            }
            prev = current; // Move to the next node
            current = current.next;
        }
    }

    // Display all tasks in the linked list
    public void displayTasks() {
        Node current = head;
        while (current != null) {
            System.out.println(current.task.name + ": " + current.task.description);
            current = current.next;
        }
    }
}

// Main TaskManager class to manage tasks
public class TaskManager {
    private TaskList taskList; // Singly linked list for task management
    private TaskSearchTree taskSearchTree; // Binary Search Tree for task searching
    private Stack<String> undoStack; // Stack to store undo actions
    private Stack<String> redoStack; // Stack to store redo actions

    // Constructor to initialize the TaskManager
    public TaskManager() {
        taskList = new TaskList();
        taskSearchTree = new TaskSearchTree();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    // Main loop to run the TaskManager
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Display current tasks
            System.out.println("\nCurrent Tasks:");
            taskList.displayTasks();

            System.out.println("\nMenu:");
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. Search Task");
            System.out.println("4. Undo");
            System.out.println("5. Redo");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            // Handle user choices
            switch (choice) {
                case 1: // Add Task
                    System.out.print("Enter task name: ");
                    String nameToAdd = scanner.nextLine();
                    System.out.print("Enter task description: ");
                    String descriptionToAdd = scanner.nextLine();
                    taskList.addTask(nameToAdd, descriptionToAdd); // Add task to linked list
                    taskSearchTree.addTask(new Task(nameToAdd, descriptionToAdd)); // Add task to BST
                    undoStack.push("remove:" + nameToAdd); // Push action to undo stack
                    redoStack.clear(); // Clear redo stack on new action
                    System.out.println("Task added successfully.");
                    break;

                case 2: // Remove Task
                    System.out.print("Enter task name to remove: ");
                    String nameToRemove = scanner.nextLine();
                    taskList.removeTask(nameToRemove); // Remove task from linked list
                    taskSearchTree.removeTask(nameToRemove); // Remove task from BST
                    undoStack.push("add:" + nameToRemove); // Push action to undo stack
                    redoStack.clear(); // Clear redo stack on new action
                    System.out.println("Task removed successfully.");
                    break;

                case 3: // Search Task
                    System.out.print("Enter task name to search: ");
                    String nameToSearch = scanner.nextLine();
                    Task foundTask = taskSearchTree.searchTask(nameToSearch); // Search task in BST
                    if (foundTask != null) {
                        System.out.println("Found Task: " + foundTask.name + ": " + foundTask.description);
                    } else {
                        System.out.println("Task not found.");
                    }
                    break;

                case 4: // Undo
                    if (!undoStack.isEmpty()) {
                        String action = undoStack.pop(); // Pop the last action from the undo stack e.g. "remove:Homework"
                        String[] parts = action.split(":"); // Split into two parts, parts[0] and parts[1]
                        String taskName = parts[1]; // e.g Homework
                        String taskDescription = parts.length > 2 ? parts[2] : "";
                        if (action.startsWith("remove:")) {
                            taskList.removeTask(taskName); // Remove the task from the list
                            redoStack.push("add:" + taskName + ":" + taskDescription); // Push the reverse action to redo stack
                            System.out.println("Undo: Task removed successfully.");
                        } else if (action.startsWith("add:")) { // If the original action was to remove the task
                            taskList.addTask(taskName, taskDescription); // Re-add the task
                            redoStack.push("remove:" + taskName); // Push reverse action to redo stack
                            System.out.println("Undo: Task added back successfully.");
                        }
                    } else {
                        System.out.println("No actions to undo.");
                    }

                    break;

                case 5: // Redo
                    if (!redoStack.isEmpty()) {
                        String action = redoStack.pop(); // Pop the last action from the redo stack e.g. "remove:Homework"
                        String[] parts = action.split(":"); // Split into two parts, parts[0] and parts[1]
                        String taskName = parts[1]; // e.g Homework

                        if (action.startsWith("remove:")) { // Redoing a task removal
                            // When "remove:" is redone, it means we need to remove the task again
                            Task taskToRedo = taskSearchTree.searchTask(taskName); // Find the task in the search tree
                            if (taskToRedo != null) {
                                taskList.removeTask(taskToRedo.name); // Remove the task again
                                undoStack.push("add:" + taskToRedo.name); // Push the reverse action (add) to the undo stack
                                System.out.println("Redo: Task removed successfully.");
                            } else {
                                System.out.println("Redo: Task not found, cannot remove.");
                            }
                        } else if (action.startsWith("add:")) { // Redoing a task addition
                            Task taskToRedo = taskSearchTree.searchTask(taskName); // Find the task in the search tree
                            if (taskToRedo != null) {
                                taskList.addTask(taskToRedo.name, taskToRedo.description); // Re-add the task with the description
                                undoStack.push("remove:" + taskToRedo.name); // Push the reverse action (remove) to the undo stack
                                System.out.println("Redo: Task added successfully.");
                            } else {
                                System.out.println("Redo: Task not found in the search tree.");
                            }
                        }
                    } else {
                        System.out.println("No actions to redo.");
                    }
                    break;

                case 6: // Exit
                    System.out.println("Exiting the program.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again."); // Handle invalid input
            }
        }
    }

    // Main method to start the TaskManager
    public static void main(String[] args) {
        TaskManager manager = new TaskManager(); // Create TaskManager instance
        manager.run(); // Run the task manager
    }
}

