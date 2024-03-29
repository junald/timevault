package com.example.timevault;

import java.lang.reflect.Method;
import java.util.Comparator;

public class ReflectionComparator implements Comparator
{
	private String methodName;

	public ReflectionComparator(String methodName)
	{
		this.methodName = methodName;
	}

	public int compare(Object o1, Object o2)
	{

		try
		{
			Method m1 = o1.getClass().getMethod(methodName, new Class[]
			{});
			Method m2 = o2.getClass().getMethod(methodName, new Class[]
			{});
			return ((String) m1.invoke(o1, new Object[]
			{})).compareTo((String) m2.invoke(o2, new Object[]
			{}));
		} catch (Exception ex)
		{
			throw new RuntimeException("ReflectionComparator", ex);
		}

	}
}