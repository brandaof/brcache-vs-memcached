package org.brandao.teste;

public class TestResult {

	private double time;
	
	private double tries;
	
	private String operationName;
	
	private int clients;
	
	private int dataLength;

	public TestResult(double time, long tries, int clients, String operationName,
			int dataLength) {
		this.time 			= time;
		this.tries 			= tries;
		this.operationName 	= operationName;
		this.dataLength 	= dataLength;
		this.clients 		= clients;
	}

	public int getClients() {
		return clients;
	}

	public void setClients(int clients) {
		this.clients = clients;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getTries() {
		return tries;
	}

	public void setTries(double tries) {
		this.tries = tries;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	
}
