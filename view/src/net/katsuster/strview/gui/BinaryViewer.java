package net.katsuster.strview.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import net.katsuster.strview.util.*;
import net.katsuster.strview.util.gui.*;
import net.katsuster.strview.io.*;

/**
 * <p>
 * バイナリデータを表示するビューアです。
 * </p>
 *
 * @author katsuhiro
 */
public class BinaryViewer extends JPanel {
    private static final long serialVersionUID = 1L;

    //バイナリデータ表示パネル
    private BinaryViewerInner viewer;
    //右端のスクロールバー
    private JScrollBar scr;

    //表示させるビットデータ
    private LargeBitList buf;

    //表示開始するアドレス（バイト単位）
    private long addr_start;
    //1列に表示させるバイト数
    private int length_raw;
    //1列の高さ（ピクセル）
    private int height_raw;

    //表示するデータ範囲
    private Range[] ranges;
    //アドレスの色
    private Color color_addr;
    //表示する色
    private Color[] colors;

    //表示領域
    private ContentBox box_all;
    //アドレス表示領域
    private ContentBox box_addr_area;
    //アドレス（1列）表示領域
    private ContentBox box_addr;
    //データ表示領域
    private ContentBox box_data_area;
    //データ（1列）表示領域
    private ContentBox box_data_raw;
    //データ（1バイト）表示領域
    private ContentBox box_data;
    //文字列表示領域
    private ContentBox box_ascii_area;
    //データ（1列）表示領域
    private ContentBox box_ascii_raw;
    //文字列（1文字）表示領域
    private ContentBox box_ascii;

    protected BinaryViewer() {
        super();

        //do nothing
    }

    public BinaryViewer(String filename) {
        this(new ByteToBitList(new FileByteList(filename)));
    }

    public BinaryViewer(LargeBitList l) {
        super();

        int i;

        //表示するバイナリデータを初期化する
        buf = null;

        //表示するデータの先頭アドレス（バイト単位）
        addr_start = 0;
        //1列に表示するバイト数
        length_raw = 16;
        //列の表示領域の高さ
        height_raw = 16;

        //表示する範囲を初期化する
        ranges = new Range[PRIORITY.MAX];
        for (i = 0; i < PRIORITY.MAX; i++) {
            ranges[i] = new SimpleRange(0, 0);
        }
        setDataRange(new SimpleRange(0, 0));
        setHighlightRange(new SimpleRange(0, 0));
        setHighlightMemberRange(new SimpleRange(0, 0));

        //フォントの色を初期化する
        color_addr = Color.BLACK;
        colors = new Color[PRIORITY.MAX];
        for (i = 0; i < PRIORITY.MAX; i++) {
            colors[i] = Color.BLACK;
        }
        setDataColor(Color.BLACK);
        setHighlightColor(Color.RED);
        setHighlightMemberColor(Color.BLUE);

        //表示領域を初期化する
        box_all = new ContentBox();
        box_addr_area = new ContentBox();
        box_addr = new ContentBox();
        box_data_area = new ContentBox();
        box_data_raw = new ContentBox();
        box_data = new ContentBox();
        box_ascii_area = new ContentBox();
        box_ascii_raw = new ContentBox();
        box_ascii = new ContentBox();

        box_all.setMargin(5, 0, 5, 0);
        box_addr_area.setPadding(5, 0, 5, 0);
        box_data_area.setPadding(5, 0, 5, 0);
        box_ascii_area.setPadding(5, 0, 5, 0);


        //背景色を設定する
        setBackground(Color.WHITE);

        //マウスホイール、リサイズイベント、リスナを追加する
        addMouseWheelListener(new BinaryViewerWheelListener());
        addComponentListener(new BinaryViewerComponentListener());


        //子パネル追加
        setLayout(new BorderLayout());

        //中央にバイナリデータ表示パネルを配置する
        viewer = new BinaryViewerInner();
        add(viewer, BorderLayout.CENTER);

        //右端にスクロールバーを配置する
        scr = new JScrollBar();
        scr.getModel().addChangeListener(viewer);
        add(scr, BorderLayout.EAST);


        //表示するファイルを設定する
        //スクロールバーの最大値をいじるので、GUI 作成の後でないとだめ
        setBinary(l);
    }

    /**
     * <p>
     * バイナリデータの読み出し元のビット列を取得します。
     * </p>
     *
     * @return データの格納されているビット列
     */
    public LargeBitList getBinary() {
        return buf;
    }

    /**
     * <p>
     * バイナリデータの読み出し元のビット列を設定します。
     * </p>
     *
     * @param l データの格納されているビット列
     */
    public void setBinary(LargeBitList l) {
        long lines;

        buf = l;

        //全域を通常のデータ表示領域とする
        setDataRange(new SimpleRange(0, getLength()));

        //1行（getLengthOfRaw() バイト）ずつスクロールできるように、
        //スクロールバー最大値はファイルの行数とする
        lines = getLength() / getLengthOfRaw() + 1;
        if (Integer.MAX_VALUE < lines) {
            scr.setMaximum(Integer.MAX_VALUE);
        } else {
            scr.setMaximum((int)lines);
        }
    }

    /**
     * <p>
     * バイナリデータのサイズを取得します。
     * </p>
     *
     * @return サイズ（バイト単位）
     */
    protected long getLength() {
        if (buf == null) {
            return 0;
        }

        return (buf.length() >>> 3);
    }

    /**
     * <p>
     * 表示開始するアドレスを取得します。
     * </p>
     *
     * @return 表示開始するアドレス（バイト単位）
     */
    public long getAddress() {
        return addr_start;
    }

    /**
     * <p>
     * 表示開始するアドレスを設定します。
     * </p>
     *
     * @param addr 表示開始するアドレス（バイト単位）
     */
    public void setAddress(long addr) {
        if (getLength() < addr) {
            addr = getLength();
        }

        addr_start = addr;
    }

    /**
     * <p>
     * 指定した列の先頭アドレス（バイト単位）を取得します。
     * </p>
     *
     * @param raw 列
     * @return 列の先頭アドレス
     */
    protected long rawToAddress(long raw) {
        long lines;
        double pos;

        lines = getLength() / getLengthOfRaw() + 1;
        pos = (double)lines * scr.getValue() / scr.getMaximum();

        return (long)(pos * getLengthOfRaw());
    }

    /**
     * <p>
     * 表示開始する列を取得します。
     * </p>
     *
     * @return 表示開始する行
     */
    public long getRaw() {
        return scr.getValue();
    }

    /**
     * <p>
     * 表示開始する列を設定します。
     * </p>
     *
     * @param raw 表示開始する行
     */
    public void setRaw(long raw) {
        scr.setValue((int)raw);
    }

    /**
     * <p>
     * 一列に表示させるバイト数を取得します。
     * </p>
     *
     * @return 一列に表示させるバイト数
     */
    public int getLengthOfRaw() {
        return length_raw;
    }

    /**
     * <p>
     * 一列に表示させるバイト数を設定します。
     * </p>
     *
     * @param w 一列に表示させるバイト数
     */
    public void setLengthOfRaw(int w) {
        length_raw = w;
    }

    /**
     * <p>
     * 一列の高さ（ピクセル数）を取得します。
     * </p>
     *
     * @return 一列の高さ（ピクセル数）
     */
    public int getHeightOfRaw() {
        return height_raw;
    }

    /**
     * <p>
     * 一列の高さ（ピクセル数）を取得します。
     * </p>
     *
     * @param h 一列の高さ（ピクセル数）
     */
    public void setHeightOfRaw(int h) {
        height_raw = h;
    }

    /**
     * <p>
     * 指定したアドレスを描画する色を取得します。
     * </p>
     *
     * @param a アドレス
     * @return 色
     */
    public Color getAddressColor(long a) {
        return color_addr;
    }

    /**
     * <p>
     * 指定したアドレス範囲を描画する色を設定します。
     * </p>
     *
     * @param r アドレス範囲
     * @param c 色
     */
    public void setAddressColor(Range r, Color c) {
        color_addr = c;
    }

    /**
     * <p>
     * バイナリデータを通常描画する際の色を取得します。
     * </p>
     *
     * @return バイナリデータを通常描画する際の色
     */
    public Color getDataColor() {
        return colors[PRIORITY.NORMAL];
    }

    /**
     * <p>
     * バイナリデータを通常描画する際の色を設定します。
     * </p>
     *
     * @param c バイナリデータを通常描画する際の色
     */
    public void setDataColor(Color c) {
        colors[PRIORITY.NORMAL] = c;
    }

    /**
     * <p>
     * バイナリデータを通常描画する範囲を設定します。
     * </p>
     *
     * @return バイナリデータを通常描画する範囲
     */
    public Range getDataRange() {
        return ranges[PRIORITY.NORMAL];
    }

    /**
     * <p>
     * バイナリデータを通常描画する範囲を設定します。
     * </p>
     *
     * @param r バイナリデータを通常描画する範囲
     */
    public void setDataRange(Range r) {
        ranges[PRIORITY.NORMAL] = r;
    }

    /**
     * <p>
     * バイナリデータを通常描画する範囲を設定します。
     * </p>
     *
     * @param s バイナリデータを通常描画する範囲の開始
     * @param l バイナリデータを通常描画する範囲の長さ
     */
    public void setDataRange(long s, long l) {
        setDataRange(new SimpleRange(s, l));
    }

    /**
     * <p>
     * バイナリデータを強調して描画する際の色を取得します。
     * </p>
     *
     * @return バイナリデータを強調して描画する際の色
     */
    public Color getHighlightColor() {
        return colors[PRIORITY.HIGHLIGHT_NODE];
    }

    /**
     * <p>
     * バイナリデータを強調して描画する際の色を設定します。
     * </p>
     *
     * @param c バイナリデータを強調して描画する際の色
     */
    public void setHighlightColor(Color c) {
        colors[PRIORITY.HIGHLIGHT_NODE] = c;
    }

    /**
     * <p>
     * バイナリデータを強調して描画する範囲を取得します。
     * </p>
     *
     * @return バイナリデータを強調して描画する範囲
     */
    public Range getHighlightRange() {
        return ranges[PRIORITY.HIGHLIGHT_NODE];
    }

    /**
     * <p>
     * バイナリデータを強調して描画する範囲を設定します。
     * </p>
     *
     * @param r バイナリデータを強調して描画する範囲
     */
    public void setHighlightRange(Range r) {
        ranges[PRIORITY.HIGHLIGHT_NODE] = r;
    }

    /**
     * <p>
     * バイナリデータを強調して描画する範囲を設定します。
     * </p>
     *
     * @param s バイナリデータを強調して描画する範囲の開始
     * @param l バイナリデータを強調して描画する範囲の長さ
     */
    public void setHighlightRange(long s, long l) {
        setHighlightRange(new SimpleRange(s, l));
    }

    /**
     * <p>
     * バイナリデータをさらに強調して描画する際の色を取得します。
     * </p>
     *
     * @return バイナリデータをさらに強調して描画する際の色
     */
    public Color getHighlightMemberColor() {
        return colors[PRIORITY.HIGHLIGHT_MEMBER];
    }

    /**
     * <p>
     * バイナリデータをさらに強調して描画する際の色を設定します。
     * </p>
     *
     * @param c バイナリデータをさらに強調して描画する際の色
     */
    public void setHighlightMemberColor(Color c) {
        colors[PRIORITY.HIGHLIGHT_MEMBER] = c;
    }

    /**
     * <p>
     * バイナリデータをさらに強調して描画する範囲を取得します。
     * </p>
     *
     * @return バイナリデータをさらに強調して描画する範囲
     */
    public Range getHighlightMemberRange() {
        return ranges[PRIORITY.HIGHLIGHT_MEMBER];
    }

    /**
     * <p>
     * バイナリデータをさらに強調して描画する範囲を設定します。
     * </p>
     *
     * @param r バイナリデータをさらに強調して描画する範囲
     */
    public void setHighlightMemberRange(Range r) {
        ranges[PRIORITY.HIGHLIGHT_MEMBER] = r;
    }

    /**
     * <p>
     * バイナリデータをさらに強調して描画する範囲を設定します。
     * </p>
     *
     * @param s バイナリデータをさらに強調して描画する範囲の開始
     * @param l バイナリデータをさらに強調して描画する範囲の長さ
     */
    public void setHighlightMemberRange(long s, long l) {
        setHighlightMemberRange(new SimpleRange(s, l));
    }

    protected class BinaryViewerWheelListener
            implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            scr.setValue(scr.getValue() + e.getWheelRotation() * 3);
        }
    }

    protected class BinaryViewerComponentListener
            implements ComponentListener {
        @Override
        public void componentHidden(ComponentEvent e) {
            //do nothing
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            //do nothing
        }

        @Override
        public void componentResized(ComponentEvent e) {
            Rectangle all_contents;

            //表示領域を更新する
            box_all.setWidth(viewer.getWidth());
            box_all.setHeight(viewer.getHeight());

            all_contents = box_all.getContents();
            box_addr_area.setHeight(all_contents.height);
            box_data_area.setHeight(all_contents.height);
            box_ascii_area.setHeight(all_contents.height);

            //スクロールバーの extent = 一画面に入る行数とする
            scr.getModel().setExtent(
                    all_contents.height / box_addr_area.getHeight());

            viewer.repaint();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            //do nothing
        }
    }

    /**
     * <p>
     * バイナリデータを描画するパネルです。
     * </p>
     *
     * @author katsuhiro
     */
    protected class BinaryViewerInner extends JComponent
            implements ChangeListener {
        private static final long serialVersionUID = 1L;

        public BinaryViewerInner() {
            super();
        }

        protected void drawAll(Graphics g, long addr) {
            FontMetrics met;
            Rectangle all_contents;
            int width_f, height_f, dx, dy;

            //フォントメトリックから表示領域の大きさを計算する
            met = g.getFontMetrics();

            //表示領域
            width_f = met.stringWidth("F");
            height_f = Math.max(met.getHeight(), getHeightOfRaw());
            box_addr_area.setWidth(width_f * 10);
            box_addr.setWidth(width_f * 10);
            box_addr.setHeight(height_f);

            box_data_area.setWidth(width_f * 3 * (getLengthOfRaw() + 1));
            box_data_raw.setWidth(width_f * 3 * (getLengthOfRaw() + 1));
            box_data_raw.setHeight(height_f);
            box_data.setWidth(width_f * 3);
            box_data.setHeight(height_f);

            box_ascii_area.setWidth(width_f * 1 * (getLengthOfRaw() + 1));
            box_ascii_raw.setWidth(width_f * 1 * (getLengthOfRaw() + 1));
            box_ascii_raw.setHeight(height_f);
            box_ascii.setWidth(width_f * 1);
            box_ascii.setHeight(height_f);


            //マージンを除いた領域を描画に使う
            all_contents = box_all.getContents();
            dx = all_contents.x;
            dy = all_contents.y;

            //アドレス、データ、文字列表現を描画する
            box_addr_area.setX(dx);
            box_addr_area.setY(dy);
            drawAddressAll(g, addr);
            dx += box_addr_area.getWidth();

            box_data_area.setX(dx);
            box_data_area.setY(dy);
            drawDataAll(g, addr);
            dx += box_data_area.getWidth();

            box_ascii_area.setX(dx);
            box_ascii_area.setY(dy);
            drawAsciiAll(g, addr);
            dx += box_ascii_area.getWidth();
        }

        protected void drawAddressAll(Graphics g, long addr) {
            Rectangle all_addr;
            //Rectangle borders_addr;
            int dx, dy;

            //マージンを除いた領域を描画に使う
            all_addr = box_addr_area.getContents();
            dx = all_addr.x;
            dy = all_addr.y;

            while (dy < all_addr.height) {
                box_addr.setX(dx);
                box_addr.setY(dy);
                drawAddress(g, addr);

                addr += getLengthOfRaw();
                dy += box_addr.getHeight();
            }

            //枠を描画
            //borders_addr = box_addr_area.getBorder();
            //g.drawRoundRect(borders_addr.x, borders_addr.y,
            //		borders_addr.width, borders_addr.height,
            //		14, 14);
        }

        /**
         * <p>
         * 適切な色を選択しながら指定されたアドレスを描画します。
         * </p>
         *
         * <p>
         * アドレスを描画するサイズ、マージンの設定については、
         * TODO: (to be determined)
         * を使用してください。
         * </p>
         *
         * @param g    描画を行うグラフィクス
         * @param addr アドレス（バイト単位）
         */
        protected void drawAddress(Graphics g, long addr) {
            Rectangle raw_addr;
            Color c;
            String str;

            //マージンを除いた領域を描画に使う
            raw_addr = box_addr.getContents();

            //文字列の色に設定する
            c = g.getColor();
            g.setColor(getAddressColor(addr));

            //描画する
            //drawString の Y 座標は文字のベースラインの位置のため、
            //本来ベースラインから文字の頭までの高さを足すべきだが、
            //簡易処理として描画領域の高さを足している
            str = String.format("%08x", addr);
            g.drawString(str, raw_addr.x, raw_addr.y + raw_addr.height);

            //元の色に戻す
            g.setColor(c);
        }

        protected void drawDataAll(Graphics g, long addr) {
            Rectangle all_data;
            //Rectangle borders_data;
            int dx, dy;

            //マージンを除いた領域を描画に使う
            all_data = box_data_area.getContents();
            dx = all_data.x;
            dy = all_data.y;

            while (dy < all_data.height) {
                box_data_raw.setX(dx);
                box_data_raw.setY(dy);
                drawDataRaw(g, addr);

                addr += getLengthOfRaw();
                dy += box_data_raw.getHeight();
            }

            //枠を描画
            //borders_data = box_data_area.getBorder();
            //g.drawRoundRect(borders_data.x, borders_data.y,
            //		borders_data.width, borders_data.height,
            //		14, 14);
        }

        protected void drawDataRaw(Graphics g, long addr) {
            Rectangle raw_data;
            Color c;
            byte[] buf_data;
            int buflen, dx, dy;
            int i;

            //1列分のデータリード
            buflen = (int)Math.min(getLengthOfRaw(), getLength() - addr);
            buflen = Math.max(buflen, 0);
            buf_data = new byte[getLengthOfRaw()];
            if (buflen > 0) {
                buf.getPackedByteArray(addr << 3, buf_data, 0, buflen << 3);
                //for (j = 0; j < buflen; j++) {
                //	buf_data[j] = buf.getPackedByte((addr + j) << 3, 8);
                //}
            }

            //マージンを除いた領域を描画に使う
            raw_data = box_data_raw.getContents();
            dx = raw_data.x;
            dy = raw_data.y;

            //元の色を覚えておく
            c = g.getColor();

            //1列分のデータ描画
            //1列分に満たない部分は ".." で埋める
            for (i = 0; i < getLengthOfRaw(); i++) {
                box_data.setX(dx);
                box_data.setY(dy);

                if (i < buflen) {
                    drawData(g, addr + i, buf_data[i]);
                } else {
                    drawDataOther(g, "..");
                }
                dx += box_data.getWidth();

                //8バイトごとに区切り文字を入れる
                if ((i & 7) == 7 && (i + 1 < getLengthOfRaw())) {
                    box_data.setX(dx);
                    box_data.setY(dy);
                    drawDataOther(g, "-");

                    dx += box_data.getWidth() / 2;
                }
            }

            //元の色に戻す
            g.setColor(c);
        }

        /**
         * <p>
         * 適切な色を選択しながら指定されたデータを描画します。
         * </p>
         *
         * <p>
         * データを描画するサイズ、マージンの設定については、
         * TODO: (to be determined)
         * を使用してください。
         * </p>
         *
         * @param g    描画を行うグラフィクス
         * @param addr アドレス
         * @param data 描画するデータ
         */
        protected void drawData(Graphics g, long addr, byte data) {
            Rectangle one_data;
            String str;

            //マージンを除いた領域を描画に使う
            one_data = box_data.getContents();

            //文字列の色を設定する
            g.setColor(getRangeColor(addr));

            //描画する
            //drawString の Y 座標は文字のベースラインの位置のため、
            //本来ベースラインから文字の頭までの高さを足すべきだが、
            //簡易処理として描画領域の高さを足している
            str = String.format("%02x", data & 0xff);
            g.drawString(str, one_data.x, one_data.y + one_data.height);
        }

        protected void drawDataOther(Graphics g, String s) {
            Rectangle one_data;

            //マージンを除いた領域を描画に使う
            one_data = box_data.getContents();

            //その他のデータの色に設定する
            g.setColor(Color.BLACK);

            //描画する
            //drawString の Y 座標は文字のベースラインの位置のため、
            //本来ベースラインから文字の頭までの高さを足すべきだが、
            //簡易処理として描画領域の高さを足している
            g.drawString(s, one_data.x, one_data.y + one_data.height);
        }

        protected void drawAsciiAll(Graphics g, long addr) {
            Rectangle all_ascii;
            //Rectangle borders_ascii;
            int dx, dy;

            //マージンを除いた領域を描画に使う
            all_ascii = box_ascii_area.getContents();
            dx = all_ascii.x;
            dy = all_ascii.y;

            while (dy < all_ascii.height) {
                box_ascii_raw.setX(dx);
                box_ascii_raw.setY(dy);
                drawAsciiRaw(g, addr);

                addr += getLengthOfRaw();
                dy += box_ascii_raw.getHeight();
            }

            //枠を描画
            //borders_ascii = box_ascii_area.getBorder();
            //g.drawRoundRect(borders_ascii.x, borders_ascii.y,
            //		borders_ascii.width, borders_ascii.height,
            //		14, 14);
        }

        protected void drawAsciiRaw(Graphics g, long addr) {
            Rectangle raw_ascii;
            Color c;
            byte[] buf_data, buf_char;
            String str_char;
            int buflen, dx, dy;
            int i;

            //1列分のデータを文字列に変換
            buflen = (int)Math.min(getLengthOfRaw(), getLength() - addr);
            buflen = Math.max(buflen, 0);
            buf_data = new byte[getLengthOfRaw()];
            buf_char = new byte[getLengthOfRaw()];
            str_char = "";
            try {
                if (buflen > 0) {
                    buf.getPackedByteArray(addr << 3, buf_data, 0, buflen << 3);
                    //for (j = 0; j < buflen; j++) {
                    //	buf_data[j] = buf.getPackedByte((addr + j) << 3, 8);
                    //}
                }

                for (i = 0; i < buf_data.length; i++) {
                    if (0x20 <= (buf_data[i] & 0xff)
                            && (buf_data[i] & 0xff) <= 0x7e) {
                        buf_char[i] = buf_data[i];
                    } else {
                        buf_char[i] = 0x2e;
                    }
                }
                str_char = new String(buf_char, "US-ASCII");
            } catch (IOException ex) {
                //do nothing
            }

            //マージンを除いた領域を描画に使う
            raw_ascii = box_ascii_raw.getContents();
            dx = raw_ascii.x;
            dy = raw_ascii.y;

            //元の色を記憶しておく
            c = g.getColor();

            //1列分の文字列を描画する
            //1列分に満たない部分は空白とする
            for (i = 0; i < getLengthOfRaw(); i++) {
                box_ascii.setX(dx);
                box_ascii.setY(dy);
                if (i < buflen) {
                    drawAscii(g, addr + i, str_char.substring(i, i + 1));
                }

                dx += box_ascii.getWidth();
            }

            //元の色に戻す
            g.setColor(c);
        }

        /**
         * <p>
         * 適切な色を選択しながら指定されたデータの文字列表現を描画します。
         * </p>
         *
         * <p>
         * データの文字列表現を描画するサイズ、マージンの設定については、
         * TODO: (to be determined)
         * を使用してください。
         * </p>
         *
         * @param g    描画を行うグラフィクス
         * @param addr アドレス
         * @param s    描画するデータの文字列表現
         */
        protected void drawAscii(Graphics g, long addr, String s) {
            Rectangle one_ascii;

            //マージンを除いた領域を描画に使う
            one_ascii = box_ascii.getContents();

            //文字列の色を設定する
            g.setColor(getRangeColor(addr));

            //描画する
            //drawString の Y 座標は文字のベースラインの位置のため、
            //本来ベースラインから文字の頭までの高さを足すべきだが、
            //簡易処理として描画領域の高さを足している
            g.drawString(s, one_ascii.x, one_ascii.y + one_ascii.height);
        }

        /**
         * <p>
         * 指定したアドレス、データを描画する色を取得します。
         * </p>
         *
         * <p>
         * 見つからなければ黒を返します。
         * </p>
         *
         * @param addr 描画するアドレス
         * @return 描画に使う色
         */
        private Color getRangeColor(long addr) {
            Color c;
            int i;

            c = Color.BLACK;
            for (i = 0; i < PRIORITY.MAX; i++) {
                if (ranges[i].isHit(addr)) {
                    c = colors[i];
                    break;
                }
            }

            return c;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            setAddress(rawToAddress(scr.getValue()));

            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            if (buf == null) {
                //do nothing
                return;
            }

            drawAll(g, getAddress());
        }
    }

    /**
     * <p>
     * 範囲の優先度の定義です。小さい番号ほど優先度が高くなります。
     * </p>
     */
    public static class PRIORITY {
        public static final int HIGHLIGHT_MEMBER = 0;
        public static final int HIGHLIGHT_NODE   = 1;
        public static final int NORMAL           = 2;
        public static final int MAX              = 3;
    }
}
