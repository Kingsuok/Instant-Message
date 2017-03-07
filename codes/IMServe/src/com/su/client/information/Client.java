package com.su.client.information;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Writer;
/*
 * Client class: store clent's information, especially the group number
 */
import java.net.Socket;
import java.util.HashSet;

public class Client {
	private String name = null;
	private String groupName = null;
	private Socket socket = null;
	private DataInputStream reader = null;
	private DataOutputStream writer = null;
	private HashSet<String> joinedGroup = null; 
	
	
	public DataInputStream getReader() {
		return reader;
	}

	

	public DataOutputStream getWriter() {
		return writer;
	}



	public Client(String name, String groupName, Socket socket, DataInputStream reader, DataOutputStream writer,HashSet<String> joinedGroup ){
		this.name = name;
		this.groupName = groupName;
		this.socket = socket;
		this.reader = reader;
		this.writer = writer;
		this.joinedGroup = joinedGroup;
	}

	public String getName() {
		return name;
	}
    
	public HashSet<String> getJoinedGroup(){
		return joinedGroup;
	}

	public String getGroupName() {
		return groupName;
	}
    
	public void setGroupName(String groupName){
		this.groupName = groupName;
	}

	public Socket getSocket() {
		return socket;
	}
	public void closeResource(){
		try {
			if (socket != null){
				
				socket.close();
				socket = null;
			}
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
}
