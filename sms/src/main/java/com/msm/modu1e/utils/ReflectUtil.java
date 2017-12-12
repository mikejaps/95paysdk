package com.msm.modu1e.utils;

import java.lang.reflect.Method;

public class ReflectUtil {
	public static void printfClassDeclareMethod(Class clazz) {
		if (clazz != null) {
			Method[] methods = clazz.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Class[] parameTypeClass = methods[i].getParameterTypes();
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < parameTypeClass.length; j++) {
					sb.append(parameTypeClass[j].getName() + " ,");
				}
				String methodName = methods[i].getName();
				Logcat.d(clazz.getName() + " : " + methodName + " : " + sb.toString());
			}
		}
	}
}
