package net.katsuster.strview.media.rmff;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * DATA チャンクヘッダ。
 * </p>
 *
 * @author katsuhiro
 */
public class RMFFHeaderDATA extends RMFFHeader
        implements Cloneable {
    public UInt num_packets;
    public UInt next_data_header;

    public RMFFHeaderDATA() {
        num_packets = new UInt();
        next_data_header = new UInt();
    }

    @Override
    public RMFFHeaderDATA clone()
            throws CloneNotSupportedException {
        RMFFHeaderDATA obj = (RMFFHeaderDATA)super.clone();

        obj.num_packets = (UInt)num_packets.clone();
        obj.next_data_header = (UInt)next_data_header.clone();

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            RMFFHeaderDATA d) {
        c.enterBlock("DATA chunk");

        RMFFHeader.read(c, d);

        if (d.object_version.intValue() == 0) {
            d.num_packets      = c.readUInt(32, d.num_packets     );
            d.next_data_header = c.readUInt(32, d.next_data_header);
        }

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             RMFFHeaderDATA d) {
        c.enterBlock("DATA chunk");

        RMFFHeader.write(c, d);

        if (d.object_version.intValue() == 0) {
            c.writeUInt(32, d.num_packets     , "num_packets"     );
            c.writeUInt(32, d.next_data_header, "next_data_header");
        }

        c.leaveBlock();
    }
}
