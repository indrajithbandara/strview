package net.katsuster.strview.io;

/**
 * <p>
 * int 型で扱える長さを超えるビット配列の共通動作を定義します。
 * </p>
 *
 * <p>
 * ビット列から整数、バイト配列を読み出すための補助メソッドを実装しています。
 * </p>
 *
 * @author katsuhiro
 */
public abstract class AbstractLargeBitList extends AbstractLargeBitListBase
        implements LargeBitList, Cloneable {
    /**
     * <p>
     * 指定された長さのビット列を作成します。
     * </p>
     *
     * @param from ビット列の開始点
     * @param to   ビット列の終了点
     */
    public AbstractLargeBitList(long from, long to) {
        super(from, to);
    }

    /**
     * <p>
     * 指定された長さのビット列を作成します。
     * </p>
     *
     * @param l ビット列の長さ
     */
    public AbstractLargeBitList(long l) {
        super(l);
    }

    @Override
    public boolean get(long index) {
        checkIndex(index);

        return getInner(index);
    }

    @Override
    public int get(long index, boolean[] dest, int offset, int length) {
        checkRemaining(index, length);

        return getInner(index, dest, offset, length);
    }

    @Override
    public int get(long index, LargeBitList dest, int offset, int length) {
        checkRemaining(index, length);

        return getInner(index, dest, offset, length);
    }

    @Override
    public void set(long index, boolean data) {
        checkIndex(index);

        setInner(index, data);
    }

    @Override
    public int set(long index, boolean[] src, int offset, int length) {
        checkRemaining(index, length);

        return setInner(index, src, offset, length);
    }

    @Override
    public int set(long index, LargeBitList src, int offset, int length) {
        checkRemaining(index, length);

        return setInner(index, src, offset, length);
    }

    @Override
    public byte getPackedByte(long index, int n) {
        byte result;

        if (n < 0 || 8 < n) {
            throw new IllegalArgumentException("getPackedByte() cannot get more than 8 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        result = (byte)getPackedIntInner(index, n);

        return result;
    }

    @Override
    public short getPackedShort(long index, int n) {
        short result;

        if (n < 0 || 16 < n) {
            throw new IllegalArgumentException("getPackedShort() cannot get more than 16 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        result = (short)getPackedIntInner(index, n);

        return result;
    }

    @Override
    public int getPackedInt(long index, int n) {
        int result;

        if (n < 0 || 32 < n) {
            throw new IllegalArgumentException("getPackedInt() cannot get more than 32 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        result = getPackedIntInner(index, n);

        return result;
    }

    @Override
    public long getPackedLong(long index, int n) {
        long result;

        if (n < 0 || 64 < n) {
            throw new IllegalArgumentException("getPackedLong() cannot get more than 64 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        result = getPackedLongInner(index, n);

        return result;
    }

    @Override
    public void getPackedByteArray(long index, byte[] dst, int off, int n) {
        if (off < 0) {
            throw new IllegalArgumentException("offset is negative"
                    + "(offset: " + off + ").");
        }
        if (n < 0) {
            throw new IllegalArgumentException("length is negative"
                    + "(length: " + n + ").");
        }
        if ((off + n) > (dst.length << 3)) {
            throw new IndexOutOfBoundsException("offset + length is too large"
                    + "(offset: " + off + ", "
                    + "(length: " + n + ", "
                    + "(dst.length: " + (dst.length << 3) + ")");
        }
        checkRemaining(index, n);

        getPackedByteArrayInner(index, dst, off, n);
    }

    @Override
    public void setPackedByte(long index, int n, byte val) {
        if (n < 0 || 8 < n) {
            throw new IllegalArgumentException("setPackedByte() cannot set more than 8 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        setPackedIntInner(index, n, val);
    }

    @Override
    public void setPackedShort(long index, int n, short val) {
        if (n < 0 || 16 < n) {
            throw new IllegalArgumentException("setPackedShort() cannot set more than 16 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        setPackedIntInner(index, n, val);
    }

    @Override
    public void setPackedInt(long index, int n, int val) {
        if (n < 0 || 32 < n) {
            throw new IllegalArgumentException("setPackedInt() cannot set more than 32 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        setPackedIntInner(index, n, val);
    }

    @Override
    public void setPackedLong(long index, int n, long val) {
        if (n < 0 || 64 < n) {
            throw new IllegalArgumentException("setPackedLong() cannot set more than 64 bits."
                    + "(" + n + "bits)");
        }
        checkRemaining(index, n);

        setPackedLongInner(index, n, val);
    }

    @Override
    public void setPackedByteArray(long index, byte[] src, int off, int n) {
        if (off < 0) {
            throw new IllegalArgumentException("offset is negative"
                    + "(offset: " + off + ").");
        }
        if (n < 0) {
            throw new IllegalArgumentException("length is negative"
                    + "(length: " + n + ").");
        }
        if ((off + n) > (src.length << 3)) {
            throw new IndexOutOfBoundsException("offset + length is too large"
                    + "(offset: " + off + ", "
                    + "(length: " + n + ", "
                    + "(src.length: " + (src.length << 3) + ")");
        }
        checkRemaining(index, n);

        setPackedByteArrayInner(index, src, off, n);
    }

    @Override
    public LargeBitList subArray(long from, long to) {
        return new SubLargeBitList(this, from, to);
    }

    /**
     * <p>
     * 指定された位置のビット値を取得します。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * @param index 要素の位置（ビット位置）
     * @return 指定された位置のビット値
     */
    protected abstract boolean getInner(long index);

    /**
     * <p>
     * 指定された位置から、length の長さだけビット値を取得し、
     * ブール値として配列に格納します。
     * ビット値が 1 ならば true、0 ならば false が返されます。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * ビット配列から get() メソッドにより、
     * 1ビットずつビットを取得する実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index  バッファの読み出し開始位置（ビット単位）
     * @param dest   結果を格納するバイト列
     * @param offset 結果の格納を開始する位置（ビット単位）
     * @param length 読みだすビット数
     * @return 実際に読みだしたビット数
     * @throws IndexOutOfBoundsException 読み出し位置が負、配列の範囲外の場合
     */
    protected int getInner(long index, boolean[] dest, int offset, int length) {
        int i;

        for (i = 0; i < length; i++) {
            dest[offset + i] = getInner(index + i);
        }

        return i;
    }

    /**
     * <p>
     * 指定された位置から、length の長さだけビット値を取得し、
     * ビット列に格納します。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * ビット配列から get() メソッドにより、
     * 1ビットずつビットを取得する実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index  バッファの読み出し開始位置（ビット単位）
     * @param dest   結果を格納するバイト列
     * @param offset 結果の格納を開始する位置（ビット単位）
     * @param length 読みだすビット数
     * @return 実際に読みだしたビット数
     * @throws IndexOutOfBoundsException 読み出し位置が負、配列の範囲外の場合
     */
    protected int getInner(long index, LargeBitList dest, int offset, int length) {
        int i;

        for (i = 0; i < length; i++) {
            dest.set(offset + i, getInner(index + i));
        }

        return i;
    }

    /**
     * <p>
     * 指定された位置にビット値を設定します。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * @param index 要素の位置（ビット単位）
     * @param data  指定された位置に設定する値
     */
    protected abstract void setInner(long index, boolean data);

    /**
     * <p>
     * 指定された位置から、length の長さだけビット値を設定します。
     * ブール値が true ならば 1 が設定され、false ならば 0 が設定されます。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * ビット配列へ set() メソッドにより、
     * 1ビットずつビットを設定する実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index  バッファの書き込み開始位置（ビット単位）
     * @param src    バッファに書きこむブール値の配列
     * @param offset データの書きこみを開始する位置（ビット単位）
     * @param length 書きこむビット数
     * @return 実際に書き込んだビット数
     * @throws IndexOutOfBoundsException 書き込み位置が負、配列の範囲外の場合
     */
    protected int setInner(long index, boolean[] src, int offset, int length) {
        int i;

        for (i = 0; i < length; i++) {
            setInner(index + i, src[offset + i]);
        }

        return i;
    }

    /**
     * <p>
     * 指定された位置から、length の長さだけビット値を設定します。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * ビット配列へ set() メソッドにより、
     * 1ビットずつビットを設定する実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index  バッファの書き込み開始位置（ビット単位）
     * @param src    バッファに書きこむビット列
     * @param offset データの書きこみを開始する位置（ビット単位）
     * @param length 書きこむビット数
     * @return 実際に書き込んだビット数
     * @throws IndexOutOfBoundsException 書き込み位置が負、配列の範囲外の場合
     */
    protected int setInner(long index, LargeBitList src, int offset, int length) {
        int i;

        for (i = 0; i < length; i++) {
            setInner(index + i, src.get(offset + i));
        }

        return i;
    }

    /**
     * <p>
     * 指定した位置から 0 ～ 32 ビットまでの任意のビット数を読み出し、
     * int 型の LSB 側に詰めた値を取得します。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * get() メソッドによりブール値の配列を取得した後、
     * 整数値に変換する実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index 読み出しを開始する位置
     * @param n     取得するビット数（32 ビットまで）
     * @return バッファから取得した n ビットの数値
     */
    protected int getPackedIntInner(long index, int n) {
        boolean[] a;

        a = new boolean[n];
        getInner(index, a, 0, n);

        return packBitsInt(a);
    }

    /**
     * <p>
     * 指定した位置から 0 ～ 64 ビットまでの任意のビット数を読み出し、
     * int 型の LSB 側に詰めた値を取得します。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * get() メソッドによりブール値の配列を取得した後、
     * 整数値に変換する実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param n     取得するビット数（64 ビットまで）
     * @param index 読み出しを開始する位置
     * @return バッファから取得した n ビットの数値
     */
    protected long getPackedLongInner(long index, int n) {
        boolean[] a;

        a = new boolean[n];
        getInner(index, a, 0, n);

        return packBitsLong(a);
    }

    /**
     * <p>
     * 指定された位置から任意のビット数を読み出し、
     * byte 型の LSB 側に詰めた値を取得します。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * getPackedByteInner() メソッドにより、
     * バイト値を取得する実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index 読み出しを開始する位置（ビット単位）
     * @param dst   取得したビットを格納する配列
     * @param off   読み出したビットを書きこむビット位置（配列内の位置）
     * @param n     取得するビット数（ビット単位）
     * @throws IllegalArgumentException 読み出すビット数が不適切だった
     * @throws IndexOutOfBoundsException ビット列の範囲外を読みだそうとした
     */
    protected void getPackedByteArrayInner(long index, byte[] dst, int off, int n) {
        //bits
        long position;
        int rem, bits;
        //bytes
        int dst_off;
        //buffer
        int buf;

        position = index;
        rem = n;
        dst_off = (off >>> 3);

        if (rem > 8) {
            //use for-loop
            bits = 8 - (off & 0x07);
            off = 0;

            //1st byte
            dst[dst_off] = (byte)getPackedIntInner(position, bits);
            position += bits;
            rem -= bits;
            dst_off += 1;

            //mid bytes
            while (rem >= 32) {
                buf = getPackedIntInner(position, 32);
                dst[dst_off + 0] = (byte)(buf >>> 24);
                dst[dst_off + 1] = (byte)(buf >>> 16);
                dst[dst_off + 2] = (byte)(buf >>> 8);
                dst[dst_off + 3] = (byte)(buf >>> 0);
                position += 32;
                rem -= 32;
                dst_off += 4;
            }
            while (rem >= 8) {
                dst[dst_off] = (byte)getPackedIntInner(position, 8);
                position += 8;
                rem -= 8;
                dst_off += 1;
            }
        }

        //last byte
        if (rem > 0) {
            dst[dst_off] =
                    (byte)(getPackedIntInner(position, rem) << (8 - rem - (off & 0x07)));
        }
    }

    /**
     * <p>
     * 指定された位置から val の LSB 側から 0 ～ 32 ビットまでの、
     * 任意のビット数をビット列に書き込みます。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * ブール値の配列を整数値に変換した後、
     * set() にて書き込む実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index 書き込みを開始する位置（ビット単位）
     * @param n     書き込むビット数（32 ビットまで）
     * @param val   書き込むビットを含んだ整数値
     */
    protected void setPackedIntInner(long index, int n, int val) {
        boolean[] a;

        a = new boolean[n];
        unpackBitsInt(val, a);

        setInner(index, a, 0, n);
    }

    /**
     * <p>
     * 指定された位置から val の LSB 側から 0 ～ 64 ビットまでの、
     * 任意のビット数をビット列に書き込みます。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * ブール値の配列を整数値に変換した後、
     * set() にて書き込む実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index 書き込みを開始する位置（ビット単位）
     * @param n     書き込むビット数（64 ビットまで）
     * @param val   書き込むビットを含んだ整数値
     */
    protected void setPackedLongInner(long index, int n, long val) {
        boolean[] a;

        a = new boolean[n];
        unpackBitsLong(val, a);

        setInner(index, a, 0, n);
    }

    /**
     * <p>
     * 指定された位置に任意のビット数を書き込みます。
     * </p>
     *
     * <p>
     * この関数は指定された位置をチェックしません。
     * ビット配列の範囲内の位置を指定する必要があります。
     * 範囲外を指定した場合の動作は不定です。
     * </p>
     *
     * <p>
     * setPackedIntInner() により、
     * バイト値の配列を書き込む実装になっています。
     * </p>
     *
     * <p>
     * 派生クラスにてより高速な実装が可能であれば、
     * オーバライドすることを推奨します。
     * </p>
     *
     * @param index 書き込みを開始する位置（ビット単位）
     * @param src   バッファに書き出すビットが入った配列
     * @param off   書き込むビットを読み出すビット位置（配列内の位置）
     * @param n     書き込むビット数（ビット単位）
     * @throws IllegalArgumentException 読み出すビット数が不適切だった
     * @throws IndexOutOfBoundsException ビット列の範囲外を読みだそうとした
     */
    protected void setPackedByteArrayInner(long index, byte[] src, int off, int n) {
        //bits
        long position;
        int rem, bits;
        //bytes
        int src_off;

        position = index;
        rem = n;
        src_off = (off >>> 3);

        if (rem > 8) {
            //use for-loop
            bits = 8 - (off & 0x07);
            off = 0;

            //1st byte
            setPackedIntInner(position, bits, src[src_off]);
            position += bits;
            rem -= bits;
            src_off += 1;

            //mid bytes
            while (rem >= 32) {
                setPackedIntInner(position, 32,
                        (src[src_off + 0] << 24)
                                | (src[src_off + 1] << 16)
                                | (src[src_off + 2] << 8)
                                | (src[src_off + 3] << 0));
                position += 32;
                rem -= 32;
                src_off += 4;
            }
            while (rem >= 8) {
                setPackedIntInner(position, 8, src[src_off]);
                position += 8;
                rem -= 8;
                src_off += 1;
            }
        }

        //last byte
        if (rem > 0) {
            setPackedIntInner(position, rem, src[src_off] >>> (8 - rem - (off & 0x07)));
        }
    }
}