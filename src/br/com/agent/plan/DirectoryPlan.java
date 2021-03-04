package br.com.agent.plan;

import java.io.File;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

@Plan
public class DirectoryPlan extends UtilityPlan{

	private String pathName;

	public DirectoryPlan(String pathName) {
		this.pathName = pathName;
	}

	@PlanBody
	public boolean isFileJava() {
		return new File(this.pathName).exists();
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully DirectoryPlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted DirectoryPlan");
	}

}
