package com.welab.lavico.middleware.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import com.welab.lavico.middleware.DB;


@Controller
public class PromotionsController {

	@RequestMapping(method=RequestMethod.GET, value="{brand}/Promotions")
    public @ResponseBody Map<String,Object> getPromotions(@PathVariable String brand) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		JdbcTemplate jdbcTpl ;
		
		try{
			jdbcTpl = DB.getJdbcTemplate(brand) ;
		} catch(NoSuchBeanDefinitionException e) {
			rspn.put("error", "The paramter brand is invalid.") ;
			rspn.put("count", 0) ;
			return rspn ;
		}

		// 查询有效的活动
		List<Map<String, Object>> promotions = jdbcTpl.queryForList("SELECT PROMOTION_CODE, PROMOTION_NAME, PROMOTION_DESC FROM DRP_PROMOTION_THEME WHERE PROMOTION_CLASS='02' AND ACTIVE = '1'") ;
		
		Iterator<Map<String, Object>> iter = promotions.iterator() ;
    	while(iter.hasNext()){
    		Map promo = (Map) iter.next();
    		
    		// 活动所有优惠券
			int total = jdbcTpl.queryForInt(
				"select count(*) from drp_promotion_coupon a left join drp_promotion_theme b on a.sys_ptheme_id = b.sys_ptheme_id where b.promotion_code = ?"
				, new Object[] { (String)promo.get("PROMOTION_CODE") }
			) ;
			promo.put("total",total) ;
    		
			// 已发优惠券
			int used = jdbcTpl.queryForInt(
				"select count(*) from drp_promotion_coupon a left join drp_promotion_theme b on a.sys_ptheme_id = b.sys_ptheme_id where a.bind_flag = '1' and b.promotion_code = ?"
				, new Object[] { (String)promo.get("PROMOTION_CODE") }
			) ;
			promo.put("used",used) ;
    	}
		
		rspn.put("promotions", promotions) ;
		rspn.put("length", promotions.size()) ;
    	return rspn ;
    }
}


