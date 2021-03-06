package net.katsuster.strview.media.riff;

import java.util.*;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * idx1 chunk
 * </p>
 *
 * <p>
 * 参考規格
 * </p>
 * <ul>
 * <li>MSDN: AVIOLDINDEX struct</li>
 * </ul>
 *
 * @author katsuhiro
 */
public class RIFFHeaderIdx1 extends RIFFHeader
        implements Cloneable {
    public List<IndexEntry> aIndex;

    public RIFFHeaderIdx1() {
        aIndex = new ArrayList<>();
    }

    @Override
    public RIFFHeaderIdx1 clone()
            throws CloneNotSupportedException {
        RIFFHeaderIdx1 obj = (RIFFHeaderIdx1)super.clone();

        obj.aIndex = new ArrayList<>();
        for (IndexEntry e : aIndex) {
            obj.aIndex.add(e.clone());
        }

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            RIFFHeaderIdx1 d) {
        c.enterBlock("idx1 chunk");

        RIFFHeader.read(c, d);

        //16 is size of IndexEntry
        int cnt = d.ckSize.intValue() / 16;
        d.aIndex = readObjectList(c, cnt, d.aIndex,
                IndexEntry.class);

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             RIFFHeaderIdx1 d) {
        c.enterBlock("idx1 chunk");

        RIFFHeader.write(c, d);

        writeObjectList(c, d.aIndex.size(), d.aIndex,
                "aIndex");

        c.leaveBlock();
    }
}
