package com.zf.qqcy.qqcymanager.service.remote.baseinfo;

import com.cea.core.mapper.CustomConvertUtils;
import com.cea.core.mapper.IgnoreHibernateLazyMapper;
import com.cea.core.modules.entity.dto.SearchFilter;
import com.cea.core.sca.ScaUtils;
import com.cea.identity.remote.client.BaseInfoClient;
import com.cea.identity.remote.dto.DictionaryDto;
import com.cea.identity.remote.dto.UserDto;

import java.util.List;

public class BaseInfoClientUtils {

	private static BaseInfoClient client = null;
	
	/**
	 * 
	 * @return
	 */
	public static BaseInfoClient getClient(){
		if(client == null){
			client = ScaUtils.getService("BaseInfoComponent", BaseInfoClient.class);
		}
		return client;
	}
	
	
	/////////////////////////////utility remote interface///////////////////////////////////////
	
	
	
	public static UserDto findByUserName(String userName){
		UserDto dto = null;
		try {
			dto = getClient().findByUserName(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	
	public static List<String> findUserPermission(String userId){
		List<String> perms = null;
		try {
			perms = getClient().findUserPermission(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return perms;
	}

	public static List<DictionaryDto> findDictionaryByPathCode(String pathCode,String level) {
		List<DictionaryDto> result = null;
		try {
			result = getClient().findDictionaryByPathCode(pathCode, level);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<DictionaryDto> findDictionaryListByPathCode(String pathCode) {
		List<DictionaryDto> result = null;
		try {
			result = getClient().findDictionaryListByPathCode(pathCode);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<DictionaryDto> findDictionaryByFilter(SearchFilter filter) {
		filter.setSortField("sortNum");
		filter.setSortDir("asc");
		List<DictionaryDto> result = null;
		try {
			result = getClient().findDictionaryByFilter(filter);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<DictionaryDto> findDictionaryByIds(List<String> ids) {
		List<DictionaryDto> result = null;
		try {
			result = getClient().findDictionaryByIds(ids);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static DictionaryDto findDictionaryById(String id) {
		DictionaryDto result = null;
		try {
			result = getClient().findDictionaryById(id);
			result = IgnoreHibernateLazyMapper.map(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static DictionaryDto findDictionaryByName(String pathCode, String name) {
		SearchFilter filter = new SearchFilter();
		filter.addFilter("LIKE_pathCode", pathCode);
		filter.addFilter("EQ_name", name);
		findDictionaryByFilter(filter);
		DictionaryDto result = null;
		try {
			List<DictionaryDto> dictionaryDtoList = getClient().findDictionaryByFilter(filter);
			dictionaryDtoList = CustomConvertUtils.covert(dictionaryDtoList, DictionaryDto.class);
			if (dictionaryDtoList == null || dictionaryDtoList.isEmpty()) {
				return null;
			}
			result = dictionaryDtoList.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


}
