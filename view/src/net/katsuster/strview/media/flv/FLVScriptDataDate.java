package net.katsuster.strview.media.flv;

import java.util.*;
import java.text.*;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * SCRIPTDATADATE
 * </p>
 *
 * @author katsuhiro
 */
public class FLVScriptDataDate extends FLVScriptData
        implements Cloneable {
    public Float64 date_time;
    public SInt local_date_time_offset;

    public FLVScriptDataDate() {
        date_time = new Float64();
        local_date_time_offset = new SInt();
    }

    @Override
    public FLVScriptDataDate clone()
            throws CloneNotSupportedException {
        FLVScriptDataDate obj = (FLVScriptDataDate)super.clone();

        obj.date_time = (Float64)date_time.clone();
        obj.local_date_time_offset = (SInt)local_date_time_offset.clone();

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            FLVScriptDataDate d) {
        c.enterBlock("SCRIPTDATADATE");

        FLVScriptData.read(c, d);

        d.date_time              = c.readFloat64(64, d.date_time          );
        d.local_date_time_offset = c.readSInt(16, d.local_date_time_offset);

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             FLVScriptDataDate d) {
        c.enterBlock("SCRIPTDATADATE");

        FLVScriptData.write(c, d);

        c.writeFloat64(64, d.date_time          , "DateTime"           , d.getDateTimeName());
        c.writeSInt(16, d.local_date_time_offset, "LocalDateTimeOffset", d.getLocalDateTimeOffsetName());

        c.leaveBlock();
    }

    public String getDateTimeName() {
        return getDateTimeName(date_time.longValue());
    }

    public static String getDateTimeName(long millis) {
        String name;
        Date d;

        d = new Date(millis);
        name = DateFormat.getDateTimeInstance().format(d);

        return name;
    }

    public String getLocalDateTimeOffsetName() {
        return getLocalDateTimeOffsetName(local_date_time_offset.intValue());
    }

    public static String getLocalDateTimeOffsetName(int minutes) {
        String name;
        int h, m;

        h = minutes / 60;
        m = minutes % 60;
        name = String.format("UTC %+03d:%02d", h, m);

        return name;
    }
}
