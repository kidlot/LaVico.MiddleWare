package com.welab.lavico.middleware.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class CouponListModel {

	public CouponListModel(JdbcTemplate jdbcTpl) {
		this.jdbcTpl = jdbcTpl ;
	}

	public int totalLength(int memberId,String status){
		return totalLength("PUB_MEMBER_COUPON.SYS_MEMBER_ID",memberId,status) ; 
	}
	public int totalLength(String promotionCode,String status){
		return totalLength("DRP_PROMOTION_THEME.PROMOTION_CODE",promotionCode,status) ; 
	}
	private int totalLength(String column,Object value,String status){
		String sql = "SELECT count(*)"
			+ " FROM PUB_MEMBER_COUPON "
				+ " left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)"
				+ " left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)"
				+ " WHERE "+column+"=? and DRP_PROMOTION_COUPON.COUPON_STATUS=?" ;
		return jdbcTpl.queryForInt(sql,new Object[]{value,status} ) ;
	}

	public List<Map<String,Object>> queryCouponList(int memberId,String status,int pageNum,int perPage){
		return queryCouponList("PUB_MEMBER_COUPON.SYS_MEMBER_ID",memberId,status,1,20) ;
	}

	public List<Map<String,Object>> queryCouponList(String promotionCode,String status,int pageNum,int perPage){
		return queryCouponList("DRP_PROMOTION_THEME.PROMOTION_CODE",promotionCode,status,pageNum,20) ;
	}
	
	private List<Map<String,Object>> queryCouponList(String column,Object value,String status,int pageNum,int perPage){

		if(status==null){
			status = "02" ;
		}

		String sql = "SELECT"
				+ " DRP_PROMOTION_COUPON.BEGIN_DATE"
				+ ", DRP_PROMOTION_COUPON.END_DATE"
				+ ", DRP_PROMOTION_COUPON.COUPON_STATUS"
				+ ", DRP_PROMOTION_COUPON.COUPON_TYPE"
				+ ", DRP_PROMOTION_COUPON.COUPON_CLASS"
				+ ", DRP_PROMOTION_COUPON.COUPON_QTY"
				+ ", DRP_PROMOTION_COUPON.CREAT_DATE"
				+ ", DRP_PROMOTION_COUPON.BIND_DATE"
				+ ", DRP_PROMOTION_COUPON.USED_DATE"
				+ ", DRP_PROMOTION_THEME.PROMOTION_CODE"
				+ ", DRP_PROMOTION_COUPON.COUPON_NO"
				+ ", PUB_MEMBER_COUPON.SYS_MEMBER_ID"
				+ ", row_number() OVER(ORDER BY null) AS \"row_number\""
			+ " FROM PUB_MEMBER_COUPON "
				+ " left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)"
				+ " left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)"
			+ " WHERE "+column+"=? and DRP_PROMOTION_COUPON.COUPON_STATUS=?" ;
		
		sql = " select *"
				+ "	from (select *"
				+ "  	from ("+sql+") p"
				+ "     where p.\"row_number\">?)"
				+ " where rownum<=?" ;
		
		List<Map<String,Object>> list = jdbcTpl.queryForList( sql,new Object[]{ value, status, (pageNum-1)*perPage, perPage } ) ;
	
		Iterator<Map<String, Object>> iter = list.iterator() ;
    	while(iter.hasNext()){
    		Map<String, Object> coupon = (Map<String, Object>) iter.next();
    		coupon.put("BEGIN_DATE", ((java.sql.Timestamp) coupon.get("BEGIN_DATE")).toString().substring(0,19)) ;
    		coupon.put("END_DATE", ((java.sql.Timestamp) coupon.get("END_DATE")).toString().substring(0,19)) ;
    	}
	
		return list ;
	}

	private JdbcTemplate jdbcTpl ;
}
