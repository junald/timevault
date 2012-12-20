package com.example.timevault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class TimeProcessUtil {
	private Map<String, Map<String, List<List<StaffData>>>> staffPunchesMonthMap = null;
	private final static String path = "\\\\ws-phsvr01\\Public\\DailyTimeRecord\\";

	private String source = null;
	public String category;
	public String excelPath = null;

	public TimeProcessUtil()
	{
		staffPunchesMonthMap = new HashMap<String, Map<String, List<List<StaffData>>>>();
	}

	public void load(String source)
	{
		this.source = source;
		try
		{
			String temp = category;
			excelPath = path + "\\" + category + "\\" + temp.replace("-", "_") +".";
			
			InputStream is = new FileInputStream(path + "\\" + category + "\\" + temp.replace("-", "_")
					+ "." + source + ".xls");

			init(is);
		} catch (Exception ex)
		{
			// ex.printStackTrace();
		}
	}

	
	public boolean isExist(String source)
	{

		String temp = category;
		// System.out.println("Source:" + path + "\\" + category + "\\"
		// + temp.replace("-", "_") + "." + source + ".xls");

		File file = new File(path + "\\" + category + "\\"
				+ temp.replace("-", "_") + "." + source + ".xls");
		return file.exists();

	}

	protected void init(InputStream is)
	{
		try
		{

			Workbook workbook = Workbook.getWorkbook(is);
			Sheet sheet = workbook.getSheet(0);

			String staffName = null;
			Map<String, List<List<StaffData>>> staffPunchesPerDayMap = new HashMap<String, List<List<StaffData>>>();
			List<StaffData> summaryPunchesList = new ArrayList<StaffData>();
			List<StaffData> detailsPunchesList = new ArrayList<StaffData>();
			List<List<StaffData>> records = new ArrayList<List<StaffData>>();

			for (int row = 5; row < sheet.getRows(); row++)
			{
				if (!sheet.getCell(1, row).getContents().trim().equals(""))
				{
					StaffData staffData = new StaffData();
					staffData.setCardNo(sheet.getCell(0, row).getContents());
					staffData.setStaffName(sheet.getCell(1, row).getContents());

					String punchTime = null;
					Cell punch = sheet.getCell(4, row);

					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					try
					{

						punchTime = punch.getContents();
						Date in = df.parse(punchTime);

					} catch (ParseException pe)
					{
						try
						{
							Calendar today = Calendar.getInstance();
							StringTokenizer st = new StringTokenizer(source,
									".");

							String month = st.nextToken();
							String day = st.nextToken();

							month = month.length() == 1 ? ("0" + month) : month;
							day = day.length() == 1 ? ("0" + day) : day;

							String[] tt = punchTime.split(" ");
							String[] t = tt[0].split(":");
							t[0] = t[0].length() == 1 ? ("0" + t[0]) : t[0];
							t[1] = t[1].length() == 1 ? ("0" + t[1]) : t[1];

							String temp = today.get(Calendar.YEAR) + "-"
									+ month + "-" + day + " " + t[0] + ":"
									+ t[1] + ":"
									+ (t.length == 2 ? "00" : t[2]) + " "
									+ tt[1];

							df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
							Date d = df.parse(temp);

							df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							punchTime = df.format(d);

						} catch (Exception e)
						{
							e.printStackTrace();
						}

					}

					// staffData.setPunchTime(sheet.getCell(4,
					// row).getContents());

					staffData.setPunchTime(punchTime);
					staffData.setDirection(sheet.getCell(5, row).getContents());
					detailsPunchesList.add(staffData);

					if (staffName == null)
					{
						summaryPunchesList.add(staffData);
						staffName = sheet.getCell(1, row).getContents();

						if (!staffName.equals(sheet.getCell(1, row + 1)
								.getContents().trim()))
						{
							// summaryPunchesList.add(staffData);

							records.add(summaryPunchesList);
							records.add(detailsPunchesList);
							staffPunchesPerDayMap.put(staffName, records);

							summaryPunchesList = new ArrayList<StaffData>();
							detailsPunchesList = new ArrayList<StaffData>();
							records = new ArrayList<List<StaffData>>();
							staffName = null;
						}

					} else
					{
						if (!staffName.equals(sheet.getCell(1, row + 1)
								.getContents().trim()))
						{
							summaryPunchesList.add(staffData);

							records.add(summaryPunchesList);
							records.add(detailsPunchesList);
							staffPunchesPerDayMap.put(staffName, records);

							summaryPunchesList = new ArrayList<StaffData>();
							detailsPunchesList = new ArrayList<StaffData>();
							records = new ArrayList<List<StaffData>>();
							staffName = null;
						}
					}
				}
			}

			staffPunchesMonthMap.put(source, staffPunchesPerDayMap);
			is.close();
			workbook.close();

		} catch (FileNotFoundException ffe)
		{
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public List<String> getStaffLists()
	{
		List<String> staffLists = new ArrayList<String>();

		Set keys = staffPunchesMonthMap.keySet();
		for (Iterator i = keys.iterator(); i.hasNext();)
		{
			Map<String, List<List<StaffData>>> staffPunchesPerDayMap = staffPunchesMonthMap
					.get((String) i.next());
			Set keyStaff = staffPunchesPerDayMap.keySet();
			for (Iterator j = keyStaff.iterator(); j.hasNext();)
			{
				String staff = (String) j.next();
				if (staffLists.size() == 0)
				{
					staffLists.add(staff);
				} else
				{
					boolean trace = false;
					for (int k = 0; k < staffLists.size(); k++)
					{
						if (staff.equals(staffLists.get(k)))
						{
							trace = true;
						}
					}
					if (!trace)
					{
						staffLists.add(staff);
					}
				}

			}
		}
		Collections.sort(staffLists);
		return staffLists;
	}

	@SuppressWarnings("unchecked")
	public Vector<StaffData> getStaffSummaryPunches(String staff)
	{

		datePunchesList = new ArrayList<String>();
		Vector<StaffData> vStaff = new Vector<StaffData>();
		Set keys = staffPunchesMonthMap.keySet();

		for (Iterator i = keys.iterator(); i.hasNext();)
		{
			try
			{
				String key = (String) i.next();
				Map<String, List<List<StaffData>>> staffPunchesPerDayMap = staffPunchesMonthMap
						.get(key);
				List<StaffData> punches = staffPunchesPerDayMap.get(staff).get(
						0);
				StaffData staffData = new StaffData();
				staffData = punches.get(0);

				try
				{
					staffData.setPunchTime2(punches.get(1).getPunchTime());
				} catch (IndexOutOfBoundsException iobe)
				{
					staffData.setPunchTime2("");
				}
				vStaff.add(staffData);
				datePunchesList.add(staffData.getPunchTime().split(" ")[0]);
			} catch (NullPointerException npe)
			{

			}

			Collections.sort(vStaff, new ReflectionComparator("getPunchTime"));
			Collections.sort(datePunchesList);
		}

		return vStaff;
	}

	List<String> datePunchesList = new ArrayList<String>();

	public List<String> getStaffSummaryDatePunches()
	{
		return datePunchesList;

	}

	public List<StaffData> getStaffDetailsPunches(String staff, String month,
			String day)
	{

		List<StaffData> lStaff = new ArrayList<StaffData>();

		try
		{
			Map<String, List<List<StaffData>>> staffPunchesPerDayMap = staffPunchesMonthMap
					.get(month + "." + day);

			lStaff = staffPunchesPerDayMap.get(staff).get(1);
		} catch (NullPointerException npe)
		{

		}
		return lStaff;
	}
	
}
