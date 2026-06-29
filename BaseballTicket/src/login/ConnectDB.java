package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.FileInputStream;

public class ConnectDB {
	public static Connection getConnection() throws Exception {
    	
		Properties props = new Properties();
		props.load(ConnectDB.class.getClassLoader().getResourceAsStream("config.properties"));

        String url = props.getProperty("db.url");// login 스키마와 연결
        String user = props.getProperty("db.user");//login id
        String password = props.getProperty("db.password");//login password
        
        
        Class.forName("com.mysql.cj.jdbc.Driver"); //드라이버 로딩
        return DriverManager.getConnection(url, user, password); //연결
    }
}
    
