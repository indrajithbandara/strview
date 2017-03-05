package net.katsuster.strview.media.m4v;

import net.katsuster.strview.media.*;

/**
 * <p>
 * MPEG4 Part 2 Visual Object
 * </p>
 *
 * @author katsuhiro
 */
public class M4VObject extends PacketAdapter
        implements Cloneable {
    public M4VObject() {
        this(new M4VHeader());
    }

    public M4VObject(M4VHeader h) {
        super(h);
    }

    @Override
    public M4VObject clone()
            throws CloneNotSupportedException {
        M4VObject obj = (M4VObject)super.clone();

        return obj;
    }

    @Override
    public String getShortName() {
        return getHeader().getStartCodeName();
    }

    /**
     * <p>
     * MPEG4 Part 2 Video オブジェクトのヘッダを取得します。
     * </p>
     *
     * @return MPEG4 Part 2 Video オブジェクト
     */
    @Override
    public M4VHeader getHeader() {
        return (M4VHeader)super.getHeader();
    }

    @Override
    protected void readHeader(PacketReader<?> c) {
        getHeader().read(c);
    }

    @Override
    protected void readBody(PacketReader<?> c) {
        long orgpos;
        int size_f = 0;
        int stepback = 0;
        int acc = 0xffffff;

        //次のスタートコードを探す
        c.alignByte();
        orgpos = c.position();
        while (c.hasNext(8)) {
            acc <<= 8;
            acc |= c.readLong(8);
            if ((acc & 0xffffff) == 0x000001) {
                stepback = 24;
                break;
            }
        }
        size_f = (int)(c.position() - orgpos - stepback);
        c.position(orgpos);

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
