/**
 * JDBC java mysql connector
 * @author yuweicheng
 *
 */

import java.sql.*;


public class JDBC implements DBConnector{
	static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static String DB_URL = "jdbc:mysql://localhost/class";
	
	static String USER = "root";
	static String PASS = "";
	
	private Connection conn = null;
	
	@Override
	public void openDefaultConnection() {
		// TODO Auto-generated method stub
		System.out.println("Connecting to database: "+DB_URL);
		try {
			Class.forName(JDBC_DRIVER).newInstance();
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void openConnection(String driver, String db_url, String user, String pass) {
		System.out.println("Connecting to database: "+ db_url);
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(db_url, user, pass);
			JDBC_DRIVER = driver;
			DB_URL = db_url;
			USER = user;
			PASS = pass;
			
		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet execQuery(String query) {
		try {
		
			Statement stat = conn.createStatement();
			System.out.println(query);
			ResultSet rs = stat.executeQuery(query);
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
	}
	
	public void printQueryResult(ResultSet rs) {
		try {
			if (rs!=null){
				int count =  rs.getMetaData().getColumnCount();

			
				while (rs.next()) {
					for (int i =1 ; i < count + 1; ++i) {
						System.out.print(rs.getString(i) + " ");
					}
					System.out.println();
				}
			}
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
