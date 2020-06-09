/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.servlet.http.HttpServlet;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "menu_items")
public class MenuItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private int id;
	
	@Column(name="itemType", nullable = false)
	@Enumerated(EnumType.STRING)
	private MenuItem.Type type = Type.LINK;
	
	@Column(nullable = false)
	private String identifier;
	
	@Column
	private String title;
	
	@Column
	private String servletClass;
	
	@Column
	private String customURL;
	
	@Column(name = "showOrder", nullable = false)
	private int order = 0;
	
	@Column(nullable = false)
	private boolean disabled = false;
	
	@Column
	private String permission;
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "parentId")
	private MenuItem parentItem;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parentItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MenuItem> children = new ArrayList<MenuItem>();
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "menuId")
	private Menu menu;
	
	private transient String resolvedURL;
	
	protected MenuItem() {}
	
	public MenuItem(Menu menu, String identifier, Class<? extends HttpServlet> servletClass) {
		this(menu, Type.LINK, identifier, null, servletClass.getName(), null, 0, null);
	}
	
	public MenuItem(Menu menu, String identifier, String url) {
		this(menu, Type.LINK, identifier, null, null, url, 0, null);
	}
	
	public MenuItem(Menu menu, MenuItem.Type type, String identifier, String title, String servletClass, String customURL, int order, String permission) {
		this.menu = menu;
		this.type = type;
		this.identifier= identifier;
		this.title = title;
		this.servletClass = servletClass;
		this.customURL = customURL;
		this.order = order;
		this.permission = permission;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public MenuItem.Type getType() {
		return type;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the servletClass
	 */
	public String getServletClass() {
		return servletClass;
	}

	/**
	 * @return the customURL
	 */
	public String getCustomURL() {
		return customURL;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * @return the parentItem
	 */
	public MenuItem getParentItem() {
		return parentItem;
	}

	/**
	 * @return the children
	 */
	public List<MenuItem> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * @return the menu
	 */
	public Menu getMenu() {
		if(this.menu == null) {
			if(this.parentItem != null) {
				return this.parentItem.getMenu();
			} else {
				return null;
			}
		} else {
			return this.menu;
		}
	}
	
	/**
	 * @return the resolvedURL
	 */
	public String getResolvedURL() {
		return resolvedURL;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(MenuItem.Type type) {
		this.type = type;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param servletClass the servletClass to set
	 */
	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	/**
	 * @param customURL the customURL to set
	 */
	public void setCustomURL(String customURL) {
		this.customURL = customURL;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * @param parentItem the parentItem to set
	 */
	public void setParentItem(MenuItem parentItem) {
		this.parentItem = parentItem;
	}
	
	public void addChild(MenuItem child) {
		MenuItem item = this.children.stream()
				.filter(ch -> child.getId() != 0)
				.filter(ch -> ch.getId() == child.getId())
				.findAny()
				.orElse(null);
		
		MenuItem itemIdent = this.children.stream()
				.filter(ch -> ch.getIdentifier().equalsIgnoreCase(child.getIdentifier()))
				.findAny()
				.orElse(null);
		
		if(child.getServletClass() == null && child.getCustomURL() == null) {
			throw new IllegalArgumentException("Custom URL or Servlet Class has to be set");
		}
		
		if(!this.children.contains(child) && item == null && itemIdent == null) {
			child.setParentItem(this);
			this.children.add(child);
		}
	}
	
	public void removeChild(MenuItem child) {
		this.children.remove(child);
	}
	
	public void removeChild(int id) {
		this.children.removeIf(menuItem -> menuItem.getId() == id);
	}
	
	public void removeChild(String identifier) {
		this.children.removeIf(menuItem -> menuItem.getIdentifier().equalsIgnoreCase(identifier));
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<MenuItem> children) {
		this.children = children;
	}

	/**
	 * @param menu the menu to set
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	/**
	 * @param resolvedURL the resolvedURL to set
	 */
	public void setResolvedURL(String resolvedURL) {
		this.resolvedURL = resolvedURL;
	}

	public enum Type {
		BUTTON,
		LINK,
		DROPDOWN,
		DROPDOWN_SPACER,
		CUSTOM_HTML;
	}
	
}
