package com.welab.lavico.middleware.rest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.welab.lavico.middleware.DB;

import org.springframework.jdbc.core.JdbcTemplate;

public class Coupons extends HttpServlet {

	private static final long serialVersionUID = 6L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException {

		if(request.getParameter("MEMBER_ID")==null){
		    rspn(response,null,"missing arg MEMBER_ID");
		    return ;
		}
		
    	JdbcTemplate jdbcTpl = DB.getJdbcTemplate() ;
    	
    	Iterator iter = jdbcTpl.queryForList(
    			"SELECT"
    				+ " DRP_PROMOTION_COUPON.BEGIN_DATE"
    				+ ", DRP_PROMOTION_COUPON.END_DATE"
    				+ ", DRP_PROMOTION_COUPON.COUPON_STATUS"
    				+ ", DRP_PROMOTION_THEME.PROMOTION_CODE"
    				+ ", DRP_PROMOTION_COUPON.COUPON_NO"
    			+ " FROM PUB_MEMBER_COUPON "
    				+ " left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)"
    				+ " left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)"
	    		+ " WHERE PUB_MEMBER_COUPON.SYS_MEMBER_ID=?"
    			,new Object[]{ Integer.valueOf(request.getParameter("MEMBER_ID").toString()) }
    		).iterator() ;
    	
    	String outBuff = "{ \"coupons\":[" ;
    	
    	int idx = 0 ;
    	while(iter.hasNext()){
    		if(idx++>0){
    			outBuff+= "," ;
    		}

    		Map userMap = (Map) iter.next();

    		outBuff+="{" ;
    		outBuff+="\"begin\":\"" + ((java.sql.Timestamp)userMap.get("BEGIN_DATE")).toString() + "\"," ;
    		outBuff+="\"end\":\"" + ((java.sql.Timestamp)userMap.get("END_DATE")).toString() + "\"," ;
    		outBuff+="\"status\":\"" + userMap.get("COUPON_STATUS").toString() + "\"," ;
    		outBuff+="\"promotion_code\":\"" + userMap.get("PROMOTION_CODE").toString() + "\"," ;
    		outBuff+="\"coupon_no\":\"" + userMap.get("COUPON_NO").toString() + "\"" ;
    		outBuff+="}" ;
    	}
    	
    	outBuff+= "]}" ;
    	
    	rspn(response,outBuff,null) ;
    }

    private void rspn(HttpServletResponse response,String json,String error){
	    response.setContentType("text/javascript;charset=UTF-8");
	    try {
		    response.getWriter().println(json==null?"{\"error\":\""+error+"\"}":json);
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
}