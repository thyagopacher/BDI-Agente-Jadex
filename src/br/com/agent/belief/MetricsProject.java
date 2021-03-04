package br.com.agent.belief;

import java.util.Collection;

import com.github.mauricioaniche.ck.CKNumber;

public class MetricsProject {
	private Collection<CKNumber> metricsByClassBeforeRefactoring;
	private Collection<CKNumber> metricsByClassAfterRefactoring;	
	
	public MetricsProject(Collection<CKNumber> metricsByClassBeforeRefactoring, Collection<CKNumber> metricsByClassAfterRefactoring) {
		this.setMetricsByClassAfterRefactoring(metricsByClassBeforeRefactoring);
		this.setMetricsByClassAfterRefactoring(metricsByClassAfterRefactoring);
	}

	public MetricsProject() {
		// TODO Auto-generated constructor stub
	}

	/**retorna a listagem de métricas retirada antes da refatoração*/
	public Collection<CKNumber> getMetricsByClassBeforeRefactoring() {
		return metricsByClassBeforeRefactoring;
	}

	public void setMetricsByClassBeforeRefactoring(Collection<CKNumber> metricsByClassBeforeRefactoring) {
		this.metricsByClassBeforeRefactoring = metricsByClassBeforeRefactoring;
	}

	/**retorna a listagem de métricas retirada depois da refatoração*/
	public Collection<CKNumber> getMetricsByClassAfterRefactoring() {
		return metricsByClassAfterRefactoring;
	}

	public void setMetricsByClassAfterRefactoring(Collection<CKNumber> metricsByClassAfterRefactoring) {
		this.metricsByClassAfterRefactoring = metricsByClassAfterRefactoring;
	}
	
	
}
