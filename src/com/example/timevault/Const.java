package com.example.timevault;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class Const {

	public static String[] CATEGORIES =
		{ "Admin-Accounting", "Developer", "MedLegal", "SysAdmin", "GSA", "QA" };

    public static String[] MONTHS =
		{ "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
    
    public static List<Integer> YEARS;
    
    public static String CURRENT_CATEGORIES = "Developer";
    public static Integer CURRENT_YEAR;
    public static Integer CURRENT_MONTH;
    public static String CURRENT_MONTHs;
    
    static {
    	YEARS = new LinkedList<Integer>();
    	int year = Calendar.getInstance().get(Calendar.YEAR);
    	CURRENT_MONTH = Calendar.getInstance().get(Calendar.MONTH);
    	System.out.println("current month:"+CURRENT_MONTH);
    	CURRENT_MONTHs = MONTHS[CURRENT_MONTH];
    	CURRENT_YEAR = year;
    	for(int i=3 ; i > 0; i--){
    		YEARS.add(year--);
    	}
    	
    }
		
	public static void main(String[] args){
		for(Integer i: YEARS){
			System.out.println(i);
		}
	}
}
