package net.katsuster.strview.gui;

import java.io.*;
import java.util.*;

import java.awt.HeadlessException;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.*;

import net.katsuster.strview.util.*;
import net.katsuster.strview.io.*;
import net.katsuster.strview.media.*;
import net.katsuster.strview.media.m2v.*;
import net.katsuster.strview.media.mkv.*;
import net.katsuster.strview.media.ps.*;
import net.katsuster.strview.media.riff.*;
import net.katsuster.strview.media.ts.*;

/**
 * <p>
 * ファイルのドラッグ＆ドロップイベントを処理するクラスです。
 * </p>
 *
 * @author katsuhiro
 */
public class FileTransferHandler extends TransferHandler {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferSupport support) {
        Transferable trans = support.getTransferable();
        Object tdata;
        List<Object> tlist;

        try {
            //ファイルリスト以外を渡された場合はドロップを拒否します
            if (!canImport(support)) {
                return false;
            }

            //ファイルリストの取得ができなければドロップを拒否します
            tdata = trans.getTransferData(DataFlavor.javaFileListFlavor);
            tlist = (List<Object>)tdata;
        } catch (Exception ex) {
            DebugInfo.printFunctionName(this);
            ex.printStackTrace();

            return false;
        }

        for (Object o: tlist) {
            //ファイル以外は無視します
            if (!(o instanceof File)) {
                continue;
            }
            File tfile = (File)o;
            if (!(tfile.isFile())) {
                continue;
            }

            //ファイルを解析します
            boolean result = openFile(tfile);
            if (!result) {
                System.err.println("open '" + tfile + "' is failed.");
                continue;
            }
        }

        return true;
    }

    /**
     * <p>
     * ファイルを開きます。
     * </p>
     *
     * <p>
     * 解析が可能なファイルだと判断した場合は、
     * ストリームの構造を表示するウインドウが開きます。
     * 解析が不可能なファイルだと判断した場合は、
     * バイナリデータを表示するウインドウが開きます。
     * </p>
     *
     * @param tfile ファイル
     * @return ファイルを開けた場合は true、ファイルを開けなかった場合は false
     */
    public boolean openFile(File tfile) {
        System.out.println(tfile);

        try {
            LargeBitList blist = new ByteToBitList(new FileByteList(tfile.getAbsolutePath()));
            LargeList<? extends Packet> list = getPacketList(getFileType(tfile), blist);
            JFrame w;

            if (list != null) {
                PacketTreeViewerWindow pw = new PacketTreeViewerWindow(tfile, list);
                w = pw;
            } else {
                BinaryViewerWindow bw = new BinaryViewerWindow(tfile);
                w = bw;
            }

            w.setSize(1024, 720);
            w.setVisible(true);
        } catch (HeadlessException ex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    enum FILE_TYPE {
        FT_UNKNOWN,
        FT_MPEG2PS,
        FT_MPEG2TS,
        FT_MATROSKA,
        FT_RIFF,
        FT_MPEG2VIDEO,
    }

    /**
     * <p>
     * 指定されたファイル形式に適したパケットリストを生成します。
     * </p>
     *
     * @param t ファイル形式
     * @param l パケットリストの元データとなるビットリスト
     * @return パケットリスト
     */
    public LargeList<? extends Packet> getPacketList(FILE_TYPE t, LargeBitList l) {
        LargeList<? extends Packet> list = null;

        switch (t) {
        case FT_MPEG2PS:
            list = new PSPackList(l);
            break;
        case FT_MPEG2TS:
            list = new TSPacketList(l);
            break;
        case FT_MATROSKA:
            list = new MKVTagList(l);
            break;
        case FT_RIFF:
            list = new RIFFChunkList(l);
            break;
        case FT_MPEG2VIDEO:
            list = new M2VDataList(l);
            break;
        case FT_UNKNOWN:
            list = null;
            break;
        }

        return list;
    }

    /**
     * <p>
     * ファイル形式を類推します。
     * </p>
     *
     * @param tfile ファイル
     * @return ファイル形式
     */
    public FILE_TYPE getFileType(File tfile) {
        String ext = getSuffix(tfile.getPath());

        if (ext.equals("ps") || ext.equals("vob")) {
            return FILE_TYPE.FT_MPEG2PS;
        }
        if (ext.equals("ts")) {
            return FILE_TYPE.FT_MPEG2TS;
        }
        if (ext.equals("mkv")) {
            return FILE_TYPE.FT_MATROSKA;
        }
        if (ext.equals("avi") || ext.equals("cur") || ext.equals("ico") || ext.equals("wav")) {
            return FILE_TYPE.FT_RIFF;
        }
        if (ext.equals("m2v")) {
            return FILE_TYPE.FT_MPEG2VIDEO;
        }

        return FILE_TYPE.FT_UNKNOWN;
    }

    /**
     * <p>
     * ファイル名の拡張子を取得します。
     * </p>
     *
     * @param n ファイル名
     * @return ファイルの拡張子、なければ空文字列
     */
    public String getSuffix(String n) {
        if (n == null) {
            return null;
        }

        int dot = n.lastIndexOf(".");
        if (dot != -1) {
            return n.substring(dot + 1);
        }
        return "";
    }
}
