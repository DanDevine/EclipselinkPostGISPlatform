package org.eclipse.persistence.platform.database;

import java.util.Hashtable;

import org.postgis.PGbox2d;
import org.postgis.PGbox3d;
import org.postgis.PGboxbase;
import org.postgis.PGgeometry;
import org.postgis.PGgeometryLW;
import org.postgresql.util.PGobject;


public class PostGISPlatform extends PostgreSQLPlatform {
    private static final long serialVersionUID = 89301276290444151L;


    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = super.buildFieldTypes();

        /*
         * Register the types that are known to the PostGIS jdbc driver
         */
        fieldTypeMapping.put(PGobject.class, java.sql.Types.OTHER);
        fieldTypeMapping.put(PGgeometry.class, java.sql.Types.OTHER);
        fieldTypeMapping.put(PGgeometryLW.class, java.sql.Types.OTHER);
        fieldTypeMapping.put(PGboxbase.class, java.sql.Types.OTHER);
        fieldTypeMapping.put(PGbox2d.class, java.sql.Types.OTHER);
        fieldTypeMapping.put(PGbox3d.class, java.sql.Types.OTHER);
        return fieldTypeMapping;
    }
}
