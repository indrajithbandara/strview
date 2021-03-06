package net.katsuster.strview.media.flv;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * VIDEODATA
 * </p>
 *
 * @author katsuhiro
 */
public class FLVHeaderVideo extends FLVHeaderES
        implements Cloneable {
    public UInt frame_type;
    public UInt codec_id;

    public FLVHeaderVideo() {
        frame_type = new UInt();
        codec_id = new UInt();
    }

    @Override
    public FLVHeaderVideo clone()
            throws CloneNotSupportedException {
        FLVHeaderVideo obj = (FLVHeaderVideo)super.clone();

        obj.frame_type = (UInt)frame_type.clone();
        obj.codec_id = (UInt)codec_id.clone();

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            FLVHeaderVideo d) {
        c.enterBlock("FLV tag (Video)");

        FLVHeaderES.read(c, d);

        d.frame_type = c.readUInt( 4, d.frame_type);
        d.codec_id   = c.readUInt( 4, d.codec_id  );

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             FLVHeaderVideo d) {
        c.enterBlock("FLV tag (Video)");

        FLVHeaderES.write(c, d);

        c.writeUInt( 4, d.frame_type, "FrameType", d.getFrameTypeName());
        c.writeUInt( 4, d.codec_id  , "CodecID"  , d.getCodecIDName());

        c.leaveBlock();
    }

    public String getFrameTypeName() {
        return FLVConsts.getFrameTypeName(frame_type.intValue());
    }

    public String getCodecIDName() {
        return FLVConsts.getCodecIDName(codec_id.intValue());
    }
}
