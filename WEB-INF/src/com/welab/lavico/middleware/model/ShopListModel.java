package com.welab.lavico.middleware.model;

import java.util.Iterator;
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
	public int totalLength(String city){

		String whereCity ;
		Object[] args ;
		if(city!=null){
			
			whereCity = " where CUSTOMER_CITY=? or CUSTOMER_CITY=?" ;
			args = new Object[]{ city, city+"市" } ;
		}
		else{
			whereCity = "" ;
			args = new Object[]{} ;
		}
		
		return jdbcTpl.queryForInt( "select count(*) from PUB_CUSTOMER_TRANSIT"+whereCity, args ) ;
	}

	public List<Map<String,Object>> queryPage(int pageNum,String city){
		return queryPage(pageNum,20,city) ;
	}
	
	public List<Map<String,Object>> queryPage(int pageNum,int perPage,String city){

		String whereCity ;
		whereCity = "where regexp_like(CUSTOMER_CODE,'^.L') and ACTIVE=1" ;
		Object[] args ;
		if(city!=null){
			whereCity += " and CUSTOMER_CITY=? or CUSTOMER_CITY=?" ;
			args = new Object[]{ city, city+"市", (pageNum-1)*perPage, perPage } ;
		}
		else{
			whereCity += "" ;
			args = new Object[]{ (pageNum-1)*perPage, perPage } ;
		}

		String sql = " select *"
				+ "	from (select *"
				+ "  	from (select "
				+ "				CUSTOMER_CODE as code"
				+ "				, CUSTOMER_NAME as name"
				+ "				, CUSTOMER_ADDRESS as addr"
				+ "				, CUSTOMER_TELEPHONE as tel"
				+ "				, CUSTOMER_CITY as city"
				+ "				, CUSTOMER_PROVINCE as  province"
				+ "				, CUSTOMER_LONGITUDE as log"
				+ "				, CUSTOMER_LATITUDE as lat"
				+ "				, CUSTOMER_PIC_URL as picurl"
				+ "				, CUSTOMER_ACTIVITY as act"
				+ "				, row_number() OVER(ORDER BY null"
				+ "			) AS \"row_number\""
				+ "			from PUB_CUSTOMER_TRANSIT"
				+ "			" + whereCity
				+ "		) p"
				+ "     where p.\"row_number\">?)"
				+ " where rownum<=?" ;
		
		System.out.println(sql) ;
		List<Map<String,Object>> list = jdbcTpl.queryForList( sql, args ) ;
		
		Iterator<Map<String,Object>> iter = list.iterator() ;
		while(iter.hasNext()){
			Map<String,Object> row = iter.next() ;

			// 删末尾的省字
			String province = (String)row.get("PROVINCE") ;
			if( province!=null ){
				if( province.charAt(province.length()-1) == '省' ){
					province = province.substring(0,province.length()-1) ;
					row.put("PROVINCE", province) ;
				}
			}

			// 删末尾的市字
			city = (String)row.get("CITY") ;
			if( city!=null ){
				if( city.charAt(city.length()-1) == '市' ){
					city = city.substring(0,city.length()-1) ;
					row.put("CITY", city) ;
				}
			}
		}
		
		return list ;
	}

	private JdbcTemplate jdbcTpl ;
}
