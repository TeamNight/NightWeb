/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.html.HtmlChangeListener;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import dev.teamnight.nightweb.core.entities.User;

/**
 * @author Jonas
 *
 */
public final class StringUtil {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final Random random = new Random();
	private static PolicyFactory htmlPolicy = new HtmlPolicyBuilder()
			.allowElements("p", "span", "div", "a", "strong", "b", "i", "k", "br", "code", "table", "thead", "tr", "td", "th", "ul", "li", "ol", "font", "img")
			.allowAttributes("style").onElements("font")
			.allowAttributes("href").onElements("a")
			.allowAttributes("style").globally()
			.allowAttributes("class").globally()
			.allowAttributes("src").onElements("img")
			.toFactory();

	private StringUtil() {}
	
	public static String getRandomString(int length) {
		return random.ints(48, 123) //Alphanumeric ASCII chars
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) //Only alphanumeric, there are some symbols in between
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint,  StringBuilder::append)
				.toString();
	}
	
	public static String sanitizeHTML(String html, User user) {
		return htmlPolicy.sanitize(html, new HtmlChangeListener<User>() {

			@Override
			public void discardedTag(User context, String elementName) {
				StringUtil.LOGGER.warn("User " + user.getId() + "#" + user.getUsername() + " tried to use unallowed tag: " + elementName);
			}

			@Override
			public void discardedAttributes(User context, String tagName, String... attributeNames) {
				StringUtil.LOGGER.warn("User " + user.getId() + "#" + user.getUsername() + " tried to use unallowed attributes on tag \"" + tagName + "\": " + String.join(" ", attributeNames));
			}
			
		}, user);
	}
	
	public static String sanitizeHTML(String html) {
		return htmlPolicy.sanitize(html);
	}
	
}
