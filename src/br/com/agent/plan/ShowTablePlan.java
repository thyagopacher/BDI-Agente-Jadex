package br.com.agent.plan;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.github.mauricioaniche.ck.CKNumber;

import br.com.agent.TableModel;
import br.com.agent.belief.MetricsProject;
import br.com.agent.belief.QualityAttributeClass;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

@Plan
public class ShowTablePlan extends UtilityPlan{

	private List<QualityAttributeClass> improvements;

	public ShowTablePlan(List<QualityAttributeClass> improvements) {
		this.improvements = improvements;
	}

	@PlanBody
	public boolean createJTable() {
		if(!this.improvements.isEmpty()) {
			int qtdImprovements = this.improvements.size();
			System.out.println("Founded " + qtdImprovements + " improvements ");
			
			JFrame frame = new JFrame("Refactoring Code");
			JTable tabela = new JTable(new TableModel(this.improvements));
			
			tabela.setAutoCreateRowSorter(true);	
			JScrollPane scroll = new JScrollPane(tabela);
			frame.add(scroll, BorderLayout.CENTER);
			frame.setLocationRelativeTo(null);
			frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
			frame.setVisible(true);
			
			return true;
		}
		return false;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully ShowTablePlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted ShowTablePlan");
	}

}
