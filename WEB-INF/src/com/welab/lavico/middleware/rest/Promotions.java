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
import org.springframework.jdbc.core.JdbcTemplate;

public class Promotions extends HttpServlet {

	private static final long serialVersionUID = 4L;

	/**
	 * http://127.0.0.1:8080/welab.middleware/Promotions
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException {
    	
    	JdbcTemplate jdbcTpt = DB.getJdbcTemplate() ;
    	List rows = jdbcTpt.queryForList("select PROMOTION_CODE, PROMOTION_NAME, PROMOTION_DESC from DRP_PROMOTION_THEME") ;
    	Iterator iter = rows.iterator() ;
    	String outBuff = "{\"promotions\":[" ;
    	int idx = 0 ;
    	while(iter.hasNext()){
    		if(idx++>0){
    			outBuff+= ',' ;
    		}
    		outBuff+="{" ;
    		Map userMap = (Map) iter.next();
    		outBuff+= "\"PROMOTION_CODE\":\"" + escape((String) userMap.get("PROMOTION_CODE")) + "\"," ;
    		outBuff+= "\"PROMOTION_NAME\":\"" + escape((String) userMap.get("PROMOTION_NAME")) + "\"," ;
    		outBuff+= "\"PROMOTION_DESC\":\"" + escape((String) userMap.get("PROMOTION_DESC")) + "\"" ;
    		outBuff+= "}" ;
    	}
    	outBuff+= "]}" ;

    	response.setContentType("text/javascript;charset=UTF-8");
	    try {
		    response.getWriter().println(outBuff);
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
    
    static public String escape(String data){
    	return data.replace("\\", "\\\\")
    			.replace("\"", "\\\"")
    			.replace("\n","\\n")
    			.replace("\r","\\r") ;
    }
}