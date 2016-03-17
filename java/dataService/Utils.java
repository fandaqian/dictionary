package com.zf.qqcy.dataService.common;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.common.collect.Sets;

import com.cea.core.mapper.CustomConvertUtils;
import com.cea.core.mapper.JsonMapper;
import com.cea.core.modules.common.DateUtil;
import com.cea.core.modules.entity.dto.SearchFilter;
import com.cea.identity.remote.dto.DictionaryDto;
import com.cea.identity.remote.dto.UserDto;
import com.zf.qqcy.dataService.common.constants.Constants;
import com.zf.qqcy.dataService.common.constants.OSEnum;
import com.zf.qqcy.dataService.common.dto.WSResult;
import com.zf.qqcy.dataService.thirdparty.BaseInfoClientUtils;

/**
 * @author zk
 *
 */
public class Utils {

	private static final int DEF_DIV_SCALE = 6;

	/**
	 * 
	 * @return
	 */
	public static String generateId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 
	 * @param orgin
	 * @return
	 */
	public static String getMD5(String orgin) {
		String result = null;
		if (StringUtils.isNotBlank(orgin)) {
			result = DigestUtils.md5Hex(orgin.getBytes());
		}
		return result;
	}

	/**
	 * 获取当前登录用户
	 * 
	 * @return
	 */
	public static UserDto getCurrentUser() {
		UserDto user = null;
		if (SecurityUtils.getSubject() != null && SecurityUtils.getSubject().getPrincipals() != null) {
			user = SecurityUtils.getSubject().getPrincipals().oneByType(UserDto.class);
		}
		return user;
	}

	/**
	 * 
	 * @param nowValue
	 * @param orgin
	 * @return
	 */
	public static String genNextValue(String nowValue, String orgin) {
		String result = null;
		if (StringUtils.isBlank(nowValue)) {
			result = Constants.LOGIN_NAME_DEFAULT_PREFIX + orgin;
		} else {
			nowValue = nowValue.replace(orgin, "");
			String before = StringUtils.substring(nowValue, 0, nowValue.length() - 1);
			String end = nowValue.substring(nowValue.length() - 1);
			if (end.toLowerCase().equals("z")) {
				end = end + "a";
			} else {
				char tmp = end.toLowerCase().charAt(0);
				end = new String(new char[] { (char) (tmp + 1) });
			}
			result = before + end + orgin;
		}
		return result;
	}

	/**
	 * 
	 * @param prefix
	 * @param nowValue
	 * @param length
	 * @return
	 */
	public static String genNextUnquieNumber(String prefix, String nowValue, int length) {
		String result = null;
		int realLength = length - prefix.length();
		nowValue = StringUtils.removeStart(nowValue, prefix);
		int value = 0;
		if (StringUtils.isBlank(nowValue)) {
			value = 1;
		} else {
			value = Integer.parseInt(nowValue) + 1;
		}
		int len = String.valueOf(value).length();
		if (len > realLength) {// 超过规定的长度，待处�?

		} else {
			result = prefix + String.format("%0" + realLength + "d", value);
		}
		return result;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static String genJsonArray(String... data) {
		String result = null;
		if (data == null) {
			result = "[]";
		} else {
			result = JsonMapper.defaultMapper().toJson(data);
		}
		return result;
	}

	/**
	 * 
	 * @param extName
	 * @return
	 */
	public static boolean isPic(String extName) {
		boolean result = false;
		if (null != extName && ! "".equals(extName)) {
			result = Constants.PIC_EXT_NAME.indexOf(extName.toUpperCase()) != -1;
		}
		return result;
	}

	/**
	 * 
	 * @param oldVal
	 * @param newVal
	 * @return
	 */
	public static Set<String> getFirstMoreThanSecond(Set<String> oldVal, Set<String> newVal) {
		Set<String> first = Sets.newHashSet(oldVal);
		Set<String> second = Sets.newHashSet(newVal);
		Set<String> removed = null;
		if (second == null || second.size() == 0) {
			removed = first;
		} else {
			if (first != null && first.size() > 0) {
				// for(String val : newVal){
				// oldVal.remove(val);
				// }
				first.removeAll(second);
				removed = first;
			}
		}
		return removed;
	}

	/**
	 * 
	 * @param dates
	 * @return
	 */
	public static Set<String> getDaysByRange(List<Date[]> dates) {
		Set<String> days = Sets.newHashSet();
		if (dates != null && dates.size() > 0) {
			for (Date[] tmp : dates) {
				days.addAll(getDaysByRange(tmp[0], tmp[1]));
			}
		}
		return days;
	}

	public static Set<String> getDaysByRange(Date begDate, Date endDate) {
		Set<String> days = Sets.newHashSet();
		Calendar begin = new GregorianCalendar();
		Calendar end = new GregorianCalendar();
		begin.setTime(begDate);
		if (endDate == null) {
			end.setTime(new Date());
		} else {
			end.setTime(endDate);
			end.add(Calendar.DAY_OF_YEAR, 1);
		}
		while (begin.before(end)) {
			days.add(DateUtil.getDateString(begin.getTime()));
			begin.add(Calendar.DAY_OF_YEAR, 1);
		}
		return days;
	}

	/**
	 * 驼峰命名�?
	 * 
	 * @param name
	 * @return
	 */
	private static String getHumpName(String name) {
		String s = name.substring(0, 1);
		String s2 = name.substring(1);
		return s.toUpperCase() + s2;
	}

	/**
	 * 设置查询大类编码
	 * 
	 * @param t
	 * @param pathCode
	 * @param level
	 * @param getKeyName
	 * @param setKeyName
	 * @return
	 */
	public static <T> void setDictionaryNameFromDto(T t, String pathCode, int level, String getKeyName,
			String setKeyName) {
		getKeyName = getHumpName(getKeyName);
		setKeyName = getHumpName(setKeyName);

		SearchFilter filter = new SearchFilter();
		filter.addFilter("LIKE_pathCode", pathCode);
		filter.addFilter("EQ_dicLevel", level);
		List<DictionaryDto> dictionaryList = BaseInfoClientUtils.findDictionaryByFilter(filter);

		Class<T> clazz = (Class<T>) t.getClass();
		try {
			Method getMethod = clazz.getDeclaredMethod("get" + getKeyName);
			String id = (String) getMethod.invoke(t);
			if (null != id)
				for (DictionaryDto dictionary : dictionaryList) {
					if (id.equals(dictionary.getId())) {
						Method setMethod = clazz.getDeclaredMethod("set" + setKeyName, String.class);
						setMethod.invoke(t, dictionary.getName());
						break;
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置查询多个大类编码
	 * 
	 * @param t
	 * @param pathCode
	 * @param level
	 * @param getKeyName
	 * @param setKeyName
	 * @return
	 */
	public static <T> void setMultiDictionaryNameFromDto(T t, String pathCode, int level, String getKeyName,
			String setKeyName) {
		getKeyName = getHumpName(getKeyName);
		setKeyName = getHumpName(setKeyName);
		
		SearchFilter filter = new SearchFilter();
		filter.addFilter("LIKE_pathCode", pathCode);
		filter.addFilter("EQ_dicLevel", level);
		List<DictionaryDto> dictionaryList = BaseInfoClientUtils.findDictionaryByFilter(filter);

		Class<T> clazz = (Class<T>) t.getClass();
		try {
			Method getMethod = clazz.getDeclaredMethod("get" + getKeyName);
			String id = (String) getMethod.invoke(t);
			if (null != id) {
				String[] ids = id.split(",");
				StringBuffer buffer = new StringBuffer("");
				for (String string : ids) {
					for (DictionaryDto dictionary : dictionaryList) {
						if (string.equals(dictionary.getId())) {
							buffer.append(dictionary.getName()).append(",");
							break;
						}
					}
				}
				if (buffer.length() > 0) {
					Method setMethod = clazz.getDeclaredMethod("set" + setKeyName, String.class);
					setMethod.invoke(t, buffer.substring(0, buffer.length() - 1));
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置查询大类编码（获取与设置KeyName�?致）
	 * 
	 * @param t
	 * @param pathCode
	 * @param keyName
	 * @return
	 */
	public static <T> void setDictionaryNameFromDto(T t, String pathCode, String keyName) {
		setDictionaryNameFromDto(t, pathCode, 3, keyName, keyName);
	}

	/**
	 * 获取大类编码Map
	 * 
	 * @param pathCode
	 * @param level
	 * @return
	 */
	public static Map<String, String> getDictionaryMap(String pathCode, int level) {
		Map<String, String> map = new HashMap<String, String>();
		List<DictionaryDto> list = BaseInfoClientUtils.findDictionaryByPathCode(pathCode, String.valueOf(level));
		if (null != list && !list.isEmpty()) {
			for (DictionaryDto dictionaryDto : list) {
				map.put(dictionaryDto.getId(), dictionaryDto.getName());
			}
		}
		return map;
	}
	
	/**
	 * 设置多个大类编码
	 * @param code
	 * @return
	 */
	public static String getMultiNameByDictionary(String code) {
		if (StringUtils.isNotBlank(code)) {
			String[] codes = code.split(",");
			StringBuffer buffer = new StringBuffer("");
			for (String string : codes) {
				buffer.append(Constants.CUSTOMER_DLBM_MAP.get(string)).append(",");
			}
			if (buffer.length() > 0) {
				return buffer.substring(0, buffer.length() - 1);
			}
		}
		return null;
	}

	/**
	 * 获取成功WSResult
	 * 
	 * @return
	 */
	public static WSResult<String> makeSuccessWSResult() {
		WSResult<String> ws = new WSResult<String>();
		ws.setMessage("操作成功");
		return ws;
	}

	/**
	 * 获取成功WSResult
	 * 
	 * @return
	 */
	public static WSResult<String> makeSuccessWSResult(String msg) {
		WSResult<String> ws = new WSResult<String>();
		ws.setMessage(msg);
		return ws;
	}

	/**
	 * 获取失败WSResult
	 * 
	 * @return
	 */
	public static WSResult<String> makeErrorWSResult() {
		WSResult<String> ws = new WSResult<String>();
		ws.setCode(WSResult.SYSTEM_ERROR);
		ws.setMessage(WSResult.SYSTEM_ERROR_MESSAGE);
		return ws;
	}

	/**
	 * 获取失败WSResult
	 * 
	 * @param msg
	 * @return
	 */
	public static WSResult<String> makeErrorWSResult(String msg) {
		WSResult<String> ws = new WSResult<String>();
		ws.setCode(WSResult.SYSTEM_ERROR);
		ws.setMessage(msg);
		return ws;
	}

	/**
	 * 获取操作系统类型
	 * 
	 * @return
	 */
	public static OSEnum getOperationSystem() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("linux") != -1) {
			return OSEnum.LINUX;
		} else if (osName.toLowerCase().indexOf("windows") != -1) {
			return OSEnum.WINDOWS;
		}
		return null;
	}
	
	/**
	 * 获取 file:///Z: 形式文件
	 * @param location
	 * @return
	 */
	public static File getFile(String location) {
		OSEnum os = getOperationSystem();
		if (os.equals(OSEnum.WINDOWS)) {
			try {
				return new File(new URI(location));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (os.equals(OSEnum.LINUX)) {
			return new File(location);
		}
		return null;
	}

	/**
	 * 获取两个时间的相隔天数
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static long getDays(Date begin, Date end) {
		long day = 0;
		day = (end.getTime() - begin.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}

	/**
	 * 两个double类型数据相加并四舍五入保留小数点后两位
	 * 
	 * @param ze
	 * @param yf
	 * @return
	 */
	public static Double add(Double double1, Double double2) {
		Double value = double1 + double2;
		BigDecimal b = new BigDecimal(value);
		Double returnValue = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return returnValue;
	}

	/**
	 * 两个double类型数据相减并四舍五入保留小数点后两位
	 * 
	 * @param ze
	 * @param yf
	 * @return
	 */
	public static Double sub(Double double1, Double double2) {
		Double value = double1 - double2;
		BigDecimal b = new BigDecimal(value);
		Double returnValue = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return returnValue;
	}

	/**
	 * 两个double类型数据相除
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Double div(Double v1, Double v2) {
		BigDecimal b1 = new BigDecimal(v1.toString());
		BigDecimal b2 = new BigDecimal(v2.toString());
		return b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 计算页数
	 * @param count
	 * @param pageSize
	 * @return
	 */
	public static int calculatePageSize(int count,int pageSize){
		int pageCount=0;
		if(count % pageSize == 0){
			pageCount = count/pageSize;
		}else{
			pageCount = count/pageSize+1;
		}
		return pageCount;
	}
	
	/**
	 * 两个double类型数据相减并四舍五入保留小数点后0位
	 * 
	 * @param ze
	 * @param yf
	 * @return
	 */
	public static Double subInt(Double double1, Double double2) {
		Double value = double1 - double2;
		BigDecimal b = new BigDecimal(value);
		Double returnValue = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		return returnValue;
	}
}
