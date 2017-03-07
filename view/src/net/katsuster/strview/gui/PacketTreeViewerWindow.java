package net.katsuster.strview.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import net.katsuster.strview.media.*;

/**
 * <p>
 * ストリームの構造を表示するウインドウです。
 * </p>
 *
 * <p>
 * 下記の情報を表示します。
 * </p>
 *
 * <ul>
 * <li>ストリームを構成するパケットの一覧</li>
 * <li>各パケットを構成するデータの一覧</li>
 * </ul>
 *
 * @author katsuhiro
 */
public class PacketTreeViewerWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private File file;
    private LargePacketList<?> list_packet;

    private FlatTextField binaryToolText;
    private BinaryViewer binaryViewer;

    private FlatTextField packetToolText;
    private PacketTreeViewer packetTreeViewer;
    private PacketTreeSelListener packetTreeListener;
    private PacketListViewer packetListViewer;
    private PacketListSelListener packetListListener;
    private MemberTreeViewer memberTreeViewer;
    private MemberTreeSelListener memberTreeListener;
    private JTextArea memberTextViewer;

    public PacketTreeViewerWindow(File f, LargePacketList<?> l) {
        //表示するファイルを保持する
        file = f;
        list_packet = l;

        setTitle(f.getAbsolutePath());
        setResizable(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTransferHandler(new FileTransferHandler());

        //メニューを作成する
        JMenuBar topMenuBar = new JMenuBar();
        setJMenuBar(topMenuBar);
        JMenu menuFile = new JMenu("ファイル(F)");
        menuFile.setMnemonic('f');
        topMenuBar.add(menuFile);

        Action actionOpen = new MenuActionFileOpen(this, "開く(O)...");
        actionOpen.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
        menuFile.add(actionOpen);
        Action actionCount = new ActionCount("全てカウント(A)");
        actionCount.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        menuFile.add(actionCount);
        menuFile.addSeparator();
        Action actionClose = new MenuActionClose(this, "閉じる(C)");
        actionClose.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
        menuFile.add(actionClose);

        JMenu menuSetting = new JMenu("設定(S)");
        menuSetting.setMnemonic('s');
        topMenuBar.add(menuSetting);

        Action actionFont = new ActionFont(this, "フォント(F)...");
        actionFont.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
        menuSetting.add(actionFont);

        //ビューアペインを作成し追加する
        JSplitPane strViewer = createViewers();
        getContentPane().add(strViewer);
    }

    protected JSplitPane createViewers() {
        JSplitPane splitStreamViewer;
        JSplitPane splitPacketViewer;

        //ビューアを左右に分割表示する
        splitStreamViewer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitStreamViewer.setDividerSize(5);
        splitStreamViewer.setDividerLocation(500);

        //左側、パケットビューア
        splitPacketViewer = createPacketViewer();
        splitStreamViewer.setLeftComponent(splitPacketViewer);

        //右側、バイナリビューア
        JPanel binaryTool = new JPanel();
        binaryTool.setLayout(new FlowLayout(FlowLayout.LEFT));
        //binaryTool.add(new JLabel("Find: "));

        binaryToolText = new FlatTextField();
        binaryToolText.setPreferredSize(new Dimension(150, 22));
        binaryTool.add(binaryToolText);

        JButton btnGo = new JButton(new ActionGotoBinary("Go"));
        binaryTool.add(btnGo);

        binaryViewer = new BinaryViewer(getFile());
        binaryViewer.setFont(new Font(Font.MONOSPACED, 0, 12));

        JPanel binaryPanel = new JPanel();
        binaryPanel.setLayout(new BorderLayout());
        binaryPanel.add(binaryTool, BorderLayout.NORTH);
        binaryPanel.add(binaryViewer, BorderLayout.CENTER);
        splitStreamViewer.setRightComponent(binaryPanel);

        return splitStreamViewer;
    }

    protected JSplitPane createPacketViewer() {
        JScrollPane scrPacketViewer;

        //ビューアを上下に表示する
        JSplitPane splitPacketMember = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPacketMember.setDividerSize(5);
        splitPacketMember.setDividerLocation(200);

        //上側、パケットツリーまたはパケットリスト
        JPanel packetTool = new JPanel();
        packetTool.setLayout(new FlowLayout(FlowLayout.LEFT));

        packetToolText = new FlatTextField();
        packetToolText.setPreferredSize(new Dimension(150, 22));
        packetTool.add(packetToolText);

        JButton btnGo = new JButton(new ActionGotoPacket("Go"));
        packetTool.add(btnGo);
        JButton btnCount = new JButton(new ActionCount("Count"));
        packetTool.add(btnCount);

        if (getPacketList().hasTreeStructure()) {
            packetTreeViewer = new PacketTreeViewer(getPacketList());
            packetTreeListener = new PacketTreeSelListener();
            packetTreeViewer.getViewer().addMouseListener(packetTreeListener);
            packetTreeViewer.getViewer().addTreeSelectionListener(packetTreeListener);

            scrPacketViewer = new JScrollPane(packetTreeViewer);
            scrPacketViewer.getVerticalScrollBar().setUnitIncrement(10);
        } else {
            packetListViewer = new PacketListViewer(getPacketList());
            packetListListener = new PacketListSelListener();
            packetListViewer.getViewer().addMouseListener(packetListListener);
            packetListViewer.getViewer().addListSelectionListener(packetListListener);

            scrPacketViewer = new JScrollPane(packetListViewer);
            scrPacketViewer.getVerticalScrollBar().setUnitIncrement(10);
        }

        JPanel packetPanel = new JPanel();
        packetPanel.setLayout(new BorderLayout());
        packetPanel.add(packetTool, BorderLayout.NORTH);
        packetPanel.add(scrPacketViewer, BorderLayout.CENTER);

        splitPacketMember.setLeftComponent(packetPanel);

        //下側、メンバービューア（ツリー、テキストをタブで切り替え）
        JTabbedPane tabMembers = new JTabbedPane();
        splitPacketMember.setRightComponent(tabMembers);

        //メンバービューア（ツリー）
        memberTreeViewer = new MemberTreeViewer();
        memberTreeListener = new MemberTreeSelListener();
        JTree m_jt = memberTreeViewer.getViewer();
        m_jt.addTreeSelectionListener(memberTreeListener);
        m_jt.addMouseListener(memberTreeListener);
        m_jt.setFont(new Font(Font.MONOSPACED, 0, 12));

        JScrollPane scrMemberViewer = new JScrollPane(memberTreeViewer);
        scrMemberViewer.getVerticalScrollBar().setUnitIncrement(10);
        tabMembers.addTab("Tree", scrMemberViewer);

        //メンバービューア（テキスト）
        memberTextViewer = new JTextArea();
        memberTextViewer.setFont(new Font(Font.MONOSPACED, 0, 12));

        JScrollPane scrText = new JScrollPane(memberTextViewer);
        tabMembers.addTab("Text", scrText);

        return splitPacketMember;
    }

    public File getFile() {
        return file;
    }

    public LargePacketList<?> getPacketList() {
        return list_packet;
    }

    public class ActionFont extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private JFrame parent;

        public ActionFont(JFrame f, String name) {
            super(name);
            parent = f;
        }

        public ActionFont(JFrame f, String name, Icon icon) {
            super(name, icon);
            parent = f;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFontChooser chooser = new JFontChooser();

            chooser.setSelectedFont(binaryViewer.getFont());
            int res = chooser.showDialog(parent);
            if (res == JFontChooser.OK_OPTION) {
                binaryViewer.setFont(chooser.getSelectedFont());
            }
        }
    }

    public class ActionGotoPacket extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ActionGotoPacket(String name) {
            super(name);
        }

        public ActionGotoPacket(String name, Icon icon) {
            super(name, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            packetToolText.setForeground(Color.BLACK);

            try {
                long num = Long.parseLong(
                        packetToolText.getTextField().getText(), 10);

                if (getPacketList().hasTreeStructure()) {
                    //TODO: not implemented

                    //packetTreeViewer
                } else {
                    //TODO: not implemented

                    //packetListViewer.getViewer().setSelectedIndex((int)num);
                }
            } catch (NumberFormatException ex) {
                packetToolText.setForeground(Color.RED);
            }
        }
    }

    public class ActionCount extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ActionCount(String name) {
            super(name);
        }

        public ActionCount(String name, Icon icon) {
            super(name, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    list_packet.count();

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (packetTreeViewer != null) {
                                packetTreeViewer.setMax(1);
                            }
                            if (packetListViewer != null) {
                                packetListViewer.setMax(1);
                            }
                        }
                    });
                }
            });
            t.start();
        }
    }

    public class ActionGotoBinary extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ActionGotoBinary(String name) {
            super(name);
        }

        public ActionGotoBinary(String name, Icon icon) {
            super(name, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            binaryToolText.setForeground(Color.BLACK);

            try {
                long addr = Long.parseLong(
                        binaryToolText.getTextField().getText(), 16);

                binaryViewer.setAddress(addr);
                binaryViewer.repaint();
            } catch (NumberFormatException ex) {
                binaryToolText.setForeground(Color.RED);
            }
        }
    }

    public class PacketTreeSelListener extends SelListener
            implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath selPath = e.getPath();
            long selRow = getIndexFromEvent(selPath);

            if (selRow != -1) {
                onLeftSingleClick(selRow);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            TreePath selPath = packetTreeViewer.getViewer().getPathForLocation(
                    e.getX(), e.getY());
            long selRow = getIndexFromEvent(selPath);

            if (selRow != -1) {
                if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON1) {
                    //左シングルのとき
                    //onLeftSingleClick(selRow);
                } else if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON3) {
                    //右シングルのとき
                    //onRightSingleClick(selRow);
                } else if (e.getClickCount() == 2
                        && e.getButton() == MouseEvent.BUTTON1) {
                    //左ダブルのとき
                    onLeftDoubleClick(selRow);
                }
            }
        }

        protected long getIndexFromEvent(TreePath selPath) {
            if (selPath == null || !(selPath.getLastPathComponent()
                    instanceof DefaultMutableTreeNode)) {
                return -1;
            }

            DefaultMutableTreeNode n = (DefaultMutableTreeNode)
                    selPath.getLastPathComponent();
            if (!(n.getUserObject() instanceof PacketTreeNode)) {
                return -1;
            }

            PacketTreeNode pn = (PacketTreeNode)n.getUserObject();
            PacketRange pr = pn.getRange();
            if (pr == null) {
                return -1;
            }

            return pr.getNumber();
        }
    }

    public class PacketListSelListener extends SelListener
            implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList l = (JList)e.getSource();

            onLeftSingleClick(l.getLeadSelectionIndex());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            long selRow = packetListViewer.getRowForLocation(
                    e.getX(), e.getY());

            if (selRow != -1) {
                if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON1) {
                    //左シングルのとき
                    //onLeftSingleClick(selRow);
                } else if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON3) {
                    //右シングルのとき
                    //onRightSingleClick(selRow);
                } else if (e.getClickCount() == 2
                        && e.getButton() == MouseEvent.BUTTON1) {
                    //左ダブルのとき
                    onLeftDoubleClick(selRow);
                }
            }
        }
    }

    public class SelListener extends MouseAdapter {
        public void onLeftSingleClick(long selRow) {
            Packet p = getPacketFromEvent(selRow);
            if (p == null) {
                return;
            }

            //ノードの文字列表現を表示する
            memberTextViewer.setText(p.toString());
            memberTextViewer.repaint();

            //ノードのツリー表現を表示する
            PacketWriter<MemberTreeNode> c = new ToMemberTreeNodeConverter();
            p.write(c);
            MemberTreeNode root = c.getResult();
            root.setName(getPacketList().getShortName());
            memberTreeViewer.setRootTreeNode(root);
            for (int row = 0; row < memberTreeViewer.getViewer().getRowCount(); row++) {
                memberTreeViewer.getViewer().expandRow(row);
            }
            memberTreeViewer.repaint();

            //ノードが指すデータ範囲をハイライトする
            if (p.getRawPacket() != null) {
                PacketRange pr = p.getRange();

                binaryViewer.setHighlightMemberRange(0, 0);
                binaryViewer.setHighlightRange(
                        pr.getStart() >>> 3, pr.getLength() >>> 3);
                binaryViewer.repaint();
            }
        }

        public void onLeftDoubleClick(long selRow) {
            Packet p = getPacketFromEvent(selRow);
            if (p == null) {
                return;
            }

            //ノードが指すデータの先頭にジャンプする
            binaryViewer.setAddress(p.getRange().getStart() >>> 3);
            binaryViewer.repaint();
        }

        protected Packet getPacketFromEvent(long selRow) {
            return getPacketList().get(selRow);
        }
    }

    public class MemberTreeSelListener extends MouseAdapter
            implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            onLeftSingleClick(0, e.getPath());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int selRow = memberTreeViewer.getViewer().getRowForLocation(
                    e.getX(), e.getY());
            TreePath selPath = memberTreeViewer.getViewer().getPathForLocation(
                    e.getX(), e.getY());

            if (selRow != -1) {
                if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON1) {
                    //左シングルのとき
                    //onLeftSingleClick(selRow, selPath);
                } else if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON3) {
                    //右シングルのとき
                    //onRightSingleClick(selRow, selPath);
                } else if (e.getClickCount() == 2
                        && e.getButton() == MouseEvent.BUTTON1) {
                    //左ダブルのとき
                    onLeftDoubleClick(selRow, selPath);
                }
            }
        }

        public void onLeftSingleClick(int selRow, TreePath selPath) {
            MemberTreeNode ptn = getPacketTreeNodeFromEvent(selPath);
            if (ptn == null) {
                return;
            }

            //メンバが指すデータ範囲をハイライトする
            if (ptn.hasNumData() || ptn.hasArrayData()) {
                long len = ptn.getDataLength();
                if ((len != 0) && (len % 8 != 0)) {
                    //bit | bit+7 | byte
                    //----+-------+-----
                    //0   | 除外  | 0
                    //1   | 8     | 1
                    //2   | 9     | 1
                    //7   | 14    | 1
                    //8   | 15    | 1
                    //9   | 16    | 2
                    len += 7;
                }

                binaryViewer.setHighlightMemberRange(
                        ptn.getDataStart() >>> 3, len >>> 3);
            } else {
                binaryViewer.setHighlightMemberRange(0, 0);
            }

            binaryViewer.repaint();
        }

        public void onLeftDoubleClick(int selRow, TreePath selPath) {
            MemberTreeNode ptn = getPacketTreeNodeFromEvent(selPath);
            if (ptn == null) {
                return;
            }

            //ノードが指すデータの先頭にジャンプする
            if (ptn.hasNumData() || ptn.hasArrayData()) {
                binaryViewer.setAddress(ptn.getDataStart() >>> 3);
                binaryViewer.repaint();
            }
        }

        protected MemberTreeNode getPacketTreeNodeFromEvent(TreePath selPath) {
            if (!(selPath.getLastPathComponent() instanceof MemberTreeNode)) {
                return null;
            }

            return (MemberTreeNode)selPath.getLastPathComponent();
        }
    }
}
