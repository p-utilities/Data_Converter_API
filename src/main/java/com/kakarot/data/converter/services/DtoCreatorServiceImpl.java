package com.kakarot.data.converter.services;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * @author Igor
 *
 */

public class DtoCreatorServiceImpl implements DtoCreatorService {

	public <T> List<String> getFieldNames(Class<T> clazz) throws Exception {
		if (clazz == null)
			throw new NullPointerException();

		var result = new ArrayList<String>();

		BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
		PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor pd : pds) {
			Method setterMethod = pd.getWriteMethod();
			if (setterMethod != null) {
				result.add(getSetterFieldName(setterMethod.getName()));
			}
		}

		return result;
	}

	public <T> T create(Class<T> clazz, Map<String, String[]> values) throws Exception {
		if (clazz == null || values == null) {
			throw new NullPointerException();
		}

		T obj = clazz.getConstructor().newInstance();

		var methodMap = getSetterMethodsAndFieldNames(clazz);

		return populateObject(obj, methodMap, values, clazz);
	}

	private <T> Map<String, Method> getSetterMethodsAndFieldNames(Class<T> clazz) throws Exception {
		var resultMap = new HashMap<String, Method>();

		BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
		PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor pd : pds) {
			Method setterMethod = pd.getWriteMethod();
			if (setterMethod != null) {
				String fieldName = getSetterFieldName(setterMethod.getName());
				resultMap.put(fieldName, setterMethod);
			}
		}

		return resultMap;
	}

	private String getSetterFieldName(String methodName) {
		String result = methodName.replaceFirst("set", "");
		return result.replace(result.charAt(0), Character.toLowerCase(result.charAt(0)));
	}

	private <T> T populateObject(T obj, Map<String, Method> methodMap, Map<String, String[]> possibleValues,
			Class<T> clazz) throws Exception {
		Set<String> fieldsName = methodMap.keySet();

		for (String key : fieldsName) {
			String[] possibleValue = possibleValues.get(key);
			Method setterMethod = methodMap.get(key);

			var parameterCanonName = setterMethod.getParameterTypes()[0].getCanonicalName();

			if (parameterCanonName.equals(String.class.getCanonicalName())) {
				String finalValue = "";

				if (possibleValue != null) {
					if (possibleValue.length == 1)
						finalValue = possibleValue[0];
					else {
						for (String value : possibleValue) {
							finalValue = finalValue.concat(value + ",");
						}
						finalValue = finalValue.substring(0, finalValue.length() - 1);
					}
				}

				setterMethod.invoke(obj, finalValue);

			} else if (parameterCanonName.equals(String[].class.getCanonicalName())) {

				setterMethod = clazz.getDeclaredMethod(setterMethod.getName(), String[].class);
				setterMethod.invoke(obj, new Object[] { possibleValue });
			}

			// should implement primitive conversion but for this task, that would be
			// impractical

		}

		return obj;
	}

}
