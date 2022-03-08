package com.seleniumProject.Libraries.DatabaseConnectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

	private Connection con;
	private Statement stmt;
	private PreparedStatement ps;
	private String dbUrl = "jdbc:mysql://localhost:3306/testdatabse";
	private String username = "root";
	private String password = "root";

	public void createDatabaseConnection() throws ClassNotFoundException {

		// Class.forName("com.mysql.jdbc.Driver");
		Class.forName("com.mysql.cj.jdbc.Driver");

		try {
			System.out.println("Username and pass is " + username + "=== pass is === " + password);
			con = DriverManager.getConnection(dbUrl, username, password);
			stmt = con.createStatement();
		} catch (SQLException e) {
			System.out.println("Connection is not created .....");
			e.printStackTrace();
		}
	}

	public int createNewDatabase() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://localhost/testdatabase?user=root&password=root");
		ps = con.prepareStatement("CREATE DATABASE vikastesting");
		int result = ps.executeUpdate();
		return result;

	}

	public ResultSet performQuery() throws SQLException {
		String query = "select *  from test_table;";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public void closeConnection() throws SQLException {
		con.close();

	}

}
