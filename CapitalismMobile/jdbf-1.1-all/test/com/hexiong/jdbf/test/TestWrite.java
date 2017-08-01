/**
 * <p>Title: java访问DBF文件的接口</p>
 * <p>Description: 测试DBF文件的读写</p>
 * <p>Copyright: Copyright (c) 2004~2012</p>
 * <p>Company: iihero.com</p>
 * @author : He Xiong
 * @version 1.1
 */

package com.hexiong.jdbf.test; 
 
import com.hexiong.jdbf.*;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

public class TestWrite
{

    public TestWrite()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        JDBField[] fields = {
            new JDBField("ID", 'C', 8, 0),
            new JDBField("Name", 'C', 32, 0),
            new JDBField("TestN", 'N', 20, 0), //第三个参数值一定不大于20
            new JDBField("TestF", 'F', 20, 6), //F类型与N类型同,且第四个参数值有小数位数，否则会截短
            new JDBField("TestD", 'D', 8, 0)
        };
        //DBFReader dbfreader = new DBFReader("E:\\hexiong\\work\\project\\book2.dbf");
        DBFWriter dbfwriter = new DBFWriter("./testwrite.dbf", fields);

        Object[][] records = {
            {"1", "hexiong", new Integer(500), new Double(500.123), new Date() },
            {"2", "hefang", new Integer(600), new Double(600.234), new Date() },
            {"3", "heqiang", new Integer(700), new Double(700.456), new Date() }
        };

        for (int i=0; i<records.length; i++){
          dbfwriter.addRecord(records[i]);
        }
        dbfwriter.close();
        System.out.println("testwrite.dbf write finished.......");
    }
}
