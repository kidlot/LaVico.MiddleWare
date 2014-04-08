package com.welab.lavico.middleware.model;

import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemberCardModel {

	public MemberCardModel(JdbcTemplate jdbcTpl,int memberId) {
		this.jdbcTpl = jdbcTpl ;
		this.memberId = memberId ;
	}
	
	/**
	 * 查询会员卡的等级
	 * @return String
	 */
	public String queryLevel(){
		try{
			
			Map<String,Object> member = jdbcTpl.queryForMap(
	    			"select TOTAL_CUR_POT, SYS_MEMBER_CARD_ID from PUB_MEMBER_ID where SYS_MEMBER_ID=?"
	    			, new Object[]{memberId}
		    	) ;
    		
	    	return (String)jdbcTpl.queryForObject(
				"select MEM_CARD_TYPE from PUB_MEMBER_CARD where SYS_MEMBER_CARD_ID=?"
				, new Object[]{member.get("SYS_MEMBER_CARD_ID")}
				, java.lang.String.class
	    	) ;
		} catch(EmptyResultDataAccessException e){
			return "" ;
		}
	}
	
	
	private JdbcTemplate jdbcTpl ;
	private int memberId ;
}
