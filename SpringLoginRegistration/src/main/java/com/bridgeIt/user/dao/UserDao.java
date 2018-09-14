package com.bridgeIt.user.dao;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import com.bridgeIt.user.model.Contract;
import com.bridgeIt.user.model.TempUser;
import com.bridgeIt.user.model.User;
@Repository
public class UserDao {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate template;
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public boolean insertBeforeAcc(User user) throws SerialException, SQLException {
		// MapSqlParameterSource in = new MapSqlParameterSource();
	//	 new SqlLobValue(bytes) new SerialBlob(myArray )
//		template = new JdbcTemplate(dataSource);
		System.out.println(user.getPassword()+" -------plain password");
		String hashPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		
		Object [] args = {user.getEmail(),user.getName(),hashPassword,user.getMobileNo(),user.getCity(),user.getRole(),user.isVerified(),user.getAuthenticatedUserKey(),user.getBalance(),user.getBank()};
	//	System.out.println(dataSource);
		System.out.println(hashPassword);
		int out=0;
		try {
			System.out.println(user);
			out = template.update("insert into UserLogin(email,name,password,mobileNo,city,role,verified,authenticated_user_key,balance,bank) values (?,?,?,?,?,?,?,?,?,?)", args);
			System.out.println("number rows affected "+out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("insert template not excuted..");
			return false;
		}

	}
	
	public boolean insert(User user) throws SerialException, SQLException {
		// MapSqlParameterSource in = new MapSqlParameterSource();
	//	 new SqlLobValue(bytes) new SerialBlob(myArray )
//		template = new JdbcTemplate(dataSource);
		
		String hashPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		
		Object [] args = {user.getEmail(),user.getName(),hashPassword,user.getMobileNo(),user.getCity(),user.getRole(),user.isVerified(),user.getAuthenticatedUserKey(),user.getAccountNumber(),user.getBalance(),user.getBank(),new SerialBlob(user.getUserAccount())};
		System.out.println(dataSource);
		System.out.println(hashPassword);
		int out=0;
		try {
			System.out.println(user);
			out = template.update("insert into UserLogin(email,name,password,mobileNo,city,role,verified,authenticated_user_key,account_number,balance,bank,user_account) values (?,?,?,?,?,?,?,?,?,?,?,?)", args);
			System.out.println("number rows affected "+out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("insert template not excuted..");
			return false;
		}

	}
	
	public boolean updateUserAccountAndAccountNo(String accountNumber,byte [] Useraccount,String email) throws SerialException, SQLException {
		
		Object [] args = {accountNumber,new SerialBlob(Useraccount),email};
		
		String sql = "update UserLogin set account_number = ? , user_account = ? where email = ?";
		
		try {
			int row = template.update(sql, args);
			System.out.println(row+" rows affected");
			return true;
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	public boolean existence (User user) {
		

		
		System.out.println(user);
	//	Object [] args = {user.getEmail()};
		String sql = "select name from UserLogin where email = ?";
		
		try {
			
			String name = (String)template.queryForObject(
					sql, new Object[] { user.getEmail()}, String.class);
			
			System.out.println(name);
			System.out.println("abcd");
			return false;
		} catch (DataAccessException e) {
			e.printStackTrace();
			System.out.println(user);
			return true;
		}
		
	}
	
	
	public boolean presence (User user) {

	
		System.out.println(user);
		Object [] args = {user.getEmail()};
		String sql="select * from UserLogin where email=?";
		
		List<User> users=template.query(sql, args, new UserMapper());
		if(users.isEmpty()!=true) {
			System.out.println(" presence returning true");
			return true;
		}
		return false;
		
	}
	

	public boolean checkUser(String email, String password ) {
	System.out.println(email+"---"+password);
	Object [] args = {email};
	
	String sql="select * from UserLogin where email = ?";
	
	List<User> user=template.query(sql, args, new UserMapper());
	
	if(user.isEmpty()!=true ) {
		User user1=user.get(0);
		System.out.println(user1);
		System.out.println(BCrypt.checkpw(password, user1.getPassword()) + " bcrypt  "+user1.getPassword()+"---"+password);
		System.out.println(user1.isVerified() + " verifed");
		if(BCrypt.checkpw(password, user1.getPassword()) && user1.isVerified() ) {
			
			return true;
		}
		return false;
	}else {
		return false;
	} 
	
	}
	
	
/*	public boolean getVerified(String email ) {
		
		template =new JdbcTemplate(dataSource);
		boolean verified=true;
		Object [] args = {verified,email};
		System.out.println(email+"--------id");
		String sql="update UserLogin set verified = ? where email = ?";
		try {
		int res=template.update(sql, args);
		System.out.println(res);
		} catch (Exception e) {		
			e.printStackTrace();
			return false;
		}
		
		return true;
	}*/
	
	public boolean getVerified(String uniqueId) {
		
		
		boolean verified = true;
		Object [] args= {verified,uniqueId};
		
		String sql = "update UserLogin set verified = ? where authenticated_user_key = ?";
		try {
			int res=template.update(sql, args);
			System.out.println(res);
			
			if(res==1) {
				return true;
			}else {
				return false;
			}
			} catch (Exception e) {		
				e.printStackTrace();
				return false;
			}
	}
	
	
	public User fetchUserByEmail(String email) {
	
		Object [] args = {email};
		
		String sql="select * from UserLogin where email=?";
		List<User> user=template.query(sql, args, new UserMapper());
		User user1=user.get(0);
		return user1;
	}
	
	public boolean resetPassword(String uuid , String newPassword) {
		Object[] args = {newPassword,uuid};
		
		String sql="update UserLogin set password = ? where authenticated_user_key = ?";
		
		try {
			int res=template.update(sql, args);
			System.out.println(res);
			
			if(res==1) {
				System.out.println("true--password updated");
				return true;
			}else {
				return false;
			}
			} catch (Exception e) {		
				e.printStackTrace();
				return false;
			}
	}
	
	
	public String getUUid(String email) {
		
		Object[] args = {email};
		
		String sql = "select authenticated_user_key from UserLogin where email = ?";
		
		
		List<String> userIds = null;
		userIds = template.queryForList(sql, String.class, args);
		System.out.println(userIds);
		
		if(userIds.isEmpty()) {
			System.out.println(true);
			return null;
		}
		String uuid=userIds.get(0);
		System.out.println(uuid);
		return uuid;
	}
	
	public void insertForgotPassword(User user) {
		System.out.println("inside insert forget password");
		java.util.Date date = new java.util.Date();
        long t = date.getTime();
        long t1 = t+720000;
        
        System.out.println(t);
        java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
        java.sql.Timestamp sqlTimestamp1 = new java.sql.Timestamp(t1);
		
		Object[] args = {user.getAuthenticatedUserKey(),user.getEmail(),sqlTimestamp,sqlTimestamp1};
		
		String sql ="insert into resetPassword (authenticated_user_key,email,starting_interval,ending_interval) values (?,?,?,?) ";
		System.out.println("before template run");
		int rows = 0;
		try {
			rows = template.update(sql, args);
		} catch (DataAccessException e) {
			
			e.printStackTrace();
		}
		System.out.println("before template run");
		System.out.println(rows);
		
		
	}
	
	public int  checkSession(String uuid) {
		
		 
		 Object [] args = {uuid};
		 
		 String sql = "select * from resetPassword where authenticated_user_key = ? ";
		 
		 List<TempUser> users = null;
		try {
			users = template.query(sql, args, new TempUserMapper());
			System.out.println(users);
		} catch (DataAccessException e) {
			
			e.printStackTrace();
		}
		 
		 if(users.isEmpty()) {
			 
			 return -1;
			 
		 }
		 
		 TempUser user = users.get(0);
		 
		 System.out.println(user.getStartingInterval()+"-------"+user.getEndingInterval());
		 java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
		 System.out.println(currentTime);
		 
		 
		 return user.getEndingInterval().compareTo(currentTime);
		 
	}
	
	public int removeTempUser(String uuid) {
			
		Object [] args = {uuid};
	
		String sql ="delete from resetPassword where authenticated_user_key = ?";
		
		int row = 0;
		try {
			row = template.update(sql, args);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		
		return row;
	}
	
	public boolean uniqueAccountNumber(String accountNumber) {
		
		Object [] args = {accountNumber};
		String sql = "select * from UserLogin where account_number = ?";
		
		 List<User> users = null;
		 
		 users = template.query(sql, args, new UserMapper());
		 
		 if(users.isEmpty()) {
			 
			 return true;
		 }
		
		return false;
	}
	
	public boolean  updateBalance(String accountNumber,int balance) {
		
		Object [] args = {balance,accountNumber};
		String sql = "update UserLogin set balance = ? where account_number = ?";
		
		try {
			int updatedRow = template.update(sql, args);
			System.out.println(updatedRow+" row are affected..");
			return true;
		} catch (Exception e) {
		
			e.printStackTrace();
			return false;
		}

	}
	
	public byte []  getUserTradeAccount (String accountNumber) {
		Object [] args = {accountNumber};
		
		String sql = "select user_account from UserLogin where account_number = ?";
		
		List<Blob> userBlobs =template.queryForList(sql, Blob.class, args);
		
		Blob userBlob =userBlobs.get(0);
		byte [] userByte = null;
		try {
			userByte =userBlob.getBytes(1, (int)userBlob.length());
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return userByte;
	}
	
	
	public boolean saveContract (Contract contract) {
		
		Object [] args = {contract.getContractId(),contract.getContractDescription(),contract.getValue(),contract.getExporterId(),contract.getCustomId(),contract.getInsuranceId(),contract.getImporterId(),contract.getImporterBankId(),contract.getPortOfLoading(),contract.getPortOfEntry(),contract.isExporterCheck(),contract.isCustomCheck(),contract.isInsuranceCheck(),contract.isImporterCheck(),contract.isImporterBankCheck(),contract.isCompletion(),contract.getPointer()};
		
		String sql ="insert into UserContract (contract_id,contract_description,value,exporter_id,custom_id,insurance_id,importer_id,importerBank_id,port_of_loading,port_of_entry,exporterCheck,customCheck,insuranceCheck,importerCheck,importerBankCheck,completion,pointer) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try {
			int row = template.update(sql, args);
			System.out.println(row+" rows affected...");
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public boolean uniqueContract(String contractId) {
		
		Object [] args = {contractId};
		String sql = "select * from UserContract where contract_id = ?";
		
		List<Contract> contractList = template.query(sql, args, new ContractMapper());
		if (contractList.isEmpty()) {	
			return true;
		}
		
		
		return false;
	}
	
	public boolean updateContract(Contract contract) {
		
		Object [] args = {contract.getContractDescription(),contract.getValue(),contract.getExporterId(),contract.getCustomId(),contract.getInsuranceId(),contract.getImporterId(),contract.getImporterBankId(),contract.getPortOfLoading(),contract.getPortOfEntry(),contract.isExporterCheck(),contract.isCustomCheck(),contract.isInsuranceCheck(),contract.isImporterCheck(),contract.isImporterBankCheck(),contract.getPointer(),contract.getContractId()};
		String sql= "update UserContract set contract_description = ?,value=?,exporter_id=?,custom_id=?,insurance_id=?,importer_id=?,importerBank_id=?,port_of_loading=?,port_of_entry=?,exporterCheck=?,customCheck=?,insuranceCheck=?,importerCheck=?,importerBankCheck=?,pointer=? where contract_id =?";
		
		try {
			int rows = template.update(sql, args);
			System.out.println(rows+" rows affected..");
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return false;
	}
	
	public boolean contractUpdateFromBC(Contract contract) {
		
		Object [] args = {contract.getContractDescription(),contract.getValue(),contract.getExporterId(),contract.getCustomId(),contract.getInsuranceId(),contract.getImporterId(),contract.getImporterBankId(),contract.getPortOfLoading(),contract.getPortOfEntry(),contract.isExporterCheck(),contract.isCustomCheck(),contract.isInsuranceCheck(),contract.isImporterCheck(),contract.isImporterBankCheck(),contract.getContractId()};
		String sql= "update UserContract set contract_description = ?,value=?,exporter_id=?,custom_id=?,insurance_id=?,importer_id=?,importerBank_id=?,port_of_loading=?,port_of_entry=?,exporterCheck=?,customCheck=?,insuranceCheck=?,importerCheck=?,importerBankCheck=? where contract_id =?";
		
		try {
			int rows = template.update(sql, args);
			System.out.println(rows+" rows affected..");
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return false;
		
		
	}
	
	public boolean deleteContract(String contractId) {
		
		Object [] args = {contractId};
		
		String sql = "delete from UserContract where contract_id =?";
		
		try {
			int rows = template.update(sql, args);
			System.out.println(rows+ " rows affected");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public Contract getContract(String contractId) {
		
		
		Object [] args = {contractId};
		
		String sql = "select * from UserContract where contract_id = ?";
		
		List<Contract> contractList = template.query(sql, args, new ContractMapper());
		if (contractList.isEmpty()) {	
			return null;
		}
		
		Contract contract = contractList.get(0);
		
		return contract;
	}
	
	public List<Contract> gellAllContract(String userId,String role) {
		
		Object [] args = {userId};
		String sql =null;
		
		
		switch (role) {
		
		case "exporter" :{
			
			sql = "select * from UserContract where exporter_id = ?";
			break;
		} 
		case "custom" :{
			sql = "select * from UserContract where custom_id = ?";
			break;
			
		}
		
		case "insurance" :{
			sql = "select * from UserContract where insurance_id = ?";
			break;
			
		}
		
		case "importer" : {
			
			sql = "select * from UserContract where importer_id = ?";
			break;
			
		}
		
		case "importerBank" : {
			
			sql = "select * from UserContract where importerBank_id = ?";
			break;
			
		}
		
		default:{
			
			break;
		}
		
		
		}
		
		List<Contract> contractList = null;
		try {
			contractList = template.query(sql, args, new ContractMapper());
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
		return contractList;
	}
	
	
	
	
	public User getUserByEmail(String email) {
		
		Object [] args = {email};
		String sql = "select * from UserLogin where email = ?";
		List<User> usersList = null;
		try {
			usersList  = template.query(sql, args, new UserMapper());
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
		User user = usersList.get(0);
		
		return user;
		
	}
	
	public boolean completionOfContract(String contractId) {
		
		Object [] args = {true,contractId};
		String sql = "update UserContract set completion = ? where contract_id =?";
		
		try {
			int rows = template.update(sql, args);		
			System.out.println(rows+" rows affected");
			System.out.println("completion of contract");
			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
		
		
		
	}
	
	
	
}
