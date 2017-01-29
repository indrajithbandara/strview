package net.katsuster.strview.media;

import net.katsuster.strview.util.*;

/**
 * <p>
 * パケットを別の形式に変換に変換するインタフェースです。
 * </p>
 *
 * <p>
 * ほとんどのメソッドが空の実装となる場合は、
 * PacketWriterAdaptor を継承し必要な関数のみをオーバライドすると便利です。
 * </p>
 *
 * <p>
 * 想定する使用例を下記に示します。
 * </p>
 *
 * <pre>
 * Packet p;
 * SomeObject r;
 * PacketWriter&lt;SomeObject&gt; c = new SomeWriter(new SomeObject());
 *
 * //add members of packet
 * p.convert(c);
 *
 * r = c.getResult();
 * </pre>
 *
 * @see PacketWriterAdapter
 * @author katsuhiro
 */
public interface PacketWriter<T> extends PacketConverter<T> {
    /**
     * <p>
     * 現在位置を更新せずに、数値を書き込みます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の数値
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeLong(int nbit, long val, String name);

    /**
     * <p>
     * 現在位置を更新せずに、数値を書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の数値
     * @param name 変換対象の名前
     * @param desc 変換対象の符号付き数値の意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeLong(int nbit, long val, String name, String desc);

    /**
     * <p>
     * 数値を書き込みます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の数値
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeLong(int nbit, long val, String name);

    /**
     * <p>
     * 数値を書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の数値
     * @param name 変換対象の名前
     * @param desc 変換対象の符号付き数値の意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeLong(int nbit, long val, String name, String desc);

    /**
     * <p>
     * 現在位置を更新せずに、符号付き数値を書き込みます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号付き数値オブジェクト
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeSInt(int nbit, SInt val, String name);

    /**
     * <p>
     * 現在位置を更新せずに、符号付き数値を書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号付き数値オブジェクト
     * @param name 変換対象の名前
     * @param desc 変換対象の符号付き数値の意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeSInt(int nbit, SInt val, String name, String desc);

    /**
     * <p>
     * 符号付き数値を書き込みます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号付き数値オブジェクト
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeSInt(int nbit, SInt val, String name);

    /**
     * <p>
     * 符号付き数値を書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号付き数値オブジェクト
     * @param name 変換対象の名前
     * @param desc 変換対象の符号付き数値の意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeSInt(int nbit, SInt val, String name, String desc);

    /**
     * <p>
     * 現在位置を更新せずに、符号無し数値を書き込みます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号無し数値オブジェクト
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeUInt(int nbit, UInt val, String name);

    /**
     * <p>
     * 現在位置を更新せずに、符号無し数値を書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号無し数値オブジェクト
     * @param name 変換対象の名前
     * @param desc 変換対象の符号無し数値の意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeUInt(int nbit, UInt val, String name, String desc);

    /**
     * <p>
     * 符号無し数値を書き込みます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号無し数値オブジェクト
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeUInt(int nbit, UInt val, String name);

    /**
     * <p>
     * 符号無し数値を書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象の符号無し数値オブジェクト
     * @param name 変換対象の名前
     * @param desc 変換対象の符号無し数値の意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeUInt(int nbit, UInt val, String name, String desc);

    /**
     * <p>
     * 現在位置を更新せずに、ビットリストを書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象のビットリスト
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeBitList(int nbit, LargeBitList val, String name);

    /**
     * <p>
     * 現在位置を更新せずに、ビットリストを書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象のビットリスト
     * @param name 変換対象の名前
     * @param desc 変換対象のビットリストの意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void pokeBitList(int nbit, LargeBitList val, String name, String desc);

    /**
     * <p>
     * ビットリストを書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象のビットリスト
     * @param name 変換対象の名前
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeBitList(int nbit, LargeBitList val, String name);

    /**
     * <p>
     * ビットリストを書き込みます。
     * データに加えて、データの意味、説明などを渡すことができます。
     * </p>
     *
     * @param nbit 変換対象のサイズ（ビット単位）
     * @param val  変換対象のビットリスト
     * @param name 変換対象の名前
     * @param desc 変換対象のビットリストの意味、説明など
     * @throws IllegalArgumentException 無効なパラメータや null を渡した場合
     */
    public void writeBitList(int nbit, LargeBitList val, String name, String desc);
}