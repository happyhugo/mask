package com.hugo.mask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerThread extends Thread {
	private Socket client;
	private BufferedOutputStream ous;
	private ServerThread other;
	public boolean use;
	public Object lock = new Object();

	public ServerThread(Socket client) {
		this.client = client;
		use = false;
	}

	public void run() {
		processSocket();
	}

	public void sendMsg2Me(byte[] b, int length) {

		try {
			// if(ChatTools.SLEEP!=0){
			// Thread.sleep(ChatTools.SLEEP*1000);
			// }

			System.out.println("transform data:" + length);
			ous.write(b, 0, length);
			ous.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			close();
		}
	}

//	public void waitTime() {
//		try {
//			synchronized (lock) {
//				while (!use) {
//					System.out.println("2");
//					lock.wait();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void wakeTime(ServerThread other) {
//		synchronized (lock) {
//			this.other = other;
//			use = true;
//			lock.notify();
//			System.out.println("3");
//		}
//	}
	
	public void ok(ServerThread other){
		this.other = other;
		use = true;
		sendMsg2Me("o".getBytes(), "o".getBytes().length);
	}

	public void close() {
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client = null;
			ChatTools.removeClient(this);
			if (other!=null&&other.client != null) {
				other.close();
			}
		}

	}

	private void processSocket() {

		try {
			byte[] size = new byte[ChatTools.BYTESIZE];
			InputStream ins = client.getInputStream();
			ous = new BufferedOutputStream(client.getOutputStream());
			BufferedInputStream bis = new BufferedInputStream(ins);
			int b;
			
			
			for (ServerThread st : ChatTools.list) {
				if (st!=this&&!st.use) {
					ok(st);
					st.ok(this);
					break;
				}
			}


			while ((b = bis.read(size, 0, size.length)) != -1) {
				
				other.sendMsg2Me(size, b);
				// if(ChatTools.stList.size()<2){
				// System.out.println("sorry,no people online");
				// continue;
				// }
				// ChatTools.stList.get(1-no).sendMsg2Me(size, b);
				// System.out.println("transform data:"+b);
			}

			ous.close();

		} catch (IOException e) {
			
			e.printStackTrace();
		} finally{
			close();
		}
	}
}
