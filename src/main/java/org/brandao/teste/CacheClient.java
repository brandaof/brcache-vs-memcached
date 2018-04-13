package org.brandao.teste;

public interface CacheClient {

	void put(String name, String value);

	String get(String name);

	void close();
	
}
