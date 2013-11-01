package eccount.util;

/**
 * @author: prayagupd
 */
public class AmountUtils {
    public static long getLong(Object value) {
        if (value instanceof Double)
            return (long) ((Double) value).doubleValue();
        if(value instanceof Integer){
            return (Integer)value;
        }
        return (Long) value;
    }
    public static double getAmount(Long amount){
        return getAmount((double) amount);
    }
    public static double getAmount(double amount){
        if(amount==Double.MIN_VALUE)return 0.0d;
        return amount/100.0d;
    }

    public static double getInt(int value){
        if(value==Integer.MIN_VALUE)return 0;
        return value;
    }

}

