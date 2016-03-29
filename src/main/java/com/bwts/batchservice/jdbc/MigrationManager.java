package com.bwts.batchservice.jdbc;

import com.carbonfive.db.migration.DataSourceMigrationManager;
import com.carbonfive.db.migration.ResourceMigrationResolver;
import com.carbonfive.db.migration.SimpleVersionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public class MigrationManager {
    private static final Logger log = LoggerFactory.getLogger(MigrationManager.class);

    private final DataSource dataSource;
    private final String migrationsLocation;

    public MigrationManager(DataSource dataSource, String migrationsLocation) {
        this.dataSource = dataSource;
        this.migrationsLocation = migrationsLocation;
    }

    @PostConstruct
    public void migrateIfEnabled() {
        // Only run migrations once
        log.info("Migrations enabled, running.");
        DataSourceMigrationManager mgr = new DataSourceMigrationManager(dataSource);
        mgr.setVersionStratgey(new SimpleVersionStrategy());
        ResourceMigrationResolver resolver = new ResourceMigrationResolver();
        if (migrationsLocation != null) {
            resolver.setMigrationsLocation(migrationsLocation);
        }
        mgr.setMigrationResolver(resolver);
        mgr.migrate();
    }
}
