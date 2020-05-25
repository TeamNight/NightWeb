/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jonas
 *
 */
public class NodeList<T> {

	private List<Node> topNodes;
	
	public NodeList() {
		this.topNodes = new ArrayList<Node>();
	}
	
	public NodeList(T[] topNodes) {
		this();
		
		for(T nodeEntry : topNodes) {
			Node node = new Node(nodeEntry);
			
			this.topNodes.add(node);
		}
	}
	
	public NodeList(Collection<T> topNodes) {
		this();
		
		for(T nodeEntry : topNodes) {
			Node node = new Node(nodeEntry);
			
			this.topNodes.add(node);
		}
	}
	
	/**
	 * @return the topNodes
	 */
	public List<Node> getTopNodes() {
		return topNodes;
	}
	
	public void addTopNode(T value) {
		Node node = new Node(value);
		
		this.topNodes.add(node);
	}
	
	public void removeTopNode(T value) {
		Node node = this.topNodes.stream().filter(obj -> obj.getValue().equals(value)).findFirst().orElse(null);
		
		if(node != null) {
			this.topNodes.remove(node);
		}
	}
	
	public void clear() {
		this.topNodes.clear();
	}
	
	public class Node {
		
		private T value;
		private Node parentNode;
		private List<Node> subNodes;
		
		public Node(T value) {
			this(value, null);
		}
		
		public Node(T value, Node parentNode) {
			this.value = value;
			this.parentNode = parentNode;
		}
		
		public Node(T value, Node parentNode, Collection<T> subNodes) {
			this(value, parentNode);
			this.subNodes = new ArrayList<Node>();
			
			subNodes.forEach(val -> {
				Node node = new Node(val, this);
				this.subNodes.add(node);
			});
		}
		
		/**
		 * @return the value
		 */
		public T getValue() {
			return value;
		}
		
		/**
		 * @param value the value to set
		 */
		public void setValue(T value) {
			this.value = value;
		}
		
		/**
		 * @return the parentNode
		 */
		public Node getParentNode() {
			return parentNode;
		}
		
		/**
		 * @param parentNode the parentNode to set
		 */
		public void setParentNode(Node parentNode) {
			this.parentNode = parentNode;
		}
		
		/**
		 * @return the subNodes
		 */
		public List<Node> getSubNodes() {
			return subNodes;
		}
		
		public boolean hasSubNodes() {
			return this.subNodes.size() > 0;
		}
		
		public void addNode(T value) {
			Node node = new Node(value, this);
			
			this.subNodes.add(node);
		}
		
		public void removeNode(T value) {
			Node node = this.subNodes.stream().filter(obj -> obj.getValue().equals(value)).findFirst().orElse(null);
			
			if(node != null) {
				this.subNodes.remove(node);
			}
		}
	}
}
