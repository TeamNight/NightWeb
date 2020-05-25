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
public class NodeMap<K, V> {

	private List<Node> topNodes;
	
	public NodeMap() {
		this.topNodes = new ArrayList<Node>();
	}
	
	public NodeMap(K[] topNodes) {
		this();
		
		for(K nodeEntry : topNodes) {
			Node node = new Node(nodeEntry);
			
			this.topNodes.add(node);
		}
	}
	
	public NodeMap(Collection<K> topNodes) {
		this();
		
		for(K nodeEntry : topNodes) {
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
	
	public void addTopNode(K key) {
		Node node = new Node(key);
		
		this.topNodes.add(node);
	}
	
	public void removeTopNode(K key) {
		Node node = this.topNodes.stream().filter(obj -> obj.getKey().equals(key)).findFirst().orElse(null);
		
		if(node != null) {
			this.topNodes.remove(node);
		}
	}
	
	public int sizeTopNodes() {
		return this.topNodes.size();
	}
	
	public int size() {
		return this.calculateSize(this.topNodes);
	}
	
	private int calculateSize(List<Node> nodes) {
		int size = 0;
		
		for(Node node : nodes) {
			size++;
			
			if(node.hasSubNodes()) {
				int nodeSize = this.calculateSize(node.getSubNodes());
				size = size + nodeSize;
			}
		}
		
		return size;
	}
	
	public boolean containsTopLevelKey(K key) {
		for(Node node : this.topNodes) {
			if(node.getKey().equals(key)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void clear() {
		this.topNodes.clear();
	}
	
	public class Node {
		
		private K key;
		private transient Node parentNode;
		private List<Node> subNodes;
		
		private List<V> values;
		
		public Node(K value) {
			this(value, null);
		}
		
		public Node(K value, Node parentNode) {
			this.key = value;
			this.parentNode = parentNode;
			this.subNodes = new ArrayList<Node>();
			this.values = new ArrayList<V>();
		}
		
		public Node(K value, Node parentNode, Collection<K> subNodes) {
			this(value, parentNode);
			
			subNodes.forEach(val -> {
				Node node = new Node(val, this);
				this.subNodes.add(node);
			});
		}
		
		/**
		 * @return the value
		 */
		public K getKey() {
			return key;
		}
		
		/**
		 * @param value the value to set
		 */
		public void setKey(K key) {
			this.key = key;
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
		
		public void addNode(K key) {
			Node node = new Node(key, this);
			
			this.subNodes.add(node);
		}
		
		public void removeNode(K key) {
			Node node = this.subNodes.stream().filter(obj -> obj.getKey().equals(key)).findFirst().orElse(null);
			
			if(node != null) {
				this.subNodes.remove(node);
			}
		}
		
		/**
		 * @return the values
		 */
		public List<V> getValues() {
			return values;
		}
		
		public boolean hasValues() {
			return this.values.size() > 0;
		}
		
		public void addValue(V value) {
			this.values.add(value);
		}
		
		public void removeValue(V value) {
			this.values.remove(value);
		}
	}
}
