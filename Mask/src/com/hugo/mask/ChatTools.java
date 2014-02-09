package com.hugo.mask;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ChatTools {
	public static List<ServerThread> list  = Collections.synchronizedList(new LinkedList<ServerThread>());
	public static final int BYTESIZE = 62*5*3;
	public static final int SLEEP = 0;
	
	public static void addClient(ServerThread ct){
		list.add(ct);			
		
		System.out.println("add a user");
		System.out.println("size:"+list.size());
//		castMsg(ct.getOwerUser(),"我上线啦！目前人数"+stList.size());
	}
//	
	public static void removeClient(ServerThread st){
		list.remove(st);
		
		System.out.println("remove a user");
		System.out.println("size:"+list.size());
//		castMsg(st.getOwerUser(),"我下线了，再见");
	}
//	
//	public static void castMsg(UserInfo sender,String msg){
//		msg = sender.getName()+"  :  "+msg;
//		for(int i=0;i<stList.size();i++){
//			ServerThread st = stList.get(i);
//			st.sendMsg2Me(msg);
//		}
//	}
//	
//	public static void castMsgToSb(UserInfo sender,String msg){
//		String[] s = msg.split(";");
//		msg = sender.getName()+"  :  "+msg;
//		for(int i=0;i<stList.size();i++){
//			ServerThread st = stList.get(i);
//			if(st.getOwerUser().getName().equals(s[1])||st.getOwerUser().getName().equals(s[2])){
//				st.sendMsg2Me(msg);
//				System.out.println(st.getName()+ " " + msg);
//			}
//		}
//	}
}
