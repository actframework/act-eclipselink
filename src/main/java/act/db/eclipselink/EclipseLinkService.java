package act.db.eclipselink;

/*-
 * #%L
 * ACT Hibernate
 * %%
 * Copyright (C) 2015 - 2018 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static act.db.jpa.JPAPlugin.*;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

import act.Act;
import act.app.App;
import act.db.jpa.JPAService;
import act.db.sql.DataSourceConfig;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.platform.server.NoServerPlatform;
import org.osgl.$;
import org.osgl.util.C;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

public class EclipseLinkService extends JPAService {

    public EclipseLinkService(String dbId, App app, Map<String, String> config) {
        super(dbId, app, config);
    }

    @Override
    protected Properties processProperties(Properties properties, DataSourceConfig dataSourceConfig, boolean useExternalDataSource) {
        if (!properties.contains(BATCH_WRITING)) {
            properties.put((BATCH_WRITING), "JDBC");
        }
        properties.put(TARGET_SERVER, NoServerPlatform.class.getName());
        String s = properties.getProperty(CONF_DDL);
        if (null == s) {
            s = Act.isDev() ? CONF_DDL_UPDATE : CONF_DDL_NONE;
        }
        properties.setProperty(DDL_GENERATION, EclipseLinkPlugin.translateDDL(s));

        s = properties.getProperty(CONF_CACHE_ENABLED);
        s = String.valueOf($.bool(s));
        properties.setProperty(CACHE_SHARED_DEFAULT, s);

        // in case it needs Eclipselink built-in connection pool
        if (!useExternalDataSource) {
            // well Eclipselink will call external datasource to getConnection with username or password,
            // which will result in java.sql.SQLFeatureNotSupportedException
            properties.setProperty(JDBC_URL, dataSourceConfig.url);
            properties.setProperty(PersistenceUnitProperties.JDBC_USER, dataSourceConfig.username);
            properties.setProperty(PersistenceUnitProperties.JDBC_PASSWORD, dataSourceConfig.password);
            properties.setProperty("eclipselink.jdbc.connection_pool.default.max", String.valueOf(dataSourceConfig.maxConnections));
            properties.setProperty("eclipselink.jdbc.connection_pool.default.min", String.valueOf(dataSourceConfig.minConnections));
        } else {
            // make suer username is not set while external datasource is used.
            properties.remove(PersistenceUnitProperties.JDBC_USER);
        }

        // TODO interesting we couldn't find out how to set autoCommit and readonly for eclipselink internal connection pool

        return super.processProperties(properties, dataSourceConfig, useExternalDataSource);
    }

    @Override
    protected Class<? extends PersistenceProvider> persistenceProviderClass() {
        return org.eclipse.persistence.jpa.PersistenceProvider.class;
    }

    @Override
    protected EntityManagerFactory createEntityManagerFactory(PersistenceUnitInfo persistenceUnitInfo) {
        org.eclipse.persistence.jpa.PersistenceProvider p = new org.eclipse.persistence.jpa.PersistenceProvider();
        EntityManagerFactoryImpl factory = (EntityManagerFactoryImpl)p.createContainerEntityManagerFactory(persistenceUnitInfo, C.Map());
        ClassLoader sessionLoader = factory.getServerSession().getLoader();
        if (app().classLoader() != sessionLoader) {
            HashMap properties = new HashMap<>();
            properties.put(PersistenceUnitProperties.CLASSLOADER, app().classLoader());
            properties.putAll(persistenceUnitInfo.getProperties());
            factory.refreshMetadata(properties);
        }
        return factory;
    }
}
