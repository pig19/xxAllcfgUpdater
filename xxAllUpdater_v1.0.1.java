/*
v1.0.0(2023/11/01)
リリース
正常動作を確認
-----------------
v1.0.1(2023/11/02)
コメントを追加
UpdateConfigSettingメソッドをdeleteConfigIdsメソッドに名称変更、不要なreplace引数を削除、searchへ統合
実質的な仕様変更はしていないためコンパイルはせず確認していない
-----------------
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class xxAllUpdaterG3A2 {
    public static void main(String[] args) {
        //使用するファイル名を定義
        String inputFileName = "BlockAndItemWithMetaNames.txt";
        String mineallConfigFileName = "..\\config\\net.minecraft.scalar.mineall.mod_mineallsmp.cfg";
        String cutallConfigFileName = "..\\config\\net.minecraft.scalar.cutall.mod_cutallsmp.cfg";

        // Reset config
        //設定済みitemIDなどを一旦消去
        deleteConfigIds(mineallConfigFileName, "S:blockIds=");
        deleteConfigIds(mineallConfigFileName, "S:itemIds=");
        deleteConfigIds(cutallConfigFileName, "S:blockIds=");
        deleteConfigIds(cutallConfigFileName, "S:itemIds=");
        deleteConfigIds(cutallConfigFileName, "S:leaveIds=");
                

        //条件に合致したitem/blockIDを入れておく配列
        List<String> ore = new ArrayList<>();
        List<String> pick = new ArrayList<>();
        List<String> log = new ArrayList<>();
        List<String> axe = new ArrayList<>();
        List<String> leave = new ArrayList<>();

        // 条件に合致したitem/blockIDを配列に格納する
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;

            //ファイルの終端まで一行ずつ読み込み、lineに入れていく
            while ((line = reader.readLine()) != null) {
                //読んだ行を","で分割し、partsに入れる
                String[] parts = line.split(",");
                //partsから要素を一つずつpartに入れて条件に合致するかを調べる、要素をすべて調べたらforを終了させる
                for (String part : parts) {
                    //条件に合致した要素を対応する変数リストに一つずつ格納していく
                    if (part.contains(":ore") || part.contains("_ore")) {
                        ore.add(part);
                    } else if (part.contains("pick")) {
                        pick.add(part);
                    } else if (part.contains(":log") || part.contains("_log")) {
                        log.add(part);
                    } else if (part.contains("_axe")) {
                        axe.add(part);
                    } else if (part.contains(":leave") || part.contains("_leave")) {
                        leave.add(part);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update mineallConfig
        //MineAllの各項目に書き込む
        updateConfigFile(mineallConfigFileName, "S:blockIds=", ore);
        updateConfigFile(mineallConfigFileName, "S:itemIds=", pick);

        // Update cutallConfig
        //cutallの各項目に書き込む
        updateConfigFile(cutallConfigFileName, "S:blockIds=", log);
        updateConfigFile(cutallConfigFileName, "S:itemIds=", axe);
        updateConfigFile(cutallConfigFileName, "S:leaveIds=", leave);
    }


    //xxAllの各種項目に書き込んでいくためのメソッド
    private static void updateConfigFile(String fileName, String configName, List<String> items) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(fileName));

            //読み込んだ行
            String line;

            //書き込みtxt全体
            String input = "";

            //ファイルの終端まで一行ずつ読み込み、lineに入れていく
            while ((line = file.readLine()) != null) {
                //入れた行が"S:xxIds="で始まるなら
                if (line.startsWith(configName)) {
                    //収集したitemsを" aaaaa, bbbbb,"のように区切って結合
                    line += " " + String.join(", ", items);
                }
                //読んだ行を書き込み用変数に入れて改行
                input += line + '\n';
                //(繰り返し)
            }

            file.close();
            
            BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
            //書き込み用変数の内容をファイルに上書き
            output.write(input);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        //作業前にコンフィグファイルの設定済みIDを一旦削除するためのメソッド
        //searchの行をsearch文字列だけに置換する
    private static void deleteConfigIds(String fileName, String search) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(fileName));

            //読み込んだ行
            String line;

            //書き込みtxt全体
            String input = "";
            
            //ファイルの終端まで一行ずつ読み込み、lineに入れていく
            while ((line = file.readLine()) != null) {
                //読んだ行にsearch文字列が入っていれば
                if (line.contains(search)) {
                    //その行をsearchのみにする
                    line = search;
                }
                //読んだ行を書き込み用変数に入れて改行
                input += line + '\n';
                //(繰り返し)
            }

            file.close();

            BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
            //書き込み用変数の内容をファイルに上書き
            output.write(input);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
