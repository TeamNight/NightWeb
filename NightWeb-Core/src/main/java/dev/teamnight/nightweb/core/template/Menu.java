/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jonas
 *
 */
public class Menu {

	private String name;
	private String activeMenu = "home";
	private Map<Integer, Menu.Item> leftItems = new TreeMap<Integer, Menu.Item>();
	private Map<Integer, Menu.Item> rightItems = new TreeMap<Integer, Menu.Item>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the activeMenu
	 */
	public String getActiveMenu() {
		return activeMenu;
	}
	
	/**
	 * @return the left items
	 */
	public Map<Integer, Menu.Item> getLeftItems() {
		return leftItems;
	}
	
	public void addLeftItem(int position, Item item) {
		this.leftItems.put(position, item);
	}
	
	/**
	 * @return the right items
	 */
	public Map<Integer, Menu.Item> getRightItems() {
		return rightItems;
	}
	
	public void addRightItem(int position, Item item) {
		this.rightItems.put(position, item);
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param activeMenu the activeMenu to set
	 */
	public void setActiveMenu(String activeMenu) {
		this.activeMenu = activeMenu;
	}
	
	public class Item {
		private String name;
		private String link;
		
		public Item() {
			this("", "");
		}
		
		public Item(String name, String link) {
			this.name = name;
			this.link = link;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the link
		 */
		public String getLink() {
			return link;
		}
		/**
		 * @return the type for freemarker
		 */
		public String getType() {
			return "item";
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @param link the link to set
		 */
		public void setLink(String link) {
			this.link = link;
		}
	}
	
	public class DropdownItem extends Item {
		public final static String type = "dropdown";
		
		private List<Item> dropdownItems = new ArrayList<Item>();
		
		/**
		 * @return the type for freemarker
		 */
		@Override
		public String getType() {
			return "dropdown";
		}
		
		/**
		 * @return the dropdownItems
		 */
		public List<Item> getDropdownItems() {
			return dropdownItems;
		}
		
		public void addDropdownItem(Item item) {
			this.dropdownItems.add(item);
		}
		
	}
	
	public class Spacer extends Item {
		/**
		 * @return the type for freemarker
		 */
		@Override
		public String getType() {
			return "spacer";
		}
	}
	
	public class Button extends Item {
		/**
		 * @return the type for freemarker
		 */
		@Override
		public String getType() {
			return "button";
		}
	}
	
	public class CustomHTMLItem extends Item {
		
		private String html;
		
		public CustomHTMLItem(String html) {
			this.html = html;
		}
		
		/**
		 * @return the html
		 */
		public String getHtml() {
			return html;
		}
		
		/**
		 * @return the type for freemarker
		 */
		@Override
		public String getType() {
			return "custom";
		}
	}
	
}
