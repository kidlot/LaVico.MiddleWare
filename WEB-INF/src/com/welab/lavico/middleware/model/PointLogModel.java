package com.welab.lavico.middleware.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;

public class PointLogModel {
	
	
	public PointLogModel(JdbcTemplate jdbcTpl,int memberId) {
		this.jdbcTpl = jdbcTpl ;
		this.memberId = memberId ;
	}

	public int totalLength(){
		return jdbcTpl.queryForInt(
    			"select count(*) from PUB_MEMBER_POINT where SYS_MEMBER_ID=? AND POT_QTY is not null"
    			, new Object[]{memberId}
    	) ;
	}

	public List<Map<String,Object>> queryPage(int pageNum){
		return queryPage(pageNum,20,null) ;
	}

	public List<Map<String,Object>> queryPage(int pageNum,int perPage,String month){
		
		String codeWhere = "";
		if(month != null){
			codeWhere = " AND to_char(POT_DATE,'yyyymm')='"+month+"'";
		}
		
		String sql = " select *"
			+ "	from (select *"
			+ "  	from (select IO_FLAG, POT_DATE, MEMO, POT_QTY, SOURCE_TYPE, row_number() OVER(ORDER BY null) AS \"row_number\""
			+ "				from PUB_MEMBER_POINT"
			+ "				where SYS_MEMBER_ID=? AND POT_QTY is not null"
			+ "				order by SYS_MEMBER_POT_ID desc) p"
			+ "         where p.\"row_number\">?"+codeWhere+")"
			+ " where rownum<=?" ;
		
    	List<Map<String,Object>> rows = jdbcTpl.queryForList( sql
    				, new Object[]{ memberId, (pageNum-1)*perPage, perPage }
    			) ;
    	Iterator<Map<String,Object>> iter = rows.iterator() ;

    	while(iter.hasNext()){

    		Map<String,Object> userMap = (Map<String,Object>) iter.next();
    		
    		int point = (((String)userMap.get("IO_FLAG")).equals("1")? +1 : -1) * ((java.math.BigDecimal)userMap.get("POT_QTY")).intValue() ;
    		userMap.put("value", point) ;
    		userMap.put("time", userMap.get("POT_DATE")) ;
    		userMap.put("memo", (String) userMap.get("MEMO")) ;
    		userMap.put("source", (String) userMap.get("SOURCE_TYPE")) ;

    		userMap.remove("IO_FLAG") ;
    		userMap.remove("POT_DATE") ;
    		userMap.remove("MEMO") ;
    		userMap.remove("POT_QTY") ;
    		userMap.remove("SOURCE_TYPE") ;
    	}
    	
    	return rows ;
	}

	private JdbcTemplate jdbcTpl ;
	
	private int memberId ;
	
}
