package br.com.agent.desire;

import br.com.agent.belief.ProprietyClass;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

/**
 * extrai as informações base via AST referente a classe lida e retorna ao agente
 * */
@Goal
public class Extractor {

	@GoalParameter
	public String pathName;

	@GoalResult
	public ProprietyClass object;
	
	public Extractor(String pathName) {
		this.pathName = pathName;
	}

}
