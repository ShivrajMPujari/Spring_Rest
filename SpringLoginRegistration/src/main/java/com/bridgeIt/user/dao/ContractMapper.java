package com.bridgeIt.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bridgeIt.user.model.Contract;


public class ContractMapper implements  RowMapper<Contract>{

	@Override
	public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		Contract contract = new Contract();
		contract.setContractId(rs.getString("contract_id"));
		contract.setContractDescription(rs.getString("contract_description"));
		contract.setValue(rs.getInt("value"));
		contract.setExporterId(rs.getString("exporter_id"));
		contract.setCustomId(rs.getString("custom_id"));
		contract.setInsuranceId(rs.getString("insurance_id"));
		contract.setImporterId(rs.getString("importer_id"));
		contract.setImporterBankId(rs.getString("importerBank_id"));
		contract.setPortOfLoading(rs.getString("port_of_loading"));
		contract.setPortOfEntry(rs.getString("port_of_entry"));
		contract.setExporterCheck(rs.getBoolean("exporterCheck"));
		contract.setCustomCheck(rs.getBoolean("customCheck"));
		contract.setInsuranceCheck(rs.getBoolean("insuranceCheck"));
		contract.setImporterCheck(rs.getBoolean("importerCheck"));
		contract.setImporterBankCheck(rs.getBoolean("importerBankCheck"));
		
		//contract.setImporterCheck(rs.getBoolean("")));
		
		
		
		return contract;
	}

	
	
}
