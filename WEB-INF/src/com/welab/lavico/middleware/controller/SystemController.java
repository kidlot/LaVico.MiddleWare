package com.welab.lavico.middleware.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;

@Controller
public class SystemController {

	/**
	 * 获取优惠券
	 * 
	 * Path Variables:
	 * @param {brand} 					品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param lastid 					上一次录取到的最后id
	 * 
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/System/FieldChange")
    public @ResponseBody Map<String,Object> fetchCoupon(@PathVariable String brand,HttpServletRequest request) {
		Map<String, Object> rspn = new HashMap<String, Object>();
		
		try{
			
			String sLastId = request.getParameter("lastid") ;
			if(sLastId==null||sLastId.isEmpty()){
				sLastId = "0" ;
			}
			int lastId = Integer.parseInt(sLastId) ;

	    	JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
	    	
	    	List<Map<String,Object>>lst = jdbcTpl.queryForList(
    			"select * from PUB_FIELD_CHANGEHIS where PK_PUB_FIELD_CHANGEHIS>? and rownum<=100"
    			, new Object[] { lastId }
    		) ;
	    	
	    	rspn.put("list", lst) ;
		} catch(Throwable e) {
			rspn.put("error",e.getMessage()) ;
		}
		
		return rspn ;
	}
}
