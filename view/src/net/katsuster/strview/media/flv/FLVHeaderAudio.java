package net.katsuster.strview.media.flv;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * AUDIODATA
 * </p>
 *
 * @author katsuhiro
 */
public class FLVHeaderAudio extends FLVHeaderES
        implements Cloneable {
    public UInt sound_format;
    public UInt sound_rate;
    public UInt sound_size;
    public UInt sound_type;

    public FLVHeaderAudio() {
        sound_format = new UInt();
        sound_rate = new UInt();
        sound_size = new UInt();
        sound_type = new UInt();
    }

    @Override
    public FLVHeaderAudio clone()
            throws CloneNotSupportedException {
        FLVHeaderAudio obj = (FLVHeaderAudio)super.clone();

        obj.sound_format = (UInt)sound_format.clone();
        obj.sound_rate = (UInt)sound_rate.clone();
        obj.sound_size = (UInt)sound_size.clone();
        obj.sound_type = (UInt)sound_type.clone();

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            FLVHeaderAudio d) {
        c.enterBlock("FLV tag (Audio)");

        FLVHeaderES.read(c, d);

        d.sound_format = c.readUInt( 4, d.sound_format);
        d.sound_rate   = c.readUInt( 2, d.sound_rate  );
        d.sound_size   = c.readUInt( 1, d.sound_size  );
        d.sound_type   = c.readUInt( 1, d.sound_type  );

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             FLVHeaderAudio d) {
        c.enterBlock("FLV tag (Audio)");

        FLVHeaderES.write(c, d);

        c.writeUInt( 4, d.sound_format, "SoundFormat", d.getSoundFormatName());
        c.writeUInt( 2, d.sound_rate  , "SoundRate"  , d.getSoundRateName());
        c.writeUInt( 1, d.sound_size  , "SoundSize"  , d.getSoundSizeName());
        c.writeUInt( 1, d.sound_type  , "SoundType"  , d.getSoundTypeName());

        c.leaveBlock();
    }

    public String getSoundFormatName() {
        return FLVConsts.getSoundFormatName(sound_format.intValue());
    }

    public String getSoundRateName() {
        return FLVConsts.getSoundRateName(sound_rate.intValue());
    }

    public String getSoundSizeName() {
        return FLVConsts.getSoundSizeName(sound_size.intValue());
    }

    public String getSoundTypeName() {
        return FLVConsts.getSoundTypeName(sound_type.intValue());
    }
}
