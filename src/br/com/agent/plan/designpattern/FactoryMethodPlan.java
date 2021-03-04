package br.com.agent.plan.designpattern;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import br.com.agent.belief.ProprietyClass;
import jadex.bdiv3.runtime.BDIFailureException;

public class FactoryMethodPlan extends DesignPattern implements java.io.Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FactoryMethodPlan() {
		
	}

	public boolean isApplicable(ProprietyClass object) {
		if(object.getName().contains("AbstractNettyUDPClient")) {
			System.out.println("entro...");
		}
		System.out.println("== Testing if is Applicable on Factory Method ==");
		Map<MethodDeclaration, List<Statement>> mapaMetodosAnalisados = new HashMap<>();
		try {
			List<?> membros = object.getType().getMembers().stream()
					.filter(linha -> linha instanceof MethodDeclaration).collect(Collectors.toList());
			for (Object methodRead : membros) {
				MethodDeclaration method = (MethodDeclaration) methodRead;
				if (method.getType().toString().equals("void") || !method.isAbstract()) {
					continue;
				}
				
				List<Parameter> parametrosMetodo = method.getParameters();
				if (parametrosMetodo == null || parametrosMetodo.isEmpty()) {
					System.out.println(" -- Método:" + method.getName() + " -- não tem parametros não é usado factory");
					continue;
				}
				if (method.getBody() != null && method.getBody().isPresent() && !method.getBody().get().isEmpty()) {
					List<Statement> linhasMetodo = method.getBody().get().getStatements();
					if (linhasMetodo != null && !linhasMetodo.isEmpty()) {
						List<Statement> instrucoesIf = new ArrayList<>();
						for (Statement linha : linhasMetodo) {
							if (linha instanceof IfStmt) {
								IfStmt conditional = ((IfStmt) linha);
								List<Node> camposCondicao = conditional.getCondition().getChildNodes();
								for (Node campo : camposCondicao) {
									for (Parameter parametro : parametrosMetodo) {
										/** adaptado para não pegar condições com OR ou AND */
										String txtCondicao = conditional.getCondition().toString();
										if (parametro.getNameAsString().toString().equals(campo.toString())
												&& !txtCondicao.contains("||") 
												&& !txtCondicao.contains("&&")
												&& !txtCondicao.contains("exist")
												&& !txtCondicao.contains("this")
												&& !txtCondicao.contains("null")
												&& (txtCondicao.contains("equals") || txtCondicao.contains("=="))) {
											System.out.println("Achou parametro na condicional...");
											if (conditional != null) {
												instrucoesIf.add(conditional);
											} 
										}
									}
								}
							}
						}
						if (instrucoesIf != null && !instrucoesIf.isEmpty()) {
							mapaMetodosAnalisados.put(method, instrucoesIf);
						}
					}
				}
			}
			object.setMapMethodsAnalyzed(mapaMetodosAnalisados);
			return mapaMetodosAnalisados.size() > 0;
		} catch (BDIFailureException ex) {
			throw new IllegalStateException("Erro - causado por: " + ex.getMessage());
		}
	}

	public void applyMethod(ProprietyClass object) {
		try {
			
			String classeAvaliada = object.getType().getNameAsString();
			object.getType().setBlockComment("Classe modificada para ter padrão Factory Method");
			String nomePacote = object.getCu().getPackageDeclaration().get().getNameAsString();
			Map<MethodDeclaration, List<Statement>> ondeModificar = object.getMapMethodsAnalyzed();
			for (Map.Entry<MethodDeclaration, List<Statement>> entrada : ondeModificar.entrySet()) {
				MethodDeclaration metodoModificado = entrada.getKey();
				
				/**esvazia o corpo do método visto e deixa ele sem parametro*/
				metodoModificado.setBody(new BlockStmt());
				metodoModificado.getParameter(0).remove();
				
				/**roda os condicionais encontrados perante o método*/
				List<Statement> condicionaisEncontrados = entrada.getValue();
				for (Statement statement : condicionaisEncontrados) {
					IfStmt ifStmt = (IfStmt) statement;
					IfStmt elseThen = null;
					
					while(true) {

						String ifThen = ifStmt.getThenStmt().getChildNodes().toString();
						String separa_then[] = ifThen.split("new ");
						String nomeClasse = separa_then[1].replaceAll("'", "").replace("(", "").replace(");]", "");
						String nomeClasseArquivo = nomeClasse + "Factory";
						
						CompilationUnit cu = new CompilationUnit();
						cu.setPackageDeclaration(nomePacote);
						cu.setBlockComment("Classe gerada pelo Factory Method");
						
						ClassOrInterfaceDeclaration type = cu.addClass(nomeClasseArquivo);						
						// create a method coloca o retorno para ser uma instancia da classe
						BlockStmt block = new BlockStmt();
						block.addStatement("return new " + nomeClasse + "();");

						MethodDeclaration method = new MethodDeclaration();
						method.setName(metodoModificado.getName());
						method.setType(metodoModificado.getType());
						method.setModifiers(EnumSet.of(Modifier.PUBLIC));
						method.setBody(block);
						type.addMember(method);
						type.addExtends(classeAvaliada);
						
						saveContent(object.getParentFile().getAbsolutePath() + "/" + nomeClasseArquivo + ".java", cu.toString());
						
						/**na última linha o else vai para o lugar do if fazendo assim rodar todos os possíveis else if*/
						if(ifStmt.getElseStmt().get() instanceof IfStmt) {
							elseThen = (IfStmt) ifStmt.getElseStmt().get();
							ifStmt = elseThen;					
						}else {
							break;
						}
					}
				}
			}
			
			/** seta classe factory pai para abstract */
			object.getType().addModifier(Modifier.ABSTRACT);
			
			saveContent(object.getAbsoluteFile().toString(), object.getCu().toString());
			super.setApplied(true);
		} catch (Exception ex) {
			super.setApplied(false);
			throw new IllegalStateException("Erro - causado por - Factory PLAN: " + ex.getMessage());
		}
	}

}
