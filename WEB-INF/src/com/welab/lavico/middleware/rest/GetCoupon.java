package com.welab.lavico.middleware.rest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import javax.servlet.ServletException;
import com.welab.lavico.middleware.DB;
import org.springframework.jdbc.core.JdbcTemplate;

public class GetCoupon extends HttpServlet {

	private static final long serialVersionUID = 6L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException {

		if(request.getParameter("openid")==null){
		    rspn(response,false,0,"missing arg openid");
		    return ;
		}
		if(request.getParameter("WECHAT_PTHEME_ID")==null){
		    rspn(response,false,0,"missing arg WECHAT_PTHEME_ID");
		    return ;
		}
		if(request.getParameter("PROMOTION_CODE")==null){
		    rspn(response,false,0,"missing arg PROMOTION_CODE");
		    return ;
		}

    	JdbcTemplate jdbcTpl = DB.getJdbcTemplate() ;
    	try {
		    Connection conn = jdbcTpl.getDataSource().getConnection();

		    CallableStatement statement = conn.prepareCall("{call PRO_MEMBER_GET_COUPON(?,?,?,?,?,?,?)}");
		    statement.setString(1, request.getParameter("openid"));
		    statement.setString(2, request.getParameter("WECHAT_PTHEME_ID"));
		    statement.setString(3, request.getParameter("PROMOTION_CODE"));
		    statement.setString(4, "L999");

		    statement.registerOutParameter(5, Types.VARCHAR);
		    statement.registerOutParameter(6, Types.VARCHAR);
		    statement.registerOutParameter(7, Types.INTEGER);

		    statement.execute() ;

			rspn(
				response
				, statement.getString(5).equals("Y")
				, statement.getInt(7)
				, statement.getString(6)
			) ;

    	} catch (Exception e) {
    		rspn(response,false,0,e.getMessage()) ;
		    e.printStackTrace();
		}
    }

    private void rspn(HttpServletResponse response,Boolean issuceed,int couponid,String error){
	    response.setContentType("text/javascript;charset=UTF-8");
	    try {
		    response.getWriter().println("{\"coupon_no\":"+couponid+",\"issuccessed\":"+(issuceed?"true":"false")+",\"error\":\""+error+"\"}");
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
}