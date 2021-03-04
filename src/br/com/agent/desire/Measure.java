package br.com.agent.desire;

import java.util.Collection;
import java.util.List;

import com.github.mauricioaniche.ck.CKNumber;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

/**
 * Avaliar o código-fonte 
 * */
@Goal
public class Measure {

	@GoalParameter
	public String directory;

	@GoalResult
	public Collection<CKNumber> metrics;
	
	public Measure(String directory){
		this.directory = directory;
	}
	
}
