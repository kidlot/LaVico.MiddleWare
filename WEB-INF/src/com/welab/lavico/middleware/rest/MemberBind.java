package com.welab.lavico.middleware.rest;

//import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import java.text.SimpleDateFormat;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import com.welab.lavico.middleware.DB;
import com.welab.lavico.middleware.DocumentNoService;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemberBind extends RestBase {
 
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

	//
	if(request.getParameter("openid")==null){
	    rspn(response,0,"N","missing arg openid") ;
	    return ;
	}
	if(request.getParameter("MOBILE_TELEPHONE_NO")==null){
	    rspn(response,0,"N","missing arg MOBILE_TELEPHONE_NO") ;
	    return ;
	}
	if(request.getParameter("MEM_OLDCARD_NO")==null){
	    rspn(response,0,"N","missing arg MEM_OLDCARD_NO") ;
	    return ;
	}
	
	
	
	
	// http://127.0.0.1:8080/LaVico/Member?openid=123&MEM_PSN_CNAME=alee&MOBILE_TELEPHONE_NO=18812341234
    	
    	JdbcTemplate jdbcTpl = DB.getJdbcTemplate() ;
    	try {
			
		    Connection conn = jdbcTpl.getDataSource().getConnection() ;
				
		    CallableStatement statement = conn.prepareCall("{call PRO_MEMBER_APPORBIND(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
		    statement.setString(1, "L");
		    statement.setString(2, request.getParameter("openid")); 
		    statement.setString(3, "L999");
		    statement.setString(4, ""); // I_MEM_APP_NO
		    statement.setString(5, "1");
		    
		    statement.setString(6, request.getParameter("MEM_PSN_CNAME"));
		    statement.setString(7, ""); // MEM_PSN_SEX
		    statement.setDate(8, null);

		    statement.setString(9, request.getParameter("MOBILE_TELEPHONE_NO"));
		    statement.setString(10, request.getParameter("MEM_OLDCARD_NO")) ; //I_MEM_OLDCARD_NO
		    
		    statement.registerOutParameter(11, Types.INTEGER);
		    statement.registerOutParameter(12, Types.VARCHAR);
		    statement.registerOutParameter(13, Types.VARCHAR);
				
		    statement.execute() ;
				
		    rspn(response,statement.getInt(11),statement.getString(12),statement.getString(13)) ;
		    
		} catch (Exception e) {
		    rspn(response,0,"N","occur error:"+e.getMessage()) ;
		    e.printStackTrace();
		}
    }
    
}
