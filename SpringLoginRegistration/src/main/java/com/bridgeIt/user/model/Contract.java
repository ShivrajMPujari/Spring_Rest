package com.bridgeIt.user.model;

public class Contract {

	private String contractId;
	private String contractDescription;
	private String importerId;
	private String exporterId;
	private String customId;
	private String importerBankId;

	private String insuranceId;
	private int value;
	private String portOfLoading;
	private String portOfEntry;
	private boolean importerCheck;
	private boolean exporterCheck;
	private boolean customCheck;
	private boolean importerBankCheck;
	private boolean insuranceCheck;
	private boolean completion;
	private String pointer;
	
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getContractDescription() {
		return contractDescription;
	}
	public void setContractDescription(String contractDescription) {
		this.contractDescription = contractDescription;
	}
	public String getImporterId() {
		return importerId;
	}
	public void setImporterId(String importerId) {
		this.importerId = importerId;
	}
	public String getExporterId() {
		return exporterId;
	}
	public void setExporterId(String exporterId) {
		this.exporterId = exporterId;
	}
	public String getCustomId() {
		return customId;
	}
	public void setCustomId(String customId) {
		this.customId = customId;
	}
	public String getImporterBankId() {
		return importerBankId;
	}
	public void setImporterBankId(String importerBankId) {
		this.importerBankId = importerBankId;
	}
	public String getInsuranceId() {
		return insuranceId;
	}
	public void setInsuranceId(String insuranceId) {
		this.insuranceId = insuranceId;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getPortOfLoading() {
		return portOfLoading;
	}
	public void setPortOfLoading(String portOfLoading) {
		this.portOfLoading = portOfLoading;
	}
	public String getPortOfEntry() {
		return portOfEntry;
	}
	public void setPortOfEntry(String portOfEntry) {
		this.portOfEntry = portOfEntry;
	}
	public boolean isImporterCheck() {
		return importerCheck;
	}
	public void setImporterCheck(boolean importerCheck) {
		this.importerCheck = importerCheck;
	}
	public boolean isExporterCheck() {
		return exporterCheck;
	}
	public void setExporterCheck(boolean exporterCheck) {
		this.exporterCheck = exporterCheck;
	}
	public boolean isCustomCheck() {
		return customCheck;
	}
	public void setCustomCheck(boolean customCheck) {
		this.customCheck = customCheck;
	}
	public boolean isImporterBankCheck() {
		return importerBankCheck;
	}
	public void setImporterBankCheck(boolean importerBankCheck) {
		this.importerBankCheck = importerBankCheck;
	}
	public boolean isInsuranceCheck() {
		return insuranceCheck;
	}
	public void setInsuranceCheck(boolean insuranceCheck) {
		this.insuranceCheck = insuranceCheck;
	}

	public boolean isCompletion() {
		return completion;
	}
	public void setCompletion(boolean completion) {
		this.completion = completion;
	}
	public String getPointer() {
		return pointer;
	}
	public void setPointer(String pointer) {
		this.pointer = pointer;
	} 
	
	@Override
	public String toString() {
		return "Contract [contractId=" + contractId + ", contractDescription=" + contractDescription + ", importerId="
				+ importerId + ", exporterId=" + exporterId + ", customId=" + customId + ", importerBankId="
				+ importerBankId + ", insuranceId=" + insuranceId + ", value=" + value + ", portOfLoading="
				+ portOfLoading + ", portOfEntry=" + portOfEntry + ", importerCheck=" + importerCheck
				+ ", exporterCheck=" + exporterCheck + ", customCheck=" + customCheck + ", importerBankCheck="
				+ importerBankCheck + ", insuranceCheck=" + insuranceCheck + ", completion=" + completion + ", pointer="
				+ pointer + "]";
	}
	
	
}
