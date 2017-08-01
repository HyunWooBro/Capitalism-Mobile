/**
 * <p>Title: java访问DBF文件的接口</p>
 * <p>Description: 测试DBF文件的读写</p>
 * <p>Copyright: Copyright (c) 2004~2012</p>
 * <p>Company: iihero.com</p>
 * @author : He Xiong
 * @version 1.1
 */

package com.hexiong.jdbf.test; 
 
import com.hexiong.jdbf.DBFReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;

public class Test
{

    public Test()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        //DBFReader dbfreader = new DBFReader((new URL("http://www.svcon.com/us48st.dbf")).openStream());
        //DBFReader dbfreader = new DBFReader("F:\\work\\book2.dbf");
        DBFReader dbfreader = new DBFReader("./book2.dbf");
        //DBFReader dbfreader = new DBFReader("E:\\hexiongshare\\test.dbf");
        int i;
        for (i=0; i<dbfreader.getFieldCount(); i++) {
          System.out.print(dbfreader.getField(i).getName()+"  ");
        }
        System.out.print("\n");
        for(i = 0; dbfreader.hasNextRecord(); i++)
        {
            Object aobj[] = dbfreader.nextRecord(Charset.forName("GBK"));
            for (int j=0; j<aobj.length; j++)
              System.out.print(aobj[j]+"  |  ");
            System.out.print("\n");
        }

        System.out.println("Total Count: " + i);
    }
}

