package br.com.agent.plan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.mauricioaniche.ck.CKNumber;

import br.com.agent.belief.MetricsProject;
import br.com.agent.belief.QualityAttributeClass;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

/**
 * calculo é feito tendo em base PM = ((original x 100) / refatorado) - 100
 * */
@Plan
public class CheckImprovementPlan extends UtilityPlan{

	private MetricsProject metricsProject;

	public CheckImprovementPlan(MetricsProject metricsProject) {
		this.metricsProject = metricsProject;
	}

	@PlanBody
	public List<QualityAttributeClass> improvementsClass() {
		List<QualityAttributeClass> improvements = new ArrayList<>();
		
		for (CKNumber metricBefore : metricsProject.getMetricsByClassBeforeRefactoring()) {
			CKNumber metricAfter = metricsProject.getMetricsByClassAfterRefactoring().stream().filter(l -> l.getClassName().equals(metricBefore.getClassName())).findAny().get();

			/*atributos para medir usabilidade*/
			int resultDit = metricBefore.getDit();
			/**verificando se realmente houve alguma alteração com a refatoração*/
			if(metricAfter.getDit() != metricBefore.getDit()) {
				resultDit = ((metricAfter.getDit() * 100) / metricBefore.getDit()) - 100;
			}
			
			int resultLoc = metricBefore.getLoc();
			if(metricAfter.getLoc() != metricBefore.getLoc()) {
				resultLoc = ((metricAfter.getLoc() * 100) / metricBefore.getLoc()) - 100;
			}
			
			int resultCC = metricBefore.getWmc();
			if(metricAfter.getWmc() != metricBefore.getWmc()) {
				resultCC = ((metricAfter.getWmc() * 100) / metricBefore.getWmc()) - 100;
			}
			
			int resultMaintenance = (resultDit + resultLoc + resultCC) / 3;
			int resultReability = (resultCC + resultLoc) / 2;
			int resultReusability = (resultDit + resultLoc) / 2;

			improvements.add(new QualityAttributeClass(metricBefore.getClassName(), metricBefore.getFile(), resultMaintenance, resultReability, resultReusability));

		}
		return improvements;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully CheckImprovementPlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted CheckImprovementPlan");
	}

}
