package com.welab.lavico.middleware.model;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;

public class ShopListModel {

	public ShopListModel(JdbcTemplate jdbcTpl) {
		this.jdbcTpl = jdbcTpl ;
	}

	/**
	 * 所有门店总数
	 * @return
	 */
	public int totalLength(){
		return jdbcTpl.queryForInt( "select count(*) from PUB_CUSTOMER_TRANSIT" ) ;
	}

	public List<Map<String,Object>> queryPage(int pageNum){
		return queryPage(pageNum,20) ;
	}
	
	public List<Map<String,Object>> queryPage(int pageNum,int perPage){

		String sql = " select *"
				+ "	from (select *"
				+ "  	from (select CUSTOMER_CODE as code, CUSTOMER_NAME as name, CUSTOMER_ADDRESS as addr, CUSTOMER_TELEPHONE as tel, CUSTOMER_CITY as city, CUSTOMER_PROVINCE as  province, row_number() OVER(ORDER BY null) AS \"row_number\""
				+ "			from PUB_CUSTOMER_TRANSIT) p"
				+ "     where p.\"row_number\">?)"
				+ " where rownum<=?" ;
    	return jdbcTpl.queryForList(
			sql, new Object[]{ (pageNum-1)*perPage, perPage }
		) ;
	}

	private JdbcTemplate jdbcTpl ;
}
