package com.welab.lavico.middleware.rest;

import javax.servlet.http.HttpServlet;
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

public class Member extends HttpServlet {
	
    private static final long serialVersionUID = 1L;
 
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

	// 检查参数
	if(request.getParameter("openid")==null){
	    rspn(response,0,"N","missing arg openid") ;
	    return ;
	}
	// http://127.0.0.1:8080/LaVico/Member?openid=123&MEM_PSN_CNAME=alee&MEM_PSN_SEX=1&MOBILE_TELEPHONE_NO=18812341234&MEM_PSN_BIRTHDAY=1982-10-11
    	
    	JdbcTemplate jdbcTpl = DB.getJdbcTemplate() ;
    	String seqid = null ;
    	try {
	    seqid = (new DocumentNoService()).getDocumentNo(303048,null,"L999",jdbcTpl,"L999") ;
	    System.out.println(seqid) ;
			
	    Connection conn = jdbcTpl.getDataSource().getConnection() ;
			
	    CallableStatement statement = conn.prepareCall("{call PRO_MEMBER_APPORBIND(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
	    statement.setString(1, "L");
	    statement.setString(2, request.getParameter("openid")); 
	    statement.setString(3, "L999");
	    statement.setString(4, seqid); // I_MEM_APP_NO VARCHAR2(20) 申请单号 我方会提供生成算法
	    statement.setString(5, "0");

	    statement.setString(6, request.getParameter("MEM_PSN_CNAME"));
	    statement.setString(7, request.getParameter("MEM_PSN_SEX"));

	    java.text.SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
	    java.util.Date ud= df.parse(request.getParameter("MEM_PSN_BIRTHDAY"));
	    statement.setDate(8, new java.sql.Date(ud.getTime()));

	    statement.setString(9, request.getParameter("MOBILE_TELEPHONE_NO"));
	    statement.setString(10, "");

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
    
    private void rspn(HttpServletResponse response,int mid,String issuceed,String error){
    	response.setContentType("text/javascript;charset=UTF-8");
    	try {
	    response.getWriter().println("{\"O_PUB_MEMBER_ID\":"+mid+",\"O_ISSUCCEED\":\""+issuceed+"\",\"O_HINT\":\""+error+"\"}");
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }
}
