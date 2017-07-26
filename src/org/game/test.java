package org.game;

import java.io.*;
import java.net.*;
import java.util.*;

import android.util.*;

public class test extends Thread {

	public test() {
		// TODO Auto-generated constructor stub
	}

	public test(Runnable runnable) {
		super(runnable);
		// TODO Auto-generated constructor stub
	}

	public test(String threadName) {
		super(threadName);
		// TODO Auto-generated constructor stub
	}

	public test(Runnable runnable, String threadName) {
		super(runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public test(ThreadGroup group, Runnable runnable) {
		super(group, runnable);
		// TODO Auto-generated constructor stub
	}

	public test(ThreadGroup group, String threadName) {
		super(group, threadName);
		// TODO Auto-generated constructor stub
	}

	public test(ThreadGroup group, Runnable runnable, String threadName) {
		super(group, runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public test(ThreadGroup group, Runnable runnable, String threadName,
			long stackSize) {
		super(group, runnable, threadName, stackSize);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		try {
			Socket s;
			s = new Socket("java.sun.com", 80);
		
			
			InputStream instream = s.getInputStream();
			OutputStream outstream = s.getOutputStream();
			
			Scanner in = new Scanner(instream);
			PrintWriter out = new PrintWriter(outstream);
			
			String command = "GET " + "/" + " HTTP/1.0\n\n";
			out.print(command);
			out.flush();
			
			while(in.hasNextLine())
			{
				String input = in.nextLine();
				Log.e("abc", input);
			}
			
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
