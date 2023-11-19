package de.team33.patterns.misc.alpha;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A functional implementation of a simple decision.
 * <p>
 * Typically used to implement multiple choices, e.g. based on an enum:
 * <pre>
 *
 * public enum Signum implements Function&lt;Integer, Integer&gt; {
 *
 *     POSITIVE(any -&gt; 1),
 *
 *     NON_ZERO(Choice.on(Condition.IS_POSITIVE).apply(POSITIVE).orReply(-1)),
 *
 *     ANY(Choice.on(Condition.IS_ZERO).reply(0).orApply(NON_ZERO));
 *
 *     private final Function&lt;Integer, Integer&gt; backing;
 *
 *     Signum(final Function&lt;Integer, Integer&gt; backing) {
 *         this.backing = backing;
 *     }
 *
 *     public static int map(final int input) {
 *         return ANY.apply(input);
 *     }
 *
 *     &#64;Override
 *     public Integer apply(final Integer input) {
 *         return backing.apply(input);
 *     }
 *
 *     interface Condition extends Predicate&lt;Integer&gt; {
 *
 *         Condition IS_ZERO = input -&gt; input == 0;
 *         Condition IS_POSITIVE = input -&gt; input &gt; 0;
 *     }
 * }
 * </pre>
 *
 * @param <P> The parameter type
 * @param <R> The result type
 */
public class Choice<P, R> implements Function<P, R> {

    private final Predicate<P> condition;
    private final Function<P, R> positive;
    private final Function<P, R> negative;

    private Choice(final Predicate<P> condition, final Function<P, R> positive, final Function<P, R> negative) {
        this.condition = condition;
        this.positive = positive;
        this.negative = negative;
    }

    /**
     * Starts the creation of a {@link Choice} giving a {@link Predicate condition}.
     */
    public static <P> Condition<P> on(final Predicate<P> condition) {
        return new Condition<P>(condition);
    }

    @Override
    public final R apply(final P parameter) {
        return (condition.test(parameter) ? positive : negative).apply(parameter);
    }

    /**
     * Represents the preliminary stage of a {@link Choice} that is only missing a negative result.
     */
    @FunctionalInterface
    public interface Consequence<P, R> {

        /**
         * Specifies a {@link Function} that will be applied when the associated {@link Predicate condition} is false
         * to a parameter.
         */
        Choice<P, R> orApply(Function<P, R> negative);

        /**
         * Specifies a fixed result that will be returned when the associated {@link Predicate condition} is false to
         * a parameter.
         */
        default Choice<P, R> orReply(final R negative) {
            return orApply(any -> negative);
        }
    }

    /**
     * Represents the start of the creation of a {@link Choice} on a given {@link Predicate condition}.
     */
    public static class Condition<P> {

        private final Predicate<P> condition;

        private Condition(final Predicate<P> condition) {
            this.condition = condition;
        }

        /**
         * Specifies a {@link Function} that will be applied when the associated {@link Predicate condition} is true to
         * a parameter.
         */
        public final <R> Consequence<P, R> apply(final Function<P, R> positive) {
            return negative -> new Choice<>(condition, positive, negative);
        }

        /**
         * Specifies a fixed result that will be returned when the associated {@link Predicate condition} is true to
         * a parameter.
         */
        public final <R> Consequence<P, R> reply(final R positive) {
            return apply(any -> positive);
        }
    }
}
