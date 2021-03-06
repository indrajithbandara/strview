package net.katsuster.strview.media.ts;

import net.katsuster.strview.media.*;

/**
 * <p>
 * MPEG2-TS(Transport Stream) パケット。
 * </p>
 *
 * @author katsuhiro
 */
public class TSPacket extends PacketAdapter {
    //TS パケットのサイズ（byte 単位）
    public static final int PACKET_SIZE = 188;

    public TSPacket() {
        this(new TSHeader());
    }

    public TSPacket(TSHeader h) {
        super(h);
    }

    @Override
    public String getShortName() {
        return "TS(pid:" + getHeader().pid + ")";
    }

    /**
     * <p>
     * TS パケットヘッダを取得します。
     * </p>
     *
     * @return TS パケットヘッダ
     */
    @Override
    public TSHeader getHeader() {
        return (TSHeader)super.getHeader();
    }

    @Override
    protected void readHeader(PacketReader<?> c) {
        getHeader().read(c);
    }

    @Override
    protected void readBody(PacketReader<?> c) {
        long size_f;

        //サイズは固定の長さ
        size_f = (PACKET_SIZE << 3);

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

        //サイズは固定の長さ
        size_f = (PACKET_SIZE << 3);

        //ヘッダ以降の本体を書き込む
        size_f -= getHeaderLength();
        c.writeBitList(size_f, getBody(), "body");
    }
}
