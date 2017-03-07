package com.su.database;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * database operations
 */
public interface DatabaseService {
	public boolean add(Object[] params);
	
	public boolean delete(Object[] params);
	
	public boolean update(Object[] params);
	
	public HashMap<String, String> viewOne(String[] selectArgs);
	
	public ArrayList<HashMap<String, String>> viewOnes(String[] selectArgs);
	
	public boolean emptyTable();
	
}
