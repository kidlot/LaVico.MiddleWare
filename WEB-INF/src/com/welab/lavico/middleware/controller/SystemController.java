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

import cn.emay.sdk.client.api.Client;

import com.welab.lavico.middleware.service.SmsClient;
import com.welab.lavico.middleware.service.SpringJdbcDaoSupport;

@Controller
public class SystemController {

	/**
	 * 取回最后修改记录
	 * 
	 * Path Variables:
	 * @param {brand} 					品牌名称
	 * 
	 * HTTP Get Query Variables:
	 * @param lastid 					上一次录取到的最后id
	 * 
	 */
	@RequestMapping(method=RequestMethod.GET, value="{brand}/System/FieldChange")
    public @ResponseBody Map<String,Object> fetchFieldChange(@PathVariable String brand,HttpServletRequest request) {
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
	

	@RequestMapping(method=RequestMethod.GET, value="{brand}/System/SendSMS")
    public @ResponseBody Map<String,Object> sendSms(@PathVariable String brand,HttpServletRequest request) {
		Map<String, Object> rspn = new HashMap<String, Object>();

		try{
			String mobile = request.getParameter("mobile") ;
			if(mobile==null||mobile.isEmpty()){
				throw new Error("miss arg mobile") ;
			}
			String[] mobiles = mobile.split(",") ;
			String content = request.getParameter("content") ;
			if(content==null||content.isEmpty()){
				throw new Error("miss arg content") ;
			}

			int res = SmsClient.getClient().sendSMS(mobiles,content,5) ;
			
			// 记录 log
			for(int i=0;i<mobiles.length;i++) {
				JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
				/*jdbcTpl.query(
					"insert into drp_message_log ("
						+ "SYS_MESSAGE_ID"
						+ ", MS_TYPE"
						+ ", MS_SOURCE_TYPE"
						+ ", MS_RECEIVE_USER"
						+ ", INPUT_USER"
						+ ", INPUT_DATE"
					+ ") values ("
						+ "SYS_DOC_ID.NEXTVAL"
						+ ", '03'"
						+ ", '05'"
						+ ", ?"
						+ ", ?"
						+ ", ?"
					, new Object[]{
						mobiles[i], brand+"999", new java.sql.Date(System.currentTimeMillis())
					}
				) ;*/
				
			}

			if(res==0){
				rspn.put("success",true) ;
			} else {
				rspn.put("error","错误代码："+res) ;
				rspn.put("success",false) ;
			}
			
		} catch(Throwable e) {
			e.printStackTrace();
			rspn.put("error",e.getMessage()) ;
			rspn.put("success",false) ;
		}

		
		return rspn ;
	}
}
