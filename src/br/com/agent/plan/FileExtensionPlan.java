package br.com.agent.plan;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

/**
 * Simple Plan to check the file extension and see if it is eligible for refactoring.
 * */
@Plan
public class FileExtensionPlan extends UtilityPlan{

	private String pathName;

	public FileExtensionPlan(String pathName) {
		this.pathName = pathName;
	}

	@PlanBody
	public boolean isFileJava() {
		boolean comecaComAbstrata = this.pathName.contains("Abstract");		
		return this.pathName.endsWith(".java")
				&& !this.pathName.toString().contains("Test")
				&& !this.pathName.contains("Strategy")
				&& !this.pathName.contains("-")
				&& !comecaComAbstrata;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully FileExtensionPlan: " + this.pathName);
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted FileExtensionPlan");
	}

}
