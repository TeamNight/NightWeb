/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import dev.teamnight.nightweb.core.entities.XmlLanguageItem;

/**
 * @author Jonas
 *
 */
public class FormattableString {

	private String string;
	
	public FormattableString(String string) {
		this.string = string;
	}
	
	public FormattableString(XmlLanguageItem item) {
		this.string = item.getValue();
	}
	
	public String format(String...replacements) {
		String formatted = this.string;
		
		int i = 0;
		for(String replacement : replacements) {
			formatted = formatted.replace("{" + i + "}", replacement);
			i++;
		}
		
		return formatted;
	}
	
	@Override
	public String toString() {
		return this.string;
	}
}
