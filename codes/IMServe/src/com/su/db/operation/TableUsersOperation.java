package com.su.db.operation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.su.database.DatabaseService;
import com.su.database.MySQL;

public class TableUsersOperation implements DatabaseService {
	
	private Connection connection = null;
	private PreparedStatement preStatement = null;
	private ResultSet resultSet = null;
	
	
	@Override
	public boolean add(Object[] params) {
		// TODO Auto-generated method stub
		boolean flag = false;
		try{
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "insert into users(name, password) values(?,?)";
			preStatement = connection.prepareStatement(sql);
			preStatement.setString(1, (String)params[0]);
			preStatement.setString(2, (String)params[1]);
			preStatement.executeUpdate();
			flag = true;
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex){
			// handle any errors
			
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			try {
				if (connection != null){
					connection.close();
					connection = null;
				}
				if (preStatement != null){
					preStatement.close();
					preStatement = null;
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		return flag;
	}

	@Override
	public boolean delete(Object[] params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Object[] params) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPassword(String name){
		String password = null;
		try {
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "select password from users where name = ?";
			preStatement = connection.prepareStatement(sql);
			preStatement.setString(1, name);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()){
				password = resultSet.getString("password");
			}
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex){
			
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally{
			try {
				if (connection != null){
					connection.close();
					connection = null;
				}
				if (preStatement != null){
					preStatement.close();
					preStatement = null;
				}
				if (resultSet != null){
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		return password;
	}
	
	public HashSet<String> usersNameSet(){
		HashSet<String> usersSet = new HashSet<String>();
		try{
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "select name from users";
			preStatement = connection.prepareStatement(sql);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()){
				usersSet.add(resultSet.getString("name"));
			}
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex) {
			// TODO: handle exception
			
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			try {
				if (connection != null){
					connection.close();
					connection = null;
				}
				if (preStatement != null){
					preStatement.close();
					preStatement = null;
				}
				if (resultSet != null){
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return usersSet;
	}
	@Override
	public HashMap<String, String> viewOne(String[] selectArgs) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public ArrayList<HashMap<String, String>> viewOnes(String[] selectArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean emptyTable() {
		// TODO Auto-generated method stub
		return false;
	}

}
