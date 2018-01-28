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

import act.Act;
import act.app.App;
import act.db.jpa.JPAService;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.osgl.util.C;

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
    protected Properties processProperties(Properties properties) {
        if (!properties.contains((PersistenceUnitProperties.BATCH_WRITING))) {
            properties.put((PersistenceUnitProperties.BATCH_WRITING), "JDBC");
        }
        String s = properties.getProperty(CONF_DDL);
        if (null == s) {
            s = Act.isDev() ? CONF_DDL_UPDATE : CONF_DDL_NONE;
        }
        properties.setProperty("eclipselink.ddl-generation", EclipseLinkPlugin.translateDDL(s));
        //properties.setProperty(COMPOSITE_UNIT, "true");
        //properties.setProperty(WEAVING, "false");
        return super.processProperties(properties);
    }

    @Override
    protected Class<? extends PersistenceProvider> persistenceProviderClass() {
        return org.eclipse.persistence.jpa.PersistenceProvider.class;
    }

    @Override
    protected EntityManagerFactory createEntityManagerFactory(PersistenceUnitInfo persistenceUnitInfo) {
        org.eclipse.persistence.jpa.PersistenceProvider p = new org.eclipse.persistence.jpa.PersistenceProvider();
        return p.createContainerEntityManagerFactory(persistenceUnitInfo, C.Map());
    }
}
