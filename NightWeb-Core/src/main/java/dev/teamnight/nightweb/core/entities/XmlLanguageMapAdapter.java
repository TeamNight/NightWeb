/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Jonas
 *
 */
public class XmlLanguageMapAdapter extends XmlAdapter<XmlLanguageItem[], Map<String, XmlLanguageItem>> {

	@Override
	public Map<String, XmlLanguageItem> unmarshal(XmlLanguageItem[] array) throws Exception {
		Map<String, XmlLanguageItem> map = new HashMap<String, XmlLanguageItem>();
		
		for(XmlLanguageItem item : array) {
			map.put(item.getKey(), item);
		}
		
		return map;
	}

	@Override
	public XmlLanguageItem[] marshal(Map<String, XmlLanguageItem> map) throws Exception {
		XmlLanguageItem[] array = new XmlLanguageItem[map.size()];
		
		int i = 0;
		for(Map.Entry<String, XmlLanguageItem> entry : map.entrySet()) {
			array[i++] = entry.getValue();
		}
		
		return array;
	}

}
