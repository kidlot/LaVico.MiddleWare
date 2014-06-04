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
	
	public List<Map<String,Object>> queryPage(int pageNum,String code){
		return queryPage(pageNum,20,code) ;
	}
	public List<Map<String,Object>> queryPage(int pageNum,int perPage,String code){
		
		String codeWhere = "" ;
		Object[] args ;
		if(code!=null){
			codeWhere = " and PROMOTION_CODE=?" ;
			args = new Object[]{ code, (pageNum-1)*perPage, perPage } ;
		}
		else{
			codeWhere = "" ;
			args = new Object[]{ (pageNum-1)*perPage, perPage } ;
		}

		// 查询有效的活动
		String sql = " select *"
				+ "	from (select *"
				+ "  	from (select PROMOTION_CODE, PROMOTION_NAME, PROMOTION_DESC, row_number() OVER(ORDER BY null) AS \"row_number\""
				+ "			from DRP_PROMOTION_THEME"
				+ "			where (PROMOTION_CLASS = '02' OR PROMOTION_CLASS = '03') AND ACTIVE = '1'"+codeWhere+") p"
				+ "     where p.\"row_number\">?)"
				+ " where rownum<=?" ;
    	List<Map<String,Object>> promotions = jdbcTpl.queryForList( sql, args ) ;

		Iterator<Map<String, Object>> iter = promotions.iterator() ;
    	while(iter.hasNext()){
    		Map<String, Object> promo = iter.next();

    		// 活动所有优惠券
    		int total = jdbcTpl.queryForInt(
    				"select count(*) as count from drp_promotion_coupon c left join drp_promotion_theme p on c.sys_ptheme_id=p.sys_ptheme_id where p.promotion_code=?"
    				, new Object[] { (String)promo.get("PROMOTION_CODE") }
    		) ;
    		int used = jdbcTpl.queryForInt(
    				"select count(*) as count from drp_promotion_coupon c left join drp_promotion_theme p on c.sys_ptheme_id=p.sys_ptheme_id where c.bind_flag = '1' and p.promotion_code=?"
    				, new Object[] { (String)promo.get("PROMOTION_CODE") }
    		) ;
    		try{
	    		Map<String,Object>rcd = jdbcTpl.queryForMap(
	    				"select COUPON_TYPE as type, COUPON_CLASS as cls, COUPON_QTY as qty from drp_promotion_coupon c left join drp_promotion_theme p on c.sys_ptheme_id=p.sys_ptheme_id"
	    				+ "	where p.promotion_code=? and rownum<=1"
	    				, new Object[] { (String)promo.get("PROMOTION_CODE") }
	    		) ;
				promo.put("TYPE",rcd.get("TYPE")) ;
				promo.put("CLS",rcd.get("CLASS")) ;
				promo.put("QTY",rcd.get("QTY")) ;
    		}catch(org.springframework.dao.EmptyResultDataAccessException e){
    			promo.put("TYPE",null) ;
    			promo.put("CLS",null) ;
    		}
			promo.put("TOTAL",total) ;
			promo.put("USED",used) ;
    	}
    	
    	return promotions ;
	}
	
	
	private JdbcTemplate jdbcTpl ;
}
