package com.welab.lavico.middleware.service ;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import cn.emay.sdk.client.api.Client;


public class SmsClient {
	private static Client client=null;
	private SmsClient(){
	}
	public synchronized static Client getClient(String softwareSerialNo,String key){
		if(client==null){
			try {
				client=new Client(softwareSerialNo,key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	public synchronized static Client getClient(){
		ResourceBundle bundle=PropertyResourceBundle.getBundle("config");
		if(client==null){
			try {
				System.out.println(bundle.getString("softwareSerialNo"));
				System.out.println(bundle.getString("key"));
				client=new Client(bundle.getString("softwareSerialNo"),bundle.getString("key"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
}
