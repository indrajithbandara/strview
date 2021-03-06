package net.katsuster.strview.media.m2v;

import net.katsuster.strview.util.*;
import net.katsuster.strview.media.*;

/**
 * <p>
 * MPEG2 Video sequence header
 * </p>
 *
 * <p>
 * 参考規格
 * </p>
 * <ul>
 * <li>ITU-T H.262, ISO/IEC 13818-2:
 * Information technology - Generic coding of moving pictures and
 * associated audio information: Video</li>
 * </ul>
 *
 * @author katsuhiro
 */
public class M2VHeaderSequence
        extends M2VHeader {
    public UInt  horizontal_size_value;
    public UInt  vertical_size_value;
    public UInt  aspect_ratio_information;
    public UInt  frame_rate_code;
    public UInt  bit_rate_value;
    public UInt  marker_bit;
    public UInt  vbv_buffer_size_value;
    public UInt  constrained_parameters_flag;
    public UInt  load_intra_quantiser_matrix;
    public UInt[] intra_quantiser_matrix;
    public UInt  load_non_intra_quantiser_matrix;
    public UInt[] non_intra_quantiser_matrix;

    public M2VHeaderSequence() {
        horizontal_size_value = new UInt();
        vertical_size_value = new UInt();
        aspect_ratio_information = new UInt();
        frame_rate_code = new UInt();
        bit_rate_value = new UInt();
        marker_bit = new UInt();
        vbv_buffer_size_value = new UInt();
        constrained_parameters_flag = new UInt();
        load_intra_quantiser_matrix = new UInt();
        intra_quantiser_matrix = new UInt[64];
        for (int i = 0; i < intra_quantiser_matrix.length; i++) {
            intra_quantiser_matrix[i] = new UInt();
        }
        load_non_intra_quantiser_matrix = new UInt();
        non_intra_quantiser_matrix = new UInt[64];
        for (int i = 0; i < non_intra_quantiser_matrix.length; i++) {
            non_intra_quantiser_matrix[i] = new UInt();
        }
    }

    @Override
    public M2VHeaderSequence clone()
            throws CloneNotSupportedException {
        M2VHeaderSequence obj = (M2VHeaderSequence)super.clone();

        obj.horizontal_size_value = (UInt)horizontal_size_value.clone();
        obj.vertical_size_value = (UInt)vertical_size_value.clone();
        obj.aspect_ratio_information = (UInt)aspect_ratio_information.clone();
        obj.frame_rate_code = (UInt)frame_rate_code.clone();
        obj.bit_rate_value = (UInt)bit_rate_value.clone();
        obj.marker_bit = (UInt)marker_bit.clone();
        obj.vbv_buffer_size_value = (UInt)vbv_buffer_size_value.clone();
        obj.constrained_parameters_flag = (UInt)constrained_parameters_flag.clone();
        obj.load_intra_quantiser_matrix = (UInt)load_intra_quantiser_matrix.clone();
        obj.intra_quantiser_matrix = intra_quantiser_matrix.clone();
        for (int i = 0; i < obj.intra_quantiser_matrix.length; i++) {
            obj.intra_quantiser_matrix[i] = (UInt)intra_quantiser_matrix[i].clone();
        }
        obj.load_non_intra_quantiser_matrix = (UInt)load_non_intra_quantiser_matrix.clone();
        obj.non_intra_quantiser_matrix = non_intra_quantiser_matrix.clone();
        for (int i = 0; i < obj.non_intra_quantiser_matrix.length; i++) {
            obj.non_intra_quantiser_matrix[i] = (UInt)non_intra_quantiser_matrix[i].clone();
        }

        return obj;
    }

    @Override
    public void read(PacketReader<?> c) {
        read(c, this);
    }

    public static void read(PacketReader<?> c,
                            M2VHeaderSequence d) {
        c.enterBlock("M2V sequence_header()");

        M2VHeader.read(c, d);

        d.horizontal_size_value       = c.readUInt(12, d.horizontal_size_value      );
        d.vertical_size_value         = c.readUInt(12, d.vertical_size_value        );
        d.aspect_ratio_information    = c.readUInt( 4, d.aspect_ratio_information   );
        d.frame_rate_code             = c.readUInt( 4, d.frame_rate_code            );
        d.bit_rate_value              = c.readUInt(18, d.bit_rate_value             );
        d.marker_bit                  = c.readUInt( 1, d.marker_bit                 );
        d.vbv_buffer_size_value       = c.readUInt(10, d.vbv_buffer_size_value      );
        d.constrained_parameters_flag = c.readUInt( 1, d.constrained_parameters_flag);

        d.load_intra_quantiser_matrix     = c.readUInt( 1, d.load_intra_quantiser_matrix);
        if (d.load_intra_quantiser_matrix.intValue() == 1) {
            for (int i = 0; i < d.intra_quantiser_matrix.length; i++) {
                d.intra_quantiser_matrix[i] = c.readUInt( 8, d.intra_quantiser_matrix[i]);
            }
        }

        d.load_non_intra_quantiser_matrix = c.readUInt( 1, d.load_non_intra_quantiser_matrix);
        if (d.load_non_intra_quantiser_matrix.intValue() == 1) {
            for (int i = 0; i < d.non_intra_quantiser_matrix.length; i++) {
                d.non_intra_quantiser_matrix[i] = c.readUInt(8, d.non_intra_quantiser_matrix[i]);
            }
        }

        c.leaveBlock();
    }

    @Override
    public void write(PacketWriter<?> c) {
        write(c, this);
    }

    public static void write(PacketWriter<?> c,
                             M2VHeaderSequence d) {
        c.enterBlock("M2V sequence_header()");

        M2VHeader.write(c, d);

        c.writeUInt(12, d.horizontal_size_value      , "horizontal_size_value"      );
        c.writeUInt(12, d.vertical_size_value        , "vertical_size_value"        );
        c.writeUInt( 4, d.aspect_ratio_information   , "aspect_ratio_information"   , d.getAspectRatioName());
        c.writeUInt( 4, d.frame_rate_code            , "frame_rate_code"            , d.getFrameRateValueName());
        c.writeUInt(18, d.bit_rate_value             , "bit_rate_value"             );
        c.writeUInt( 1, d.marker_bit                 , "marker_bit"                 );
        c.writeUInt(10, d.vbv_buffer_size_value      , "vbv_buffer_size_value"      );
        c.writeUInt( 1, d.constrained_parameters_flag, "constrained_parameters_flag");

        c.writeUInt( 1, d.load_intra_quantiser_matrix    , "load_intra_quantiser_matrix");
        if (d.load_intra_quantiser_matrix.intValue() == 1) {
            for (int i = 0; i < d.intra_quantiser_matrix.length; i++) {
                c.writeUInt( 8, d.intra_quantiser_matrix[i], "intra_quantiser_matrix[" + i + "]");
            }
        }

        c.writeUInt( 1, d.load_non_intra_quantiser_matrix, "load_non_intra_quantiser_matrix");
        if (d.load_non_intra_quantiser_matrix.intValue() == 1) {
            for (int i = 0; i < d.non_intra_quantiser_matrix.length; i++) {
                c.writeUInt( 8, d.non_intra_quantiser_matrix[i], "non_intra_quantiser_matrix[" + i + "]");
            }
        }

        c.leaveBlock();
    }

    public String getAspectRatioName() {
        return M2VConsts.getAspectRatioName(aspect_ratio_information.intValue());
    }

    public String getFrameRateValueName() {
        return M2VConsts.getFrameRateValueName(frame_rate_code.intValue());
    }

    public float getFrameRateValueValue() {
        return M2VConsts.getFrameRateValueValue(frame_rate_code.intValue());
    }
}
