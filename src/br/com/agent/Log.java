package br.com.agent;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	private Date hoje = new Date();
	private SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
	private String hojeTxtAmericano = formatador.format(hoje);	
			
	public Log() {
		
	}
	
	public void saveContent(String content) {
		try {
			FileWriter fileWriter1 = new FileWriter("logAgent-" + hojeTxtAmericano + ".txt", true);
			String msg = hoje.getHours() + ":"+ hoje.getMinutes() + ":" + hoje.getSeconds() + " -> " + content;
			System.out.println(msg);
			fileWriter1.write(msg + "\n");
			fileWriter1.flush();
			fileWriter1.close();
		} catch (IOException ex) {
			throw new IllegalStateException("Error caused by: " + ex.getMessage());
		}
	}
}
