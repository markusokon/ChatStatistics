package de.markus.statbot.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Configuration;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Erlaubt die Benutzung von EclipseLink als JPA Vendor
 *
 * @author Nils Bauer
 *
 */
@Configuration
public class EclipseLinkJpaConfiguration extends JpaBaseConfiguration {

    /**
     * @param dataSource
     * @param properties
     * @param jtaTransactionManager
     * @param transactionManagerCustomizers
     */
    protected EclipseLinkJpaConfiguration(DataSource dataSource, JpaProperties properties,
                                          ObjectProvider<JtaTransactionManager> jtaTransactionManager,
                                          ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManager, transactionManagerCustomizers);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration#
     * createJpaVendorAdapter()
     */
    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration#
     * getVendorProperties()
     */
    @Override
    protected Map<String, Object> getVendorProperties() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(PersistenceUnitProperties.WEAVING, detectWeavingMode());
        // Erstellt automatisch die Tabellen und erweitert sie bei Änderungen
        map.put(PersistenceUnitProperties.DDL_GENERATION, "create-or-extend-tables");
        return map;
    }

    /**
     * Erkennt ob Weaving benutzt werden kann
     *
     * @return "true" falls ja, "static" falls nein
     */
    private String detectWeavingMode() {
        return InstrumentationLoadTimeWeaver.isInstrumentationAvailable() ? "true" : "static";
    }

}

