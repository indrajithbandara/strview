package net.katsuster.strview.media.flv;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * SCRIPTDATAOBJECTEND
 * </p>
 *
 * @author katsuhiro
 */
public class FLVScriptDataObjectEnd extends FLVScriptData
        implements Cloneable {
    //shall be 0x00, 0x00, 0x09
    public UInt object_end_marker;

    public FLVScriptDataObjectEnd() {
        object_end_marker = new UInt();
    }

    @Override
    public FLVScriptDataObjectEnd clone()
            throws CloneNotSupportedException {
        FLVScriptDataObjectEnd obj = (FLVScriptDataObjectEnd)super.clone();

        obj.object_end_marker = (UInt)object_end_marker.clone();

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            FLVScriptDataObjectEnd d) {
        c.enterBlock("SCRIPTDATAOBJECTEND");

        FLVScriptData.read(c, d);

        d.object_end_marker = c.readUInt(24, d.object_end_marker);

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             FLVScriptDataObjectEnd d) {
        c.enterBlock("SCRIPTDATAOBJECTEND");

        FLVScriptData.write(c, d);

        c.writeUInt(24, d.object_end_marker, "ObjectEndMarker");

        c.leaveBlock();
    }
}
