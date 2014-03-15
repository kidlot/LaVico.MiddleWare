package com.welab.lavico.middleware;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DB {

	private static DB uniqueInstance = null;

	public String driver = "oracle.jdbc.driver.OracleDriver";
	public String strUrl = "jdbc:oracle:thin:@localhost:1521:hldrp301";
	public Statement stmt = null;
	public ResultSet rs = null;
	public Connection conn = null;
	public CallableStatement cstmt = null;
	
	private DB() {
//		String classPath = DB.class.getClassLoader().getResource("").toString();
//		String webxmlPath = "";
//
//		Pattern p = Pattern.compile("(.*)/classes/");
//		Matcher m = p.matcher(classPath);
//
//		if(m.find()){
//			webxmlPath = m.group(1);
//		}else{
//			try {
//				throw new Exception("配置文件路径不正确！");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		

	}
	
	public Connection conn() {
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(strUrl, "wintest", "testpound");
			conn.setAutoCommit(false);

		} catch (SQLException ex2) {
			ex2.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public void close(){

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static DB getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DB();
		}
		return uniqueInstance;
	}
	
	public static JdbcTemplate getJdbcTemplate(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		JdbcDaoSupport dao = (JdbcDaoSupport)ctx.getBean("jdbcDao");
		return dao.getJdbcTemplate() ;
	}
}