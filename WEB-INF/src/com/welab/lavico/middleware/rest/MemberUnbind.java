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

public class MemberUnbind extends RestBase {
 
	/**
	 * http://127.0.0.1:8080/welab.middleware/MemberUnbind?MEMBER_ID=123&openid=123
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
    	
		if(request.getParameter("openid")==null){
		    rspn(response,0,"N","missing arg openid") ;
		    return ;
		}
		
		if(request.getParameter("MEMBER_ID")==null){
		    rspn(response,0,"N","missing arg MEMBER_ID") ;
		    return ;
		}
		
    	JdbcTemplate jdbcTpl = DB.getJdbcTemplate() ;
    	int res = jdbcTpl.update(
    			"update PUB_MEMBER_ID set SYS_MEMBER_MIC_ID='' where SYS_MEMBER_ID=? and SYS_MEMBER_MIC_ID=?" ,
    			new Object[]{
    			   request.getParameter("MEMBER_ID")
    			   , request.getParameter("openid")
    			}
    		) ;

    	rspn(response,0, res>=1? "Y": "N","") ;
    }
    
}
