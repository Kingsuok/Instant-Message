package com.su.message.processing;

import java.util.HashMap;

public interface MessageService {
	public void register(HashMap<String, String> message);
	public void login(HashMap<String, String> message);
	//public void logout(HashMap<String, String> message);
	public void joinGroup(HashMap<String, String> message);
	public void leaveGroup(HashMap<String, String> message);
	public void groupChat(HashMap<String, String> message);
	public void groupList(HashMap<String, String> message);
	public void buddyList(HashMap<String, String> message);
	//public void success();
	//public void failure();
	public void createGroup(HashMap<String, String> message);
	public void deleteGroup(HashMap<String, String> message);
	public void logout(HashMap<String, String> message);
	public void netProblem(HashMap<String, String> message);
}
