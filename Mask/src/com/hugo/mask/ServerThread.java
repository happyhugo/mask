package com.hugo.mask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ServerThread extends Thread{
	private Socket client;
	private BufferedOutputStream ous;
	public int no;
	
	public ServerThread(Socket client,int no){
		this.client = client;
		this.no = no;
	}

	public void run(){
		processSocket();
	}

	public void sendMsg2Me(byte[] b,int length){

		try {
			if(ChatTools.SLEEP!=0){
				Thread.sleep(ChatTools.SLEEP*1000);
			}
			System.out.println("transform data:"+length);
			ous.write(b,0,length);
			ous.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processSocket() {
		try {
			byte[] size = new byte[ChatTools.BYTESIZE]; 
			InputStream ins = client.getInputStream();
			ous = new BufferedOutputStream(client.getOutputStream());
			BufferedInputStream bis = new BufferedInputStream(ins);
			ChatTools.addClient(this);
			int b;
			while((b = bis.read(size, 0, size.length))!= -1){
				if(ChatTools.stList.size()<2){
					System.out.println("sorry,no people online");
					continue;
				}
				ChatTools.stList.get(1-no).sendMsg2Me(size, b);
//				System.out.println("transform data:"+b);
			}
			client.close();
			ChatTools.removeClient(this);			
		} catch (IOException e) {
			e.printStackTrace();
			ChatTools.removeClient(this);
		}		
	}
}
