package br.com.agent.belief;

public class QualityAttributeClass {

	private String fileClass;
	private String nameClass;
	private int impromentMaintenance;
	private int impromentReability;
	private int impromentReusability;
	
	public QualityAttributeClass(String nameClass, String fileClass, int impromentMaintenance, int impromentReability, int impromentReusability) {
		this.nameClass = nameClass;
		this.fileClass = fileClass;
		this.impromentReability = impromentReability;
		this.impromentMaintenance = impromentMaintenance;
		this.impromentReusability = impromentReusability;
	}

	public int getImpromentMaintenance() {
		return impromentMaintenance;
	}

	public void setImpromentMaintenance(int impromentMaintenance) {
		this.impromentMaintenance = impromentMaintenance;
	}

	public int getImpromentReability() {
		return impromentReability;
	}

	public void setImpromentReability(int impromentReability) {
		this.impromentReability = impromentReability;
	}

	public int getImpromentReusability() {
		return impromentReusability;
	}

	public void setImpromentReusability(int impromentReusability) {
		this.impromentReusability = impromentReusability;
	}

	public QualityAttributeClass() {
		// TODO Auto-generated constructor stub
	}

	public String getNameClass() {
		return nameClass;
	}

	public void setNameClass(String nameClass) {
		this.nameClass = nameClass;
	}

	public String getFileClass() {
		return fileClass;
	}

	public void setFileClass(String fileClass) {
		this.fileClass = fileClass;
	}


}
