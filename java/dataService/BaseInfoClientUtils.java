package com.zf.qqcy.dataService.thirdparty;

import com.cea.core.mapper.CustomConvertUtils;
import com.cea.core.mapper.IgnoreHibernateLazyMapper;
import com.cea.core.modules.entity.dto.SearchFilter;
import com.cea.core.sca.ScaUtils;
import com.cea.identity.remote.client.BaseInfoClient;
import com.cea.identity.remote.dto.DictionaryDto;
import com.cea.identity.remote.dto.UserDto;
import com.zf.qqcy.dataService.common.constants.Constants;
import com.zf.qqcy.dataService.common.constants.CustomerConstants;
import org.apache.commons.lang.StringUtils;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BaseInfoClientUtils {

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
	
	
	/////////////////////////////identity remote interface///////////////////////////////////////
	
	
	
	public static UserDto findByUserName(String userName){
		UserDto result = null;
		try {
			result = getBaseInfoClient().findByUserName(userName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static List<String> findUserPermission(String userId){
		List<String> result = null;
		try {
			result = getBaseInfoClient().findUserPermission(userId);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<DictionaryDto> findDictionaryByIds(List<String> ids) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryByIds(ids);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static List<DictionaryDto> findAllParentByIds(String id) {
		List<DictionaryDto> result = null;
		try {
			DictionaryDto tmp =  getBaseInfoClient().findDictionaryById(id);
			tmp = IgnoreHibernateLazyMapper.map(tmp, DictionaryDto.class);
			if(tmp != null){
				String[] ids = tmp.getDicPath().split(Constants.PATH_SPLIT);
				result = findDictionaryByIds(Arrays.asList(ids));
				Collections.sort(result, new Comparator<DictionaryDto>(){
					@Override
					public int compare(DictionaryDto o1, DictionaryDto o2) {
						return StringUtils.countMatches(o1.getDicPath(), Constants.PATH_SPLIT)
								- StringUtils.countMatches(o2.getDicPath(), Constants.PATH_SPLIT) ;
					}
					
				});
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<DictionaryDto> findDictionaryByPathCode(String pathCode,String level) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryByPathCode(pathCode, level);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<DictionaryDto> findDictionaryByDLBM(CustomerConstants.DLBM dlbm) {
		return findDictionaryByPathCode(dlbm.getCode(), String.valueOf(dlbm.getLevel()));
	}
	
	public static List<DictionaryDto> findDictionaryListByPathCode(String pathCode) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryListByPathCode(pathCode);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<DictionaryDto> findDictionaryByFilter(SearchFilter filter) {
		List<DictionaryDto> result = null;
		try {
			result = getBaseInfoClient().findDictionaryByFilter(filter);
			result = CustomConvertUtils.covert(result, DictionaryDto.class);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}

    public static DictionaryDto findDictionaryById(String id) {
        DictionaryDto result = null;
        try {
            result = getBaseInfoClient().findDictionaryById(id);
			result = IgnoreHibernateLazyMapper.map(result, DictionaryDto.class);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	
	/**
	 * 
	 * @param id
	 * @param join
	 * @return
	 */
	public static String findAllParentNameByDicId(String id, String levels, String join){
		String value = "";
		if(StringUtils.isBlank(join)){
			join = "";
		}
		List<DictionaryDto> result = findAllParentByIds(id);
		if(result != null && result.size() > 0){
			for(DictionaryDto dto : result){
				if(StringUtils.isNotBlank(levels)){
					String dicLevel = String.valueOf(StringUtils.countMatches(dto.getDicPath(), Constants.PATH_SPLIT) -1) ;
					if(levels.indexOf(dicLevel) == -1){
						continue;
					}
				}
				value = value + join + dto.getName();
			}
			value = StringUtils.removeStart(value, join);
		}
		return value;
	}
	
}
