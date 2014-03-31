package com.welab.lavico.middleware.service;

public class DaoBrandError extends Error {
	private static final long serialVersionUID = 6238325879180036278L;
	public DaoBrandError(){
		super("The paramter brand is invalid.") ;
	}
}
