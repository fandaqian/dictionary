package com.zf.qqcy.qqcyw.service.remote;

import com.cea.core.mapper.CustomConvertUtils;
import com.cea.core.mapper.IgnoreHibernateLazyMapper;
import com.cea.core.modules.entity.dto.SearchFilter;
import com.cea.core.sca.ScaUtils;
import com.cea.identity.remote.client.BaseInfoClient;
import com.cea.identity.remote.dto.DictionaryDto;
import com.cea.identity.remote.dto.UserDto;
import com.zf.qqcy.dataService.common.constants.CustomerConstants;

import java.util.List;

public class ClientUtils {

	private static BaseInfoClient baseInfoClient = null;
	
	/**
	 * 
	 * @return
	 */
	public static BaseInfoClient getBaseInfoClient(){
		if(baseInfoClient == null){
			baseInfoClient = ScaUtils.getService("BaseInfoComponent", BaseInfoClient.class);
		}
		return baseInfoClient;
	}

	
	///////////////////////////////////////////////////////////////////////
	
	
	
	public static UserDto findByUserName(String userName){
		UserDto result = null;
		try {
			result = getBaseInfoClient().findByUserName(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	public static List<String> findUserPermission(String userId){
		List<String> result = null;
		try {
			result = getBaseInfoClient().findUserPermission(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static List<DictionaryDto> findDictionaryByPathCode(String pathCode,String level) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryByPathCode(pathCode, level);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<DictionaryDto> findDictionaryListByPathCode(String pathCode) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryListByPathCode(pathCode);
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
			result = getBaseInfoClient().findDictionaryByFilter(filter);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<DictionaryDto> findDictionaryByFilterNoSort(SearchFilter filter) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryByFilter(filter);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<DictionaryDto> findDictionaryByIds(List<String> ids) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryByIds(ids);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    public static DictionaryDto findDictionaryById(String id) {
        DictionaryDto result = null;
        try {
            result = getBaseInfoClient().findDictionaryById(id);
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
        DictionaryDto result = null;
        try {
            List<DictionaryDto> dictionaryDtoList = findDictionaryByFilter(filter);
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

	public static List<DictionaryDto> findDictionaryByDLBM(CustomerConstants.DLBM dlbm) {
		return findDictionaryByPathCode(dlbm.getCode(), String.valueOf(dlbm.getLevel()));
	}

	public static void main(String[] args) {

	}
}
