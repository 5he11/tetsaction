package com.frenzy.core.entity;


import com.frenzy.core.utils.yftools;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

public class PageData extends HashMap implements Map{
	
	private static final long serialVersionUID = 1L;
	
	Map map = null;
	HttpServletRequest request;
	
	public PageData(HttpServletRequest request){
		this.request = request;
		Map properties = request.getParameterMap();
		Map returnMap = new HashMap(); 
		Iterator entries = properties.entrySet().iterator(); 
		Entry entry;
		String name = "";  
		String value = "";  
		while (entries.hasNext()) {
			entry = (Entry) entries.next();
			name = (String) entry.getKey(); 
			Object valueObj = entry.getValue(); 
			if(null == valueObj){ 
				value = ""; 
			}else if(valueObj instanceof String[]){ 
				String[] values = (String[])valueObj;
				for(int i=0;i<values.length;i++){ 
					 value = values[i] + ",";
				}
				value = value.substring(0, value.length()-1); 
			}else{
				value = valueObj.toString(); 
			}
			returnMap.put(name, value); 
		}
		map = returnMap;
	}
	
	public PageData() {
		map = new HashMap();
	}
	
	@Override
	public Object get(Object key) {
		Object obj = null;
		if(map.get(key) instanceof Object[]) {
			Object[] arr = (Object[])map.get(key);
			obj = request == null ? arr:(request.getParameter((String)key) == null ? arr:arr[0]);
		} else {
			obj = map.get(key);
		}
		return obj;
	}

	public String getString(Object key) {
		String reval="";
		try {
			if(get(key)==null){
				return reval;
			}else{
				if(get(key).toString().trim().equals("null")){
					return "";
				}else{
					reval=get(key).toString().trim();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return yftools.filterEmoji(reval);
	}


	public int getInt(Object key) {
		int reval=0;
		try {
			if(get(key)==null){
				return reval;
			}else{
				new BigDecimal((String) get(key)).intValue();
//				reval=(int)get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reval;
	}

	public int getInt1(Object key) {
		int reval=0;
		try {
			if(get(key)==null){
				return (int)reval;
			}else{
				reval=(int)get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reval;
	}

	public double getDouble(Object key) {
		double reval=0.0;
		try {
			if(get(key)==null){
				return (double)reval;
			}else{
				reval=(double)get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reval;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		return map.put(key, value);
	}
	
	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return map.containsValue(value);
	}

	public Set entrySet() {
		// TODO Auto-generated method stub
		return map.entrySet();
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	public Set keySet() {
		// TODO Auto-generated method stub
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	public void putAll(Map t) {
		// TODO Auto-generated method stub
		map.putAll(t);
	}

	public int size() {
		// TODO Auto-generated method stub
		return map.size();
	}

	public Collection values() {
		// TODO Auto-generated method stub
		return map.values();
	}
	
}
