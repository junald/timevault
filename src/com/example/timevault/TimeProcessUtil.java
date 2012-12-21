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
		private static Map<String, Map<String, Map<String, List<List<StaffData>>>>> _STORE = null;
	private Map<String, Map<String, List<List<StaffData>>>> staffPunchesMonthMap = new HashMap<String, Map<String, List<List<StaffData>>>>();
	private final static String path = "\\\\ws-phsvr01\\Public\\DailyTimeRecord\\";

	private String source = null;
	public String department;
	public String excelPath = null;

	static {
		_STORE = new HashMap<String, Map<String, Map<String, List<List<StaffData>>>>>();
	}

	public TimeProcessUtil() {

	}

	protected void removeFromStore(String ip) {
		try {
			_STORE.remove(ip);
		} catch (Exception e) {

		}

	}

	protected Map<String, Map<String, List<List<StaffData>>>> getFromStore(
			String ip) {
		return _STORE.get(ip);
	}

	public void load(String ip, String department, String month, String year) {

		removeFromStore(ip);
		staffPunchesMonthMap = new HashMap<String, Map<String, List<List<StaffData>>>>();
		month = getMonth(month);
		System.out.println("LoadTimeEntries:load(" + ip + "," + department
				+ "," + month + "," + year + ")");
		try {
			this.department = department;
			for (int i = 1; i <= 31; i++) {
				String day = String.valueOf(i);
				String source = month + "." + day;

				// First Check
				if (!isExist(source, year,month)) {
					if (day.length() == 1) {
						source = month + "."
								+ (day.length() == 1 ? "0" + day : day);

						if (!isExist(source, year,month)) {

						}
						loadData(ip, source, month,year);
					}
				} else {
					loadData(ip, source, month,year);
				}
			}

		} catch (Exception e) {

		}
	}

	protected void loadData(String ip, String source, String month, String year) {
		System.out.println("LoadTimeEntries:load(" + ip + "," + source + ","
				+ year + ")");
		this.source = source;
		try {
			String temp = department;
			excelPath = path + "\\" + department + "\\"
					+ temp.replace("-", "_") + "." + year + "\\"
					+ temp.replace("-", "_") + "." + month+ "." +  year.substring(2,year.length()) + "\\"
					+ temp.replace("-", "_") + ".";
			String xlsPath = path + "\\" + department + "\\"
					+ temp.replace("-", "_") + "." + year + "\\"
					+ temp.replace("-", "_") + "." + month+ "." +  year.substring(2,year.length()) + "\\"
					+ temp.replace("-", "_") + "." + source + ".xls";

			InputStream is = new FileInputStream(xlsPath);

			init(ip, is);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public boolean isExist(String source, String year, String month) {

		String temp = department;
		// System.out.println("Source:" + path + "\\" + category + "\\"
		// + temp.replace("-", "_") + "." + source + ".xls");

		File file = new File(path + "\\" + department + "\\"
				+ temp.replace("-", "_") + "." + year + "\\"
				+ temp.replace("-", "_") + "." + month+ "." +  year.substring(2,year.length()) + "\\"
				+ temp.replace("-", "_") + "." + source + ".xls");

		boolean trace = file.exists();
		System.out.println("LoadTimeEntries:isExist():" + trace + "\tFile:"
				+ file.getAbsolutePath());
		return trace;

	}

	protected void init(String ip, InputStream is) {
		try {
			System.out.println("LoadTimeEntries:init()");
			Workbook workbook = Workbook.getWorkbook(is);
			Sheet sheet = workbook.getSheet(0);

			String staffName = null;
			Map<String, List<List<StaffData>>> staffPunchesPerDayMap = new HashMap<String, List<List<StaffData>>>();
			List<StaffData> summaryPunchesList = new ArrayList<StaffData>();
			List<StaffData> detailsPunchesList = new ArrayList<StaffData>();
			List<List<StaffData>> records = new ArrayList<List<StaffData>>();

			for (int row = 5; row < sheet.getRows(); row++) {
				if (!sheet.getCell(1, row).getContents().trim().equals("")) {
					StaffData staffData = new StaffData();
					staffData.setCardNo(sheet.getCell(0, row).getContents());
					staffData.setStaffName(sheet.getCell(1, row).getContents());

					String punchTime = null;
					Cell punch = sheet.getCell(4, row);

					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					try {

						punchTime = punch.getContents();
						Date in = df.parse(punchTime);

					} catch (ParseException pe) {
						try {
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

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					// staffData.setPunchTime(sheet.getCell(4,
					// row).getContents());

					staffData.setPunchTime(punchTime);
					staffData.setDirection(sheet.getCell(5, row).getContents());
					detailsPunchesList.add(staffData);

					if (staffName == null) {
						summaryPunchesList.add(staffData);
						staffName = sheet.getCell(1, row).getContents();

						if (!staffName.equals(sheet.getCell(1, row + 1)
								.getContents().trim())) {
							// summaryPunchesList.add(staffData);

							records.add(summaryPunchesList);
							records.add(detailsPunchesList);
							staffPunchesPerDayMap.put(staffName, records);

							summaryPunchesList = new ArrayList<StaffData>();
							detailsPunchesList = new ArrayList<StaffData>();
							records = new ArrayList<List<StaffData>>();
							staffName = null;
						}

					} else {
						if (!staffName.equals(sheet.getCell(1, row + 1)
								.getContents().trim())) {
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

			System.out.println("staffPunchesMonthMap:"
					+ staffPunchesMonthMap.size());
			System.out.println("staffPunchesPerDayMap:"
					+ staffPunchesPerDayMap.size());
			is.close();
			workbook.close();

			_STORE.put(ip, staffPunchesMonthMap);

		} catch (FileNotFoundException ffe) {
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public List<String> getStaffLists(String ip) {
		System.out.println("LoadTimeEntries:getStaffLists()");
		List<String> staffLists = new ArrayList<String>();

		staffPunchesMonthMap = _STORE.get(ip);
		try {
			Set keys = staffPunchesMonthMap.keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) {
				Map<String, List<List<StaffData>>> staffPunchesPerDayMap = staffPunchesMonthMap
						.get((String) i.next());
				Set keyStaff = staffPunchesPerDayMap.keySet();
				for (Iterator j = keyStaff.iterator(); j.hasNext();) {
					String staff = (String) j.next();
					if (staffLists.size() == 0) {
						staffLists.add(staff);
					} else {
						boolean trace = false;
						for (int k = 0; k < staffLists.size(); k++) {
							if (staff.equals(staffLists.get(k))) {
								trace = true;
							}
						}
						if (!trace) {
							staffLists.add(staff);
						}
					}

				}
			}
			Collections.sort(staffLists);
			System.out.println(staffLists);
		} catch (Exception e) {

		}
		return staffLists;
	}

	@SuppressWarnings("unchecked")
	public Vector<StaffData> getStaffSummaryPunches(String ip, String staff) {
		System.out.println("LoadTimeEntries:getStaffSummaryPunches(" + staff
				+ ")");
		staffPunchesMonthMap = _STORE.get(ip);
		datePunchesList = new ArrayList<String>();
		Vector<StaffData> vStaff = new Vector<StaffData>();
		Set keys = staffPunchesMonthMap.keySet();

		for (Iterator i = keys.iterator(); i.hasNext();) {
			try {
				String key = (String) i.next();
				Map<String, List<List<StaffData>>> staffPunchesPerDayMap = staffPunchesMonthMap
						.get(key);
				List<StaffData> punches = staffPunchesPerDayMap.get(staff).get(
						0);
				StaffData staffData = new StaffData();
				staffData = punches.get(0);

				try {
					staffData.setPunchTime2(punches.get(1).getPunchTime());
				} catch (IndexOutOfBoundsException iobe) {
					staffData.setPunchTime2("");
				}
				vStaff.add(staffData);
				datePunchesList.add(staffData.getPunchTime().split(" ")[0]);
			} catch (NullPointerException npe) {

			}

			Collections.sort(vStaff, new ReflectionComparator("getPunchTime"));
			Collections.sort(datePunchesList);
		}

		return vStaff;
	}

	List<String> datePunchesList = new ArrayList<String>();

	public List<String> getStaffSummaryDatePunches() {
		System.out.println("LoadTimeEntries:getStaffSummaryDatePunches()");
		return datePunchesList;

	}

	public List<StaffData> getStaffDetailsPunches(String ip, String staff,
			String month, String day) {

		month = getMonth(month);
		System.out.println("LoadTimeEntries:getStaffDetailsPunches(" + staff
				+ "," + month + "," + day + ")");

		staffPunchesMonthMap = _STORE.get(ip);
		List<StaffData> lStaff = new ArrayList<StaffData>();
		Map<String, List<List<StaffData>>> staffPunchesPerDayMap = new HashMap<String, List<List<StaffData>>>();
		try {

			System.out.println("Details Punch:" + Integer.parseInt(month) + "."
					+ day);
			staffPunchesPerDayMap = staffPunchesMonthMap.get(Integer
					.parseInt(month)
					+ "." + day);
			lStaff = staffPunchesPerDayMap.get(staff).get(1);
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			aiobe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Details Punch:" + Integer.parseInt(month) + "."
				+ Integer.parseInt(day));

		if (lStaff.size() == 0) {
			staffPunchesPerDayMap = staffPunchesMonthMap.get(Integer
					.parseInt(month)
					+ "." + Integer.parseInt(day));
			lStaff = staffPunchesPerDayMap.get(staff).get(1);
		}

		System.out.println("LoadTimeEntries:getStaffDetailsPunches():"
				+ lStaff.size());
		return lStaff;

	}

	protected String getMonth(String monthStr) {
		String strVal = "";

		if (monthStr.equalsIgnoreCase("January"))
			strVal = "1";
		if (monthStr.equalsIgnoreCase("February"))
			strVal = "2";
		if (monthStr.equalsIgnoreCase("March"))
			strVal = "3";
		if (monthStr.equalsIgnoreCase("April"))
			strVal = "4";
		if (monthStr.equalsIgnoreCase("May"))
			strVal = "5";
		if (monthStr.equalsIgnoreCase("June"))
			strVal = "6";
		if (monthStr.equalsIgnoreCase("July"))
			strVal = "7";
		if (monthStr.equalsIgnoreCase("August"))
			strVal = "8";
		if (monthStr.equalsIgnoreCase("September"))
			strVal = "9";
		if (monthStr.equalsIgnoreCase("October"))
			strVal = "10";
		if (monthStr.equalsIgnoreCase("November"))
			strVal = "11";
		if (monthStr.equalsIgnoreCase("December"))
			strVal = "12";

		return strVal;

	}
	
	public static void main(String[] args){
		TimeProcessUtil tpu = new TimeProcessUtil();
		String ip="192.168.1.201";
		tpu.load(ip, "Developer", "December", "2012");
		for(String s: tpu.getStaffLists(ip)){
			System.out.println(s);
			Vector<StaffData> vs = tpu.getStaffSummaryPunches(ip,s);
			System.out.println(s+"============");
			for(StaffData sd : vs){
				System.out.println("    " + sd.getCardNo() + " : " + sd.getStaffNo() + " : " + sd.getPunchTime());
			}	
			System.out.println(s+"============");
		}
		
	}
}

