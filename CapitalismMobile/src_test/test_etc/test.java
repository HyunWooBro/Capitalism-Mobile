package test_etc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import core.framework.Core;

public class test extends Thread {

	public test() {
		
	}

	public test(Runnable runnable) {
		super(runnable);
		
	}

	public test(String threadName) {
		super(threadName);
		
	}

	public test(Runnable runnable, String threadName) {
		super(runnable, threadName);
		
	}

	public test(ThreadGroup group, Runnable runnable) {
		super(group, runnable);
		
	}

	public test(ThreadGroup group, String threadName) {
		super(group, threadName);
		
	}

	public test(ThreadGroup group, Runnable runnable, String threadName) {
		super(group, runnable, threadName);
		
	}

	public test(ThreadGroup group, Runnable runnable, String threadName,
			long stackSize) {
		super(group, runnable, threadName, stackSize);
		
	}

	@Override
	public void run() {
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
				Core.APP.error("abc", input);
			}
			
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
