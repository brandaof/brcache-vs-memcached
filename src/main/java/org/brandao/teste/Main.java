package org.brandao.teste;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

public class Main {

	private static DecimalFormat format = new DecimalFormat("#0.0000");

	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException{
		
		OperationCache operationCache = new OperationCache();
		String[] cmds = new String[]{"put", "get"};
		
		int startDataLength = 1024;
		int clients    = 300;
		int clientsInc = 50;
		
		System.out.println(
				"Operação\t" + 
				"Tempo médio de uma operação (nano)\t" + 
				"Operações por segundo\t" + 
				"Tamanho\t" +
			
				"Clientes\t" +
				"Tempo total (nano)\t" +
				"Total de operações");
		
		for(String cmd: cmds){
			System.out.println("--------------------------------------------------------------------------------------");
			
			for(int c=clientsInc;c<=clients;c += clientsInc){
				int dataLength = startDataLength;
				for(int i=0;i<1;i++){//5
					TestResult result = null;
					double avg = -1;
					
					for(int j=0;j<3;j++){
						TestResult localResult = operationCache.test(dataLength, c, args[0], cmd);
						double localAvg = localResult.getTime() / localResult.getTries();
						
						if(avg == -1 || avg > localAvg){
							avg = localAvg;
							result = localResult;
						}
					}
					
					System.out.println(
							result.getOperationName() + "\t" + 
							format.format(avg) + "\t" + 
							format.format(1000000000 / avg) + "\t" + 
							dataLength + "\t" +
							c + "\t" +
							result.getTime() + "\t" +
							result.getTries());
					dataLength = dataLength*2;
				}
			}
		}
		System.exit(0);
	}
}
