package br.com.agent.desire;

import java.util.List;

import br.com.agent.belief.QualityAttributeClass;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

@Goal
public class ShowResults {

	@GoalParameter
	public List<QualityAttributeClass> improvements;

	@GoalResult
	public boolean isShowed;
	
	public ShowResults(List<QualityAttributeClass> improvements){
		this.improvements = improvements;
	}
}
