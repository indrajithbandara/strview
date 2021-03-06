package net.katsuster.strview.media.flv;

import java.util.*;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * SCRIPTDATAECMAARRAY
 * </p>
 *
 * @author katsuhiro
 */
public class FLVScriptDataECMAArray extends FLVScriptData
        implements Cloneable {
    public UInt ecma_array_length;
    public List<FLVScriptDataObjectProperty> variables;
    //SCRIPTDATAECMAARRAY が Script タグの終端にある場合、省略されることがある
    public FLVScriptDataObjectEnd list_terminator;

    public FLVScriptDataECMAArray() {
        ecma_array_length = new UInt();
        variables = new ArrayList<>();
        list_terminator = new FLVScriptDataObjectEnd();
    }

    @Override
    public FLVScriptDataECMAArray clone()
            throws CloneNotSupportedException {
        FLVScriptDataECMAArray obj =
                (FLVScriptDataECMAArray)super.clone();

        obj.ecma_array_length = (UInt)ecma_array_length.clone();

        obj.variables = new ArrayList<>();
        for (FLVScriptDataObjectProperty v : variables) {
            obj.variables.add(v.clone());
        }

        obj.list_terminator = list_terminator.clone();

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            FLVScriptDataECMAArray d) {
        c.enterBlock("SCRIPTDATAECMAARRAY");

        FLVScriptData.read(c, d);

        d.ecma_array_length = c.readUInt(32, d.ecma_array_length);

        d.variables.clear();
        for (int i = 0; i < d.ecma_array_length.intValue(); i++) {
            FLVScriptDataObjectProperty v = new FLVScriptDataObjectProperty();
            v.setLimit(d.getLimit());
            v.read(c);
            d.variables.add(v);
        }
        if (c.peekLong(24) == 0x000009) {
            d.list_terminator.read(c);
        }

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             FLVScriptDataECMAArray d) {
        c.enterBlock("SCRIPTDATAECMAARRAY");

        FLVScriptData.write(c, d);

        c.writeUInt(32, d.ecma_array_length, "ECMAArrayLength");

        writeObjectList(c, d.variables.size() /*d.ecma_array_length.intValue()*/,
                d.variables, "Variables");

        d.list_terminator.write(c);

        c.leaveBlock();
    }
}
