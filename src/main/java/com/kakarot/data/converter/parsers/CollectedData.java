package com.kakarot.data.converter.parsers;

import java.util.ArrayList;
import java.util.List;

public class CollectedData<T> {
	private String parrentName = "";
	private String tagName = "";
	private List<String> fieldNames = new ArrayList<String>();
	private List<T> data = new ArrayList<T>();

	public String getParrentName() {
		return parrentName;
	}

	public void setParrentName(String parrentName) {
		this.parrentName = parrentName;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public void setIndividualData(T data) {
		this.data.add(data);
	}

	@Override
	public String toString() {
		return "CollectedData [parrentName=" + parrentName + ", tagName=" + tagName
				+ ", fieldNames=" + fieldNames + ", data=" + data + "]";
	}

}
