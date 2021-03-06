package net.katsuster.strview.media.test;

import net.katsuster.strview.media.*;

/**
 * <p>
 * 先頭にマーカーのあるパケット。
 * </p>
 *
 * @author katsuhiro
 */
public class MarkedPacket extends PacketAdapter {
    public MarkedPacket() {
        this(new MarkedHeader());
    }

    public MarkedPacket(MarkedHeader h) {
        super(h);
    }

    @Override
    public String getShortName() {
        return "Marked Packet(a:" + getHeader().start_code + ")";
    }

    @Override
    public MarkedHeader getHeader() {
        return (MarkedHeader)super.getHeader();
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

        setBody(c.readBitList(size_f, getBody()));
    }

    @Override
    protected void writeHeader(PacketWriter<?> c) {
        getHeader().write(c);
    }

    @Override
    protected void writeBody(PacketWriter<?> c) {
        long size_f = getBody().length();

        //FIXME: tentative
        c.writeBitList(size_f, getBody(), "body");
    }
}
