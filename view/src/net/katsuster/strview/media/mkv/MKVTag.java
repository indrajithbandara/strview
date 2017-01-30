package net.katsuster.strview.media.mkv;

import net.katsuster.strview.media.*;

/**
 * <p>
 * Matroska タグ。
 * </p>
 *
 * @author katsuhiro
 */
public class MKVTag extends PacketAdapter
        implements Cloneable {
    //Matroska タグ最小ヘッダサイズ（byte 単位、id と size の最小値）
    public static final int TAG_HEADER_SIZE = 2;

    public MKVTag() {
        this(new MKVHeader());
    }

    public MKVTag(MKVHeader header) {
        this(null, header);
    }

    public MKVTag(MKVTag parent, MKVHeader header) {
        super(parent);

        setHeader(header);
    }

    @Override
    public MKVTag clone()
            throws CloneNotSupportedException {
        MKVTag obj = (MKVTag)super.clone();

        return obj;
    }

    @Override
    public String getShortName() {
        return getHeader().getTagIdName();
    }

    /**
     * <p>
     * Matroska タグヘッダを返す。
     * </p>
     *
     * @return Matroska タグヘッダ
     */
    @Override
    public MKVHeader getHeader() {
        return (MKVHeader)super.getHeader();
    }

    /**
     * <p>
     * タグ ID を返す。
     * </p>
     *
     * @return タグ ID
     */
    public long getTagID() {
        return getHeader().tag_id.getValue();
    }

    /**
     * <p>
     * タグの長さを返す。
     * </p>
     *
     * @return タグの長さ
     */
    public long getTagLength() {
        return getHeader().tag_len.getValue();
    }

    @Override
    protected void readHeader(PacketReader<?> c) {
        getHeader().read(c);
    }

    @Override
    protected void readBody(PacketReader<?> c) {
        MKVHeader head = getHeader();
        long size_f;

        //tag_len はタグ ID とタグサイズより後のデータサイズを表す
        size_f = head.tag_id.getSizeAll()
                + head.tag_len.getSizeAll()
                + (getTagLength() << 3);

        size_f -= getHeaderLength();
        setBody(c.readSubList(size_f, getBody()));
    }

    @Override
    protected void writeHeader(PacketWriter<?> c) {
        AbstractPacket.write(c, this);

        getHeader().write(c);
    }

    @Override
    protected void writeBody(PacketWriter<?> c) {
        //long size_f;

        //TODO: not implemented
        //c.writeSubList(size_f, getBody(), "body");
    }
}
