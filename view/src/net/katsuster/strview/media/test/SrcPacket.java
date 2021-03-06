package net.katsuster.strview.media.test;

import net.katsuster.strview.media.*;

/**
 * <p>
 * ソースパケット。
 * </p>
 *
 * @author katsuhiro
 */
public class SrcPacket extends PacketAdapter {
    public SrcPacket() {
        this(new SrcHeader(""));
    }

    public SrcPacket(SrcHeader h) {
        super(h);
    }

    @Override
    public String getShortName() {
        return "Source Packet(name:" + getHeader().getName() + ")";
    }

    @Override
    public SrcHeader getHeader() {
        return (SrcHeader)super.getHeader();
    }

    @Override
    protected void readHeader(PacketReader<?> c) {
        getHeader().read(c);
    }

    @Override
    protected void readBody(PacketReader<?> c) {
        long size_f;

        //FIXME: サイズは固定の長さ
        size_f = 1;

        //ヘッダ以降の本体を読み込む
        size_f -= getHeaderLength();
        setBody(c.readBitList(size_f, getBody()));
    }

    @Override
    protected void writeHeader(PacketWriter<?> c) {
        getHeader().write(c);
    }

    @Override
    protected void writeBody(PacketWriter<?> c) {
        long size_f;

        //FIXME: サイズは固定の長さ
        size_f = 1;

        //ヘッダ以降の本体を書き込む
        size_f -= getHeaderLength();
        c.writeBitList(size_f, getBody(), "body");
    }
}
