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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "menus")
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private int id;
	
	@Column(nullable = false, unique = true)
	private String identifier;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("order ASC")
	private List<MenuItem> nodes = new ArrayList<MenuItem>();
	
	@ManyToOne(optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "moduleId", nullable = false)
	private ModuleData module;

	protected Menu() {}
	
	public Menu(String identifier, ModuleData module) {
		this.identifier = identifier;
		this.module = module;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the nodes
	 */
	public List<MenuItem> getNodes() {
		return Collections.unmodifiableList(nodes);
	}
	
	/**
	 * @param identifier
	 * @return the node
	 */
	public MenuItem getNode(String identifier) {
		return this.nodes.stream().filter(item -> item.getIdentifier().equalsIgnoreCase(identifier)).findFirst().orElse(null);
	}

	/**
	 * @return the module
	 */
	public ModuleData getModule() {
		return module;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(List<MenuItem> nodes) {
		this.nodes = nodes;
	}
	
	
	public void addNode(MenuItem node) {
		MenuItem item = this.nodes.stream()
				.filter(ch -> node.getId() != 0)
				.filter(ch -> ch.getId() == node.getId())
				.findAny()
				.orElse(null);
		
		MenuItem itemIdent = this.nodes.stream()
				.filter(ch -> ch.getIdentifier().equalsIgnoreCase(node.getIdentifier()))
				.findAny()
				.orElse(null);
		
		if(node.getServletClass() == null && node.getCustomURL() == null) {
			throw new IllegalArgumentException("Custom URL or Servlet Class has to be set");
		}
		
		if(!this.nodes.contains(node) && item == null && itemIdent == null) {
			this.nodes.add(node);
		}
	}
	
	public void removeNode(MenuItem node) {
		this.nodes.remove(node);
	}
	
	public void removeNode(int id) {
		this.nodes.removeIf(menuItem -> menuItem.getId() == id);
	}
	
	public void removeNode(String identifier) {
		this.nodes.removeIf(menuItem -> menuItem.getIdentifier().equalsIgnoreCase(identifier));
	}
}
