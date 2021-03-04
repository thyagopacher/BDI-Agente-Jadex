package br.com.agent.plan;

import br.com.agent.belief.ProprietyClass;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

/**
 * Plan to extract class information via AST
 * */
@Plan
public class ExtractorPlan extends UtilityPlan{

	private String pathName;
	private ProprietyClass object;

	public ExtractorPlan(String pathName) {
		this.pathName = pathName;
	}

	@PlanBody
	public ProprietyClass extractInformations() {
		
		return setObject(new ProprietyClass(pathName));
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully ExtractorPlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted ExtractorPlan");
	}

	public ProprietyClass getObject() {
		return object;
	}

	public ProprietyClass setObject(ProprietyClass object) {
		this.object = object;
		return object;
	}

}
