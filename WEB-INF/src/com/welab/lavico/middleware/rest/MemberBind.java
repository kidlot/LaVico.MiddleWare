package com.welab.lavico.middleware.rest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import com.welab.lavico.middleware.DB;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemberBind extends HttpServlet {

	private static final long serialVersionUID = 2L;


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		//
		if(request.getParameter("openid")==null){
		    rspn(response,0,false,"missing arg openid") ;
		    return ;
		}
		if(request.getParameter("MOBILE_TELEPHONE_NO")==null){
		    rspn(response,0,false,"missing arg MOBILE_TELEPHONE_NO") ;
		    return ;
		}
		if(request.getParameter("MEM_OLDCARD_NO")==null){
		    rspn(response,0,false,"missing arg MEM_OLDCARD_NO") ;
		    return ;
		}
    	
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
				
		    rspn(
		    	response,statement.getInt(11)
		    	, statement.getString(12).equals("Y")? true: false
		    	, statement.getString(13)
		    ) ;
		    
		} catch (Exception e) {
		    rspn(response,0,false,"occur error:"+e.getMessage()) ;
		    e.printStackTrace();
		}
    }


    private void rspn(HttpServletResponse response,int mid,Boolean issuceed,String error){
	    response.setContentType("text/javascript;charset=UTF-8");
	    try {
		    response.getWriter().println("{\"MEMBER_ID\":"+mid+",\"issuccessed\":"+(issuceed?"true":"false")+",\"error\":\""+error+"\"}");
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
}
