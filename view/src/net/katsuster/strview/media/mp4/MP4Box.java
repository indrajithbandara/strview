package net.katsuster.strview.media.mp4;

import net.katsuster.strview.media.*;

/**
 * <p>
 * MPEG4 media file format の Box。
 * </p>
 *
 * @author katsuhiro
 */
public class MP4Box extends PacketAdapter {
    //MP4 Box 最小ヘッダサイズ（byte 単位、size と type のみ）
    public static final int BOX_HEADER_SIZE = 8;

    public MP4Box() {
        this(new MP4Header());
    }

    public MP4Box(MP4Header header) {
        setHeader(header);
    }

    @Override
    public MP4Box clone()
            throws CloneNotSupportedException {
        MP4Box obj = (MP4Box)super.clone();

        return obj;
    }

    @Override
    public String getShortName() {
        return getHeader().getTypeName();
    }

    @Override
    public boolean isRecursive() {
        return getHeader().isRecursive();
    }

    /**
     * <p>
     * MP4 Box ヘッダを返す。
     * </p>
     *
     * @return MP4 Box ヘッダ
     */
    @Override
    public MP4Header getHeader() {
        return (MP4Header)super.getHeader();
    }

    @Override
    protected void readHeader(PacketReader<?> c) {
        getHeader().read(c);
    }

    @Override
    protected void readBody(PacketReader<?> c) {
        MP4Header head = getHeader();
        long size_f;

        //size は size を含むボックス全体のサイズを表す
        size_f = (head.size.longValue() << 3);
        if (size_f <= 0) {
            //0 以下は異常値なので無視し、
            //size, type メンバのサイズに変更する
            //読み出し位置が進まず、無限ループする不具合回避も兼ねている
            size_f = (BOX_HEADER_SIZE << 3);
        }

        //ヘッダ以降を本体として読み込む
        size_f -= getHeaderLength();
        setBody(c.readSubList(size_f, getBody()));
    }

    @Override
    protected void writeHeader(PacketWriter<?> c) {
        getHeader().write(c);
    }

    @Override
    protected void writeBody(PacketWriter<?> c) {
        long size_f = getBody().length();

        //FIXME: tentative
        c.writeSubList(size_f, getBody(), "body");
    }
}
