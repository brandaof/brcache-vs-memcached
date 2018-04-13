package org.brandao.teste;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.brandao.teste.clients.Memcached;
import org.brandao.teste.clients.RemoteBRCache;

public class OperationCache {

	private double putTotal;

	private double getTotal;
	
	private int triesTotal;
	
	private int tries = 50;
	
	public TestResult test(int dataLength, int clients, String clientType, String operationType) throws InterruptedException{
		
		if("put".equals(operationType)){
			return this.testPut(dataLength, clients, clientType);
		}
		else
		if("get".equals(operationType)){
			return this.testGet(dataLength, clients, clientType);
		}
		else{
			throw new IllegalStateException("operation: " + operationType);
		}
		
	}
	
	public TestResult testPut(int dataLength, int clients, final String clientType) throws InterruptedException{

		putTotal          = 0;
		triesTotal        = 0;
		byte[] dta        = new byte[dataLength];
		
		for(int i=0;i<dta.length; i++){
			dta[i] = (byte)('A' + (i % 4));
		}
		
		final String data = new String(dta);
		List<Callable<Object>> tasks  = new ArrayList<Callable<Object>>();
		
		for(int i=0;i<clients;i++){
			final int clientId = i;
			Callable<Object> call = new Callable<Object>(){

				public Object call() throws Exception{
					CacheClient client = null;
					try{
						double time = 0;
						client = createClient(clientType);
						
						for(int k=0;k<tries;k++){
							String key = clientId + ":" + k;
							double start = System.nanoTime();
							client.put(key, data);
							double end = System.nanoTime();
							time       = end - start;
						}
						
						synchronized(OperationCache.class){
							OperationCache.this.putTotal   += time;
							OperationCache.this.triesTotal += tries;
						}
						
						return null;
					}
					catch(Throwable e){
						e.printStackTrace();
						return null;
					}
					finally{
						if(client != null){
							client.close();
						}
					}
				}
				
			};
			
			tasks.add(call);
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(clients);
		executorService.invokeAll(tasks);
		executorService.shutdown();
		return new TestResult(putTotal, triesTotal, clients, "put", data.length());
	}

	public TestResult testGet(int dataLength, int clients, final String clientType) throws InterruptedException{

		getTotal          = 0;
		triesTotal        = 0;
		byte[] dta        = new byte[dataLength];
		
		final String data = new String(dta);
		List<Callable<Object>> tasks  = new ArrayList<Callable<Object>>();
		
		for(int i=0;i<clients;i++){
			final int clientId = i;
			Callable<Object> call = new Callable<Object>(){

				public Object call() throws Exception{
					CacheClient client = null;
					try{
						double time = 0;
						client = createClient(clientType);
						
						for(int k=0;k<tries;k++){
							String key = clientId + ":" + k;
							double start = System.nanoTime();
							client.get(key);
							double end = System.nanoTime();
							time       = end - start;
						}
						
						synchronized(OperationCache.class){
							OperationCache.this.getTotal   += time;
							OperationCache.this.triesTotal += tries;
						}
						
						return null;
					}
					catch(Throwable e){
						e.printStackTrace();
						return null;
					}
					finally{
						if(client != null){
							client.close();
						}
					}
				}
				
			};
			
			tasks.add(call);
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(clients);
		executorService.invokeAll(tasks);
		executorService.shutdown();
		return new TestResult(getTotal, triesTotal, clients, "get", data.length());
	}
	
	private CacheClient createClient(String name) 
			throws UnknownHostException, IOException{
		
		if(name.equals("memcached")){
			return new Memcached();
		}
		else{
			return new RemoteBRCache();
		}
		
	}
	
}
