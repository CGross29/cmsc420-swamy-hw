import java.util.HashMap;
import java.util.Map;

/**
* Dictionary class that stores words and associates them with their definitions
*/
public class Dictionary {
    /**
    * Constructor to initialize the Dictionary
    */
    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean isWord = false;
        String definition = null;
    }

    private final Node root;

    public Dictionary() {
        root = new Node();
    }

    /**
    * A method to add a new word to the dictionary
    * If the word is already in the dictionary, this method will change its
    * definition
    *
    * @param word       The word we want to add to our dictionary
    * @param definition The definition we want to associate with the word
    */
    public void add(String word, String definition) {
        if (word == null || word.isEmpty()) return;

        Node current = root;
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new Node());
            current = current.children.get(c);
        }
        current.isWord = true;
        current.definition = definition;
    }

    /**
    * A method to remove a word from the dictionary
    *
    * @param word The word we want to remove from our dictionary
    */
    public void remove(String word) {
        removeHelper(root, word, 0);
    }

    private boolean removeHelper(Node node, String word, int depth) {
        if (node == null) return false;

        if (depth == word.length()) {
            if (!node.isWord) return false;
            node.isWord = false;
            node.definition = null;
            return node.children.isEmpty();
        }

        char c = word.charAt(depth);
        Node child = node.children.get(c);
        if (removeHelper(child, word, depth + 1)) {
            node.children.remove(c);
            return !node.isWord && node.children.isEmpty();
        }

        return false;
    }

    /**
    * A method to get the definition associated with a word from the dictionary
    * Returns null if the word is not in the dictionary
    *
    * @param word The word we want to get the definition for
    * @return The definition of the word, or null if not found
    */
    public String getDefinition(String word) {
        Node node = findNode(word);
        return (node != null && node.isWord) ? node.definition : null;
    }

    /**
    * A method to get a string representation of the sequence of nodes which would
    * store the word
    * in a compressed trie consisting of all words in the dictionary
    * Returns null if the word is not in the dictionary
    *
    * @param word The word we want the sequence for
    * @return The sequence representation, or null if word not found
    */
    public String getSequence(String word) {
        if (word == null || word.isEmpty()){
            return null;
        }

        Node current = root;
        StringBuilder sequence = new StringBuilder();
        boolean foundPrefix = false;

        for (char c : word.toCharArray()) {
            if (!current.children.containsKey(c)){
                return null;
            }
            current = current.children.get(c);
            sequence.append(c);
            if (current.isWord) {
                sequence.append('-');
                foundPrefix = true;
            }
        }

        if (!foundPrefix){
            return word;
        }
        if (sequence.charAt(sequence.length() - 1) == '-') {
            sequence.setLength(sequence.length() - 1);
        }
        return sequence.toString();
    }

    /**
    * Gives the number of words in the dictionary with the given prefix
    *
    * @param prefix The prefix we want to count words for
    * @return The number of words that start with the prefix
    */
    public int countPrefix(String prefix) {
        Node node = findNode(prefix);
        return (node != null) ? countWords(node) : 0;
    }

    private int countWords(Node node) {
        int count = node.isWord ? 1 : 0;
        for (Node child : node.children.values()) {
            count += countWords(child);
        }
        return count;
    }

    /**
    * Compresses the trie by combining nodes with single children
    * This operation should not change the behavior of any other methods
    */
    public void compress() {
        compressHelper(root);
    }

    private void compressHelper(Node node) {
        if (node == null){
            return;
        }

        for (char c : node.children.keySet()) {
            Node child = node.children.get(c);
            compressHelper(child);

            if (child.children.size() == 1 && !child.isWord) {
                char grandchildKey = child.children.keySet().iterator().next();
                Node grandchild = child.children.get(grandchildKey);

                Node merged = new Node();

                merged.children.putAll(grandchild.children);
                merged.isWord = grandchild.isWord;
                merged.definition = grandchild.definition;

                node.children.put(c, merged);
            }
        }
    }

    private Node findNode(String prefix) {
        Node current = root;
        for (char c : prefix.toCharArray()) {
            current = current.children.get(c);
            if (current == null) return null;
        }
        return current;
    }
}