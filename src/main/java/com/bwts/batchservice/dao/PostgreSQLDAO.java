package com.bwts.batchservice.dao;

import com.bwts.batchservice.entity.Company;
import com.bwts.batchservice.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
public class PostgreSQLDAO {

    @Autowired
    @Qualifier(value = "postgreSQLJdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Document> getDocumentList(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT\n" +
                "  organization_name,\n" +
                "  organization_tax_number,\n" +
                "  total_in_tax,\n" +
                "  effective_time,\n" +
                "  invoice_number,\n" +
                "  invoice_code\n" +
                "FROM tenant_organization t INNER JOIN document d ON t.tenant_id = d.tenant_id\n" +
                "  INNER JOIN document_invoice_data did ON d.id = did.document_id\n" +
                "WHERE d.id = d.iid AND d.flow_status = 1 AND d.effective_time >= :startTime AND d.effective_time < :endTime";

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("startTime", startTime);
        namedParameters.addValue("endTime", endTime);
        return jdbcTemplate.query(sql, namedParameters, new DocumentMapper());
    }

    public List<Company> getCompanyList(Timestamp startTime, Timestamp endTime) {
        String sql = "SELECT\n" +
                "  organization_name,\n" +
                "  organization_tax_number,\n" +
                "  count(*)\n" +
                "FROM tenant_organization t INNER JOIN document d ON t.tenant_id = d.tenant_id\n" +
                "WHERE d.id = d.iid AND d.effective_time >= :startTime AND d.effective_time < :endTime\n" +
                "GROUP BY organization_name, organization_tax_number;";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("startTime", startTime);
        namedParameters.addValue("endTime", endTime);
        return jdbcTemplate.query(sql, namedParameters, new CompanyMapper());
    }

    private class DocumentMapper implements RowMapper<Document> {

        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document document = new Document();
            document.setCompanyName(rs.getString("organization_name"));
            document.setCompanyCode(rs.getString("organization_tax_number"));
            document.setCreateTime(rs.getTimestamp("effective_time"));
            document.setInvoiceAmount(rs.getFloat("total_in_tax"));
            document.setInvoiceNumber(rs.getString("invoice_number"));
            document.setInvoiceCode(rs.getString("invoice_code"));
            return document;
        }
    }

    private class CompanyMapper implements RowMapper<Company> {
        @Override
        public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
            Company company = new Company();
            company.setCompanyName(rs.getString("organization_name"));
            company.setCompanyCode(rs.getString("organization_tax_number"));
            company.setInvoiceCount(rs.getInt("count"));
            return company;
        }
    }
}
