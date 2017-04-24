package net.katsuster.strview.util;

/**
 * <p>
 * 数値、位置、長さを格納するクラスの基底クラス。
 * </p>
 *
 * @author katsuhiro
 */
public abstract class AbstractNum
        implements Num, Cloneable {
    //リストが存在する範囲
    private Range r;

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
    };

    /**
     * <p>
     * 数値の長さ（ビット単位）と、
     * 数値が存在する位置（ビット単位）を 0 に設定した、
     * 新たな数値を構築します。
     * </p>
     */
    public AbstractNum() {
        r = new SimpleRange(null, 0, 0);
    }

    /**
     * <p>
     * 数値の長さ（ビット単位）と、
     * 数値が存在する位置（ビット単位）を設定して、
     * 新たな数値を構築します。
     * </p>
     *
     * @param p 数値の存在する位置（ビット単位）
     * @param l 数値の長さ（ビット単位）
     */
    public AbstractNum(long p, int l) {
        r = new SimpleRange(p, l);
    }

    /**
     * <p>
     * 新たな数値を構築します。
     * </p>
     *
     * @param obj 数値
     */
    public AbstractNum(AbstractNum obj) {
        r = new SimpleRange(obj.r);
    }

    @Override
    public Object clone()
            throws CloneNotSupportedException {
        AbstractNum obj = (AbstractNum)super.clone();

        obj.r = (Range)r.clone();

        return obj;
    }

    @Override
    public Range getRange() {
        return r;
    }

    @Override
    public void setRange(Range range) {
        r = range;
    }

    @Override
    public abstract byte byteValue();

    @Override
    public abstract short shortValue();

    @Override
    public abstract int intValue();

    @Override
    public abstract long longValue();

    @Override
    public abstract float floatValue();

    @Override
    public abstract double doubleValue();

    @Override
    public abstract long getBitsValue();

    /**
     * <p>
     * 数値を 8ビットの符号無し整数とみなして、文字列に変換します。
     * </p>
     *
     * @param v 数値
     * @return 数値の文字列表記
     */
    protected static String uint8ToString(byte v) {
        char[] buf = new char[3];
        int n, i = buf.length - 1;

        if ((v & 0x80) != 0) {
            v = (byte)(v & ~0x80);
            n = (int)(v % 10) + 8;
            v = (byte)(v / 10 + 12);
            if (n >= 10) {
                n -= 10;
                v += 1;
            }
        } else {
            n = (int)(v % 10);
            v = (byte)(v / 10);
        }
        buf[i--] = DIGITS[n];

        while (v != 0) {
            n = (int)(v % 10);
            v = (byte)(v / 10);
            buf[i--] = DIGITS[n];
        }

        return new String(buf, i + 1, buf.length - i - 1);
    }

    /**
     * <p>
     * 数値を 16ビットの符号無し整数とみなして、文字列に変換します。
     * </p>
     *
     * @param v 数値
     * @return 数値の文字列表記
     */
    protected static String uint16ToString(short v) {
        char[] buf = new char[5];
        int n, i = buf.length - 1;

        if ((v & 0x8000) != 0) {
            v = (short)(v & ~0x8000);
            n = (int)(v % 10) + 8;
            v = (short)(v / 10 + 3276);
            if (n >= 10) {
                n -= 10;
                v += 1;
            }
        } else {
            n = (int)(v % 10);
            v = (short)(v / 10);
        }
        buf[i--] = DIGITS[n];

        while (v != 0) {
            n = (int)(v % 10);
            v = (short)(v / 10);
            buf[i--] = DIGITS[n];
        }

        return new String(buf, i + 1, buf.length - i - 1);
    }

    /**
     * <p>
     * 数値を 32ビットの符号無し整数とみなして、文字列に変換します。
     * </p>
     *
     * @param v 数値
     * @return 数値の文字列表記
     */
    protected static String uint32ToString(int v) {
        char[] buf = new char[10];
        int n, i = buf.length - 1;

        if ((v & 0x80000000) != 0) {
            v = (int)(v & ~0x80000000);
            n = (int)(v % 10) + 8;
            v = (int)(v / 10 + 214748364);
            if (n >= 10) {
                n -= 10;
                v += 1;
            }
        } else {
            n = (int)(v % 10);
            v = (int)(v / 10);
        }
        buf[i--] = DIGITS[n];

        while (v != 0) {
            n = (int)(v % 10);
            v = (int)(v / 10);
            buf[i--] = DIGITS[n];
        }

        return new String(buf, i + 1, buf.length - i - 1);
    }

    /**
     * <p>
     * 数値を 64ビットの符号無し整数とみなして、文字列に変換します。
     * </p>
     *
     * @param v 数値
     * @return 数値の文字列表記
     */
    protected static String uint64ToString(long v) {
        char[] buf = new char[20];
        int n, i = buf.length - 1;

        if ((v & 0x8000000000000000L) != 0) {
            v = (long)(v & ~0x8000000000000000L);
            n = (int)(v % 10) + 8;
            v = (long)(v / 10 + 922337203685477580L);
            if (n >= 10) {
                n -= 10;
                v += 1;
            }
        } else {
            n = (int)(v % 10);
            v = (long)(v / 10);
        }
        buf[i--] = DIGITS[n];

        while (v != 0) {
            n = (int)(v % 10);
            v = (long)(v / 10);
            buf[i--] = DIGITS[n];
        }

        return new String(buf, i + 1, buf.length - i - 1);
    }

    /**
     * <p>
     * 数値を 8ビットの浮動小数点の小数点部とみなして、文字列に変換します。
     * </p>
     *
     * @param n 数値
     * @return 数値の文字列表記
     */
    protected static String fraction8ToString(int n) {
        long dec[] = {
                390625L,
                781250L,
                1562500L,
                3125000L,
                6250000L,
                12500000L,
                25000000L,
                50000000L,
        };
        long result;
        int dig;

        result = 0;
        for (dig = 0; dig < dec.length; dig++) {
            if (((n >> dig) & 1) == 1) {
                result += dec[dig];
            }
        }
        while ((result != 0) && (result % 1000 == 0)) {
            result /= 1000;
        }
        while ((result != 0) && (result % 10 == 0)) {
            result /= 10;
        }

        return Long.toString(result);
    }

    /**
     * <p>
     * 数値を 16ビットの浮動小数点の小数点部とみなして、文字列に変換します。
     * </p>
     *
     * @param n 数値
     * @return 数値の文字列表記
     */
    protected static String fraction16ToString(int n) {
        long dec[] = {
                152587890625L,
                305175781250L,
                610351562500L,
                1220703125000L,
                2441406250000L,
                4882812500000L,
                9765625000000L,
                19531250000000L,
                39062500000000L,
                78125000000000L,
                156250000000000L,
                312500000000000L,
                625000000000000L,
                1250000000000000L,
                2500000000000000L,
                5000000000000000L,
        };
        long result;
        int dig;

        result = 0;
        for (dig = 0; dig < dec.length; dig++) {
            if (((n >> dig) & 1) == 1) {
                result += dec[dig];
            }
        }
        while ((result != 0) && (result % 10000000 == 0)) {
            result /= 10000000;
        }
        while ((result != 0) && (result % 1000 == 0)) {
            result /= 1000;
        }
        while ((result != 0) && (result % 10 == 0)) {
            result /= 10;
        }

        return Long.toString(result);
    }
}
