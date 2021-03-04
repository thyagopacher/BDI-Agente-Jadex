package br.com.agent.desire;

import java.util.Collection;
import java.util.List;

import com.github.mauricioaniche.ck.CKNumber;

import br.com.agent.belief.MetricsProject;
import br.com.agent.belief.QualityAttributeClass;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

@Goal
public class CheckImprovement {

	@GoalParameter
	public MetricsProject metricsProject;

	@GoalResult
	public List<QualityAttributeClass> improvements;
	
	public CheckImprovement(MetricsProject metricsProject){
		this.metricsProject = metricsProject;
	}
}
