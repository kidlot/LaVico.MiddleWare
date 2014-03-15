package com.welab.lavico.middleware ;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("documentNumberService")
public class DocumentNoService  {

	public String getDocumentNo(Integer pk_setNo, String class_field,
			String center_field,JdbcTemplate jdbcTemplate,String user_id) throws Exception {
		// 得到单据号生成定义表 中单据生成所需数据
		List<Map<String, Object>> listSetNo = getSetNo(pk_setNo,jdbcTemplate);

		// 得到最大单据号表中单据生成所需数据
		List<Map<String, Object>> listMaxNo = getMaxNo(pk_setNo,class_field,null,jdbcTemplate);

		// 取出单据号生成定义表所需字段
		Map<String, Object> mapSetNo = listSetNo.get(0);
		// 是否有单据分类字段
		String is_in_class = (String) mapSetNo.get("IS_IN_CLASS");	
		// 是否有单据中心字段
		String is_in_center = (String) mapSetNo.get("IS_IN_CENTER");
		// 是否有日期格式字段
		String is_in_date = (String) mapSetNo.get("IS_IN_DATE");
		
		//取出最大单据号表所需字段
		Map<String, Object> mapMaxNo = listMaxNo.get(0);
		//单据分类字段值
		String class_field_value = (String) mapMaxNo.get("CLASS_FIELD_VALUE");
		//单据中心字段值
//		String center_field_value = (String) mapMaxNo.get("CENTER_FIELD_VALUE");
		//当前单据日期
		Date now_doc_date = (Date) mapMaxNo.get("NOW_DOC_DATE");
		//前缀字符方式
		String doc_pr_type = (String) mapMaxNo.get("DOC_PR_TYPE");
		//自定义前缀值
		String doc_pr_value = (String) mapMaxNo.get("DOC_PR_VALUE");

		StringBuffer str = new StringBuffer();

		// 判断是否需要分类
		if (is_in_class.equals("Y")) {
			// 判断是否存在分类
			if (!isClass(pk_setNo, class_field,jdbcTemplate).isEmpty()) {
				//添加前缀
				if(StringUtils.equals(doc_pr_type, "01")){
					str.append(class_field_value);
				} else {
					if(StringUtils.isNotEmpty(doc_pr_value)){
						str.append(doc_pr_value);
					}
				}
			} else {
				throw new Exception("分类不存在，请联系管理员！");
			}
		} else {
			//不需要分类时添加前缀
			if(StringUtils.equals(doc_pr_type, "01")){
				
			} else {
				if(StringUtils.isNotEmpty(doc_pr_value)){
					str.append(doc_pr_value);
				}
			}
		}
		// 判断是否需要中心
		if (is_in_center.equals("Y")) {
			// 判断是否存在该中心
			if (!getMaxNo(pk_setNo,class_field,center_field,jdbcTemplate).isEmpty()) {
				str.append(center_field);
			} else {
				Integer seq = this.getSequence1(jdbcTemplate);
				Object objs[] = new Object[] { seq,
						mapMaxNo.get("PK_PUB_DOC_SETNO"),
						mapMaxNo.get("CLASS_FIELD_VALUE"),
						mapMaxNo.get("DOC_PR_TYPE"),
						mapMaxNo.get("DOC_PR_VALUE"),
						mapMaxNo.get("NOW_DOC_SEQ"),
						new Date(), center_field, 
						//commonManage.getCurrentPersonID(),
						user_id,
						new Date(),new Date(),
						//commonManage.getCurrentPersonID()
						user_id};
				String insertSql = "INSERT INTO PUB_DOC_MAXNO"
						+ "(PK_PUB_DOC_MAXNO,"
						+ "PK_PUB_DOC_SETNO,CLASS_FIELD_VALUE,"
						+ "DOC_PR_TYPE,DOC_PR_VALUE,NOW_DOC_SEQ,"
						+ "NOW_DOC_DATE,CENTER_FIELD_VALUE,"
						+ "PK_MAKE_USER, CREATE_DATE, "
						+ "LAST_UPDATE_DATE,LAST_UPDATE_USER,ACTIVE) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,1) ";
				jdbcTemplate.update(insertSql, objs);
				str.append(center_field);
			}
		}
		// 是否需要日期格式
		if (is_in_date.equals("Y")) {
			Date date = new Date();
			String date_type = (String) mapSetNo.get("DATE_TYPE");
			SimpleDateFormat simdf = new SimpleDateFormat(date_type);
			String now_date = simdf.format(now_doc_date);
			String sys_date = simdf.format(date);
			// 现有日期与当前日期是否相同
			if (StringUtils.equals(now_date, sys_date)) {
				// 把日期处理为字符串
				
				SimpleDateFormat sdf = new SimpleDateFormat(date_type);
				String strdate = sdf.format(now_doc_date);
				str.append(strdate);			
			}
			// 日期不相同，忽略当前最大流水号，初始当前流水号为0
			else {
				BigDecimal pk_maxNo =  (BigDecimal) mapMaxNo.get("PK_PUB_DOC_MAXNO");
				String updateSql = "UPDATE PUB_DOC_MAXNO SET NOW_DOC_SEQ=0,NOW_DOC_DATE=?, " +
						"LAST_UPDATE_DATE=?,LAST_UPDATE_USER=? WHERE PK_PUB_DOC_MAXNO=?";
				jdbcTemplate.update(updateSql,
						new Object[] {date,new Date(),user_id,pk_maxNo });
				str.append(sys_date);
				
			}
		}
		BigDecimal  pk_maxNo =  (BigDecimal) getMaxNo(pk_setNo,class_field,center_field,jdbcTemplate).get(0).get("PK_PUB_DOC_MAXNO");
		String sql = "SELECT A.NOW_DOC_SEQ FROM PUB_DOC_MAXNO A WHERE A.PK_PUB_DOC_MAXNO=?";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql, new Object[]{pk_maxNo});
		Map<String,Object> map = list.get(0);
		StringBuffer docMaxNo = new StringBuffer();
		docMaxNo.append(str);
		
		//取得流水号，最大流水号+1
		BigDecimal now_doc_seq = (BigDecimal) map.get("NOW_DOC_SEQ");
		int serialNo = 0;
		if(now_doc_seq.intValue()==0){
			serialNo = now_doc_seq.intValue() + 1;
		}else{
			serialNo = now_doc_seq.intValue();
		}
		String serialNumber = ""+serialNo;
		String strlength = serialNumber;
		BigDecimal seg_length = (BigDecimal) mapSetNo.get("SEG_LENGTH");
		for(int i=0;i<seg_length.intValue()-strlength.length();i++){
			serialNumber = "0"+serialNumber;
		}
		str.append(serialNumber);
		
		
		int docNo = serialNo+1;
		String docNuber = ""+docNo;
		String doclength = docNuber;
		BigDecimal doc_length = (BigDecimal) mapSetNo.get("SEG_LENGTH");
		for(int i=0;i<doc_length.intValue()-doclength.length();i++){
			docNuber = "0"+docNuber;
		}
		docMaxNo.append(docNuber);
		jdbcTemplate.update("UPDATE PUB_DOC_MAXNO SET NOW_DOC_SEQ=?,NOW_DOC_DATE=?,NOW_DOC_NO=?," +
				"LAST_UPDATE_DATE=?,LAST_UPDATE_USER=? WHERE PK_PUB_DOC_MAXNO=?", 
				new Object[]{serialNo+1,new Date(),docMaxNo.toString(),
						new Date(),user_id,pk_maxNo});
		return str.toString();
	}

	/**
	 * 得到单据号生成定义表 中单据生成所需数据
	 * 
	 * @param pk_setNo
	 * @return
	 */
	private List<Map<String, Object>> getSetNo(Integer pk_setNo,JdbcTemplate jdbcTemplate) {
		String sqlDocSetNo = "SELECT " 
				+ "A.PK_PUB_DOC_SETNO,"
				+ "A.IS_IN_CLASS," 
				+ "A.CLASS_FIELD_NAME," 
				+ "A.IS_IN_CENTER,"
				+ "A.CENTER_FIELD_NAME," 
				+ "A.IS_IN_DATE," 
				+ "A.DATE_TYPE,A.SEG_LENGTH "
				+ "FROM PUB_DOC_SETNO A " 
				+ "WHERE A.PK_PUB_DOC_SETNO=?";
		Object args[] = new Object[] { pk_setNo };
		return jdbcTemplate.queryForList(sqlDocSetNo, args);
	}

	
	/**
	 * 得到最大单据号表中单据生成所需数据
	 * 
	 * @param pk_setNo
	 * @return
	 */
	private List<Map<String, Object>> getMaxNo(Integer pk_setNo, String class_field,
			String center_field,JdbcTemplate jdbcTemplate) {
		// 得到最大单据号表中单据生成所需数据
		String sqlDocMaxNo = "SELECT "
				+ "B.PK_PUB_DOC_MAXNO,"
				+ "B.PK_PUB_DOC_SETNO," 
				+ "B.CLASS_FIELD_VALUE,"
				+ "B.DOC_PR_TYPE," 
				+ "B.DOC_PR_VALUE,"
				+ "B.CENTER_FIELD_VALUE," 
				+ "B.NOW_DOC_SEQ,B.NOW_DOC_DATE," 
				+ "B.DOC_PR_TYPE," 
				+ "B.DOC_PR_VALUE "
				+ "FROM PUB_DOC_MAXNO B " 
				+ "WHERE B.PK_PUB_DOC_SETNO=? ";
		StringBuffer str = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		params.add(pk_setNo);
		if(StringUtils.isNotEmpty(class_field)){
			str.append(" AND B.CLASS_FIELD_VALUE=? ");
			params.add(class_field);
		} /*
		else {
			str.append(" AND B.CLASS_FIELD_VALUE IS NULL ");
		}*/
		if(StringUtils.isNotEmpty(center_field)){
			str.append(" AND B.CENTER_FIELD_VALUE=? ");
			params.add(center_field);
		} /*
		else {
			str.append(" AND B.CENTER_FIELD_VALUE IS NULL ");
		}
		*/
		sqlDocMaxNo = sqlDocMaxNo+str.toString();
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sqlDocMaxNo, params.toArray());
		return list;
	}
	
	private List<Map<String,Object>> isClass(Integer pk_setNo, String class_field,JdbcTemplate jdbcTemplate){
		String sql = "SELECT CLASS_FIELD_VALUE FROM PUB_DOC_MAXNO  " 
				+ " WHERE PK_PUB_DOC_SETNO=? " 
				+ " AND CLASS_FIELD_VALUE =?";
		Object args[] = new Object[]{pk_setNo,class_field};
		return jdbcTemplate.queryForList(sql, args);
	}
	
	public Integer getSequence1(JdbcTemplate jdbcTemplate) {
        Map<String, Object> data = jdbcTemplate.queryForMap(
                "select " + "SYS_DOC_ID" + ".nextval from dual",
                new Object[] {});
        if (data != null) {
            BigDecimal nextValue = (BigDecimal) data.get("NEXTVAL");
            return (int) nextValue.longValue();
        }
        return null;
    }


}
