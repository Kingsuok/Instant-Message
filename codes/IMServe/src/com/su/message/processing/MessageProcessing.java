package com.su.message.processing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.su.client.information.Client;
import com.su.client.listener.ClientListenerThread;
import com.su.db.operation.TableGroupsOperation;
import com.su.db.operation.TableUsersOperation;
import com.su.message.type.MessageType;
import com.su.my.time.MyTime;

public class MessageProcessing implements MessageService {
	private DataInputStream reader = null;
	private DataOutputStream writer = null;
	private String receiveMessage = null;
	private String sendMessage = null;
	private HashSet<Client> clients = null;
	private Client client = null; // client information
	private Socket clientSocket = null; // client's socket
	private TableGroupsOperation tableGroupsOperation = null;
	private TableUsersOperation tableUsersOperation = null;
	private HashMap<String, String> sendMessageMap = null;
	private String type = "type";
	private HashMap<String, String> message = null;
	private boolean exit = false;
	public MessageProcessing(DataInputStream reader, DataOutputStream writer, HashSet<Client> clients, Socket clientSocket){
		this.reader = reader;
		this.writer = writer;
		this.clients = clients;
		tableGroupsOperation = new TableGroupsOperation();
		tableUsersOperation = new TableUsersOperation();
		sendMessageMap = new HashMap<String, String>();
		this.clientSocket = clientSocket;
	}
	public void messageProcessing(){
		
		try {
				receiveMessage = reader.readUTF();
				message = JSON.parseObject(receiveMessage, new TypeReference<HashMap<String, String>>(){});
	            if (message.get(type).equals(MessageType.MSG_TYPE_REGISTER)){
	            	register(message);
	            }else if (message.get(type).equals(MessageType.MSG_TYPE_LOGIN)){
	            	login(message);
	            }
	            //else if (message.get(type).equals(MessageType.MSG_TYPE_LOGOUT)){
	            //	logout(message);
	            //}
	            else if (message.get(type).equals(MessageType.MSG_TYPE_JOIN_GROUP)){
	            	joinGroup(message);
	            }else if (message.get(type).equals(MessageType.MSG_TYPE_LEAVE_GROUP)){
	            	leaveGroup(message);
	            }else if (message.get(type).equals(MessageType.MSG_TYPE_GROUP_LIST)){
	            	groupList(message);
	            }else if (message.get(type).equals(MessageType.MSG_TYPE_GROUP_BUDDY)){
	            	buddyList(message);
	            }else if (message.get(type).equals(MessageType.MSG_TYPE_GROUP_CHAT)){
					groupChat(message);
				}else if (message.get(type).equals(MessageType.MSG_TYPE_CREATE_GROUP)) {
					createGroup(message);
				}else if(message.get(type).equals(MessageType.MSG_TYPE_DELETE_GROUP)){
					deleteGroup(message);
				}else if (message.get(type).equals(MessageType.MSG_TYPE_LOGOUT)){
					logout(message);
				}else if (message.get(type).equals(MessageType.MSG_TYPE_NET_PROBLEM_BACK)){
					netProblem(message);
				}
			
		} catch (IOException e1) {
				/*
				 // if client cut off the connection, the server's readUTF() does not throw a exception, so it does not work.
				if (!client.getJoinedGroup().isEmpty()){
					HashSet<String> joinedGroups = client.getJoinedGroup();
					String clientName = client.getName();
					clients.remove(client);
					for (String groupName :joinedGroups){
						sendMessageToGroup( groupName, clientName,  MessageType.MSG_TYPE_NET_PROBLEM_LEAVE);
					}
					System.out.println("---Net problem leave: " + clientName +" leaves");
				}else {
					System.out.println("--Link breaks--" + clientSocket);
				}*/
			try {
				if (reader != null){
					reader.close();
					reader = null;
				}
				
				if (writer != null){
					writer.close();
					writer = null;
				}
			} catch (Exception e){
				//e.printStackTrace();
			}
		} 
	}
	@Override
	public void register(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		HashSet<String> usersNameSet = tableUsersOperation.usersNameSet();
		if (!usersNameSet.contains(message.get("name"))){
			boolean success = tableUsersOperation.add(new String[]{message.get("name"), message.get("password")});
			if (success){
				System.out.println("--register successfully--");
				sendMessageMap.put(type, MessageType.MSG_TYPE_REGISTER);
				sendMessageMap.put("status", MessageType.MSG_TYPE_SUCCESS);
				sendMessage = JSON.toJSONString(sendMessageMap);
				sendMessageMap.clear(); // empty the map for next use
				try {
					writer.writeUTF(sendMessage);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("---sending failed---");
				}
				
				System.out.println("---sending successfully---");
				
			}else{
				System.out.println("--register failed--");
				sendMessageMap.put(type, MessageType.MSG_TYPE_REGISTER);
				sendMessageMap.put("status", MessageType.MSG_TYPE_FAILURE);
				sendMessageMap.put("reason", "Server problem");
				sendMessage = JSON.toJSONString(sendMessageMap);
				sendMessageMap.clear(); // empty the map for next use
				try {
					writer.writeUTF(sendMessage);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("---sending failed---");
				}
			}
			
		}else {
			System.out.println("--register failed--");
			sendMessageMap.put(type, MessageType.MSG_TYPE_REGISTER);
			sendMessageMap.put("status", MessageType.MSG_TYPE_FAILURE);
			sendMessageMap.put("reason", "Name repetition");
			sendMessage = JSON.toJSONString(sendMessageMap);
			sendMessageMap.clear(); // empty the map for next use
			try {
				writer.writeUTF(sendMessage);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("---sending failed---");
			}
		}
	}
	@Override
	public void login(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		HashSet<String> usersNameSet = tableUsersOperation.usersNameSet();
		if (usersNameSet.contains(message.get("name"))){
			String password = tableUsersOperation.getPassword(message.get("name"));
			if (password.equals(message.get("password"))){
				System.out.println("--Login successfully--");
				HashSet<String> joinedGroup = new HashSet<String>();
				client = new Client(message.get("name"), "", clientSocket, reader, writer,joinedGroup);
				clients.add(client);								
				//sockets.add(clientSocket);//  use for creating group to send message to all clients whether they join a group or not
				sendMessageMap.put(type, MessageType.MSG_TYPE_LOGIN);
				sendMessageMap.put("status", MessageType.MSG_TYPE_SUCCESS);
				sendMessage = JSON.toJSONString(sendMessageMap);
				sendMessageMap.clear(); // empty the map for next use
				try {
					writer.writeUTF(sendMessage);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("---sending failed---");
				}
				
				System.out.println("---sending successfully---");
				
			}else{
				System.out.println("--Login failed--");
				sendMessageMap.put(type, MessageType.MSG_TYPE_LOGIN);
				sendMessageMap.put("status", MessageType.MSG_TYPE_FAILURE);
				sendMessageMap.put("reason", "password is wrong");
				sendMessage = JSON.toJSONString(sendMessageMap);
				sendMessageMap.clear(); // empty the map for next use
				try {
					writer.writeUTF(sendMessage);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("---sending failed---");
				}
			}
			
		}else {
			System.out.println("--Login failed--");
			sendMessageMap.put(type, MessageType.MSG_TYPE_LOGIN);
			sendMessageMap.put("status", MessageType.MSG_TYPE_FAILURE);
			sendMessageMap.put("reason", "Name does not exist");
			sendMessage = JSON.toJSONString(sendMessageMap);
			sendMessageMap.clear(); // empty the map for next use
			try {
				writer.writeUTF(sendMessage);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("---sending failed---");
			}
		}
	}
	/*
	@Override
	public void logout(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		if (clients.contains(client)){
			String groupName = client.getGroupName();
			String clientName = client.getName();
			clients.remove(client);
			sendMessageToGroup(groupName, clientName, MessageType.MSG_TYPE_LEAVE_GROUP); 
		}
		System.out.println("--"+ message.get("name" + " logout--"));
		// do not have to close socket, because there is heart packet mechanism to close it
	}
	*/
	@Override
	public void joinGroup(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		//if (clients.contains(client)){
			//clients.remove(client);
		
		    		//String groupName = client.getGroupName();
		    		String clientName = client.getName();
		    		//if (!groupName.equals("")){
					//	sendMessageToGroup(groupName, clientName, MessageType.MSG_TYPE_LEAVE_GROUP);
					//	System.out.println(message.get("name")+ " leaves group: " + groupName);
					//	client.setGroupName(message.get("group"));
					//	sendMessageToGroup(message.get("group"), clientName,  MessageType.MSG_TYPE_JOIN_GROUP);
				   //}else {
		    		if (client.getJoinedGroup().contains(message.get("group"))){
		    			sendMessageMap.put(type, MessageType.MSG_TYPE_JOIN_GROUP);
						sendMessageMap.put("time", MyTime.getTime());
						sendMessageMap.put("name", clientName);
						sendMessageMap.put("group", message.get("group"));
						sendMessageMap.put("content", clientName + " joins the group: " + message.get("group"));
						sendMessage = JSON.toJSONString(sendMessageMap);
						sendMessageMap.clear();
						try {
							writer.writeUTF(sendMessage);
							writer.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
		    		}else {
		    			client.getJoinedGroup().add(message.get("group"));
						sendMessageToGroup(message.get("group"), clientName,  MessageType.MSG_TYPE_JOIN_GROUP);
					}
		    		   
					   client.setGroupName(message.get("group"));
				  // }
		    	System.out.println(message.get("name")+ " joins group: " + message.get("group"));
	}
	
   

	@Override
	public void groupChat(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		
		String groupName = client.getGroupName();
		String clientName = client.getName();
		sendMessageToGroup(groupName, clientName, MessageType.MSG_TYPE_GROUP_CHAT);
		System.out.println("--chat successfully--");
	}
	@Override
	public void groupList(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		
		HashSet<String> groupSet = tableGroupsOperation.groupSet();
		ArrayList<String> groupList = new ArrayList<String>(groupSet);
		String groupListToString = JSON.toJSONString(groupList);
		sendMessageMap.put(type, MessageType.MSG_TYPE_GROUP_LIST);
		sendMessageMap.put("groupList", groupListToString);
		sendMessage = JSON.toJSONString(sendMessageMap);
		sendMessageMap.clear();	
		try {
			writer.writeUTF(sendMessage);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	@Override
	public void buddyList(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		ArrayList<String> buddyList = new ArrayList<String>();
		String groupName = client.getGroupName();
		for (Client eachClient : clients){
			if (eachClient.getJoinedGroup().contains(groupName)){
				buddyList.add(eachClient.getName());
			}
		}
		String buddyListToString = JSON.toJSONString(buddyList);
		sendMessageMap.put(type, MessageType.MSG_TYPE_GROUP_BUDDY);
		sendMessageMap.put("buddyList", buddyListToString);
		sendMessage = JSON.toJSONString(sendMessageMap);
		sendMessageMap.clear();	
		try {
			writer.writeUTF(sendMessage);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	@Override
	public void createGroup(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		HashSet<String> groupSet = tableGroupsOperation.groupSet();
		if (groupSet.contains(message.get("groupName"))){
			System.out.println("--Create failed--");
			sendMessageMap.put(type, MessageType.MSG_TYPE_CREATE_GROUP);
			sendMessageMap.put("status", MessageType.MSG_TYPE_FAILURE);
			sendMessageMap.put("reason", "Name repetition");
			sendMessage = JSON.toJSONString(sendMessageMap);
			sendMessageMap.clear(); // empty the map for next use
			try {
				writer.writeUTF(sendMessage);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("---sending failed---");
			}
		} else {
			boolean success = tableGroupsOperation.add(new String[]{message.get("groupName"), message.get("creator"),  message.get("time")});
			if (success){
				System.out.println("--Create successfully--");
				sendMessageMap.put(type, MessageType.MSG_TYPE_CREATE_GROUP);
				sendMessageMap.put("status", MessageType.MSG_TYPE_SUCCESS);
				sendMessageMap.put("creator", message.get("creator"));
				sendMessageMap.put("newGroup", message.get("groupName"));
				sendMessage = JSON.toJSONString(sendMessageMap);
				sendMessageMap.clear(); // empty the map for next use
				DataOutputStream writer = null;
				
				for (Client eachClient : clients){// send to all client
					try{	
							writer = eachClient.getWriter();
							if (writer != null){
								writer.writeUTF(sendMessage);
								writer.flush();
							}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("---sending failed---");
					}finally{
						System.out.println("----Create group: Continue to finish sending message---");
					}
					//DataOutputStream writer = new DataOutputStream(eachclient.getSocket().getOutputStream());						
				   }
				
				System.out.println("---sending successfully---");
				
			}else{
				System.out.println("--Create failed--");
				sendMessageMap.put(type, MessageType.MSG_TYPE_CREATE_GROUP);
				sendMessageMap.put("status", MessageType.MSG_TYPE_FAILURE);
				sendMessageMap.put("reason", "Server problem");
				sendMessage = JSON.toJSONString(sendMessageMap);
				sendMessageMap.clear(); // empty the map for next use
				try {
					writer.writeUTF(sendMessage);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("---sending failed---");
				}
			}
		}
	}
	@Override
	public void deleteGroup(HashMap<String, String> message) {
		// TODO Auto-generated method stub
		String groupName = message.get("groupName");
		String creator = message.get("creator");
		if (tableGroupsOperation.getCreator(groupName).equals(creator)){
			boolean success = tableGroupsOperation.delete(new String[]{groupName, creator});
			if (success){
				System.out.println("--delete successfully--");
			
				//clients.remove(client);
				for (Client eachClient : clients){
					eachClient.getJoinedGroup().remove(groupName);
					if (eachClient.getGroupName().equals(groupName)){
						eachClient.setGroupName("");
					}
				}
				sendMessageToAllClients(groupName, creator, MessageType.MSG_TYPE_DELETE_GROUP);
				
				System.out.println("---sending successfully---");
				
			}else{
				System.out.println("--delete failed--");
				sendMessageMap.put(type, MessageType.MSG_TYPE_DELETE_GROUP);
				sendMessageMap.put("reason", "Server problem");
				sendMessage = JSON.toJSONString(sendMessageMap);
				sendMessageMap.clear(); // empty the map for next use
				try {
					writer.writeUTF(sendMessage);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("---sending failed---");
				}
			}
			
		}else{
			System.out.println("--delete failed--");
			sendMessageMap.put(type, MessageType.MSG_TYPE_DELETE_GROUP);
			sendMessageMap.put("status", MessageType.MSG_TYPE_FAILURE);
			sendMessageMap.put("reason", "You are not the creator");
			sendMessage = JSON.toJSONString(sendMessageMap);
			sendMessageMap.clear(); // empty the map for next use
			try {
				writer.writeUTF(sendMessage);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("---sending failed---");
			}
		}
		
	}
	
  
  
@Override
public void leaveGroup(HashMap<String, String> message) {
	// TODO Auto-generated method stub
		//clients.remove(client);
		String groupName = message.get("group");
		String clientName = client.getName();
		
		sendMessageToGroup(groupName, clientName, MessageType.MSG_TYPE_LEAVE_GROUP);
		client.getJoinedGroup().remove(groupName);
		client.setGroupName("");
		System.out.println(message.get("name")+ " leaves group: " + groupName);
}
// because leaving a group and joining a group and group chat, message will be received by the members of the group, so the method should be written lonely.
public void sendMessageToGroup(String groupName, String clientName, String messageType){
	DataOutputStream writer = null;
	for (Client eachGroupBuddy : clients){
		//if (eachGroupBuddy.getGroupName().contains(groupName)&& !groupName.equals("Not Connected")){// inform the group: leave
		if (eachGroupBuddy.getJoinedGroup().contains(groupName)&& !groupName.equals("Not Connected")){// inform the group: leave
				try {
					writer = eachGroupBuddy.getWriter();
					if (messageType.equals(MessageType.MSG_TYPE_LEAVE_GROUP)){
						sendMessageMap.put(type, MessageType.MSG_TYPE_LEAVE_GROUP);
						sendMessageMap.put("time", MyTime.getTime());
						sendMessageMap.put("name", clientName);
						sendMessageMap.put("group", groupName);
						sendMessageMap.put("content", clientName + " leaves the group: " + groupName);
						sendMessage = JSON.toJSONString(sendMessageMap);
						sendMessageMap.clear();
					}else if (messageType.equals(MessageType.MSG_TYPE_JOIN_GROUP)){
						sendMessageMap.put(type, MessageType.MSG_TYPE_JOIN_GROUP);
						sendMessageMap.put("time", MyTime.getTime());
						sendMessageMap.put("name", clientName);
						sendMessageMap.put("group", groupName);
						sendMessageMap.put("content", clientName + " joins the group: " + groupName);
						sendMessage = JSON.toJSONString(sendMessageMap);
						sendMessageMap.clear();
					}else if (messageType.equals(MessageType.MSG_TYPE_GROUP_CHAT)){
						sendMessageMap.put(type, MessageType.MSG_TYPE_GROUP_CHAT);
						sendMessageMap.put("time", MyTime.getTime());
						sendMessageMap.put("name", clientName);
						sendMessageMap.put("group", groupName);
						sendMessageMap.put("content", message.get("content"));
						sendMessage = JSON.toJSONString(sendMessageMap);
						sendMessageMap.clear();	
					}else if (messageType.equals(MessageType.MSG_TYPE_NET_PROBLEM_BACK)){
						sendMessageMap.put(type, MessageType.MSG_TYPE_NET_PROBLEM_BACK);
						sendMessageMap.put("time", message.get("time"));
						sendMessageMap.put("name", clientName);
						sendMessageMap.put("group", groupName);
						String content = "Net is good: "+ clientName + " backs to group: " + groupName;
						sendMessageMap.put("content", content);
						sendMessage = JSON.toJSONString(sendMessageMap);
						sendMessageMap.clear();	
					}/*else if (messageType.equals(MessageType.MSG_TYPE_NET_PROBLEM_LEAVE)){
						String time = MyTime.getTime();
						String content = "Net is bad: " + clientName + " is out Of group: " + groupName; 
						sendMessageMap.put("type", MessageType.MSG_TYPE_NET_PROBLEM_LEAVE);
						sendMessageMap.put("time", time);
						sendMessageMap.put("name", clientName);
						sendMessageMap.put("group", groupName);
						sendMessageMap.put("content", content);
						sendMessage = JSON.toJSONString(sendMessageMap);
						sendMessageMap.clear();	
					}*/
					
					writer.writeUTF(sendMessage);
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (writer != null){
						try {
							writer.close();
							writer = null;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
						}
						
					}
				} finally{
					
						System.out.println("----Group message: Continue to finish sending to clients message---");
				
				}
			}
		}
}

public void sendMessageToAllClients(String groupName, String clientName, String messageType){
		    
		    sendMessageMap.put(type, MessageType.MSG_TYPE_DELETE_GROUP);
			sendMessageMap.put("status", MessageType.MSG_TYPE_SUCCESS);
			sendMessageMap.put("time", MyTime.getTime());
			sendMessageMap.put("name", clientName);
			sendMessageMap.put("group", message.get("groupName"));
			sendMessageMap.put("content", message.get("content"));
			sendMessage = JSON.toJSONString(sendMessageMap);
			sendMessageMap.clear();	
			DataOutputStream writer = null;
		   for (Client eachClient : clients){
			   eachClient.getJoinedGroup().remove(groupName);
			   if (eachClient.getGroupName().equals(groupName)){
				   eachClient.setGroupName("");
			   }
			   try {
				writer = eachClient.getWriter();
			   if (writer != null){
				   writer.writeUTF(sendMessage);
				   writer.flush();
			   }
			   
			   } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (writer != null){
					try {
						writer.close();
						writer = null;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}finally{
				System.out.println("---To all clients: continue to finish sending to all clients---");
			}
			   
		   }
	   }

public boolean getExit(){
	return exit;
}
@Override
public void logout(HashMap<String, String> message) {
	// TODO Auto-generated method stub
	System.out.println("----"+ client.getName() + " logout----" + MyTime.getTime());
	clients.remove(client);
	
	exit = true;
	try {
		if (reader != null){
			reader.close();
			reader = null;
		}
		
		if (writer != null){
			writer.close();
			writer = null;
		}
	} catch (Exception e){
		e.printStackTrace();
	}
	
}
@Override
public void netProblem(HashMap<String, String> message) {
	// TODO Auto-generated method stub
	// this for when some reason, the connection is broken during chat, so the client will retry to link, client will send joinGroup,if clients does not remove the old one 
    	Client client = null;
        for (Client eachClient : clients){
        	if (eachClient.getName().equals(message.get("name"))){
        		client = eachClient;
        	}
        }
       
    clients.remove(client);
    //if (client != null){
    //	client.closeResource();
   // }
	HashSet<String> stillJoinedGroup = JSON.parseObject(message.get("stillJoinedGroup"), new TypeReference<HashSet<String>>(){});
	this.client = new Client(message.get("name"), message.get("group"), clientSocket, reader, writer, stillJoinedGroup);
	clients.add(this.client);
	sendMessageToGroup(message.get("group"), message.get("name"),  MessageType.MSG_TYPE_NET_PROBLEM_BACK);
    System.out.println("Net is good:"+ message.get("name")+ " backs to group: " + message.get("group"));
}
}
