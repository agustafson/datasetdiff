package datasetdiff;

/**
 * Function which can compare 2 values.
 *
 * @param <T> The type to be compared
 * @author agustafson
 */
public interface ComparatorFunction<T> {
    /**
     * Compare 2 values and return true if the 2 values can be compared equal.
     *
     * @param left  The left value to compare
     * @param right The right value to compare
     * @return true if the 2 values can be compared equal
     */
    boolean compare(T left, T right);
}
