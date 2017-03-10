package net.katsuster.strview.autogen;

import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * <p>
 * ISO13818 の データ構造記法を
 * スケルトンコードに変換する自動生成ツールです。
 * </p>
 *
 * <p>
 * スケルトンコードでは、基本的な型を持つメンバについて、
 * メンバ変数定義、ディープコピーを行う clone 関数、
 * メンバを読み取る read 関数、write 関数を定義します。
 * </p>
 *
 * <p>
 * 標準入力からクラスの定義を読み取ります。
 * 解釈できない行は、スケルトンコード内にコメントとして記述されます。
 * </p>
 *
 * <ul>
 * <li>先頭にクラス名</li>
 * <li>2行目以降、コメントおよびメンバの定義</li>
 * </ul>
 *
 * <p>
 * 下記にクラスの定義例を示します。
 * </p>
 *
 * <pre>
 * name_of_header {
 * # you can use shell type comment.
 * #   legend: member_name bits type
 * member_foo 2 bslbf
 * member_bar 4 bslbf
 * //you can also use C-type comment.
 * }
 * </pre>
 *
 * @author katsuhiro
 */
public class GenMPEG2 {
    protected GenMPEG2() {
        //do nothing
    }

    public static void parseStream(InputStream in, OutputStream out) {
        BufferedReader r = new BufferedReader(
                new InputStreamReader(in));
        BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(out));
        SkeltonCode h = new SkeltonCode();
        String nameClass;
        List<Generator> lMem;
        int i;

        try {
            //クラス名の処理
            nameClass = parseClassName(r);
            h.setBaseClassName("MPEG2Header");
            h.setClassName(nameClass);

            //メンバ名の処理
            lMem = parseMemberName(r);
            for (i = 0; i < lMem.size(); i++) {
                h.addSkelton(lMem.get(i));
            }

            //コードを出力する
            w.write(h.toCode());
            w.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>
     * 定義ファイルからクラス名を抽出し、
     * テンプレートコード生成クラスに登録します。
     * </p>
     *
     * @param r ストリーム
     * @return クラス名
     * @throws IOException 入出力エラーが発生した場合
     */
    protected static String parseClassName(BufferedReader r)
            throws IOException {
        String l;
        Matcher m;
        String nameClass = "";

        for (l = r.readLine(); l != null; l = r.readLine()) {
            if (l.length() == 0) {
                //空行は無視する
                continue;
            }

            //括弧や中括弧を外す
            m = Pattern.compile(
                    "([^({\\s]*)").
                    matcher(l);
            m.find();

            //for debug
            //System.out.println("'" + l + "'");
            //showAllMatches(m);

            nameClass = m.group(1);
            break;
        }

        return nameClass;
    }

    /**
     * <p>
     * 定義ファイルからメンバ名を抽出し、
     * リストを返します。
     * </p>
     *
     * @param r ストリーム
     * @return メンバ名コードジェネレータのリスト
     * @throws IOException 入出力エラーが発生した場合
     */
    protected static List<Generator> parseMemberName(BufferedReader r)
            throws IOException {
        List<Generator> lMem = new ArrayList<>();
        List<Token> lToken;
        String l;
        Matcher m;
        boolean isComment, result;

        for (l = r.readLine(); l != null; l = r.readLine()) {
            if (l.length() == 0) {
                //空行は無視する
                continue;
            }

            isComment = false;

            //index : 意味(条件)
            //1: メンバ名(アルファベット、数字、アンダースコア)
            //2: ビット長(数字)
            //3: 型      (アルファベット、数字、アンダースコア)
            m = Pattern.compile("\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s+"
                    + "([0-9]+)\\s+"
                    + "([a-zA-Z_][a-zA-Z0-9_]*)").
                    matcher(l);
            result = m.find();

            if (!result || m.groupCount() < 3) {
                //マッチしないので、コメント扱いにする
                lMem.add(new CommentGenerator(l));
                continue;
            }

            //for debug
            //System.out.println("'" + l + "'");
            //showAllMatches(m);

            //create list
            lToken = new ArrayList<>();
            lToken.add(new Token(m.group(1)));
            lToken.add(new Token(m.group(2)));
            lToken.add(new Token(m.group(3)));

            //型チェック
            if (lToken.get(0).f_num) {
                //メンバ名のトークンが異常なので、コメント扱いにする
                isComment = true;
            }
            if (!lToken.get(1).f_num) {
                //ビット長のトークンが異常なので、コメント扱いにする
                isComment = true;
            }
            if (lToken.get(2).f_num) {
                //型名のトークンが異常なので、コメント扱いにする
                isComment = true;
            }
            if (!isValidType(lToken.get(2).sval)) {
                //未知の型なのでコメント扱いする
                isComment = true;
            }

            if (isComment) {
                //コメントとして登録する
                lMem.add(new CommentGenerator(l));
            } else {
                //メンバ名として登録する
                lMem.add(new MemberGenerator(
                        lToken.get(2).sval,
                        lToken.get(0).sval,
                        lToken.get(1).num));
            }
        }

        return lMem;
    }

    /**
     * <p>
     * 自動生成ツールが解釈可能な型かそうでないかを取得します。
     * </p>
     *
     * @param type 型名を表す文字列
     * @return 既知の型であれば true、未知の型であれば false
     */
    protected static boolean isValidType(String type) {
        String[] tableTypes = MemberGenerator.TABLE_VALID_TYPES;
        int i;

        for (i = 0; i < tableTypes.length; i++) {
            if (tableTypes[i].equals(type)) {
                //既知の型である
                return true;
            }
        }

        //未知の型である
        return false;
    }

    /**
     * <p>
     * 正規表現にマッチしたグループを全部表示します。
     * デバッグ用です。
     * </p>
     *
     * @param m 正規表現のマッチングエンジン
     */
    protected static void showAllMatches(Matcher m) {
        int i;

        //group 0 はマッチした文字列全体を意味する
        System.out.println("group0: "
                + "'" + m.group() + "'");
        for (i = 1; i < m.groupCount() + 1; i++) {
            System.out.println("group(" + i + "): "
                    + "'" + m.group(i) + "'");
        }
    }
}
