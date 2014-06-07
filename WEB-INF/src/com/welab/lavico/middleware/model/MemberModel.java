package com.welab.lavico.middleware.model;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.welab.lavico.middleware.service.DocumentNoService;

public class MemberModel {

	public MemberModel(JdbcTemplate jdbcTpl,int memberId) {
		this.jdbcTpl = jdbcTpl ;
		this.memberId = memberId ;
	}

	/**
	 * 保存会员卡资料
	 * 
	 * HTTP Get Query Variables:
	 * @param email 			电子邮箱
	 * @param industry 			行业
	 * @param province 			省份
	 * @param city 				城市
	 * @param addr 				地址
	 * @param hoppy 			喜好款式
	 * @param color 			喜好颜色
	 * 
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int save(
			String brand
			, String email
			, String industry
			, String province
			, String city
			, String addr
			, String hoppy
			, String color) throws Exception{
		
		int psnId = jdbcTpl.queryForInt(
				"select SYS_MEMBER_PSN_ID from PUB_MEMBER_ID where SYS_MEMBER_ID=?"
				, new Object[]{ memberId }) ;
	
		System.out.println(memberId+">"+psnId) ;
		

		String seqid = (new DocumentNoService()).getDocumentNo(303050,null,brand+"999",jdbcTpl,brand+"999") ;
		
		
    	int aff = jdbcTpl.update(
    			"insert into PUB_MEMBER_APPLY (MEM_PSN_EMAIL"
    			+ " 	, INPUT_DATE"
    			+ " 	, MEM_APP_DATE"
    			+ " 	, PROCESS_STEP"
    			+ " 	, SOURCE_TYPE"
    			+ " 	, WAREHOUSE_CODE"
    			+ " 	, BRAND_CODE"
    			+ " 	, APP_TYPE"
    			+ " 	, MEM_INDUSTRY"
    			+ " 	, PROVINCE"
    			+ " 	, CITY"
    			+ " 	, MEM_PSN_ADDRESS"
    			+ " 	, MEM_PSN_HOPPY"
    			+ " 	, MEM_PSN_COLOR"
    			+ "		, SYS_MEMBER_PSN_ID"
    			+ "		, SYS_MEMBER_APPLY_ID"
    			+ "		, SYS_MEMBER_ID"
    			+ "		, INPUT_USER"
    			+ "		, MEM_APP_NO"
    			+ "		, LAST_UPDATE_DATE"
    			+ "		, LAST_UPDATE_USER"
    			+ ") values (?,SYSDATE,SYSDATE,0,'03','L999','L',1,?,?,?,?,?,?,?,SYS_DOC_ID.NEXTVAL,?,'L999',?,SYSDATE,'L999') "
    			, new Object[]{ email,industry,province,city,addr,hoppy,color,psnId,memberId,seqid}
    		) ;
    	System.out.println(aff);
    	return aff ;
	}
	
	public Map<String,Object> query(){
		return jdbcTpl.queryForMap(
    			"SELECT MEM_PSN_EMAIL"
    			+ " 	, MEM_INDUSTRY"
    			+ " 	, MEM_PSN_BIRTHDAY"
    			+ " 	, PROVINCE"
    			+ " 	, CITY"
    			+ " 	, MEM_PSN_ADDRESS"
    			+ " 	, MEM_PSN_HOPPY"
    			+ " 	, MEM_PSN_COLOR"
    			+ " 	, MEM_CARD_NO"
    			+ " 	, MEM_PSN_CNAME"
    			+ " 	, MEM_PSN_ENAME"
    			+ " FROM"
    			+ "		PUB_MEMBER_ID "
    			+ " left join PUB_MEMBER_PSN on (PUB_MEMBER_PSN.SYS_MEMBER_PSN_ID=PUB_MEMBER_ID.SYS_MEMBER_PSN_ID)"
    			+ " left join PUB_MEMBER_CARD on (PUB_MEMBER_CARD.SYS_MEMBER_CARD_ID=PUB_MEMBER_ID.SYS_MEMBER_CARD_ID)"
    			+ " WHERE"
    			+ "		PUB_MEMBER_ID.SYS_MEMBER_ID=?"
    			, new Object[]{memberId}
	    	) ;
	}
	
	private JdbcTemplate jdbcTpl ;
	private int memberId ;
}
