package com.welab.lavico.middleware.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;
import com.welab.lavico.middleware.service.DaoBrandError;

public class MemberCardService {

	/**
	 * 处理新会员申请的服务方法
	 * 
	 * @param brand 	品牌名称
	 * @param openid 	微信id
	 * @param name 		会员姓名
	 * @param mobile 	手机号码
	 * @param gender 	性别 0=女, 1=男
	 * @param birthday 	生日
	 * 
	 * @return MEMBER_ID
	 * 
	 * @throws DaoBrandError
	 * @throws Exception 
	 */
	public int apply( String brand, String openid
			, String name
			, String mobile
			, String gender
			, String birthday
			) throws Error, DaoBrandError, Exception{

		JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
		
		CallableStatement statement = makesql(jdbcTpl,brand,openid,name,mobile) ;

	    // get seq no
		String seqid = (new DocumentNoService()).getDocumentNo(303048,null,brand+"999",jdbcTpl,brand+"999") ;
	    statement.setString(4, seqid); // I_MEM_APP_NO
	    
	    statement.setString(5, "0");
    	statement.setString(7, gender);	// gender
	    // birthday
	    if(birthday!=null){
		    java.text.SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		    java.util.Date ud= df.parse(birthday);
		    statement.setDate(8, new java.sql.Date(ud.getTime()));
	    }
		
	    // execute process
	    statement.execute() ;
	    
	    if( statement.getString(12).equals("Y") ){
	    	return statement.getInt(11) ;
	    }
	    else {
	    	throw new Exception(statement.getString(13)) ;
	    }
	}

	/**
	 * 处理老会员卡绑定的服务方法
	 * 
	 * @param brand 		品牌名称
	 * @param openid 		微信id
	 * @param name 			会员姓名
	 * @param mobile 		手机号码
	 * @param oldCardNo 	老会员卡卡号
	 * 
	 * @return MEMBER_ID
	 * 
	 * @throws DaoBrandError
	 * @throws Error
	 * @throws Exception 
	 */
	public int bind( String brand, String openid
			, String name
			, String mobile
			, String oldCardNo
			) throws DaoBrandError, Error, Exception {

		if(oldCardNo==null||oldCardNo.isEmpty()){
			throw new Error("missing arg old card NO.") ;
		}
		
		JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
		
		CallableStatement statement = makesql(jdbcTpl,brand,openid,name,mobile) ;
		
	    statement.setString(5, "1");
	    statement.setString(10, oldCardNo) ;
		
	    // execute process
	    statement.execute() ;
	    
	    if( statement.getString(12).equals("Y") ){
	    	return statement.getInt(11) ;
	    }
	    else {
	    	throw new Exception(statement.getString(13)) ;
	    }
	}

	/**
	 * 解除微信和会员的绑定服务
	 * 
	 * @param brand		品牌代号
	 * @param openid	微信ID
	 * @param memberid	会员ID
	 * 
	 * @return boolean
	 * 
	 * @throws DaoBrandError
	 * @throws Error
	 */
	public boolean unbind(String brand,String openid,String memberid)
			throws DaoBrandError, Error {

		JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;

		// 检查参数
		if(memberid==null||memberid.isEmpty()){
			throw new Error("missing arg member id") ;
		}
		if(openid==null||openid.isEmpty()){
			throw new Error("missing arg openid") ;
		}

    	int res = jdbcTpl.update(
    			"update PUB_MEMBER_ID set SYS_MEMBER_MIC_ID='' where SYS_MEMBER_ID=? and SYS_MEMBER_MIC_ID=?" ,
    			new Object[]{ memberid, openid }
    		) ;
    	
    	return res>=1 ;
	}


	/**
	 * 创建一个 CallableStatement 对象，并配置 申请会员，绑定会员卡 两个操作过程中 相同的参数
	 * 
	 * @param jdbcTpl
	 * @param brand
	 * @param openid
	 * @param name
	 * @param mobile
	 * 
	 * @return CallableStatement
	 * 
	 * @throws Error
	 * @throws DaoBrandError
	 * @throws SQLException
	 * @throws ParseException
	 */
	private CallableStatement makesql(
			JdbcTemplate jdbcTpl
			, String brand
			, String openid
			, String name
			, String mobile
		) throws Error, DaoBrandError, SQLException, ParseException {

		if(openid==null||openid.isEmpty()){
			throw new Error("missing arg openid.") ;
		}
		if(mobile==null||mobile.isEmpty()){
			throw new Error("missing arg mobile.") ;
		}
		if(name==null||name.isEmpty()){
			throw new Error("missing arg member name.") ;
		}

	    Connection conn = jdbcTpl.getDataSource().getConnection();

	    CallableStatement statement = conn.prepareCall("{call PRO_MEMBER_APPORBIND(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
	    statement.setString(1, brand);
	    statement.setString(2, openid); 
	    statement.setString(3, brand+"999");
	    statement.setString(4, null); 			// I_MEM_APP_NO
	    statement.setString(5, "0");
	    statement.setString(6, name);
	    statement.setString(7, null);			// MEM_PSN_SEX
	    statement.setDate(8, null);
	    statement.setString(9, mobile);
	    statement.setString(10, "");

	    statement.registerOutParameter(11, Types.INTEGER);
	    statement.registerOutParameter(12, Types.VARCHAR);
	    statement.registerOutParameter(13, Types.VARCHAR);

	    return statement ;
	}
}

