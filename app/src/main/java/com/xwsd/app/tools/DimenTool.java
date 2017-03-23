package com.xwsd.app.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自动生成Dimens文件
 */
public class DimenTool {

    public static void gen() {

        File file = new File("./app/src/main/res/values/dimens.xml");
        BufferedReader reader = null;
        StringBuilder sw160 = new StringBuilder();
        StringBuilder sw240 = new StringBuilder();
        StringBuilder sw320 = new StringBuilder();
        StringBuilder sw480 = new StringBuilder();
        StringBuilder sw600 = new StringBuilder();
        StringBuilder sw800 = new StringBuilder();

        try {

            System.out.println("生成不同分辨率：");
            reader = new BufferedReader(new FileReader(file));
            String tempString;

            int line = 1;

            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {

                if (tempString.contains("</dimen>")) {
                    String start = tempString.substring(0, tempString.indexOf(">") + 1);
                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);
                    int num = Integer.valueOf(tempString.substring(tempString.indexOf(">") + 1, tempString.indexOf("</dimen>") - 2));

                    sw160.append(start).append((int) Math.round(num * 0.5)).append(end).append(" ");
                    sw240.append(start).append((int) Math.round(num * 0.75)).append(end).append(" ");
                    sw320.append(tempString).append(" ");
                    sw480.append(start).append((int) Math.round(num * 1.5)).append(end).append(" ");
                    sw600.append(start).append((int) Math.round(num * 1.875)).append(end).append(" ");
                    sw800.append(start).append((int) Math.round(num * 2.5)).append(end).append(" ");

                } else {
                    sw160.append(tempString).append(" ");
                    sw240.append(tempString).append(" ");
                    sw320.append(tempString).append(" ");
                    sw480.append(tempString).append(" ");
                    sw600.append(tempString).append(" ");
                    sw800.append(tempString).append(" ");
                }
                line++;
            }

            reader.close();

            System.out.println("<!--  sw160 -->");
            System.out.println(sw160);
            System.out.println("<!--  sw240 -->");
            System.out.println(sw240);
            System.out.println("<!--  sw320 -->");
            System.out.println(sw320);
            System.out.println("<!--  sw480 -->");
            System.out.println(sw480);
            System.out.println("<!--  sw600 -->");
            System.out.println(sw600);
            System.out.println("<!--  sw800 -->");
            System.out.println(sw800);
            String sw120file = "./app/src/main/res/values-sw160dp/dimens.xml";
            String sw240file = "./app/src/main/res/values-sw240dp/dimens.xml";
            String sw320file = "./app/src/main/res/values-sw320dp/dimens.xml";
            String sw480file = "./app/src/main/res/values-sw480dp/dimens.xml";
            String sw600file = "./app/src/main/res/values-sw600dp/dimens.xml";
            String sw800file = "./app/src/main/res/values-sw800dp/dimens.xml";
            writeFile(sw120file, sw160.toString());
            writeFile(sw240file, sw240.toString());
            writeFile(sw320file, sw320.toString());
            writeFile(sw480file, sw480.toString());
            writeFile(sw600file, sw600.toString());
            writeFile(sw800file, sw800.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.close();
    }

    public static void main(String[] args) {
        gen();
    }
}
