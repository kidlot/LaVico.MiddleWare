package com.welab.lavico.middleware.service;

import org.springframework.jdbc.core.JdbcTemplate;

public class TagAndCollectionService {
	
	public TagAndCollectionService(String brand){
		this.brand = brand ;
	}
	

	public boolean addCollection(int memberId,String goodsCode) {
		return addCollection(memberId,goodsCode,brand+"999") ;
	}
	public boolean addCollection(int memberId,String goodsCode,String user) {
		return add(memberId,goodsCode,user,"COLLECTION","GOODS_STYLE_CODE") ;
	}
	public boolean removeCollection(int memberId,String goodsCode) {
		return remove(memberId,goodsCode,"COLLECTION","GOODS_STYLE_CODE") ;
	}

	public boolean addTag(int memberId,String tagName) {
		return addTag(memberId,tagName,brand+"999") ;
	}
	public boolean addTag(int memberId,String tagName,String user) {
		return add(memberId,tagName,user,"TAGS","TAG") ;
	}
	public boolean removeTag(int memberId,String tagName) {
		return remove(memberId,tagName,"TAGS","TAG") ;
	}

	private boolean add(int memberId,String content,String user,String table,String column){

		JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
		
		if( jdbcTpl.queryForInt(
				"select count(*) from PUB_MEMBER_"+table+" where SYS_MEMBER_ID=? and "+column+"=?"
				,new Object[]{memberId,content})>0
			){
			throw new Error("无法保存重复的内容") ;
		}

		java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

		return jdbcTpl.update(
				"insert into PUB_MEMBER_"+table+" ( "
				+ "SYS_MEMBER_"+table+"_ID"
				+ ",SYS_MEMBER_ID"
				+ ","+column+""
				+ ",ACTIVE"
				+ ",INPUT_USER"
				+ ",INPUT_DATE"
				+ ",LAST_UPDATE_USER"
				+ ",LAST_UPDATE_DATE"
				+ ") values (SYS_DOC_ID.NEXTVAL,?,?,?,?,?,?,?)"
				, new Object[]{memberId,content,"1",user,now,user,now}
		) > 0 ;

	}
	
	private boolean remove(int memberId,String content,String table,String column){
		JdbcTemplate jdbcTpl = SpringJdbcDaoSupport.getJdbcTemplate(brand) ;
		return jdbcTpl.update(
				"delete from PUB_MEMBER_"+table+" where SYS_MEMBER_ID=? and "+column+"=?"
				, new Object[]{memberId,content}
		) > 0 ;
	}
	
	private String brand;
}
