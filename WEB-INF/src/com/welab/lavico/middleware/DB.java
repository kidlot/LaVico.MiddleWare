package com.welab.lavico.middleware;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DB {
	public static JdbcTemplate getJdbcTemplate(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		JdbcDaoSupport dao = (JdbcDaoSupport)ctx.getBean("jdbcDao");
		return dao.getJdbcTemplate() ;
	}
}