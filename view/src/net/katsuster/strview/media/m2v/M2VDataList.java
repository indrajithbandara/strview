package net.katsuster.strview.media.m2v;

import java.util.*;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;
import net.katsuster.strview.media.m2v.M2VConsts.*;

/**
 * <p>
 * MPEG2 Video データリスト。
 * </p>
 *
 * @author katsuhiro
 */
public class M2VDataList extends AbstractPacketList<M2VData> {
    private LargeBitList buf;

    private NavigableMap<Long, M2VHeaderSequence> cacheSeq;
    private NavigableMap<Long, M2VHeaderExtSequence> cacheExtSeq;
    private NavigableMap<Long, M2VHeaderExtSequenceScalable> cacheExtSeqSca;
    private NavigableMap<Long, M2VHeaderExtPictureCoding> cacheExtPicCod;

    public M2VDataList() {
        super(LENGTH_UNKNOWN);
    }

    public M2VDataList(LargeBitList l) {
        super(LENGTH_UNKNOWN);

        buf = l;

        cacheSeq = new TreeMap<>();
        cacheExtSeq = new TreeMap<>();
        cacheExtSeqSca = new TreeMap<>();
        cacheExtPicCod = new TreeMap<>();
    }

    @Override
    public String getShortName() {
        return "MPEG2 Video";
    }

    @Override
    public boolean hasTreeStructure() {
        return false;
    }

    @Override
    public void count() {
        FromBitListConverter c = new FromBitListConverter(buf);

        countSlow(c);
    }

    @Override
    protected M2VData readNextInner(PacketReader<?> c, PacketRange pr) {
        M2VHeader tagh = createHeader(c, pr);

        M2VData packet = new M2VData(tagh);
        packet.setRange(pr);
        packet.read(c);

        cachePacket(packet);

        return packet;
    }

    @Override
    protected M2VData getInner(long index) {
        FromBitListConverter c = new FromBitListConverter(buf);

        seek(c, index);

        return (M2VData)readNext(c, index);
    }

    @Override
    protected void setInner(long index, M2VData data) {
        //TODO: not implemented yet
    }

    protected M2VHeader createHeader(PacketReader<?> c, PacketRange pr) {
        M2VHeader tagh;

        M2VHeader tmph = new M2VHeader();
        tmph.peek(c);

        int sc = tmph.start_code.intValue();

        if (sc == START_CODE.EXTENSION) {
            M2VHeaderExt tmphext = new M2VHeaderExt();
            tmphext.peek(c);

            int ec = tmphext.extension_start_code_identifier.intValue();
            if (ec == EXTENSION_START_CODE.PICTURE_DISPLAY) {
                tagh = createHeaderExtPictureDisplay(c, pr);
            } else {
                tagh = M2VConsts.m2vExtFactory.createPacketHeader(
                        tmphext.extension_start_code_identifier.intValue());
            }
            if (tagh == null) {
                tagh = tmphext;
            }
        } else if (START_CODE.SLICE_START <= sc
                && sc <= START_CODE.SLICE_END) {
            tagh = createHeaderSlice(c, pr);
        } else {
            tagh = M2VConsts.m2vFactory.createPacketHeader(
                    tmph.start_code.intValue());
        }
        if (tagh == null) {
            //unknown
            tagh = tmph;
        }

        return tagh;
    }

    protected M2VHeader createHeaderSlice(PacketReader<?> c, PacketRange pr) {
        Map.Entry<Long, M2VHeaderSequence> entSeq = cacheSeq.floorEntry(pr.getNumber());
        Map.Entry<Long, M2VHeaderExtSequence> entExtSeq = cacheExtSeq.floorEntry(pr.getNumber());
        Map.Entry<Long, M2VHeaderExtSequenceScalable> entExtSeqSca = cacheExtSeqSca.floorEntry(pr.getNumber());
        if (entSeq == null) {
            return null;
        }

        return new M2VHeaderSlice(entSeq, entExtSeq, entExtSeqSca);
    }

    protected M2VHeader createHeaderExtPictureDisplay(PacketReader<?> c, PacketRange pr) {
        Map.Entry<Long, M2VHeaderExtSequence> entExtSeq = cacheExtSeq.floorEntry(pr.getNumber());
        Map.Entry<Long, M2VHeaderExtPictureCoding> entExtPicCod = cacheExtPicCod.floorEntry(pr.getNumber());
        if (entExtSeq == null || entExtPicCod == null) {
            return null;
        }

        return new M2VHeaderExtPictureDisplay(entExtSeq, entExtPicCod);
    }

    protected void cachePacket(M2VData packet) {
        M2VHeader h = packet.getHeader();
        PacketRange pr = packet.getRange();

        if (h instanceof M2VHeaderSequence) {
            cacheSeq.put(pr.getNumber(), (M2VHeaderSequence)h);
        }
        if (h instanceof M2VHeaderExtSequence) {
            cacheExtSeq.put(pr.getNumber(), (M2VHeaderExtSequence)h);
        }
        if (h instanceof M2VHeaderExtSequenceScalable) {
            cacheExtSeqSca.put(pr.getNumber(), (M2VHeaderExtSequenceScalable)h);
        }
        if (h instanceof M2VHeaderExtPictureCoding) {
            cacheExtPicCod.put(pr.getNumber(), (M2VHeaderExtPictureCoding)h);
        }
    }
}
