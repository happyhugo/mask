package com.hugo.mask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatTools {
	public static Map<Integer,ServerThread> stList = new HashMap<Integer,ServerThread>();
	public static final int BYTESIZE = 62*5*3;
	public static final int SLEEP = 0;
	public static int no = 0;
	
	public static void addClient(ServerThread ct){
		stList.put(ct.no, ct);
		no++;
		if(no==2){
			no=0;
		}
		System.out.println("add a user");
		System.out.println("size:"+stList.size());
//		castMsg(ct.getOwerUser(),"����������Ŀǰ����"+stList.size());
	}
//	
	public static void removeClient(ServerThread st){
		no = st.no;
		stList.remove(st.no);
		st = null;
		System.out.println("remove a user");
		System.out.println("size:"+stList.size());
//		castMsg(st.getOwerUser(),"�������ˣ��ټ�");
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
