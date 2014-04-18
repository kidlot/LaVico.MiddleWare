package com.welab.lavico.middleware.service;

import org.springframework.jdbc.core.JdbcTemplate;

public class PointService {

	public PointService(String brand,int memberId){
		
		this.brand = brand ;
		this.memberId = memberId ;
	}
	
	public void change(int qty,String memo){

		JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;

		if( jdbcTpl.queryForInt(
				"select count(*) from PUB_MEMBER_ID where SYS_MEMBER_ID=?"
				,new Object[]{memberId})<1
			){
			throw new Error("memberId 无效") ;
		}
		
		java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
		System.out.println(memo) ;
		jdbcTpl.update(
				"insert into PUB_MEMBER_POINT ("
				+ "SYS_MEMBER_POT_ID"
				+ ", SYS_MEMBER_ID"
				+ ", IO_FLAG"
				+ ", ACTIVE"
				+ ", POT_DATE"
				+ ", POT_QTY"
				+ ", MEMO"
				+ ") values (SYS_DOC_ID.NEXTVAL,?,?,?,?,?,?)"
				, new Object[]{
						memberId
						, qty<0?"2":"1"
						, "1"
						, now
						, Math.abs(qty)
						, memo==null? "": memo
				}) ;
	}

	private String brand;
	private int memberId;
}
