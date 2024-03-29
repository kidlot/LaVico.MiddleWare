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
		
		java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
		
		jdbcTpl.update(
				"insert into PUB_MEMBER_POINT ("
				+ "SYS_MEMBER_POT_ID"
				+ ", SYS_MEMBER_ID"
				+ ", IO_FLAG"
				+ ", ACTIVE"
				+ ", POT_DATE"
				+ ", POT_QTY"
				+ ", MEMO"
				+ ", INPUT_DATE"
				+ ", INPUT_USER"
				+ ", SOURCE_TYPE"
				+ ") values (SYS_DOC_ID.NEXTVAL,?,?,?,?,?,?,?,?,?)"
				, new Object[]{
						memberId
						, qty<0?"2":"1"
						, "1"
						, now
						, Math.abs(qty)
						, memo==null? "": memo
						, now
						, brand + "999"
						, "02"
				}) ;
	}

	private String brand;
	private int memberId;
}
