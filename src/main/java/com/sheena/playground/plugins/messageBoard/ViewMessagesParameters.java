package com.sheena.playground.plugins.messageBoard;

public class ViewMessagesParameters {
	
	private final int defaultPage = 0;
	private final int defaultSize = 10;
	
	private int page;
	private int size;
	
	public ViewMessagesParameters() {
		page = defaultPage;
		size = defaultSize;
	}

	public ViewMessagesParameters(int page, int size) {
		super();
		this.page = page;
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "ViewMessagesParameters [defaultPage=" + defaultPage + ", defaultSize=" + defaultSize + ", page=" + page
				+ ", size=" + size + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + defaultPage;
		result = prime * result + defaultSize;
		result = prime * result + page;
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewMessagesParameters other = (ViewMessagesParameters) obj;
		if (defaultPage != other.defaultPage)
			return false;
		if (defaultSize != other.defaultSize)
			return false;
		if (page != other.page)
			return false;
		if (size != other.size)
			return false;
		return true;
	}
}
