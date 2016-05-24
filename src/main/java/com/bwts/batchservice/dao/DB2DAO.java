//package com.bwts.batchservice.dao;
//
//import com.bwts.batchservice.entity.Company;
//import com.bwts.batchservice.entity.Document;
//import org.springframework.beans.factory.ObjectFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.core.namedparam.SqlParameterSource;
//import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class DB2DAO {
//
//    @Autowired
//    @Qualifier(value = "db2JdbcTemplate")
//    private NamedParameterJdbcTemplate jdbcTemplate;
//
//    public void insertInvoiceDetailInfo(List<Document> documentList) {
//        String sql = "INSERT INTO ADMINISTRATOR.InvoiceDetailInfo(CompanyCode, CompanyName, InvoiceAmount, CreateTime, InvoiceNo, InvoiceCode) VALUES(:companyCode, :companyName, :invoiceAmount, :createTime, :invoiceNumber, :invoiceCode)";
//
//        SqlParameterSource[] parameters = SqlParameterSourceUtils.createBatch(documentList.toArray());
//
//        jdbcTemplate.batchUpdate(sql, parameters);
//    }
//
//    public boolean checkInvoiceInfoExists() {
//        String sql = "SELECT * FROM ADMINISTRATOR.InvoiceInfo WHERE InvoiceType = :invoiceType";
//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("invoiceType", "02");
//        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, namedParameters);
//        return !list.isEmpty();
//    }
//
//    public void insertInvoiceInfo(int invoiceNumber) {
//        String sql = "INSERT INTO ADMINISTRATOR.InvoiceInfo(InvoiceType, InvoiceNum) VALUES('02', :invoiceNumber)";
//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("invoiceNumber", invoiceNumber);
//
//        jdbcTemplate.update(sql, namedParameters);
//    }
//
//    public void updateInvoiceInfo(int invoiceNumber) {
//        String sql = "UPDATE ADMINISTRATOR.InvoiceInfo SET InvoiceNum = InvoiceNum + :invoiceNumber WHERE InvoiceType = '02'";
//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("invoiceNumber", invoiceNumber);
//        jdbcTemplate.update(sql, namedParameters);
//    }
//
//    public boolean checkCompanyExists(Company company) {
//        String sql = "SELECT * FROM ADMINISTRATOR.ComInvoiceInfo WHERE CompanyName = :companyName";
//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("companyName", company.getCompanyName());
//
//        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, namedParameters);
//        return !list.isEmpty();
//
//    }
//
//    public void insertComInvoiceInfo(Company company) {
//        String sql = "INSERT INTO ADMINISTRATOR.ComInvoiceInfo(CompanyCode, InvoiceType, InvoiceNum, CompanyName) VALUES(:companyCode, '02', :invoiceNumber, :companyName)";
//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("companyName", company.getCompanyName());
//        namedParameters.addValue("companyCode", company.getCompanyCode());
//        namedParameters.addValue("invoiceNumber", company.getInvoiceCount());
//
//        jdbcTemplate.update(sql, namedParameters);
//    }
//
//    public void updateComInvoiceInfo(Company company) {
//        String sql = "UPDATE ADMINISTRATOR.ComInvoiceInfo SET InvoiceNum = InvoiceNum + :invoiceNumber WHERE CompanyName = :companyName";
//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        namedParameters.addValue("companyName", company.getCompanyName());
//        namedParameters.addValue("invoiceNumber", company.getInvoiceCount());
//
//        jdbcTemplate.update(sql, namedParameters);
//    }
//}


package com.bwts.batchservice.dao;

import com.bwts.batchservice.entity.Company;
import com.bwts.batchservice.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DB2DAO {

    @Autowired
    @Qualifier(value = "db2JdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void insertInvoiceDetailInfo(List<Document> documentList) {
        String sql = "INSERT INTO DZPT_PJ_ZZSPDZ_FPMX(ID, XHDWDM, XHDWMC, HJJE, KPRQ, FPHM, FPDM) VALUES(" + getId() + ", :companyCode, :companyName, :invoiceAmount, :createTime, :invoiceNumber, :invoiceCode)";

        SqlParameterSource[] parameters = SqlParameterSourceUtils.createBatch(documentList.toArray());

        jdbcTemplate.batchUpdate(sql, parameters);
    }

    public boolean checkInvoiceInfoExists() {
        String sql = "SELECT * FROM InvoiceInfo WHERE InvoiceType = :invoiceType";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("invoiceType", "02");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, namedParameters);
        return !list.isEmpty();
    }

    public void insertInvoiceInfo(int invoiceNumber) throws InterruptedException {
        String sql = "INSERT INTO InvoiceInfo(SERIALNO, InvoiceType, InvoiceNum) VALUES(" + getId() + ", '02', :invoiceNumber)";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("invoiceNumber", invoiceNumber);

        jdbcTemplate.update(sql, namedParameters);
    }

    public void updateInvoiceInfo(int invoiceNumber) {
        String sql = "UPDATE InvoiceInfo SET InvoiceNum = InvoiceNum + :invoiceNumber WHERE InvoiceType = '02'";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("invoiceNumber", invoiceNumber);
        jdbcTemplate.update(sql, namedParameters);
    }

    public boolean checkCompanyExists(Company company) {
        String sql = "SELECT * FROM ComInvoiceInfo WHERE CompanyName = :companyName";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("companyName", company.getCompanyName());

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, namedParameters);
        return !list.isEmpty();

    }

    public void insertComInvoiceInfo(Company company) throws InterruptedException {
        String sql = "INSERT INTO ComInvoiceInfo(SERIALNO, CompanyCode, InvoiceType, InvoiceNum, CompanyName) VALUES(" + getId() + ", :companyCode, '02', :invoiceNumber, :companyName)";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("companyName", company.getCompanyName());
        namedParameters.addValue("companyCode", company.getCompanyCode());
        namedParameters.addValue("invoiceNumber", company.getInvoiceCount());

        jdbcTemplate.update(sql, namedParameters);
    }

    public void updateComInvoiceInfo(Company company) {
        String sql = "UPDATE ComInvoiceInfo SET InvoiceNum = InvoiceNum + :invoiceNumber WHERE CompanyName = :companyName";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("companyName", company.getCompanyName());
        namedParameters.addValue("invoiceNumber", company.getInvoiceCount());

        jdbcTemplate.update(sql, namedParameters);
    }

    private long getId() {
        String time = System.currentTimeMillis() + "";
        long t = Long.parseLong(time.substring(7));
        long random = (long)(Math.random() * 1000);
        return t * 1000 + random;
    }
}
