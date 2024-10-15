import java.util.*;
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

    // Create instance variables here.
    private Map<Integer, TreeSet<IntPair>> valleysByDepth;

    private final Comparator<IntPair> valleyComparator = Comparator
        .comparingInt((IntPair p) -> p.second)
        .thenComparingInt(p -> p.first);

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
        if (heights == null || values == null) {
            throw new IllegalArgumentException("null values");
        }
        if (heights.length != values.length) {
            throw new IllegalArgumentException("heights and values arrays are not equal");
        }

        this.valleysByDepth = new HashMap<>();
        int currDepth = 0;
        int lastHeight = heights[0];

        for (int i = 0; i < heights.length; i++) {
            if (currDepth > 0 && heights[i] > lastHeight) {
                currDepth = 0;  
            }
            if (heights[i] < lastHeight) {
                currDepth++;  
            }

            IntPair newValley = new IntPair(heights[i], values[i]);
            TreeSet<IntPair> valleys = valleysByDepth.computeIfAbsent(currDepth, k -> new TreeSet<>(valleyComparator));
            valleys.add(newValley);

            lastHeight = heights[i];
        }
    }

    /**
     * Checks if the entire landscape is excavated (i.e., there are no points
     * left).
     *
     * @return true if the landscape is empty, false otherwise.
     */
    public boolean isEmpty() {
        return valleysByDepth.isEmpty();
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
    public boolean insertAtMostValuableValley(int height, int value, int depth) {
        TreeSet<IntPair> valleys = valleysByDepth.computeIfAbsent(depth, k -> new TreeSet<>(valleyComparator));
        return valleys.add(new IntPair(height, value));
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
        return insertAtMostValuableValley(height, value, depth);
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
    public IntPair removeMostValuableValley(int depth) {
        TreeSet<IntPair> valleys = valleysByDepth.get(depth);
        if (valleys == null || valleys.isEmpty()) {
            return null;
        }
        IntPair mostValuable = valleys.last(); 
        valleys.remove(mostValuable);
        if (valleys.isEmpty()) {
            valleysByDepth.remove(depth); 
        }
        return mostValuable;
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
        TreeSet<IntPair> valleys = valleysByDepth.get(depth);
        if (valleys == null || valleys.isEmpty()) {
            return null; 
        }
        IntPair leastValuable = valleys.first(); 
        valleys.remove(leastValuable);
        if (valleys.isEmpty()) {
            valleysByDepth.remove(depth);
        }
        return leastValuable;
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
        TreeSet<IntPair> valleys = valleysByDepth.get(depth);
        if (valleys == null || valleys.isEmpty()) {
            return null; 
        }
        return valleys.last(); 
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
        TreeSet<IntPair> valleys = valleysByDepth.get(depth);
        if (valleys == null || valleys.isEmpty()) {
            return null; // No valleys at the specified depth
        }
        return valleys.first(); 
    }

    /**
     * A method to get the number of valleys of a given depth
     *
     * @param depth The depth that we want to count valleys for
     *
     * @return The number of valleys of the specified depth
     */
    public int getValleyCount(int depth) {
        TreeSet<IntPair> valleys = valleysByDepth.get(depth);
        return valleys == null ? 0 : valleys.size();
    }
}