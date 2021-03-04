package br.com.agent.plan;

import java.util.ArrayList;
import java.util.List;

import br.com.agent.belief.Candidate;
import br.com.agent.belief.ProprietyClass;
import br.com.agent.plan.designpattern.DesignPattern;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

/**
 * Plan to identify if the class is a candidate for refactoring througth some design pattern
 * aqui já se identifica as classes candidatas e já vai aplicando os mesmos.
 * */
@Plan
public class RefactoringPlan extends UtilityPlan{

	private ProprietyClass object;
	private DesignPattern designPattern = new DesignPattern();
	
	public RefactoringPlan(ProprietyClass object) {
		this.object = object;
	}

	@PlanBody
	public Candidate checkDesignPatterns() {
		List<DesignPattern> designPatternFounded = new ArrayList<DesignPattern>();
		if(!designPattern.getDesignPatterns().isEmpty()) {
			for (DesignPattern designPattern : designPattern.getDesignPatterns()) {
				String nameDesignPattern = designPattern.getClass().getSimpleName();
				if(designPattern.isApplicable(this.object)) {
					designPattern.applyMethod(this.object);
					designPatternFounded.add(designPattern);
					super.logSystem.saveContent("Design Pattern "+nameDesignPattern+" is applicable for this class...");
				}else {
					super.logSystem.saveContent("Design Pattern "+nameDesignPattern+" is not applicable...");
				}
			}
		}
		return new Candidate(object, designPatternFounded);
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully RefactoringPlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted RefactoringPlan");
	}

}
