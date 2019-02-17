/*
 * Copyright (c) 2015 AdSpore Incorporated
 */

package org.eclipse.persistence.platform.database.postgis.converter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;


/**
 * Simple Converter class that permits the usage of PostGIS spatial types with
 * Eclipselink.  This is much simpler than the other conversions that I've seen,
 * it appears that the trick is to specify that the returned type is of PGobject-->PGgeometry,
 * which is the base class upon which all PostgreSQL types are formed.
 * 
 * @author ddevine
 *
 */
public class PGgeometryConverter implements Converter {
    private static final long serialVersionUID = 4516918363697406455L;
    public static final String POSTGIS_GEOMETRY_DB_TYPE = "geometry";
    public static final Class<? extends PGobject> POSTGIS_GEOMETRY_CLASS = PGgeometry.class;

    

    public PGgeometry convertObjectValueToDataValue(Object objectValue, Session session) {
        if ((null == objectValue) || (null == session) || (!(objectValue instanceof org.postgis.Geometry)))
            return null;
        return new org.postgis.PGgeometry((org.postgis.Geometry)objectValue);
    }
    
    

    public Geometry convertDataValueToObjectValue(Object dataValue, Session session) {
        if ((null == dataValue) || (null == session) || (!(dataValue instanceof PGgeometry)))
            return null;
        return ((PGgeometry)dataValue).getGeometry();
    }



    public boolean isMutable() {
        return false;
    }


    public void initialize(DatabaseMapping mapping, Session session) {
        mapping.getField().setSqlType(java.sql.Types.OTHER); 
    }
}
