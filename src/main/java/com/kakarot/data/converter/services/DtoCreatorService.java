package com.kakarot.data.converter.services;

import java.util.List;
import java.util.Map;

public interface DtoCreatorService {

	<T> List<String> getFieldNames(Class<T> clazz) throws Exception;
	<T> T create(Class<T> clazz, Map<String, String[]> possibleValues) throws Exception;

}