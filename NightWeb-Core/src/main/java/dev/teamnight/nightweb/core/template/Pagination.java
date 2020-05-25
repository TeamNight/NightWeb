/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

/**
 * Pagination Helper Class
 * @author Jonas
 */
public class Pagination {

	private long maxPages, currentPage;
	
	public Pagination(long count, long perSite) {
		this.maxPages = count / perSite;
		
		if(count % perSite != 0) {
			this.maxPages++;
		}
		
		if(this.maxPages == 0L) {
			this.maxPages = 1L;
		}
	}
	
	/**
	 * @param currentPage the currentPage to set
	 */
	public Pagination currentPage(long currentPage) {
		this.currentPage = currentPage;
		
		return this;
	}
	
	/**
	 * @return the currentPage
	 */
	public long currentPage() {
		return currentPage;
	}
	
	/**
	 * Returns the next page and increments currentPage by one
	 * @return
	 */
	public long nextPageAndIncrement() {
		long nextPage = this.nextPage();
		
		if(nextPage != -1L)
			this.currentPage++;
		
		return nextPage;
	}
	
	/**
	 * Returns the next page or -1L if there is no more page
	 * @return
	 */
	public long nextPage() {
		if(this.maxPages > this.currentPage) {
			return this.currentPage + 1;
		} else {
			return -1L;
		}
	}
	
	/**
	 * Returns the last page
	 * @return
	 */
	public long lastPage() {
		if(this.currentPage > 1) {
			return this.currentPage - 1;
		} else {
			return -1L;
		}
	}
	
	public boolean hasNextPages() {
		return this.maxPages > this.currentPage;
	}
	
	public boolean hasBackPages() {
		return this.currentPage > 1;
	}
}
