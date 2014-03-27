package com.welab.lavico.middleware.controller;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import com.welab.lavico.middleware.DB;
import com.welab.lavico.middleware.DocumentNoService;


@Controller
public class MemberController {

	/**
	 * 申请会员卡
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Apply")
	public @ResponseBody Map<String,Object> doApply(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("MEMBER_ID", null) ;
		rspn.put("issuccessed", false) ;
		
		JdbcTemplate jdbcTpl ;
		try{
			jdbcTpl = DB.getJdbcTemplate(brand) ;
		} catch(NoSuchBeanDefinitionException e) {
			rspn.put("error", "The paramter brand is invalid.") ;
			return rspn ;
		}

		String seqid = null ;
		try {
		    seqid = (new DocumentNoService()).getDocumentNo(303048,null,brand+"999",jdbcTpl,brand+"999") ;
		} catch (Exception e) {
	    	rspn.put("error",e.getMessage()) ;
		    e.printStackTrace();
		}

		callDBProc(jdbcTpl,rspn,brand,seqid,true,request) ;

		return rspn ;
    }
    

	/**
	 * 绑定老会员卡
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Bind")
	public @ResponseBody Map<String,Object> doBind(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("MEMBER_ID", null) ;
		rspn.put("issuccessed", false) ;
		
		JdbcTemplate jdbcTpl ;
		try{
			jdbcTpl = DB.getJdbcTemplate(brand) ;
		} catch(NoSuchBeanDefinitionException e) {
			rspn.put("error", "The paramter brand is invalid.") ;
			return rspn ;
		}

		if(request.getParameter("MEM_OLDCARD_NO")==null){
			rspn.put("error", "missing arg MEM_OLDCARD_NO") ;
		    return rspn ;
		}
		
		callDBProc(jdbcTpl,rspn,brand,null,false,request) ;
    	
		return rspn ;
	}



	/**
	 * 会员卡解除绑定
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/Member/Unbind")
	public @ResponseBody Map<String,Object> doUnbind(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("issuccessed", false) ;
		
		JdbcTemplate jdbcTpl ;
		try{
			jdbcTpl = DB.getJdbcTemplate(brand) ;
		} catch(NoSuchBeanDefinitionException e) {
			rspn.put("error", "The paramter brand is invalid.") ;
			return rspn ;
		}

		// 检查参数
		if(request.getParameter("MEMBER_ID")==null){
			rspn.put("error", "missing arg MEMBER_ID") ;
		    return rspn ;
		}
		if(request.getParameter("openid")==null){
			rspn.put("error", "missing arg openid") ;
		    return rspn ;
		}

    	int res = jdbcTpl.update(
    			"update PUB_MEMBER_ID set SYS_MEMBER_MIC_ID='' where SYS_MEMBER_ID=? and SYS_MEMBER_MIC_ID=?" ,
    			new Object[]{
    			   request.getParameter("MEMBER_ID")
    			   , request.getParameter("openid")
    			}
    		) ;
    	
    	rspn.put("issuccessed",res>=1) ;
		
		return rspn ;
	}

	private void callDBProc ( JdbcTemplate jdbcTpl, Map<String, Object> rspn
			,String brand
			,String seqid
			,Boolean isapply
			,HttpServletRequest request){

		if(request.getParameter("openid")==null){
			rspn.put("error", "missing arg openid.") ;
		    return ;
		}
		if(request.getParameter("MOBILE_TELEPHONE_NO")==null){
			rspn.put("error", "missing arg MOBILE_TELEPHONE_NO") ;
		    return ;
		}
		if(request.getParameter("MEM_PSN_CNAME")==null){
			rspn.put("error", "missing arg MEM_PSN_CNAME") ;
		    return ;
		}
		
    	try {

		    Connection conn = jdbcTpl.getDataSource().getConnection() ;

		    CallableStatement statement = conn.prepareCall("{call PRO_MEMBER_APPORBIND(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
		    statement.setString(1, brand);
		    statement.setString(2, request.getParameter("openid")); 
		    statement.setString(3, brand+"999");
		    statement.setString(4, seqid); // I_MEM_APP_NO
		    statement.setString(5, isapply?"0":"1");

		    statement.setString(6, request.getParameter("MEM_PSN_CNAME"));
		    
		    // 申请会员卡 ---
		    if(isapply) {
		    	statement.setString(7, request.getParameter("MEM_PSN_SEX"));

		    	// 生日
			    String birthday = request.getParameter("MEM_PSN_BIRTHDAY") ;
			    if(birthday==null){
				    statement.setDate(8,null);
			    }
			    else {
				    java.text.SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				    java.util.Date ud= df.parse(birthday);
				    statement.setDate(8, new java.sql.Date(ud.getTime()));
			    }
			    //I_MEM_OLDCARD_NO
			    statement.setString(10, "") ; 
		    }
		    // 绑定会员卡 ---
		    else {
		    	// MEM_PSN_SEX
			    statement.setString(7, ""); 
		    	// 生日
			    statement.setDate(8, null);
			    //I_MEM_OLDCARD_NO
			    statement.setString(10, request.getParameter("MEM_OLDCARD_NO")) ;
		    }

		    statement.setString(9, request.getParameter("MOBILE_TELEPHONE_NO"));

		    statement.registerOutParameter(11, Types.INTEGER);
		    statement.registerOutParameter(12, Types.VARCHAR);
		    statement.registerOutParameter(13, Types.VARCHAR);

		    statement.execute() ;

		    if( statement.getString(12).equals("Y") ){
		    	rspn.put("MEMBER_ID",statement.getInt(11)) ;
		    	rspn.put("issuccessed",true) ;
		    }
		    else {
		    	rspn.put("error",statement.getString(13)) ;
		    }
		    
		} catch (Exception e) {
	    	rspn.put("error",e.getMessage()) ;
		    e.printStackTrace();
		}
	}
}