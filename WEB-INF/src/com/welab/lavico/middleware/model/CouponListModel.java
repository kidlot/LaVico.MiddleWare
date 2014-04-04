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
		String sql = "SELECT count(*)"
			+ " FROM PUB_MEMBER_COUPON "
				+ " left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)"
				+ " left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)"
				+ " WHERE PUB_MEMBER_COUPON.SYS_MEMBER_ID=? and DRP_PROMOTION_COUPON.COUPON_STATUS=?" ;
		return jdbcTpl.queryForInt(sql,new Object[]{memberId,status} ) ;
	}

	public List<Map<String,Object>> queryCouponList(int memberId,String status){
		return queryCouponList(memberId,status,1,20) ;
	}

	public List<Map<String,Object>> queryCouponList(int memberId,String status,int pageNum){
		return queryCouponList(memberId,status,pageNum,20) ;
	}
	
	public List<Map<String,Object>> queryCouponList(int memberId,String status,int pageNum,int perPage){

		if(status==null){
			status = "02" ;
		}

		String sql = "SELECT"
				+ " DRP_PROMOTION_COUPON.BEGIN_DATE"
				+ ", DRP_PROMOTION_COUPON.END_DATE"
				+ ", DRP_PROMOTION_COUPON.COUPON_STATUS"
				+ ", DRP_PROMOTION_THEME.PROMOTION_CODE"
				+ ", DRP_PROMOTION_COUPON.COUPON_NO"
				+ ", row_number() OVER(ORDER BY null) AS \"row_number\""
			+ " FROM PUB_MEMBER_COUPON "
				+ " left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)"
				+ " left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)"
				+ " WHERE PUB_MEMBER_COUPON.SYS_MEMBER_ID=? and DRP_PROMOTION_COUPON.COUPON_STATUS=?" ;
		
		sql = " select *"
				+ "	from (select *"
				+ "  	from ("+sql+") p"
				+ "     where p.\"row_number\">?)"
				+ " where rownum<=?" ;
		
		List<Map<String,Object>> list = jdbcTpl.queryForList( sql,new Object[]{ memberId, status, (pageNum-1)*perPage, perPage } ) ;
	
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
