package net.katsuster.strview.util;

/**
 * <p>
 * 64bit の符号あり整数
 * </p>
 *
 * @author katsuhiro
 */
public class SInt extends BaseInt
        implements Comparable<SInt> {
    public SInt() {
        this(0);
    }

    public SInt(long v) {
        super();

        setBitsValue(v);
    }

    public SInt(LargeBitList b, long p, int l) {
        super(b, p, l);
    }

    public SInt(SInt obj) {
        super(obj);
    }

    @Override
    public int compareTo(SInt obj) {
        return compareAsSInt(getBitsValue(), obj.getBitsValue());
    }

    /**
     * 値を表す String オブジェクトを返します。
     *
     * @return 10 進数 (基数 10) による文字列表現
     */
    @Override
    public String toString() {
        return Long.toString(getBitsValue());
    }
}
