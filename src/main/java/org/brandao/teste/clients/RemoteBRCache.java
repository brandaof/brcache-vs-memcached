package org.brandao.teste.clients;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.brandao.teste.CacheClient;

public class RemoteBRCache implements CacheClient{

	private static final String PUT_HEADER = "put {name} 0 0 {len} 0\r\n";

	private static final String GET_HEADER = "get {name} 0 0\r\n";
	
	private Socket con;
	
	private BufferedInputStream reader;

	private BufferedOutputStream writer;
	
	public RemoteBRCache() throws UnknownHostException, IOException{
		this.con    = new Socket("localhost", 9090);
		this.con.setTcpNoDelay(true);
		this.reader = new BufferedInputStream(this.con.getInputStream());
		this.writer = new BufferedOutputStream(this.con.getOutputStream());
	}
	
	public void put(String name, String value) {
		try{
			byte[] dta = value.getBytes("UTF-8");
			String header = PUT_HEADER.replace("{name}", name).replace("{len}", String.valueOf(dta.length));
			
			this.writer.write(header.getBytes("UTF-8"));
			this.writer.write(dta);
			this.writer.write("\r\n".getBytes("UTF-8"));
			this.writer.flush();
			
			String r = this.readLine();
			if(r.charAt(0) == 'E'){
				throw new RuntimeException(r);
			}
		}
		catch(Throwable e){
			throw new RuntimeException(e);
		}
	}

	public String get(String name) {
		try{
			String header = GET_HEADER.replace("{name}", name);
			this.writer.write(header.getBytes("UTF-8"));
			this.writer.flush();
			
			header   = this.readLine();
			
			String result   = null;
			String[] params = header.split("\\s");
			int size        = Integer.parseInt(params[2]);
			
			if(size > 0){
				byte[] buf = new byte[size + 2];
				int l      = 0;
				int off    = 0;
				
				while(off < buf.length){
					l = this.reader.read(buf, off, buf.length - off);
					off += l;
				}
				
				result = new String(buf, 0, size);
			}
			
			String end      = this.readLine();
			
			if(!end.equals("end")){
				throw new IllegalStateException("expected end");
			}
			
			return result;
		}
		catch(Throwable e){
			throw new RuntimeException(e);
		}	
	}

	public void close(){
		try{
			this.con.close();
		}
		catch(Throwable e){
			throw new IllegalStateException(e);
		}
	}

	private String readLine() throws IOException{
		this.reader.mark(256);
		
		int c;
		int i = 0;
		while((c = this.reader.read()) != -1 && c != '\n'){
			i++;
		}
		
		if(c == '\n'){
			this.reader.reset();
			byte[] buf = new byte[i + 1];
			this.reader.read(buf, 0, buf.length);
			return new String(buf, 0, buf.length - 2);
		}
		else
			throw new IllegalStateException();
		
	}
	
}
