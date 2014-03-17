package com.welab.lavico.middleware.rest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestBase extends HttpServlet {

    protected void rspn(HttpServletResponse response,int mid,String issuceed,String error){
    	response.setContentType("text/javascript;charset=UTF-8");
    	try {
	    response.getWriter().println("{\"O_PUB_MEMBER_ID\":"+mid+",\"O_ISSUCCEED\":\""+issuceed+"\",\"O_HINT\":\""+error+"\"}");
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }
}
