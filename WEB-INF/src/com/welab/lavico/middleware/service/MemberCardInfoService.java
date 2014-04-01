package com.welab.lavico.middleware.service;

import java.util.Map;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemberCardInfoService {

	/**
	 * 取回指定会员的当前剩余积分
	 * 
	 * @param brand			品牌代号
	 * @param memberId		会员 MEMBER_ID
	 * @return	int			如果返回 -1 表示 memberId 不存在
	 * @throws DaoBrandError
	 */
	public int getCurrentPoint(String brand, int memberId)
			throws DaoBrandError{
		
		JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;

		Map<String,Object> member = null ;
    	try {
    		member = jdbcTpl.queryForMap(
    			"select TOTAL_CUR_POT from PUB_MEMBER_ID where SYS_MEMBER_ID=?"
    			, new Object[]{memberId}
		    ) ;

    		return ((java.math.BigDecimal) member.get("TOTAL_CUR_POT")).intValue() ;
    		
    	} catch (IncorrectResultSizeDataAccessException e) {
    		return -1 ;
    	}
	}
	
	
}
