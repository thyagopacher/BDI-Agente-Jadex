package br.com.agent.belief;

import java.util.List;

import br.com.agent.plan.designpattern.DesignPattern;

public class Candidate {
	
	private ProprietyClass object;
	private List<DesignPattern> designPatterns;
	
	public Candidate(ProprietyClass object, List<DesignPattern> designPatterns){
		this.object = object;
		this.setDesignPatterns(designPatterns);
	}
	
	public ProprietyClass getObject() {
		return object;
	}
	public void setObject(ProprietyClass object) {
		this.object = object;
	}

	public List<DesignPattern> getDesignPatterns() {
		return designPatterns;
	}

	public void setDesignPatterns(List<DesignPattern> designPatterns) {
		this.designPatterns = designPatterns;
	}
		
}
