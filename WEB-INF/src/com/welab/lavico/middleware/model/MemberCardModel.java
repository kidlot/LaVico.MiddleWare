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
	
	/**
	 * 检查手机号码是否存在
	 * 
	 * @param mobile
	 * @return true表示该手机号码已经存在
	 */
	public boolean isMobileExists(String brand,String mobile){
		String sql = 
			"SELECT COUNT(*) NUM "
				+ "	FROM PUB_MEMBER_ID I,PUB_MEMBER_PSN P,PUB_MEMBER_CARD C "
				+ " WHERE I.SYS_MEMBER_PSN_ID=P.SYS_MEMBER_PSN_ID"
				+ " 	AND I.SYS_MEMBER_CARD_ID=C.SYS_MEMBER_CARD_ID"
				+ " 	AND I.BRAND_CODE=?"
				+ " 	AND P.MOBILE_TELEPHONE_NO=?" ;
		return jdbcTpl.queryForInt(sql,new Object[]{brand,mobile})>0 ;
	}
	
	/**
	 * 检查电话是否验证过
	 * 
	 * @param mobile
	 * @return true 表示该手机号码已经验证过
	 */
	public boolean isMobileChecked(String brand,String mobile){
		String sql = 
			"SELECT I.IS_CHECKED"
			+ " FROM PUB_MEMBER_ID I,PUB_MEMBER_PSN P,PUB_MEMBER_CARD C"
			+ " WHERE I.SYS_MEMBER_PSN_ID = P.SYS_MEMBER_PSN_ID"
			+ "		AND I.SYS_MEMBER_CARD_ID = C.SYS_MEMBER_CARD_ID"
			+ "		AND I.BRAND_CODE=?"
			+ "		AND P.MOBILE_TELEPHONE_NO=?" ;
		try{
			String isChecked = (String)jdbcTpl.queryForObject(sql,String.class,new Object[]{brand,mobile}) ;
			System.out.println("yyyyy:"+isChecked+":"+isChecked.length()) ;
			return isChecked.equals("1") ;
		}catch(Throwable e){
			e.printStackTrace();
			return false ;
		}
	}

	
	/**
	 * 检查老卡和手机号码是否有效
	 * 
	 * @param mobile
	 * @param oldcard
	 * @return true 表示该手机号码已经验证过
	 */
	public boolean isMobileAndOldcardValid(String brand,String mobile,String oldcard){
		String sql = 
			"SELECT I.SYS_MEMBER_ID AS V_PUB_MEMBER_ID"
			+ "	FROM PUB_MEMBER_ID I,PUB_MEMBER_PSN P,PUB_MEMBER_CARD C"
			+ "	WHERE I.SYS_MEMBER_PSN_ID = P.SYS_MEMBER_PSN_ID"
			+ "		AND I.SYS_MEMBER_CARD_ID = C.SYS_MEMBER_CARD_ID"
			+ "		AND I.BRAND_CODE=?"
			+ "		AND I.SYS_MEMBER_MIC_ID IS NULL"
			+ "		AND P.MOBILE_TELEPHONE_NO=?"
			+ "		AND C.MEM_OLDCARD_NO=?" ;
		try{
			String memberid = (String)jdbcTpl.queryForObject(sql,String.class,new Object[]{brand,mobile,oldcard}) ;
			return memberid!=null ;
		}catch(Throwable e){
			e.printStackTrace();
			return false ;
		}
	}
	
	private JdbcTemplate jdbcTpl ;
	private int memberId ;
}
