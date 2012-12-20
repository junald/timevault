package com.example.timevault;

public class StaffData {
	private String staffNo;
	private String staffName;
	private String cardNo;
	private String punchTime;
	private String punchTime2;
	private String direction;

	public void setStaffNo(String sd)
	{
		staffNo = sd;
	}

	public void setStaffName(String sd)
	{
		staffName = sd;
	}

	public void setCardNo(String sd)
	{
		cardNo = sd;
	}

	public void setPunchTime(String sd)
	{
		punchTime = sd;
	}

	public void setPunchTime2(String sd)
	{
		punchTime2 = sd;
	}

	public void setDirection(String sd)
	{
		direction = sd;
	}

	public String getStaffNo()
	{
		return staffNo;
	}

	public String getStaffName()
	{
		return staffName;
	}

	public String getCardNo()
	{
		return cardNo;
	}

	public String getPunchTime()
	{
		return punchTime;
	}

	public String getPunchTime2()
	{
		return punchTime2;
	}

	public String getDirection()
	{
		return direction;
	}
}
