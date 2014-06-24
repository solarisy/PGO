package com.wismay.pms.comm;

import java.util.Date;

public class DateProvider {
	
	public static DateProvider DEFAULT=new DateProvider();
	
	private DateProvider(){
		super();
	}
	
	public Date getDate(){
		return new Date();
	}

}
