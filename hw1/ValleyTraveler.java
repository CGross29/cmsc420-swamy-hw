/**
 * ValleyTraveler class represents a magical map that can identify and modify
 * valley points in the landscape of Numerica.
 * 
 * @author <Your Name goes here>
 */
public class ValleyTraveler {

    public class Node {
        int value;
        Node next;
        Node previous;

        public Node(int value) {
            this.value = value;
            this.next = null;
            this.previous = null;
        }
    }
    
    private Node headNode;
    private Node tailNode;
    private Node currentNode;

    private boolean valleyFound;
   
    public ValleyTraveler(int[] landscape) {
        if (landscape == null) {
            return;
        }

        for (int i = 0; i < landscape.length; i++) {
            Node newNode = new Node(landscape[i]);
            if (headNode == null) {
                headNode = newNode;
                tailNode = newNode;
                
                if (currentNode == null && i + 1 < landscape.length && 
                landscape[i] < landscape[i + 1]) {
                    currentNode = newNode;
                    valleyFound = true;
                }
            } else { 
                tailNode.next = newNode;
                newNode.previous = tailNode;
                tailNode = newNode;

                if (currentNode == null && i + 1 < landscape.length && 
                landscape[i] < landscape[i + 1] && landscape[i] < landscape[i - 1]) {
                    currentNode = newNode;
                    valleyFound = true;
                } else if (currentNode == tailNode && 
                i + 1 >= landscape.length && landscape[i] < landscape[i - 1]) {
                    currentNode = newNode;
                    valleyFound = true;
                }
            }
        }
    }

    /**
     * Checks if the entire landscape is excavated (i.e., there are no landforms
     * left).
     * 
     * @return true if the landscape is empty, false otherwise.
     */
    public boolean isEmpty() {
        return headNode == null; 
    }

    /**
     * Locates the first valley point in the landscape of Numerica.
     * 
     * @return The first valley point in the landscape.
     */
    public int getFirst() {
        if (headNode == null){
            return -1;
        }

        Node tempNode;
        if (valleyFound && currentNode != null) {
            return currentNode.value;
        } else if (!valleyFound && currentNode != null) {
            tempNode = currentNode;
        } else {
            tempNode = headNode;
        }

        while (tempNode != null) {
            boolean singleElement = tempNode.previous == null && tempNode.next == null;
            boolean firstElementIsValley = tempNode.previous == null && tempNode.next != null && tempNode.value < tempNode.next.value; 
            boolean middleElementIsValley = tempNode.previous != null && tempNode.next != null && tempNode.value < tempNode.next.value && tempNode.value < tempNode.previous.value;
            boolean lastElementIsValley = tempNode.previous != null && tempNode.next == null && tempNode.value < tempNode.previous.value;

            if (singleElement || firstElementIsValley || middleElementIsValley || lastElementIsValley) {
                currentNode = tempNode;
                valleyFound = true;
                return tempNode.value;
            }
            tempNode = tempNode.next;
        }
        return -1;
    }

    /**
     * Excavates the first valley point, removing it from the landscape of Numerica.
     * 
     * @return The excavated valley point.
     */
    public int remove() {
       if(isEmpty()){
        return -1;
    }

    int firstValley = getFirst();
    if(currentNode == null) {
        return -1;
    }

    Node previousNode = currentNode.previous;
    if (currentNode.previous == null) {
        headNode = currentNode.next;
    } else {
        currentNode.previous.next = currentNode.next;
    }
    if (currentNode.next == null) {
        tailNode = currentNode.previous;
    } else {
        currentNode.next.previous = currentNode.previous;
    }

    int removedValue = currentNode.value;
    currentNode.previous = null;
    currentNode.next = null;
    currentNode = previousNode;
    valleyFound = false;
    return removedValue;
}


    /**
     * Creates a new landform at the first valley point.
     * 
     * @param num The height of the new landform.
     */ 
    public void insert(int height) {
        Node newNode = new Node(height);
        if (isEmpty()) {
            headNode = newNode;
            tailNode = newNode;
            return;
        }

        getFirst();
        if(currentNode == null) {
            return;
        }

        newNode.next = currentNode;
        newNode.previous = currentNode.previous;

        boolean isFirstNode = currentNode == headNode;
        boolean isInternalNode = currentNode.previous != null;
        boolean isLastNode = currentNode == tailNode;

        if (isInternalNode) {
            currentNode.previous.next = newNode;
        }

        currentNode.previous = newNode;
        if (isFirstNode) {
            headNode = newNode;
        }
        if (isLastNode) {
            tailNode = newNode;
        }
        valleyFound = false;
        currentNode = newNode.previous;
    }
}