package com.su.client.listener;

/*
 * monitor the client
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

import com.su.client.information.Client;
import com.su.message.processing.MessageProcessing;

public class ClientListenerThread extends Thread {
	
	//public volatile boolean exit = false; // when link breaks, then exit = true. exit form the thread
	private Socket client = null;
	private HashSet<Client> clients = null;
	private DataInputStream reader = null;
	private DataOutputStream writer = null;
	private MessageProcessing messageProcessing = null;
	public ClientListenerThread(Socket client, HashSet<Client> clients) {
		// TODO Auto-generated constructor stub
		super();
		this.client = client;
		this.clients = clients;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*
		 *  because do not know when the client will send a message, so use endless loop to wait. 
		 *  endless loop can work :
		 *  1, when read a message negatively, the end of the input stream could not appear, so the program will continue endlessly
		 *  2, readUTF is a block method, when there is not data from the client, it will block and stop here until message sent from the client
		 *  3, when readUTF read data from the input stream, it will read data from ending position of the last time; 
		 *  so once it read the data, if the client does not send a message, readUTF will block
		 */
		try{
			while (true){ 
				//if (exit == true){
					//do something, clients 
					
				//	close();
				//	break;
				//}else {
					if (reader == null){
						reader = new DataInputStream(client.getInputStream());
					}
				if (writer == null){
						writer = new DataOutputStream(client.getOutputStream());
				}
				
				if (messageProcessing == null){
					messageProcessing = new MessageProcessing(reader, writer,clients, client);
				}
				messageProcessing.messageProcessing(); // message processing
			    //if (messageProcessing.getExit()){
			   // 	exit = true;
			   // 	close();
			   // }
				//}
			}
		} catch (Exception e){
			
			close();
			//e.printStackTrace();
		} 
	}
	//public void setExit(boolean exit){ // change exit
	//	this.exit = exit;
	//}
	//public boolean getExit(){
	//	return exit;
	//}
	
	public void close(){
		try {
			if (client != null){
				
				client.close();
				client = null;
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
	


