package com.welab.lavico.middleware.rest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import com.welab.lavico.middleware.DB;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Points extends HttpServlet {

	private static final long serialVersionUID = 5L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException {

		if(request.getParameter("MEMBER_ID")==null){
		    rspn(response,null,"missing arg MEMBER_ID") ;
		    return ;
		}

    	JdbcTemplate jdbcTpt = DB.getJdbcTemplate() ;
    	Integer remaining = 0 ;
    	try {
	    	remaining = (Integer)jdbcTpt.queryForObject(
	    			"select TOTAL_CUR_POT from PUB_MEMBER_ID where SYS_MEMBER_ID=?"
	    			, new Object[]{request.getParameter("MEMBER_ID")}
		    		, java.lang.Integer.class
		    	) ;
    	} catch (IncorrectResultSizeDataAccessException e) {
    		if(e.getActualSize()==0){
    			rspn(response,null,"指定的会员不存在") ;
			    return ;
    		}
		    throw e ;
		}

    	List rows = jdbcTpt.queryForList("select "
    			+ "IO_FLAG, POT_DATE, POT_KIND, POT_QTY"
    			+ " from PUB_MEMBER_POINT"
    			+ " where SYS_MEMBER_ID=?",new Object[]{request.getParameter("MEMBER_ID")}) ;
    	Iterator iter = rows.iterator() ;

    	String outBuff = "{"
    			+ "\"remaining\":" + remaining.toString() + ","
    			+ "\"log\":[" ;
    	int idx = 0 ;
    	while(iter.hasNext()){
    		if(idx++>0){
    			outBuff+= "," ;
    		}
    		outBuff+="{" ;

    		Map userMap = (Map) iter.next();

    		int point = (((String)userMap.get("IO_FLAG")).equals("1")? +1 : -1) * ((java.math.BigDecimal)userMap.get("POT_QTY")).intValue() ;

    		outBuff+= "\"value\":\"" + point + "\"," ;
    		outBuff+= "\"date\":\"" + escape(((java.sql.Timestamp) userMap.get("POT_DATE")).toString()) + "\"," ;
    		outBuff+= "\"kind\":\"" + escape((String) userMap.get("POT_KIND")) + "\"" ;
    		outBuff+= "}" ;
    	}
    	outBuff+= "]}" ;

    	rspn(response,outBuff,null) ;
    }
    
    static public String escape(String data){
    	if(data==null)
    		return "" ;
    	return data.replace("\\", "\\\\")
    			.replace("\"", "\\\"")
    			.replace("\n","\\n")
    			.replace("\r","\\r") ;
    }

    private void rspn(HttpServletResponse response,String json,String error){
	    response.setContentType("text/javascript;charset=UTF-8");
	    try {
		    response.getWriter().println(
	    		json==null?
    				"{\"error\":\""+error+"\"}" :
    				json
		    ) ;
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
}