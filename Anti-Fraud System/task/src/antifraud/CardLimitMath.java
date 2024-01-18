package antifraud;

public class CardLimitMath {
    public static long increase(long currentLimit, long valueFromTransaction) {
        return (long) Math.ceil(0.8 * currentLimit + 0.2 * valueFromTransaction);

    }
    public static long decrease(long currentLimit, long valueFromTransaction) {
        return (long) Math.ceil(0.8 * currentLimit - 0.2 * valueFromTransaction);
    }
}