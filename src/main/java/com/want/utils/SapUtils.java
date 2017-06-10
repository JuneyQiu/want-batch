package com.want.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SapUtils {

	public static ArrayList<Object[]> getMapValue(List<HashMap> list,
			String... names) {
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		for (HashMap data : list) {
			String[] value = new String[names.length];
			for (int i = 0; i < names.length; i++)
				value[i] = (String) data.get(names[i]);
			result.add(value);
		}
		return result;
	}

	public static ArrayList<Object[]> getUniqueMapValue(List<HashMap> list, String... values) {
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Object[]> result = new ArrayList<Object[]>();

		for (HashMap data : list) {
			String key = (String) data.get(values[0]);
			if (!keys.contains(key)) {
				String[] value = new String[values.length];
				value[0] = key;
				keys.add(key);
				for (int i = 1; i < values.length; i++)
					value[i] = (String) data.get(values[i]);
				result.add(value);
			}
		}
		return result;
	}

}
