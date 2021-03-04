package br.com.agent.desire;

import br.com.agent.belief.Candidate;
import br.com.agent.belief.ProprietyClass;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

@Goal
public class IdentifyCandidates {

	@GoalParameter
	public ProprietyClass object;

	@GoalResult
	public Candidate candidate;
	
	public IdentifyCandidates(ProprietyClass object) {
		this.object = object;
	}
	
}
