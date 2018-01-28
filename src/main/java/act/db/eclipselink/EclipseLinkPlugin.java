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

import act.app.App;
import act.db.DbService;
import act.db.jpa.JPAPlugin;

import java.util.Map;

public class EclipseLinkPlugin extends JPAPlugin {
    @Override
    public DbService initDbService(String id, App app, Map<String, String> conf) {
        return new EclipseLinkService(id, app, conf);
    }

    public static String translateDDL(String ddl) {
        if (CONF_DDL_CREATE.equals(ddl)) {
            return "create-tables";
        } else if (CONF_DDL_UPDATE.equals(ddl)) {
            return "create-or-extend-tables";
        } else if (CONF_DDEL_CREATE_DROP.equals(ddl)) {
            return "drop-and-create-tables";
        } else {
            return "none";
        }
    }

}
