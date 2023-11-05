// package xxAllcfgUpdater;
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
v1.0.2(2023/11/02)
digAllへの対応
-----------------
v1.0.3(2023/11/05)
redstone, glowstone, obsidian, ic2oreが対象でない具合を修正
-----------------
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class xxAllcfgUpdater {
    public static void main(String[] args) {
        //使用するファイル名を定義
        String inputFileName = "BlockAndItemWithMetaNames.txt";
        String mineallConfigFileName = "..\\config\\net.minecraft.scalar.mineall.mod_mineallsmp.cfg";
        String cutallConfigFileName = "..\\config\\net.minecraft.scalar.cutall.mod_cutallsmp.cfg";
        String digallConfigFileName = "..\\config\\net.minecraft.scalar.digall.mod_digallsmp.cfg";

        // Reset config
        //設定済みitemIDなどを一旦消去
        deleteConfigIds(mineallConfigFileName, "S:blockIds=");
        deleteConfigIds(mineallConfigFileName, "S:itemIds=");

        deleteConfigIds(cutallConfigFileName, "S:blockIds=");
        deleteConfigIds(cutallConfigFileName, "S:itemIds=");
        deleteConfigIds(cutallConfigFileName, "S:leaveIds=");

        deleteConfigIds(digallConfigFileName, "S:itemIds=");
                

        //条件に合致したitem/blockIDを入れておく配列
        List<String> ore = new ArrayList<>();
        List<String> pick = new ArrayList<>();
        List<String> log = new ArrayList<>();
        List<String> axe = new ArrayList<>();
        List<String> leave = new ArrayList<>();
        List<String> shovel = new ArrayList<>();

        // 条件に合致したitem/blockIDを配列に格納する
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;

            //redstone(lighting)
            ore.add("lit_redstone_ore, glowstone, obsidian");

            //ファイルの終端まで一行ずつ読み込み、lineに入れていく
            while ((line = reader.readLine()) != null) {
                
                //行の初めの","区切り部分を文字列を各種ID格納用配列に追加
                    // String[] parts = line.split(","); //読んだ行を","で分割し、partsに入れる
                    
                    if (line.contains(":ore") || line.contains("_ore")) {
                        String[] parts = line.split(",");
                        ore.add(parts[0]);
                    } else if (line.contains("pick")) {
                        String[] parts = line.split(",");
                        pick.add(parts[0]);
                    } else if (line.contains(":log") || line.contains("_log")) {
                        String[] parts = line.split(",");
                        log.add(parts[0]);
                    } else if (line.contains("_axe")) {
                        String[] parts = line.split(",");
                        axe.add(parts[0]);
                    } else if (line.contains(":leave") || line.contains("_leave")) {
                        String[] parts = line.split(",");
                        leave.add(parts[0]);
                    } else if (line.contains("shovel")) {
                        String[] parts = line.split(",");
                        shovel.add(parts[0]);
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

        // Update digallConfig
        //digallの各項目に書き込む
        updateConfigFile(digallConfigFileName, "S:itemIds=", shovel);
    }


    //xxAllの各種項目に書き込んでいくためのメソッド
    private static void updateConfigFile(String fileName, String configName, List<String> items) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(fileName));

            //読み込んだ行
            String line;

            //書き込みtxt全体
            //ic2はic2:resourceでまとめられているため単独で入れておく
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
