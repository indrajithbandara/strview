package net.katsuster.strview.media.mkv;

import java.util.*;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;
import net.katsuster.strview.media.mkv.MKVConsts.*;

/**
 * <p>
 * Matroska Block タグヘッダ。
 * </p>
 *
 * @author katsuhiro
 */
public class MKVHeaderBlock extends MKVHeader {
    public EBMLvalue track_number;
    public UInt timecode;
    public UInt reserved1;
    public UInt invisible;
    public UInt lacing;
    public UInt reserved2;

    //EBML lacing
    public UInt lacing_head;
    public EBMLvalue lacing_size0;
    public ArrayList<EBMLlacing> lacing_diffs;

    //Lacing の各フレームのサイズ（バイト単位）
    private ArrayList<Long> lacing_sizes;

    public MKVHeaderBlock() {
        track_number = new EBMLvalue();
        timecode = new UInt();
        reserved1 = new UInt();
        invisible = new UInt();
        lacing = new UInt();
        reserved2 = new UInt();

        lacing_head = new UInt();
        lacing_size0 = new EBMLvalue();
        lacing_diffs = new ArrayList<EBMLlacing>();

        lacing_sizes = new ArrayList<Long>();
    }

    @Override
    public MKVHeaderBlock clone()
            throws CloneNotSupportedException {
        MKVHeaderBlock obj = (MKVHeaderBlock)super.clone();

        obj.track_number = track_number.clone();
        obj.timecode = timecode.clone();
        obj.reserved1 = reserved1.clone();
        obj.invisible = invisible.clone();
        obj.lacing = lacing.clone();
        obj.reserved2 = reserved1.clone();

        obj.lacing_head = lacing_head.clone();
        obj.lacing_size0 = lacing_size0.clone();

        obj.lacing_diffs = new ArrayList<EBMLlacing>();
        for (EBMLlacing v : lacing_diffs) {
            obj.lacing_diffs.add(v.clone());
        }

        lacing_sizes = new ArrayList<Long>();
        for (Long v : lacing_sizes) {
            obj.lacing_sizes.add(new Long(v));
        }

        return obj;
    }

    @Override
    public boolean isMaster() {
        return false;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            MKVHeaderBlock d) {
        EBMLlacing val;
        long pos;
        long frame_size, frame_sum;
        int i;

        c.enterBlock("Matroska Block");

        MKVHeader.read(c, d);

        pos = c.position();

        d.track_number.read(c);

        c.readUInt(16, d.timecode );
        c.readUInt( 4, d.reserved1);
        c.readUInt( 1, d.invisible);
        c.readUInt( 2, d.lacing   );
        c.readUInt( 1, d.reserved2);

        if (d.lacing.intValue() == LACING.EBML) {
            c.readUInt( 8, d.lacing_head);
            d.lacing_size0.read(c);

            //最初のフレームサイズは絶対値
            frame_size = d.lacing_size0.getValue();
            frame_sum = frame_size;
            d.lacing_sizes.add(frame_size);

            for (i = 0; i < d.lacing_head.intValue() - 1; i++) {
                val = new EBMLlacing();
                val.read(c);
                d.lacing_diffs.add(val);

                //2 ～ n-1 のフレームサイズは相対値
                frame_size += val.getValue();
                frame_sum += frame_size;
                d.lacing_sizes.add(frame_size);
            }

            //最後のフレームサイズはブロックサイズから計算
            frame_size = (d.tag_len.getValue() - frame_sum)
                    - ((c.position() - pos) >> 3);
            d.lacing_sizes.add(frame_size);
        } else if (d.lacing.intValue() == LACING.FIXED_SIZE) {
            c.readUInt( 8, d.lacing_head);
        }

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             MKVHeaderBlock d) {
        int i;

        c.enterBlock("Matroska Block");

        MKVHeader.write(c, d);

        c.mark("track_number", "");
        d.track_number.write(c);

        c.writeUInt(16, d.timecode , "timecode");
        c.writeUInt( 4, d.reserved1, "reserved1");
        c.writeUInt( 1, d.invisible, "invisible");
        c.writeUInt( 2, d.lacing   , "lacing"   , d.getLacingName());
        c.writeUInt( 1, d.reserved2, "reserved2");

        if (d.lacing.intValue() == LACING.EBML) {
            c.writeUInt( 8, d.lacing_head, "lacing_head");
            c.mark("lacing_size0", "");
            d.lacing_size0.write(c);

            for (i = 0; i < d.lacing_head.intValue() - 1; i++) {
                c.mark("lacing_diffs[" + i + "]", "");
                d.lacing_diffs.get(i).write(c);
            }

            for (i = 0; i < d.lacing_head.intValue() + 1; i++) {
                c.mark("lacing_sizes[" + i + "]",
                        d.lacing_sizes.get(i));
            }
        } else if (d.lacing.intValue() == LACING.FIXED_SIZE) {
            c.writeUInt( 8, d.lacing_head, "lacing_head");
        }

        c.leaveBlock();
    }

    public String getLacingName() {
        return MKVConsts.getLacingName(lacing.intValue());
    }
}
