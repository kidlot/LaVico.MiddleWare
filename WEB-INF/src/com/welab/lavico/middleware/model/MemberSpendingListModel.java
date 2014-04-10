package com.welab.lavico.middleware.model;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemberSpendingListModel {

	public MemberSpendingListModel(JdbcTemplate jdbcTpl,String memberId) {
		this.jdbcTpl = jdbcTpl ;
		this.memberId = memberId ;
	}

	/**
	 * 所有消费记录的总数
	 * @return
	 */
	public int totalLength(){
		return jdbcTpl.queryForInt( "select count(*) from DRP_RETAIL_HEADER where SYS_MEMBER_ID=?", memberId ) ;
	}

	public List<Map<String,Object>> queryPage(int pageNum){
		return queryPage(pageNum,20) ;
	}
	
	public List<Map<String,Object>> queryPage(int pageNum,int perPage){
		String sql = " select *"
				+ "	from (select *"
				+ "  	from ("
				+ "			select"
				+ "				DRP_RETAIL_DETAIL.SALE_AMT as amt,"
				+ "				DRP_RETAIL_DETAIL.SALE_DATE as \"date\","
				+ "				DRP_RETAIL_DETAIL.GAIN_POINT as point,"
				+ "				PUB_CUSTOMER_TRANSIT.CUSTOMER_NAME as shop_name,"
				+ "				PUB_BARCODE.PRODUCT_NAME as product_name,"
				+ "				PUB_BARCODE.GOODS_STYLE_NO as goodsNo,"
				+ "				row_number() OVER(ORDER BY null) AS \"row_number\""
				+ "			from DRP_RETAIL_HEADER"
				+ "				left join DRP_RETAIL_DETAIL on (DRP_RETAIL_HEADER.SYS_DOC_ID=DRP_RETAIL_DETAIL.SYS_DOC_ID)"
				+ "				left join PUB_CUSTOMER_TRANSIT on ('H'||DRP_RETAIL_HEADER.WAREHOUSE_CODE=PUB_CUSTOMER_TRANSIT.CUSTOMER_CODE)"
				+ "				left join PUB_BARCODE on (DRP_RETAIL_DETAIL.BARCODE=PUB_BARCODE.BARCODE) "
				+ "			where DRP_RETAIL_HEADER.SYS_MEMBER_ID=?"
				+ "		)"
				+ "     where \"row_number\">?)"
				+ " where rownum<=?" ;
    	return jdbcTpl.queryForList(
			sql, new Object[]{ memberId, (pageNum-1)*perPage, perPage }
		) ;
	}

	private JdbcTemplate jdbcTpl ;
	private String memberId ;
}
