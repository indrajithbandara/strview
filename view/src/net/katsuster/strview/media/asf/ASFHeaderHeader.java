package net.katsuster.strview.media.asf;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * ASF (Advanced Systems Format) Header Object
 * </p>
 *
 * <p>
 * 参考規格
 * </p>
 * <ul>
 * <li>Advanced Systems Format (ASF) Specification: Revision 01.20.06</li>
 * </ul>
 *
 * @author katsuhiro
 */
public class ASFHeaderHeader extends ASFHeader
        implements Cloneable {
    public UIntR number_of_header_objects;
    public UIntR reserved1;
    public UIntR reserved2;

    public ASFHeaderHeader() {
        number_of_header_objects = new UIntR();
        reserved1 = new UIntR();
        reserved2 = new UIntR();
    }

    @Override
    public ASFHeaderHeader clone()
            throws CloneNotSupportedException {
        ASFHeaderHeader obj = (ASFHeaderHeader)super.clone();

        obj.number_of_header_objects = (UIntR)number_of_header_objects.clone();
        obj.reserved1 = (UIntR)reserved1.clone();
        obj.reserved2 = (UIntR)reserved2.clone();

        return obj;
    }

    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            ASFHeaderHeader d) {
        c.enterBlock("Header Object");

        ASFHeader.read(c, d);

        d.number_of_header_objects = c.readUIntR(32, d.number_of_header_objects);
        d.reserved1                = c.readUIntR( 8, d.reserved1               );
        d.reserved2                = c.readUIntR( 8, d.reserved2               );

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             ASFHeaderHeader d) {
        c.enterBlock("Header Object");

        ASFHeader.write(c, d);

        c.writeUIntR(32, d.number_of_header_objects, "Number of Header Objects");
        c.writeUIntR( 8, d.reserved1               , "Reserved 1");
        c.writeUIntR( 8, d.reserved2               , "Reserved 2");

        c.leaveBlock();
    }
}
