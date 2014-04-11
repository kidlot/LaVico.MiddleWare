package com.welab.lavico.middleware.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class PromotionListModel {

	public PromotionListModel(JdbcTemplate jdbcTpl) {
		this.jdbcTpl = jdbcTpl ;
	}

	public int totalLength(){
		return jdbcTpl.queryForInt( "select count(*) from DRP_PROMOTION_THEME where PROMOTION_CLASS='02' AND ACTIVE = '1'" ) ;
	}
	
	public List<Map<String,Object>> queryPage(int pageNum){
		return queryPage(pageNum,20) ;
	}
	public List<Map<String,Object>> queryPage(int pageNum,int perPage){

		// 查询有效的活动
		String sql = " select *"
				+ "	from (select *"
				+ "  	from (select PROMOTION_CODE, PROMOTION_NAME, PROMOTION_DESC, row_number() OVER(ORDER BY null) AS \"row_number\""
				+ "			from DRP_PROMOTION_THEME"
				+ "			where PROMOTION_CLASS='02' AND ACTIVE = '1') p"
				+ "     where p.\"row_number\">?)"
				+ " where rownum<=?" ;
    	List<Map<String,Object>> promotions = jdbcTpl.queryForList(
    			sql, new Object[]{ (pageNum-1)*perPage, perPage }
			) ;

		Iterator<Map<String, Object>> iter = promotions.iterator() ;
    	while(iter.hasNext()){
    		Map<String, Object> promo = iter.next();
    		
    		// 活动所有优惠券
    		sql = "select COUPON_TYPE as type, COUPON_CLASS as cls, COUPON_QTY as qty, count(*) as count from drp_promotion_coupon c left join drp_promotion_theme p on c.sys_ptheme_id=p.sys_ptheme_id where p.promotion_code=? group by COUPON_TYPE, COUPON_CLASS, COUPON_QTY" ;
			List<Map<String,Object>> qtylist = jdbcTpl.queryForList(
				sql, new Object[] { (String)promo.get("PROMOTION_CODE") }
			) ;

			Iterator<Map<String, Object>> qtyiter = qtylist.iterator() ;
			while(qtyiter.hasNext()){
	    		Map<String, Object> qty = qtyiter.next();
	    		
	    		
	    		// 已发优惠券
	    		sql = "select count(*) from drp_promotion_coupon a left join drp_promotion_theme b on a.sys_ptheme_id = b.sys_ptheme_id where a.bind_flag = '1' and a.COUPON_QTY=? and b.promotion_code = ?" ;
				int used = jdbcTpl.queryForInt(
					sql, new Object[] { qty.get("qty"), (String)promo.get("PROMOTION_CODE") }
				) ;
				qty.put("USED",used) ;
			}
			
			promo.put("coupons",qtylist) ;
    	}
    	
    	return promotions ;
	}
	
	
	private JdbcTemplate jdbcTpl ;
}
