package com.hugo.mask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Start {
    int no = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Start().init();
	}
	
	public void init() {
		
		new Thread(new Runnable() {		
			
			public void run() {
			    ServerSocket serverSocket = null;
			    try {
			    	serverSocket =  new ServerSocket(1234);
			    }
			    catch (IOException e) {
			    	e.printStackTrace();
			    	System.exit(1);
			    }
			    while(true){
			        Socket socket = null;
			        try {
			        	System.out.println("linking:");
			            socket = serverSocket.accept();
		    			System.out.println("receivebuffer:"+socket.getReceiveBufferSize());
		    			System.out.println("sendbuffer:"+socket.getSendBufferSize());
			            if(ChatTools.stList.size()<2){
			            	new ServerThread(socket,ChatTools.no).start();
			            }else{
			            	System.out.println("refuse user");
			            	socket.close();
			            }
			        }catch(Exception e){
			            e.printStackTrace();
			        } 
			    }       
			}
		}).start();
	}


}
