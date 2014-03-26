package com.welab.lavico.middleware.controller;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RequestMethod;  
import org.springframework.web.bind.annotation.ResponseBody;
import com.welab.lavico.middleware.DB;
import org.springframework.jdbc.core.JdbcTemplate;

@Controller
public class Promotions {

	@RequestMapping(method=RequestMethod.GET, value="/promotions/{id}", headers={"Accept=text/xml, application/json"} )
    public @ResponseBody List<Map<String, Object>> getPromotions() {
    	
    	JdbcTemplate jdbcTpt = DB.getJdbcTemplate() ;
    	List<Map<String, Object>> rows = jdbcTpt.queryForList("select PROMOTION_CODE, PROMOTION_NAME, PROMOTION_DESC from DRP_PROMOTION_THEME") ;

    	return rows ;
    }
}


