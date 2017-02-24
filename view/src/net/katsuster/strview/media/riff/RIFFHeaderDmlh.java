package net.katsuster.strview.media.riff;

import net.katsuster.strview.media.PacketReader;
import net.katsuster.strview.media.PacketWriter;
import net.katsuster.strview.util.UInt;

/**
 * <p>
 * dmlh (Extended AVI Header) チャンクヘッダ。
 * </p>
 *
 * <p>
 * 参考規格
 * </p>
 * <ul>
 * <li>Microsoft Multimedia Standards Update: Revision 1.0.97</li>
 * <li>OpenDML AVI File Format Extensions: Version 1.02</li>
 * </ul>
 *
 * @author katsuhiro
 */
public class RIFFHeaderDmlh extends RIFFHeader
        implements Cloneable {
    public UInt dwTotalFrames;

    public RIFFHeaderDmlh() {
        dwTotalFrames = new UInt();
    }

    @Override
    public RIFFHeaderDmlh clone()
            throws CloneNotSupportedException {
        RIFFHeaderDmlh obj = (RIFFHeaderDmlh)super.clone();

        obj.dwTotalFrames = dwTotalFrames.clone();

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            RIFFHeaderDmlh d) {
        c.enterBlock("RIFF dmlh chunk header");

        RIFFHeader.read(c, d);

        d.dwTotalFrames = c.readUIntR(32, d.dwTotalFrames);

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             RIFFHeaderDmlh d) {
        c.enterBlock("RIFF dmlh chunk header");

        RIFFHeader.write(c, d);

        c.writeUIntR(32, d.dwTotalFrames, "dwTotalFrames");

        c.leaveBlock();
    }
}
