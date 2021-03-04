package br.com.agent.desire;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;


/**
 * Para qualificar a classe aberta para verificar se o mesmo é com extensão .java
 * */
@Goal
public class DirectoryQualifier {

	@GoalParameter
	public String pathName;

	@GoalResult
	public boolean exists;
	
	public DirectoryQualifier(String pathName) {
		this.pathName = pathName;
	}
	

	
}
