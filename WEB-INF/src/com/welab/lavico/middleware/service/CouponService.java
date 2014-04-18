package com.welab.lavico.middleware.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.JdbcTemplate;

import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;

public class CouponService {

	
	/**
	 * 
	 * @param brand				品牌代号
	 * @param openid			微信ID
	 * @param promotionCode		活动代码，DRP_PROMOTION_THEME 表里的 PROMOTION_CODE 字段
	 * @param otherPromId		其他系统里的活动ID，例如 welab
	 * @param qty				优惠券金额
	 * @param point				积分(正数为增加积分，负数为扣减积分，0为于积分无关)
	 * @return
	 * @throws Error
	 */
	public String GetCoupon(String brand,String openid
			,String promotionCode
			,String otherPromId
			,float qty
			,int point
			,String memo
			) throws Error {

    	JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;

    	try {
		    Connection conn = jdbcTpl.getDataSource().getConnection();

		    CallableStatement statement = conn.prepareCall("{call PRO_MEMBER_GET_COUPON(?,?,?,?,?,?,?,?,?,?,?,?)}");
		    statement.setString(1, openid);
		    statement.setString(2, otherPromId);
		    statement.setString(3, promotionCode);
		    statement.setString(4, brand+"999");
		    
		    
		    if(qty<0){
		    	statement.setString(5, null);
		    }
		    else{
		    	statement.setFloat(5, qty);
		    }

		    if(point==0){
			    statement.setString(6, null);
			    statement.setString(7, null);
		    }
		    else {
			    statement.setString(6, point<0? "2":"1");
			    statement.setInt(7, Math.abs(point));
		    }

		    statement.setString(8, "1");
		    statement.setString(9, qty<0?"1":"0");

		    statement.registerOutParameter(10, Types.VARCHAR);
		    statement.registerOutParameter(11, Types.VARCHAR);
		    statement.registerOutParameter(12, Types.VARCHAR);

		    statement.execute() ;

		    if( statement.getString(10).equals("N") ){
		    	throw new Error(statement.getString(11)) ;
		    }
		    
		    String couponno = statement.getString(12) ;
		    
		    // 更新 memo 
		    if(memo!=null){
			    jdbcTpl.update(
			    		"update DRP_PROMOTION_COUPON set MEMO=? where COUPON_NO=?"
			    		, new Object[]{memo,couponno}
			    ) ;
		    }

		    return couponno ;
		    		
    	} catch (SQLException e) {
    		throw new Error("系统在为会员发放优惠券时，遇到了错误。"+e.getMessage(),e) ;
		}
	}
}
