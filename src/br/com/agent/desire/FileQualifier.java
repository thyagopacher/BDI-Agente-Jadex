package br.com.agent.desire;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;


/**
 * Para qualificar a classe aberta para verificar se o mesmo é com extensão .java
 * */
@Goal
public class FileQualifier {

	@GoalParameter
	public String pathName;

	@GoalResult
	public boolean isJava;
	
	public FileQualifier(String pathName) {
		this.pathName = pathName;
	}
	

	
}
