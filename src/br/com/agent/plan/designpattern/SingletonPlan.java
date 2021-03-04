package br.com.agent.plan.designpattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import br.com.agent.belief.ProprietyClass;
import jadex.bdiv3.runtime.BDIFailureException;

public class SingletonPlan extends DesignPattern implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public SingletonPlan() {
	}

	public boolean isApplicable(ProprietyClass object) {
	
		System.out.println("== Testing if is Applicable on Singleton ==");


		if (object.getType().getExtendedTypes().size() > 0) {
			System.out.println("Class " + object.getType().getName() + " have inheritance and cannot is Singleton");
			return false;
		} else if (object.getType().isInterface()
				|| object.getType().isAbstract()) {
			System.out.println("Class " + object.getType().getName() + " is interface or abstract  and cannot is Singleton");
			return false;
		}

		List<Statement> constructors = new ArrayList<>();
		try {
			List<?> membros = object.getType().getMembers().stream()
					.filter(linha -> linha instanceof ConstructorDeclaration).collect(Collectors.toList());
			if (!membros.isEmpty()) {
				for (Object method : membros) {
					if (method instanceof ConstructorDeclaration) {
						ConstructorDeclaration constructor = (ConstructorDeclaration) method;
						if(!constructor.isAbstract() || !object.getType().getMethodsByName("getInstance").isEmpty()) {
							continue;//se ja houver método getInstance ou o construtor é abstrato
						}
						
						if (constructor.getParameters().size() > 0) {
							System.out.println("Constructor of the class " + object.getType().getName() + " have parameters and cannot is Singleton");
							return false;
						} else if (constructor.isPrivate()
								&& object.getType().getMethodsByName("getInstance").size() > 0) {
							System.out.println("Class " + object.getType().getName()
									+ " has a private constructor and getInstance method and so it cannot be Singleton");
							return false;
						} else {
							constructors.add(new ConstructorDeclaration().getBody());
						}
					}
				}
			}
			return true;
		} catch (BDIFailureException ex) {
			throw new IllegalStateException("Error caused by: " + ex.getMessage());
		}
	}
	
	public void applyMethod(ProprietyClass object) {
		try {
			object.getType().setBlockComment("Classe modified for have Design Pattern Singleton");

			/** field  of the type static and private */
			VariableDeclarator variableSingleton = new VariableDeclarator();
			variableSingleton.setName("singleton");
			variableSingleton.setType(object.getType().getNameAsString());
			
			FieldDeclaration fieldSingleton = new FieldDeclaration();	
			fieldSingleton.addVariable(variableSingleton);
			fieldSingleton.setPrivate(true);
			fieldSingleton.setStatic(true);
			object.getType().getMembers().add(fieldSingleton);
			
			/** searching if have constructor */
			List<?> membros = object.getType().getMembers().stream()
					.filter(linha -> linha instanceof ConstructorDeclaration).collect(Collectors.toList());
			if (!membros.isEmpty()) {
				for (Object method : membros) {
					((ConstructorDeclaration) method).setModifiers(Modifier.PRIVATE.toEnumSet());
				}
			} else {
				object.getType().addConstructor(Modifier.PRIVATE);
			}

			/** body of the Singleton method  */
			BlockStmt block = new BlockStmt();
			block.addStatement("if(singleton == null){singleton = new " + object.getType().getNameAsString() + "();}")
					.addStatement("return singleton;");
			
			
			/** method declaration to return single instance */
			MethodDeclaration method = new MethodDeclaration(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), object.getTypeClass(), "getInstance");
			method.setBody(block).setBlockComment("Method Singleton for return one instance unique");
			object.getType().addMember(method);
			saveContent(object.getAbsolutePath(), object.getCu().toString());
			super.setApplied(true);
		} catch (Exception ex) {
			super.setApplied(false);
			throw new IllegalStateException("Error caused by: " + ex.getMessage());
		}
	}

}
