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

public class TableGroupsOperation implements DatabaseService {

	private Connection connection  = null;
	private PreparedStatement  preStatement = null;
	private ResultSet resultSet = null;
	
	@Override
	public boolean add(Object[] params) {
		boolean flag = false;
		// TODO Auto-generated method stub
		try {
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "insert into groups(name, creator,time) value(?,?,?) ";
			preStatement = connection.prepareStatement(sql);
			preStatement.setString(1, (String)params[0]);
			preStatement.setString(2, (String)params[1]);
			preStatement.setString(3, (String)params[2]);
			preStatement.executeUpdate();
			flag = true;
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally{
			try {
				if (preStatement != null){
					preStatement.close();
					preStatement = null;
				}
				
				if (connection != null){
					connection.close();
					connection = null;
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
		boolean flag = false;
		try{
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "delete from groups where name = ? and creator = ?";
			preStatement = connection.prepareStatement(sql);
			preStatement.setString(1, (String)params[0]);
			preStatement.setString(2, (String)params[1]);
			preStatement.executeUpdate();
			flag = true;
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch(SQLException ex){
			// handle any errors
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
					preStatement.cancel();
					preStatement = null;
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		return flag;
	}

	@Override
	public boolean update(Object[] params) {
		// TODO Auto-generated method stub
		return false;
	}

	public HashSet<String> groupSet() {
		HashSet<String> result = new HashSet<String>();
		
		try {
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "select name from groups";
			preStatement = connection.prepareStatement(sql);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()){
				result.add(resultSet.getString("name"));
			}
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally{
			try{
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
		return result;
	}
	
	public String getCreator(String groupName){
		String creator = null;
		try {
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "select creator from groups where name = ?";
			preStatement = connection.prepareStatement(sql);
			preStatement.setString(1, groupName);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()){
				creator = resultSet.getString("creator");
			}
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally{
			try{
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
		return creator;
	}
	
	public HashMap<String, String> viewOne(String name) {
		// TODO Auto-generated method stub
		HashMap<String, String> groupInfor = new HashMap<>();
		try {
			Class.forName(MySQL.driveName);
			connection = DriverManager.getConnection(MySQL.dbLink);
			String sql = "select * from groups where name = ?";
			preStatement = connection.prepareStatement(sql);
			preStatement.setString(1, name);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()){
				groupInfor.put("creator", resultSet.getString("creator"));
				groupInfor.put("time", resultSet.getString("time"));
				
			}
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLException: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally{
			try{
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
		return groupInfor;
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
