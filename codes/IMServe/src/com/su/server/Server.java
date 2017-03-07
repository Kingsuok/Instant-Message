package com.su.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.HashSet;

import com.su.client.information.Client;
import com.su.client.listener.ClientListenerThread;
import com.su.message.processing.MessageProcessing;


public class Server {
	private static ServerSocket serverSocket = null;
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		HashSet<Client> clients = new HashSet<Client>();
	    try {
		serverSocket = new ServerSocket(6666);
		
		System.out.println("--server has started--" + new java.util.Date().toString());
		// avoid block the main thread, open a new thread to monitor clients
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true){
						
						int i = 1;
						Socket client = serverSocket.accept();// accept a client to connect, this method will block until a client apply to connect 
						System.out.println("--A client to connect--" + client);
						// open a thread to deal with this client
						ClientListenerThread clientListenerThread = new ClientListenerThread(client, clients); //monitor messages
						clientListenerThread.start();
						//check whether the connection is still alive or broken
						//new CheckConnection(clientListenerThread, client, clients).start(); // checke link connection status
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			
			}
		   }).start();
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		
			try {
				if (serverSocket != null){
					serverSocket.close();
					serverSocket = null;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
	} 

}
