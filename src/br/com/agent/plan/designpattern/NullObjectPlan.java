package br.com.agent.plan.designpattern;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

import br.com.agent.belief.ProprietyClass;
import jadex.bdiv3.runtime.BDIFailureException;

public class NullObjectPlan extends DesignPattern implements java.io.Serializable {

	private ProprietyClass object;
	private static final long serialVersionUID = 1L;
	private FieldDeclaration campoClasse;
	
	public NullObjectPlan() {
		
	}

	/**
	 * verifica se o método é um set para variáveis declarados na classe
	 */
	public boolean isSetter(MethodDeclaration m, FieldDeclaration f) {
		if (m.getParameters().size() >= 1 && !m.isPrivate()) {
			List<?> variaveisDeclaradas = m.getBody().get().getChildNodes();
			for (Object s : variaveisDeclaradas) {
				if (s instanceof ExpressionStmt) {
					ExpressionStmt variavelDeclarada = (ExpressionStmt) s;
					String nomeVariavel = variavelDeclarada.getExpression().getChildNodes().get(1).toString();
					if (nomeVariavel.equals(f.getVariable(0).getName().toString())) {
						return m.getParameters().stream().filter(l -> l.getName().toString().equals(nomeVariavel))
								.collect(Collectors.toList()).size() > 0;
					}
				}
			}
		}
		return false;
	}

	/**
	 * busca algum if no método diferente de nulo e dentro dele deve haver uma
	 * invocação de método 1 - != NULL na comparação 2 - não tem else 3 - tem a
	 * chama de algum método dentro do if
	 */
	public boolean isGFIv1Conditional(FieldDeclaration f, Statement s) {
		boolean res = false;
		if (s instanceof IfStmt) {
			IfStmt condicao = (IfStmt) s;
			res = this.checksNullInequality(condicao.getCondition(), f) && !condicao.getElseStmt().isPresent()
					&& this.isFieldInvocationFragment(condicao.getThenStmt().getChildNodes(), f);
		}
		return res;
	}

	/**
	 * procura condicionais que tenha: 1 - != NULL na comparação 2 - else com corpo
	 * e throw dentro deste else
	 */
	public boolean isGFIv2Conditional(FieldDeclaration f, Statement s) {
		boolean res = false;
		if (s instanceof IfStmt) {
			IfStmt condicao = (IfStmt) s;
			boolean temThrowNoElse = condicao.getElseStmt().isPresent() && condicao.getElseStmt().get().getChildNodes()
					.stream().filter(l -> l instanceof ThrowStmt).collect(Collectors.toList()).size() > 0;
			res = this.checksNullInequality(condicao.getCondition(), f)
					&& this.isFieldInvocationFragment(condicao.getThenStmt().getChildNodes(), f) && temThrowNoElse;
		}
		return res;
	}

	/**
	 * procura condicionais que tenha: 1 - == NULL na comparação 2 - não tem else 3
	 * - dentro do if tem throw
	 */
	public boolean isGFIv3Conditional(FieldDeclaration f, Statement s) {
		boolean res = false;
		if (s instanceof IfStmt) {
			IfStmt condicao = (IfStmt) s;

			boolean temThrowNoIf = themThrowNoIf(condicao);
			res = this.checksNullEquality(condicao.getCondition(), f) && temThrowNoIf
					&& !condicao.getElseStmt().isPresent();
		}
		return res;
	}

	/**
	 * procura condicionais que tenha: 1 - == NULL na comparação 2 - tem else e no
	 * corpo deste tem alguma chamada de método
	 */
	public boolean isGFIv4Conditional(FieldDeclaration f, Statement s) {
		boolean res = false;
		if (s instanceof IfStmt) {
			IfStmt condicao = (IfStmt) s;
			boolean temThrowNoIf = themThrowNoIf(condicao);
			res = this.checksNullEquality(condicao.getCondition(), f) && temThrowNoIf
					&& condicao.getElseStmt().isPresent()
					&& this.isFieldInvocationFragment(condicao.getElseStmt().get().getChildNodes(), f);
		}
		return res;
	}

	public boolean themThrowNoIf(IfStmt condicao) {
		return condicao.getThenStmt().getChildNodes().stream().filter(l -> l instanceof ThrowStmt)
				.collect(Collectors.toList()).size() > 0;
	}

	public boolean isFieldInvocationFragment(List<Node> s, FieldDeclaration f) {
		boolean res = false;
		for (Node statement : s) {
			if (statement instanceof ExpressionStmt) {
				ExpressionStmt expressao = (ExpressionStmt) statement;
				if (expressao.getExpression() instanceof MethodCallExpr && this.isFieldInvocation(expressao, f)) {
					return true;
				}
				if (expressao.getExpression() instanceof AssignExpr) {
					return true;
				}
			}

		}
		return res;
	}

	/**
	 * verifica senão chama algum método de outro objeto analisando a expressão para
	 * ter objeto.metodo()
	 */
	public boolean isFieldInvocation(ExpressionStmt e, FieldDeclaration f) {
		boolean res = false;
		String nomeCampo = f.getVariable(0).getName().toString();
		res = e.getExpression() instanceof MethodCallExpr && e.getExpression().toString().contains(nomeCampo);
		return res;
	}

	/**
	 * verifica se em um metodo de nome x se tem um objeto dentro dele chamando
	 * método x tal como objeto.x()
	 */
	public boolean invokesFieldsMethod(ExpressionStmt e, FieldDeclaration f, MethodDeclaration m) {
		String nomeMetodo = m.getNameAsString();
		String metodoChamado = e.getExpression().getChildNodes().get(1).toString();
		return this.isFieldInvocation(e, f) && metodoChamado.equals(nomeMetodo);
	}

	/**
	 * verifica se o tipo passado é void
	 * 
	 * @return true - caso tipo passado seja void
	 */
	public boolean isVoidType(Type type) {
		return type != null && type.toString().equals("void");
	}

	/**
	 * verifica se o tipo retornado é string
	 */
	public boolean isLiteralType(Type type) {
		return type instanceof PrimitiveType && !type.toString().equals("void") || !type.toString().equals("String");
	}

	/**
	 * verifica senão tem nenhuma comparação com nulo nos condicionais do método
	 */
	public boolean checksNullEquality(Expression e, FieldDeclaration f) {
		boolean res = false;
		String nomeCampo = f.getVariable(0).getName().toString();
		if (e instanceof BinaryExpr) {
			BinaryExpr ex = (BinaryExpr) e;
			res = ex.getOperator().asString().equals("==") && ((ex.getRight() instanceof NullLiteralExpr
					&& ex.getLeft() instanceof Expression && ex.getLeft().toString().equals(nomeCampo))
					|| (ex.getLeft() instanceof NullLiteralExpr && ex.getRight() instanceof Expression
							&& ex.getRight().toString().equals(nomeCampo)));
		}
		return res;
	}

	/**
	 * verifica se a comparação tem diferente de nulo
	 */
	public boolean checksNullInequality(Expression e, FieldDeclaration f) {
		boolean res = false;
		String nomeCampo = f.getVariable(0).getName().toString();
		if (e instanceof BinaryExpr) {
			BinaryExpr ex = (BinaryExpr) e;
			res = ex.getOperator().asString().equals("!=") && ((ex.getRight() instanceof NullLiteralExpr
					&& ex.getLeft() instanceof Expression && ex.getLeft().toString().equals(nomeCampo))
					|| (ex.getLeft() instanceof NullLiteralExpr && ex.getRight() instanceof Expression
							&& ex.getRight().toString().equals(nomeCampo)));
		}
		return res;
	}

	public boolean emptyOnNull(FieldDeclaration f, MethodDeclaration m) {
		boolean res = false;
		if (isVoidType(f.getVariable(0).getType())) {
			res = true;
		}
		return res;
	}

	public boolean isOptional(FieldDeclaration f, String nomeClasse) {
		VariableDeclarator v = f.getVariable(0);
		String nomeCampo = v.getNameAsString();
		boolean setadoNoConstrutor = false;

		/** verifica se o campo da classe está setado ou inicializado no construtor */
		int qtdConstrutor = object.getType().getMembers().stream()
				.filter(l -> l instanceof ConstructorDeclaration).collect(Collectors.toList()).size();
		if (qtdConstrutor > 0) {
			ConstructorDeclaration construtor = (ConstructorDeclaration) object.getType().getMembers()
					.stream().filter(l -> l instanceof ConstructorDeclaration).collect(Collectors.toList()).get(0);
			BlockStmt corpoConstrutor = construtor.getBody();
			for (Statement linha : corpoConstrutor.getStatements()) {
				if ((linha.toString().contains(nomeCampo) && linha.toString().contains("="))) {
					setadoNoConstrutor = true;
					break;
				} else if (linha instanceof ExpressionStmt) {
					ExpressionStmt es = (ExpressionStmt) linha;
					if (es.getExpression() instanceof MethodCallExpr) {
						MethodCallExpr m = (MethodCallExpr) es.getExpression();
						List<MethodDeclaration> elementosMetodo = object.getType()
								.getMethodsByName(m.getName().toString());
						// verifica se tem algum método com aquele nome
						if (!elementosMetodo.isEmpty()) {
							MethodDeclaration md = elementosMetodo.get(0);
							if (isSetter(md, f)) {
								setadoNoConstrutor = true;
								break;
							}
						}
					}
				}
			}
		}

		boolean ehString = v.getType().toString().equals("String");
		boolean estaInicializadoNulo = !v.getInitializer().isPresent()
				|| v.getInitializer().get().toString().equals("null");
		boolean resFinal = ehString == false && estaInicializadoNulo && setadoNoConstrutor;
		return resFinal;
	}

	/**
	 * pré condições para verificar os campos nos ifs
	 */
	public boolean violatesOptionalFieldPreconditions(FieldDeclaration f) {
		try {
			String nomeClasse = f.getVariable(0).getType().toString();
			String nomeArquivo = nomeClasse + ".java";
			File arquivo = new File(object.getParentFile().getAbsoluteFile() + "\\" + nomeArquivo);

			if (arquivo.exists()) {
				CompilationUnit cu = JavaParser.parse(arquivo);
				if (cu.getType(0) instanceof ClassOrInterfaceDeclaration) {
					ClassOrInterfaceDeclaration classe = (ClassOrInterfaceDeclaration) cu.getType(0);

					boolean naoTemExtensao = classe.getExtendedTypes().isEmpty();
					boolean naoEhInterface = !classe.isInterface();

					return !naoTemExtensao || !naoEhInterface || !f.isPrivate() || classe.isAbstract();
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			throw new IllegalStateException("Erro - causado por: " + ex.getMessage());
		}
	}

	/**
	 * verifica se o campo está em algum if do método e com comparação nula ou
	 * diferente de nulo
	 */
	public boolean isGuardedInvocation(FieldDeclaration f, MethodDeclaration m) {
		boolean res = false;
		if (f != null && m != null) {
			List<Node> linhas = m.getChildNodes();
			for (Node node : linhas) {
				if (node instanceof IfStmt) {
					IfStmt condicao = (IfStmt) node;
					if (checksNullEquality(condicao.getCondition(), f)
							|| checksNullInequality(condicao.getCondition(), f)) {
						return true;
					}
				}
			}
		}
		return res;
	}
	
	
	public boolean isApplicable(ProprietyClass object) {
		this.object = object;
		boolean isGFI = false;
		System.out.println("== Testando se é aplicável Null Object => " + object.getType().getNameAsString());
		Map<MethodDeclaration, List<Statement>> mapaMetodosAnalisados = new HashMap<>();
	
		try {
			List<FieldDeclaration> camposClasse = object.getType().getFields();
			for (FieldDeclaration campo : camposClasse) {
				boolean ehOptional = isOptional(campo, object.getType().getNameAsString());
				boolean violaPreCondicoes = violatesOptionalFieldPreconditions(campo);
				if (ehOptional == false || violaPreCondicoes == true) {
					continue;
				}

				List<MethodDeclaration> metodos = object.getType().getMethods();
				if (!metodos.isEmpty()) {
					for (MethodDeclaration metodo : metodos) {
						/** para cada método um novo conjunto de ifs possíveis. */
						List<Statement> instrucoesIf = new ArrayList<>();
						if(!metodo.isAbstract()) {
							continue;//adaptação ao método
						}
						if (metodo.getBody() != null && metodo.getBody().isPresent()
								&& !metodo.getBody().get().isEmpty()) {
							List<Statement> linhasMetodo = metodo.getBody().get().getStatements();
							if (linhasMetodo != null && !linhasMetodo.isEmpty()) {
								for (Statement linha : linhasMetodo) {
									if (linha instanceof IfStmt) {
										isGFI = false;

										IfStmt condicional = ((IfStmt) linha);
										if (isGFIv1Conditional(campo, condicional)) {
											isGFI = true;
											instrucoesIf.add(condicional);
										} else if (isGFIv2Conditional(campo, condicional)) {
											isGFI = true;
											instrucoesIf.add(condicional);
										} else if (isGFIv3Conditional(campo, condicional)) {
											isGFI = true;
											instrucoesIf.add(condicional);
										} else if (isGFIv4Conditional(campo, condicional)) {
											isGFI = true;
											instrucoesIf.add(condicional);
										}
									}
								}
								if (instrucoesIf != null && !instrucoesIf.isEmpty()) {
									System.out.println("Método: " + metodo.getNameAsString());
									mapaMetodosAnalisados.put(metodo, instrucoesIf);
								}
							}
						}
					}
				}
			}
			object.setMapMethodsAnalyzed(mapaMetodosAnalisados);
			return mapaMetodosAnalisados.size() > 0;
		} catch (BDIFailureException ex) {
			throw new IllegalStateException("Error caused by in analyze NULL Object: " + ex.getMessage());
		}
	}

	public void applyMethod(ProprietyClass object) {

		String nomePacote = object.getCu().getPackageDeclaration().get().getNameAsString();
		boolean arquivoAbstratoExiste = false, arquivoNullExiste = false;
		VariableDeclarator variavel = null;
		String nomeClasse = "", nomeObjeto = "", nomeClasseNullObjeto = "", nomeClasseNaoEncontrada = "",
				nomeCampo = "", nomeMetodoAssignTo = "";

		/** métodos da clase visualizada */
		object.getType().setBlockComment("Classe modificada para ter padrão NullObject");
		Map<MethodDeclaration, List<Statement>> ondeModificar = object.getMapMethodsAnalyzed();
		for (Map.Entry<MethodDeclaration, List<Statement>> entrada : ondeModificar.entrySet()) {
			List<Statement> condicionaisEncontrados = entrada.getValue();

			IfStmt ifStmt1 = (IfStmt) condicionaisEncontrados.get(0);
			String campoIf = ifStmt1.getCondition().getChildNodes().get(0).toString();
			String campoIf2;
			if(campoIf.equals("null")) {
				campoIf2 = ifStmt1.getCondition().getChildNodes().get(1).toString();
				campoClasse = object.getCu().getTypes().get(0).getFields().stream()
						.filter(l -> l.getVariable(0).getNameAsString().equals(campoIf2)).findAny().orElse(null);
			}else {
			campoClasse = object.getCu().getTypes().get(0).getFields().stream()
					.filter(l -> l.getVariable(0).getNameAsString().equals(campoIf)).findAny().orElse(null);
			}
			
			if (campoClasse != null) {
				variavel = campoClasse.getVariable(0);
				nomeObjeto = variavel.getType().toString();
				if(nomeObjeto == null || nomeObjeto.isEmpty()) {
					continue;
				}
				nomeObjeto = nomeObjeto.toString().replace("<", "").replace(">", "").replace("Set", "");
				nomeCampo = variavel.getNameAsString().toString();
				nomeClasse = "Abstract" + nomeObjeto;
				break;
			}
		}

		try {
			
			/** Leitura da classe objeto que foi encontrada com comparação nula */
			String diretorioAnalisado = object.getParentFile().toString();
			
			/**o objeto null pode não estar na mesma pasta do diretório analisado*/
			ProprietyClass objectNull = null;
			if(new File(diretorioAnalisado + "\\" + nomeObjeto + ".java").exists()) {
				objectNull = new ProprietyClass(diretorioAnalisado + "\\" + nomeObjeto + ".java");
			}else {
				File diretorio = new File(diretorioAnalisado);		
				String diretorioAnterior = diretorio.getParentFile().getPath();
				if(new File(diretorioAnterior + "\\" + nomeObjeto + ".java").exists()) {
					objectNull = new ProprietyClass(diretorioAnterior + "\\" + nomeObjeto + ".java");
				}else {
					File diretorio2 = new File(diretorioAnterior);		
					String diretorioAnterior2 = diretorio2.getParentFile().getPath();	
					objectNull = new ProprietyClass(diretorioAnterior2 + "\\" + nomeObjeto + ".java");
				}
			}
			NodeList<BodyDeclaration<?>> metodosObjeto = objectNull.getType().getMembers();
			nomeClasseNaoEncontrada = nomeObjeto + "NotFoundException";
			nomeClasseNullObjeto = "Null" + nomeObjeto;

			/**
			 * verifica e adiciona caso não tenha a classe para ser usada no throw "não
			 * encontrado"
			 */
			File classeNaoEncontrada = new File(objectNull.getParentFile().toString() + "\\" + nomeClasseNaoEncontrada);
			if (!classeNaoEncontrada.exists()) {
				CompilationUnit cu = new CompilationUnit();
				cu.addImport("javassist.NotFoundException");
				cu.setPackageDeclaration(nomePacote);
				ClassOrInterfaceDeclaration type = cu.addClass(nomeClasseNaoEncontrada);
				type.addExtends("NotFoundException");
				type.setBlockComment("Classe criada para retornar excessão quando não for encontrado o objeto lido");
				BlockStmt corpoConstrutor = new BlockStmt();
				corpoConstrutor.addAndGetStatement("super(msg)");

				ConstructorDeclaration construtor = new ConstructorDeclaration(EnumSet.of(Modifier.PUBLIC),
						nomeClasseNaoEncontrada);
				construtor.setBody(corpoConstrutor);
				construtor.addParameter("String", "msg");
				type.addMember(construtor);
				saveContent(objectNull.getParentFile().toString() + "/" + nomeClasseNaoEncontrada + ".java",
						cu.toString());
			}

			arquivoAbstratoExiste = new File(objectNull.getParentFile().toString() + "\\" + nomeClasse).exists();
			arquivoNullExiste = new File(objectNull.getParentFile().toString() + "\\" + nomeClasseNullObjeto).exists();

			if (!arquivoAbstratoExiste || !arquivoNullExiste) {
				CompilationUnit cu1 = null, cu2 = null;
				ClassOrInterfaceDeclaration type1 = null, type2 = null;

				if (!arquivoAbstratoExiste) {
					cu1 = new CompilationUnit();
					cu1.setPackageDeclaration(object.getCu().getPackageDeclaration().get());
					/** importações especiais que não existem na classe objeto */
					cu1.addImport(nomePacote + "." + nomeClasseNaoEncontrada);
					cu1.addImport(nomePacote + "." + nomeObjeto);

					/** verifica quais são as importações da classe objeto e pega todas */
					addImposts(nomeClasse, cu1);
					
					cu1.setBlockComment("Classe abstrata para uso na classe NullObjeto e Objeto");
					type1 = cu1.addClass(nomeClasse);

				}

				if (!arquivoNullExiste) {
					cu2 = new CompilationUnit();
					cu2.setPackageDeclaration(object.getCu().getPackageDeclaration().get());
					/** importações especiais que não existem na classe objeto */
					cu2.addImport(nomePacote + "." + nomeClasse);
					cu2.addImport(nomePacote + "." + nomeObjeto);

					/** verifica quais são as importações da classe objeto e pega todas */
					addImposts(nomeClasseNullObjeto, cu2);

					cu2.setBlockComment(
							"Classe NullObjeto para referenciar o objeto criando um novo ao invés de setar nulo direto");
					type2 = cu2.addClass(nomeClasseNullObjeto);
					if(!type2.getExtendedTypes(0).toString().contains(nomeClasse)) {
						type2.addExtends(nomeClasse);
					}
				}

				for (BodyDeclaration<?> member : metodosObjeto) {
					if (member instanceof MethodDeclaration) {
						MethodDeclaration method0 = (MethodDeclaration) member;// metodo original
						EnumSet<Modifier> modificadorMetodo = (method0.getModifiers());

						if (!arquivoAbstratoExiste) {
							MethodDeclaration methodAbstrato = new MethodDeclaration(modificadorMetodo.clone(), method0.getType(), method0.getNameAsString());
							methodAbstrato.addModifier(Modifier.ABSTRACT);
							methodAbstrato.setBody(null);// deixa o corpo do metodo vazio
							type1.addMember(methodAbstrato);
						}
						if (!arquivoNullExiste) {
							MethodDeclaration methodNull = new MethodDeclaration(modificadorMetodo, method0.getType(), method0.getNameAsString());
							BlockStmt body = new BlockStmt();
							if (!method0.getType().toString().equals("void")) {
								if (method0.getType().toString().equals("double")
										|| method0.getType().toString().equals("int")
										|| method0.getType().toString().equals("float")) {
									body.addStatement("return 0;");
								} else if (method0.getType().toString().equals("boolean")) {
									body.addStatement("return true;");
								} else {
									body.addStatement("return null;");
								}
							} else {
								body.addStatement("throw new " + nomeClasseNaoEncontrada + "();");
							}
							methodNull.setBody(body);
							type2.addMember(methodNull);

						}
					}
				}

				if (!arquivoAbstratoExiste) {
					this.addMethod("isNull", "boolean", type1, "", EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT), null);
					this.addMethod("getReference", nomeObjeto, type1, "", EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT), null);
					this.addMethod("assertNotNull", "void", type1, "", EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT),
							NodeList.nodeList(new ClassOrInterfaceType(nomeClasseNaoEncontrada)));

					saveContent(objectNull.getParentFile().toString() + "/" + nomeClasse + ".java", cu1.toString());
				}

				if (!arquivoNullExiste) {
					this.addMethod("isNull", "boolean", type2, "return true;");
					this.addMethod("getReference", nomeObjeto, type2, "return null;");
					this.addMethod("assertNotNull", "void", type2, "throw new " + nomeClasseNaoEncontrada + "();",
							EnumSet.of(Modifier.PUBLIC), NodeList.nodeList(new ClassOrInterfaceType(nomeClasseNaoEncontrada)));

					saveContent(objectNull.getParentFile().toString() + "/" + nomeClasseNullObjeto + ".java",
							cu2.toString());
				}

				/** métodos especiais sendo atribuidos a classe objeto null */
				this.addMethod("isNull", "boolean", objectNull.getType(), "return false;");
				this.addMethod("getReference", nomeObjeto, objectNull.getType(), "return this;");
				this.addMethod("assertNotNull", "void", objectNull.getType(), "");
				
				/**adiciona a extensão da classe Abstrata a classe Objeto*/
				object.getType().addExtends(nomeClasse);
				
				/**
				 * grava classe objeto com 3 novos métodos exigidos e a herança a classe
				 * abstrata
				 */
				saveContent(object.getParentFile().toString() + "/" + nomeObjeto + ".java",
						object.getCu().toString());

			} /* fim da verificação se existe arquivo NullObjeto e AbstratoObjeto java */

			/** modificando o campo da classe que tem condicionais NULL */
			variavel.setType(nomeClasse);
			variavel.setInitializer("new " + nomeClasseNullObjeto + "()");

			BlockStmt corpoMetodoEspecialClasseCondicionais = new BlockStmt();
			corpoMetodoEspecialClasseCondicionais
					.addStatement("if(" + nomeCampo + " == null){return " + nomeClasseNullObjeto + "();}");
			corpoMetodoEspecialClasseCondicionais.addAndGetStatement("else return " + nomeCampo);
			nomeMetodoAssignTo = "assignTo" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1);
			MethodDeclaration metodoEspecialClasseCondicionais = new MethodDeclaration(EnumSet.of(Modifier.PRIVATE),
					new ClassOrInterfaceType(nomeClasse), nomeMetodoAssignTo);
			metodoEspecialClasseCondicionais.setBody(corpoMetodoEspecialClasseCondicionais);
			object.getType().addMember(metodoEspecialClasseCondicionais);

			/** verificando se tem inicialização no construtor e modificando */
			String nomeParametroConstrutor = "";
			ConstructorDeclaration construtor = (ConstructorDeclaration) object.getType()
					.getMembers().stream().filter(l -> l instanceof ConstructorDeclaration).collect(Collectors.toList())
					.get(0);
			if (construtor.getParameters().size() > 0) {
				for (Parameter parametro : construtor.getParameters()) {
					if (parametro.getType().toString().equals(nomeObjeto)) {
						nomeParametroConstrutor = parametro.getNameAsString();
						break;
					}
				}

				BlockStmt corpoConstrutor = construtor.getBody();
				for (Statement linha : corpoConstrutor.getStatements()) {
					if (linha.toString().contains(nomeCampo) && linha.toString().contains("=")) {
						linha.remove();
						corpoConstrutor.addStatement(
								nomeCampo + " = " + nomeMetodoAssignTo + "(" + nomeParametroConstrutor + ");");
						break;
					}
				}
			}

			/**
			 * verifica método a método e todos os condicionais perante a classe que tem
			 * NULL
			 */
			boolean finalizaLacoFora = false;
			List<MethodDeclaration> metodosClasseLida = object.getType().getMethods();
			for (MethodDeclaration metodo : metodosClasseLida) {
				BlockStmt corpoMetodo = metodo.getBody().get();
				for (Statement linha : corpoMetodo.getStatements()) {
					if (linha.toString().contains(nomeCampo) && linha.toString().contains("=")
							&& isSetter(metodo, campoClasse)) {
						linha.remove();// subtitui o método set
						corpoMetodo.addStatement("this." + nomeCampo + " = " + nomeMetodoAssignTo + "("
								+ nomeParametroConstrutor + ");");
						break;
					} else if (linha.toString().contains(nomeCampo) && linha.toString().contains("return")) {
						linha.remove();// substitui o método get
						corpoMetodo.addStatement("return " + nomeCampo + ".getReference();");
						break;
					} else if (linha instanceof IfStmt) {
						finalizaLacoFora = true;
						break;// estes serão vistos no prox laço pois ele também vai ter os condicionais
					}
				}
				if(finalizaLacoFora) {
					break;
				}
			}

			for (Map.Entry<MethodDeclaration, List<Statement>> entrada : ondeModificar.entrySet()) {
				MethodDeclaration metodoModificado = entrada.getKey();
				BlockStmt corpoMetodo = metodoModificado.getBody().get();
				List<Statement> condicionaisEncontrados = entrada.getValue();
				
				int i = 0;
				for (Statement linha : condicionaisEncontrados) {
					if (linha instanceof IfStmt) {
						if (linha.toString().contains(nomeCampo) && linha.toString().contains("null")) {
							IfStmt condicional = (IfStmt) linha;
							if (linha.toString().contains("!=")) {
								Statement s = JavaParser.parseStatement(condicional.getThenStmt().toString().replace("{", "").replace("}", ""));
								corpoMetodo.setStatement(i, s);
								linha.remove();
							} else if (linha.toString().contains("==")) {
								corpoMetodo.addStatement(nomeCampo + ".assertNotNull();");
								linha.remove();
							}
						}
					}
					i++;
				}
			}

			object.getCu().addImport(nomePacote + "." + nomeObjeto);
			object.getCu().addImport(nomePacote + "." + nomeClasse);
			object.getCu().addImport(nomePacote + "." + nomeClasseNullObjeto);
			saveContent(object.getAbsolutePath().toString(), object.getCu().toString());
			super.setApplied(true);
		} catch (Exception ex) {
			super.setApplied(false);
			throw new IllegalStateException("Error caused by (NULL OBJECT): " + ex.getMessage() + " - name class: " + object.getName());
		}
	}
	
	public void addImposts(String nomeClasse, CompilationUnit cu) {
		for (ImportDeclaration importacao : object.getCu().getImports()) {
			if (!importacao.getNameAsString().contains(nomeClasse)) {
				cu.addImport(importacao);
			}
		}
	}

	public void addMethod(String nomeMetodo, String tipoMetodo, ClassOrInterfaceDeclaration classeLida,
			String corpoMetodo) {
		addMethod(nomeMetodo, tipoMetodo, classeLida, corpoMetodo, EnumSet.of(Modifier.PUBLIC), null);
	}

	/** facilitador para adicionar métodos no objeto com condições nulas. */
	public void addMethod(String nomeMetodo, String tipoMetodo, ClassOrInterfaceDeclaration classeLida,
			String corpoMetodo, EnumSet<Modifier> modificadores, NodeList<ReferenceType> throwObjeto) {
		boolean jaTemMetodo = classeLida.getMethodsByName(nomeMetodo).size() > 0;
		MethodDeclaration methodObjeto;
		if (!jaTemMetodo) {
			methodObjeto = new MethodDeclaration();
		} else {
			methodObjeto = classeLida.getMethodsByName(nomeMetodo).get(0);
		}
		methodObjeto.setName(nomeMetodo);
		methodObjeto.setType(tipoMetodo);
		methodObjeto.setModifiers(modificadores);

		BlockStmt corpo = new BlockStmt();
		if (!corpoMetodo.isEmpty()) {
			corpo.addStatement(corpoMetodo);
		}else {
			corpo = null;
		}
		if (throwObjeto != null) {
			methodObjeto.setThrownExceptions(throwObjeto);
		}
		methodObjeto.setBody(corpo);
		if (!jaTemMetodo) {
			classeLida.addMember(methodObjeto);
		}
	}

}
