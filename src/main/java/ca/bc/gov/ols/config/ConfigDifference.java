package ca.bc.gov.ols.config;

public class ConfigDifference<T> {
	private T current;
	private T other;
	
	public ConfigDifference(T current, T other) {
		this.current = current;
		this.other = other;
	}
	
	public T getCurrent() {
		return current;
	}
	
	public T getOther() {
		return other;
	}
}
