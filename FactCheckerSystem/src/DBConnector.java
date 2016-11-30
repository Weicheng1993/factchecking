import java.sql.Connection;
import java.sql.ResultSet;

public interface DBConnector {
	public void openDefaultConnection();
	public void openConnection(String driver, String url, String user, String pass );
	public ResultSet execQuery(String query);
	public Connection getConnection();
	public void printQueryResult(ResultSet rs);
}
