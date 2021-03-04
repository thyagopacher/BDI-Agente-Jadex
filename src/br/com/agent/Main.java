package br.com.agent;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;

public class Main {

	public String operationalSystem = System.getProperty("os.name");
	public String systemVersion  = System.getProperty("os.version");
	public String systemArchitecture = System.getProperty("os.arch");

	/**
	 * define o caminho ao qual vai verificar o projeto
	 * */
	public String caminho;
	private IExternalAccess platform;
	private IFuture<IComponentManagementService> fut;

	Main(){
		/**
		 * definindo um caminho qualquer para poder testar o agente na classe
		 * */
		this.caminho = "C:\\codigo-fonte\\programa-java\\projetos-luan-teste-refatoracao\\p34-gradle-retrolambda-master-sem-refatoracao";
	}
	
	public void iniciaAgenteFrame() {
		try {

			PlatformConfiguration platformConfig = PlatformConfiguration.getDefaultNoGui();
			platformConfig.setGui(false);
			platformConfig.setWelcome(false);
			platformConfig.setPrintPass(false);
			platformConfig.setUsePass(false);
			platformConfig.setLogging(false);
			platformConfig.setDebugFutures(false);
			platformConfig.setAddress(true);
			platformConfig.setAsyncExecution(true);
			platformConfig.setAutoShutdown(false);
			platformConfig.setMessage(true);
			
			platformConfig.setKernels(PlatformConfiguration.KERNEL.micro,
					PlatformConfiguration.KERNEL.component, PlatformConfiguration.KERNEL.v3);			
			platformConfig.setAwaMechanisms(PlatformConfiguration.AWAMECHANISM.broadcast,
					PlatformConfiguration.AWAMECHANISM.relay);
			platform = Starter.createPlatform(platformConfig).get();

			fut = SServiceProvider.getService(platform, IComponentManagementService.class);
			IComponentManagementService cms = fut.get();
			CreationInfo ci = new CreationInfo(
					SUtil.createHashMap(new String[] { "pathName" }, new Object[] { caminho }));
			cms.createComponent("agente-refatoracao", "br.com.agent.RefactoringBDI.class", ci);	
			
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}

	/**
	 * ele mata a plataforma de agente do jadex
	 */
	public void pararAgente() {
		platform.killComponent();
	}

	public static void main(String[] args) {
			Main m = new Main();
			m.iniciaAgenteFrame();
	}
}
