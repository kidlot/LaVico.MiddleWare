package com.welab.lavico.middleware.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class CouponListModel {

	public CouponListModel(JdbcTemplate jdbcTpl) {
		this.jdbcTpl = jdbcTpl ;
	}

	/*
	public int totalLength(int memberId,String status){
		return totalLength("PUB_MEMBER_COUPON.SYS_MEMBER_ID",memberId,status) ; 
	}
	public int totalLength(String promotionCode,String status){
		return totalLength("DRP_PROMOTION_THEME.PROMOTION_CODE",promotionCode,status) ; 
	}
	*/
	public int totalLength(String coupon_no,int memberId,String promotionCode,String status){
		String where = "";
		if(coupon_no != null && !coupon_no.isEmpty()){
			where += " DRP_PROMOTION_COUPON.COUPON_NO = '" + coupon_no + "' and";
		}
		if(memberId != 0){
			where += " PUB_MEMBER_COUPON.SYS_MEMBER_ID = " + memberId + " and";
		}
		if(promotionCode != null && !promotionCode.isEmpty()){
			where += " DRP_PROMOTION_THEME.PROMOTION_CODE = '" + promotionCode + "' and";
		}
		String sql = "SELECT count(*)"
			+ " FROM PUB_MEMBER_COUPON "
				+ " left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)"
				+ " left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)"
				+ " WHERE "+where+" DRP_PROMOTION_COUPON.COUPON_STATUS=?" ;
		return jdbcTpl.queryForInt(sql,new Object[]{status} ) ;
	}

	/*
	public List<Map<String,Object>> queryCouponList(int memberId,String status,int pageNum,int perPage){
		return queryCouponList("PUB_MEMBER_COUPON.SYS_MEMBER_ID",memberId,status,pageNum,perPage) ;
	}

	public List<Map<String,Object>> queryCouponList(String promotionCode,String status,int pageNum,int perPage){
		return queryCouponList("DRP_PROMOTION_THEME.PROMOTION_CODE",promotionCode,status,pageNum,perPage) ;
	}
	*/
	public List<Map<String,Object>> queryCouponList(String coupon_no,int memberId,String promotionCode,String status,int pageNum,int perPage){
		
		String where = "";
		if(coupon_no != null && !coupon_no.isEmpty()){
			where += " DRP_PROMOTION_COUPON.COUPON_NO = '" + coupon_no + "' and";
		}
		if(memberId != 0){
			where += " PUB_MEMBER_COUPON.SYS_MEMBER_ID = " + memberId + " and";
		}
		if(promotionCode != null && !promotionCode.isEmpty()){
			where += " DRP_PROMOTION_THEME.PROMOTION_CODE = '" + promotionCode + "' and";
		}
		return queryCouponList(where,status,pageNum,perPage) ;
	}
	
	private List<Map<String,Object>> queryCouponList(String where,String status,int pageNum,int perPage){

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
				+ ", DRP_PROMOTION_COUPON.MEMO"
				+ ", PUB_MEMBER_COUPON.SYS_MEMBER_ID"
				+ ", PUB_BASE_CODE.BASE_CODE_NAME"
				+ ", row_number() OVER(ORDER BY null) AS \"row_number\""
			+ " FROM PUB_MEMBER_COUPON "
				+ " left join DRP_PROMOTION_COUPON on (PUB_MEMBER_COUPON.SYS_PCOUPON_ID=DRP_PROMOTION_COUPON.SYS_PCOUPON_ID)"
				+ " left join DRP_PROMOTION_THEME on (DRP_PROMOTION_COUPON.SYS_PTHEME_ID=DRP_PROMOTION_THEME.SYS_PTHEME_ID)"
				+ " left join PUB_BASE_CODE on (DRP_PROMOTION_COUPON.COUPON_TYPE=PUB_BASE_CODE.BASE_CODE_ID)"
			+ " WHERE "+where+" DRP_PROMOTION_COUPON.COUPON_STATUS=? and PUB_BASE_CODE.BASE_CODE_TYPE='326'"
			+ " ORDER by DRP_PROMOTION_COUPON.CREAT_DATE desc" ;
		
		sql = " select *"
				+ "	from (select *"
				+ "  	from ("+sql+") p"
				+ "     where p.\"row_number\">?)"
				+ " where rownum<=?" ;

		System.out.println(sql);
		return jdbcTpl.queryForList( sql,new Object[]{ status, (pageNum-1)*perPage, perPage } ) ;
	}

	private JdbcTemplate jdbcTpl ;
}
