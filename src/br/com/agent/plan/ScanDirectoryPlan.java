package br.com.agent.plan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

@Plan
public class ScanDirectoryPlan extends UtilityPlan{

	private String pathName;
	private List<File> filesInProject = new ArrayList<File>();
	
	public ScanDirectoryPlan(String pathName) {
		this.pathName = pathName;
	}

	public File scanProject(String path) {
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if(file.isDirectory()) {
				this.scanProject(file.getAbsolutePath());
			}else if(!file.isDirectory()) {
				filesInProject.add(file);
			}
		}
		return null;
	}	
	
	@PlanBody
	public List<File> filesDirectory() {
			
		this.scanProject(this.pathName);
		return filesInProject;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully ScanDirectoryPlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted ScanDirectoryPlan");
	}

}
