package br.com.agent.plan.designpattern;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import br.com.agent.belief.ProprietyClass;


public class DesignPattern {
	

	private boolean isApplied = false;
	
	public DesignPattern() {
		
	}
	
	/**
	 * saves content inside the class file
	 * @param pathName - path where the file is located
	 * */
	public void saveContent(String pathName, String content) {
		try {
			FileWriter fileWriter1 = new FileWriter(pathName);
			fileWriter1.write(content);
			fileWriter1.flush();
			fileWriter1.close();
		} catch (IOException ex) {
			throw new IllegalStateException("Error caused by: " + ex.getMessage());
		}
	}	
	
	/**
	 * returns list of Design Patterns implemented
	 * */
	public List<DesignPattern> getDesignPatterns(){
		List<DesignPattern> designPatterns = new ArrayList<>();
		if(designPatterns == null || designPatterns.isEmpty()) {
			designPatterns = new ArrayList<DesignPattern>();
			designPatterns.add(new SingletonPlan());
			designPatterns.add(new StrategyPlan());
			designPatterns.add(new FactoryMethodPlan());
			designPatterns.add(new NullObjectPlan());
		}
		return designPatterns;
	}

	public boolean isApplicable(ProprietyClass object) {
		return false;
	}

	public void applyMethod(ProprietyClass object) {
	}

	public boolean isApplied() {
		return isApplied;
	}

	public void setApplied(boolean isApplied) {
		this.isApplied = isApplied;
	}
	
}
