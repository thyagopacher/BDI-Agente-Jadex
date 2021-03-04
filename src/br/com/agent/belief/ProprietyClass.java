package br.com.agent.belief;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaParserBuild;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;

import jdk.internal.reflect.Reflection;

/**
 * Código escrito em uma linguagem
 * */
public class ProprietyClass extends File{

	private static final long serialVersionUID = 1L;
	private CompilationUnit cu;
	private ClassOrInterfaceDeclaration type;
	private Map<MethodDeclaration, List<Statement>> mapMethodsAnalyzed = new HashMap<>();
	
	public ProprietyClass(String pathname) {
		super(pathname);
		try {
			FileInputStream file = new FileInputStream(pathname);
			CompilationUnit cu = JavaParser.parse(file.toString());
			setCu(cu);
			if(cu.getTypes().size() > 0) {
				if(cu.getType(0) instanceof ClassOrInterfaceDeclaration) {
					setType((ClassOrInterfaceDeclaration) cu.getType(0));
				}
			}			
		} catch (FileNotFoundException e) {
			System.out.println("Error caused by - PropriertyClass: " + e.getMessage());
		}
	}

	public CompilationUnit getCu() {
		return cu;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	public ClassOrInterfaceDeclaration getType() {
		return type;
	}

	public void setType(ClassOrInterfaceDeclaration type) {
		this.type = type;
	}

	public Type getTypeClass() {
		return JavaParser.parseClassOrInterfaceType(type.getNameAsString());
	}

	public Map<MethodDeclaration, List<Statement>> getMapMethodsAnalyzed() {
		return mapMethodsAnalyzed;
	}

	public void setMapMethodsAnalyzed(Map<MethodDeclaration, List<Statement>> mapMethodsAnalyzed) {
		this.mapMethodsAnalyzed = mapMethodsAnalyzed;
	}		
}
