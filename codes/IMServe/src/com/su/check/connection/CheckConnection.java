package com.su.check.connection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PrimitiveIterator.OfDouble;

import com.alibaba.fastjson.JSON;
import com.su.client.information.Client;
import com.su.client.listener.ClientListenerThread;
import com.su.message.type.MessageType;
import com.su.my.time.MyTime;
/*
public class CheckConnection extends Thread{
	private Socket socket;
	private HashSet<Client> clients = null;
	private HashMap<String, String> sendMessageMap = null;
	private ClientListenerThread clientListenerThread = null;
	public CheckConnection(ClientListenerThread clientListenerThread, Socket socket, HashSet<Client> clients) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.clients = clients;
		sendMessageMap = new HashMap<>();
		this.clientListenerThread = clientListenerThread;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		try {
			//socket.setOOBInline(true);// 
			socket.setKeepAlive(true);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			
			
			try {
				while (true){
				
				socket.sendUrgentData(0xFF);
				
				Thread.sleep(500);
				}
			} catch (Exception e) {
				Client client = null;
				for (Client eachClient : clients){
					if (eachClient.getSocket() == socket){
						client = eachClient;
					}
				}
				if (client == null){
					System.out.println("---A client to disconnect--" + socket);
				}else{
					if (!clientListenerThread.getExit()){// when client logout automatically, do not execute try connect again
						System.out.println("--Link breaks--" + socket);
						clientListenerThread.setExit(true);
						e.printStackTrace();
						//Client client = null;
						//for (Client eachClient : clients){
						//	if (eachClient.getSocket() == socket){
						//		client = eachClient;
						//	}
						//}
						//if (client != null){
							clients.remove(client);
							String groupName = client.getGroupName();
							String clientName = client.getName();
							String time = MyTime.getTime();
							String content = "Net is bad: " + clientName + " is out Of group: " + groupName; 
							sendMessageMap.put("type", MessageType.MSG_TYPE_NET_PROBLEM_LEAVE);
							sendMessageMap.put("time", time);
							sendMessageMap.put("name", clientName);
							sendMessageMap.put("group", groupName);
							sendMessageMap.put("content", content);
							DataOutputStream writer = null;
							for (String group : client.getJoinedGroup()){
								for (Client eachClient : clients){
									if (eachClient.getJoinedGroup().contains(group)){
										  writer = eachClient.getWriter();
										   String sendMessage = JSON.toJSONString(sendMessageMap);
										   sendMessageMap.clear();	
										try {
											writer.writeUTF(sendMessage);
											writer.flush();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}finally{
											System.out.println("---Net problem leave: continue to finish sending to message");
										}
									}
									 
								}
							}
								
					}
				}
				
				
				
			}
			
		

	}

}*/
