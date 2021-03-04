package br.com.agent.plan;

import java.util.Collection;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

@Plan
public class MeasurePlan extends UtilityPlan{

	private String pathName;
	private CK ckMetrics = new CK();
	private CKReport ckReport;
	
	public MeasurePlan(String pathName) {
		this.pathName = pathName;
	}

	@PlanBody
	public Collection<CKNumber> checkMetrics() {
		try {
			this.ckReport = ckMetrics.calculate(pathName);			
			return ckReport.all();
		}catch(Exception ex) {
			System.out.println("Erro causado por in checkmetrics: " + ex.getLocalizedMessage());
		}
		return null;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully MeasurePlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted MeasurePlan");
	}
	
	public int analyzesCyclomaticComplexity() {
		return ckReport.all().stream().mapToInt(CKNumber::getWmc).sum();
	}
	
	public int analyzesDepthOfInheritanceTree() {
		return ckReport.all().stream().mapToInt(CKNumber::getDit).sum();
	}	

	public int analyzesLoc() {
		return ckReport.all().stream().mapToInt(CKNumber::getLoc).sum();
	}

	public CKReport getCkReport() {
		return ckReport;
	}

	public void setCkReport(CKReport ckReport) {
		this.ckReport = ckReport;
	}


}
