package com.welab.lavico.middleware.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.welab.lavico.middleware.DB;

@Controller
public class PointController {

	@RequestMapping(method=RequestMethod.GET, value="{brand}/Points")
    public @ResponseBody Map<String,Object> getPoints(@PathVariable String brand,HttpServletRequest request) {

		Map<String, Object> rspn = new HashMap<String, Object>();
		rspn.put("remaining", 0) ;
		rspn.put("level", "") ;

		if(request.getParameter("MEMBER_ID")==null){
			rspn.put("error", "missing arg MEMBER_ID") ;
		    return rspn ;
		}
		
		JdbcTemplate jdbcTpl ;
		
		try{
			jdbcTpl = DB.getJdbcTemplate(brand) ;
		} catch(NoSuchBeanDefinitionException e) {
			rspn.put("error", "The paramter brand is invalid.") ;
			return rspn ;
		}

		Map<String,Object> member ;
    	String level = "" ;
    	try {
    		member = jdbcTpl.queryForMap(
	    			"select TOTAL_CUR_POT, SYS_MEMBER_CARD_ID from PUB_MEMBER_ID where SYS_MEMBER_ID=?"
	    			, new Object[]{request.getParameter("MEMBER_ID")}
		    	) ;
        	rspn.put( "remaining", ((java.math.BigDecimal)member.get("TOTAL_CUR_POT")).intValue() ) ;
        	
	    	level = (String)jdbcTpl.queryForObject(
	    			"select MEM_CARD_TYPE from PUB_MEMBER_CARD where SYS_MEMBER_CARD_ID=?"
	    			, new Object[]{member.get("SYS_MEMBER_CARD_ID")}
	    			, java.lang.String.class
		    	) ;

    	} catch (IncorrectResultSizeDataAccessException e) {
    		if(e.getActualSize()==0){
    			rspn.put("error","指定的会员不存在") ;
			    return rspn ;
    		}
		    throw e ;
		}

    	List<Map<String,Object>> rows = jdbcTpl.queryForList("select "
    			+ "IO_FLAG, POT_DATE, MEMO, POT_QTY"
    			+ " from PUB_MEMBER_POINT"
    			+ " where SYS_MEMBER_ID=?",new Object[]{request.getParameter("MEMBER_ID")}) ;
    	Iterator<Map<String,Object>> iter = rows.iterator() ;

    	while(iter.hasNext()){

    		Map<String,Object> userMap = (Map<String,Object>) iter.next();
    		
    		int point = (((String)userMap.get("IO_FLAG")).equals("1")? +1 : -1) * ((java.math.BigDecimal)userMap.get("POT_QTY")).intValue() ;
    		userMap.put("value", point) ;
    		userMap.put("time", ((java.sql.Timestamp) userMap.get("POT_DATE")).toString()) ;
    		userMap.put("memo", (String) userMap.get("MEMO")) ;

    		userMap.remove("IO_FLAG") ;
    		userMap.remove("POT_DATE") ;
    		userMap.remove("MEMO") ;
    		userMap.remove("POT_QTY") ;
    	}

    	rspn.put("level",level) ;
    	rspn.put("log",rows) ;

		return rspn ;
	}
}
