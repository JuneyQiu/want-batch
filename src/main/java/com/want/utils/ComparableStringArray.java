package com.want.utils;

public class ComparableStringArray {
	private String[] values;
	public ComparableStringArray(String...args) {
		values = args;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComparableStringArray))
			return false;
		String e[] = ((ComparableStringArray)obj).get();
		if (e.length != values.length)
			return false;
		for (int i=0; i<e.length; i++) {
			if (!e[i].equals(values[i]))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return values.hashCode();
	}
	
	public String[] get() {
		return values;
	}
	
	public String get(int i) {
		return values[i];
	}
}
