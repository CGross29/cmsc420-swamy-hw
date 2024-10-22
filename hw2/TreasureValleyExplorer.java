import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A convenient class that stores a pair of integers.
 * DO NOT MODIFY THIS CLASS.
 */

class IntPair {
    // Make the fields final to ensure they cannot be changed after initialization
    public final int first;
    public final int second;

    public IntPair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public String toString() {
        return "(" + first + "," + second + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IntPair other = (IntPair) obj;
        return first == other.first && second == other.second;
    }

    @Override
    public int hashCode() {
        return 31 * first + second;
    }
}

/**
 * TreasureValleyExplorer class operates on a landscape of Numerica,
 * selectively modifying the most and least valuable valleys of a specified
 * depth.
 * 
 * DO NOT MODIFY THE SIGNATURE OF THE METHODS PROVIDED IN THIS CLASS.
 * You are encouraged to add methods and variables in the class as needed.
 *
 * @author <Corey Gross>
 */
public class TreasureValleyExplorer {

    class Node {
        int height;
        int value;
        int depth;
        boolean isValley;
        Node next;
        Node previous;
        boolean isPeak;
    
        public Node(int height, int value) {
            this.height = height;
            this.value = value;
            this.depth = 0;
            this.isValley = false;
            this.next = null;
            this.previous = null;
            this.isPeak = false;
        }
    }

    // Create instance variables here.
    private Node first;
    private Node last;
    private Map<Integer, TreeMap<Integer, Node>> treeDepth;

    /**
     * Constructor to initialize the TreasureValleyExplorer with the given heights
     * and values
     * of points in Numerica.
     *
     * @param heights An array of distinct integers representing the heights of
     *                points in the landscape.
     * @param values  An array of distinct integers representing the treasure value
     *                of points in the landscape.
     */
    public TreasureValleyExplorer(int[] heights, int[] values) {
        this.treeDepth = new HashMap<>();
        int current = 0;
        int prevHeight = heights[0];

        for (int i = 0; i < heights.length; i++){
            if (current > 0 && heights[i] > prevHeight){
                current = 0;
            }
            if (heights[i] < prevHeight){
                current++;
            }

            prevHeight = heights[i];
            Node newNode = new Node(heights[i], values[i]);

            newNode.depth = current;
            if(this.first == null){
                
                this.first = newNode;
                this.last = newNode;

                if(i + 1  < heights.length && ((heights[i]) < heights[i + 1])){
                    newNode.isValley = true;
                    insertInTree(current, newNode);
                } else if (i + 1 >= heights.length){
                    newNode.isValley = true;
                    insertInTree(current, newNode);
                }
            } else {
                this.last.next = newNode;
                newNode.previous = last;
                this.last = newNode;

                if (i + 1 >= heights.length && heights[i] < heights[i - 1]) {
                    newNode.isValley = true;
                    insertInTree(current, newNode);
                }
                else if (i + 1 < heights.length && heights[i] < heights[i + 1] && heights[i] < heights[i - 1]){
                    newNode.isValley = true;
                    insertInTree(current, newNode);
                }
            }
        }
    }

    private void insertInTree (int depth, Node node){
        treeDepth.computeIfAbsent(depth, k -> new TreeMap<>()).put(node.value, node);
    }

    private void removeFromTree(int depth, Node node){
        TreeMap<Integer, Node> valley = treeDepth.get(depth);

        if(valley != null) {
            valley.remove(node.value);
            if (valley.isEmpty()){

                treeDepth.remove(depth);
            }
        }
    }

    private void recalculateDepth(Node node){
        if (node.previous != null && node.height < node.previous.height){
            node.depth = node.previous.depth + 1;
        } else {
            node.depth = 0;
        }
    }

    private void recalculatePeakValley(Node node){
        int prevHeight  = node.previous != null ? node.previous.height : Integer.MAX_VALUE;
        int nextHeight  = node.next != null ? node.next.height : Integer.MAX_VALUE;

        node.isPeak = (node.height > prevHeight && node.height > nextHeight);
        node.isValley = (node.height < prevHeight && node.height < nextHeight);
    }

    /**
     * Checks if the entire landscape is excavated (i.e., there are no points
     * left).
     *
     * @return true if the landscape is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.first == null;
    }

    /**
     * A method to insert a new landform prior to the most valuable valley of the
     * specified depth
     *
     * @param height The height of the new landform
     * @param value  The treasure value of the new landform
     * @param depth  The depth of the valley we wish to insert at
     *
     * @return true if the insertion is successful, false otherwise
     */

    private void insertBefore(Node targetValley, Node newNode){
        newNode.next = targetValley;
        newNode.previous = targetValley.previous;

        if (targetValley.previous != null) {
            targetValley.previous.next = newNode;
        }

        targetValley.previous = newNode;

        if (targetValley == this.first){
            this.first = newNode;
        }

        if (newNode.next == null) {
            this.last = newNode;
        }
    }

    private void updatedNodes(Node node, boolean insert){

        Node curr = node.previous;
        if(curr == null || curr.next == this.first){
            if (insert){
                curr = node;
            } else {
                curr = node.next;
            }
        }

        int count = 0;
        while (curr != null){
            boolean wasValley = curr.isValley;
            int elaDepth = curr.depth;
            recalculatePeakValley(curr);
            recalculateDepth(curr);

            if (wasValley){
                removeFromTree(elaDepth, curr);
            }
            if(curr.isValley){
                insertInTree(curr.depth, curr);
            }

            if (count > 1 && elaDepth == curr.depth){
                break;
            }

            curr = curr.next;
            count++;
        }
    }
    
    private Node findMostValuableValleyAtDepth(int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);
        if (valleys == null || valleys.isEmpty()) {
            return null;
        }
        return valleys.lastEntry().getValue();
    }

    public boolean insertAtMostValuableValley(int height, int value, int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);
        if (valleys == null || valleys.isEmpty()) {
            return false;
        }
        Node targetValley = valleys.lastEntry().getValue();
        Node newNode = new Node(height, value);

        insertBefore(targetValley, newNode);
        updatedNodes(newNode, true);

        return true;
    }

    /**
     * A method to insert a new landform prior to the least valuable valley of the
     * specified depth
     *
     * @param height The height of the new landform
     * @param value  The treasure value of the new landform
     * @param depth  The depth of the valley we wish to insert at
     *
     * @return true if the insertion is successful, false otherwise
     */
    public boolean insertAtLeastValuableValley(int height, int value, int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);
        if (valleys == null || valleys.isEmpty()){
            return false;
        }

        Node targetValley = valleys.firstEntry().getValue();
        Node newNode = new Node(height, value);

        if (targetValley == null) {
            return false;
        }

        insertBefore(targetValley, newNode);
        updatedNodes(newNode, true);

        return true;
    }

    /**
     * A method to remove the most valuable valley of the specified depth
     *
     * @param depth The depth of the valley we wish to remove
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the removed valley
     * @return null if no valleys of the specified depth exist
     */

     private void removeNode(Node removeNode) {
        if (removeNode.previous != null) {
            removeNode.previous.next = removeNode.next;
        } else {
            this.first = removeNode.next;
        }
        
        if (removeNode.next != null) {
            removeNode.next.previous = removeNode.previous;
        } else {
            this.last = removeNode.previous;
        }

        if(removeNode.isValley){
            removeFromTree(removeNode.depth, removeNode);
        }
    }
    
    public IntPair removeMostValuableValley(int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);

        if (valleys == null || valleys.isEmpty()){
            return null;
        }

        Node targetValley = valleys.lastEntry().getValue();

        IntPair retValue = new IntPair(targetValley.height, targetValley.value);
        removeNode(targetValley);

        updatedNodes(targetValley,false);
        return retValue;
    }

    /**
     * A method to remove the least valuable valley of the specified depth
     *
     * @param depth The depth of the valley we wish to remove
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the removed valley
     * @return null if no valleys of the specified depth exist
     */
    public IntPair removeLeastValuableValley(int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);

        if (valleys == null || valleys.isEmpty()){
            return null;
        }

        Node targetValley = valleys.firstEntry().getValue();
        IntPair retValue = new IntPair(targetValley.height, targetValley.value);

        removeNode(targetValley);
        updatedNodes(targetValley, false);

        return retValue;
    }

    /**
     * A method to get the treasure value of the most valuable valley of the
     * specified depth
     *
     * @param depth The depth of the valley we wish to find the treasure value of
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the found valley
     * @return null if no valleys of the specified depth exist
     */
    public IntPair getMostValuableValley(int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);
        if (valleys == null || valleys.isEmpty()){
            return null;
        }

        Node targetValley = valleys.lastEntry().getValue();
        return new IntPair(targetValley.height, targetValley.value);
    }

    /**
     * A method to get the treasure value of the least valuable valley of the
     * specified depth
     *
     * @param depth The depth of the valley we wish to find the treasure value of
     *
     * @return An IntPair where the first field is the height and the second field
     *         is the treasure value of the found valley
     * @return null if no valleys of the specified depth exist
     */
    public IntPair getLeastValuableValley(int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);
        if (valleys == null || valleys.isEmpty()) {
            return null;
        }

        Node targetValley = valleys.firstEntry().getValue();
        return new IntPair(targetValley.height, targetValley.value);
    }

    /**
     * A method to get the number of valleys of a given depth
     *
     * @param depth The depth that we want to count valleys for
     *
     * @return The number of valleys of the specified depth
     */
    public int getValleyCount(int depth) {
        TreeMap<Integer, Node> valleys = treeDepth.get(depth);
        if (valleys == null || valleys.isEmpty()){
            return 0;
        }
        return valleys.size();
    }
}