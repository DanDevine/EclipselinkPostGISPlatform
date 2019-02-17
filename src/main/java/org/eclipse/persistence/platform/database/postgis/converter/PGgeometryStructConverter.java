/*
 * Copyright (c) 2015 AdSpore Incorporated
 */

package org.eclipse.persistence.platform.database.postgis.converter;

import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.postgis.Geometry;
import org.postgis.PGgeometry;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;


public class PGgeometryStructConverter implements StructConverter {
    public static final String POSTGIS_GEOMETRY_DB_TYPE = "POSTGIS_GEOMETRY_DB_TYPE";
    public static final Class POSTGIS_GEOMETRY_CLASS = org.postgis.PGgeometry.class;



    public String getStructName() {
        return POSTGIS_GEOMETRY_DB_TYPE;
    }


    public Class getJavaType() {
        return POSTGIS_GEOMETRY_CLASS;
    }


    public Geometry convertToObject(Struct struct) throws SQLException {
        if ((null == struct) || (!(struct instanceof PGgeometry)))
            return null;
        return ((PGgeometry)struct).getGeometry();
    }


    public Struct convertToStruct(Object struct, Connection connection) throws SQLException {
        if ((null == struct) || (null == connection) || (!(struct instanceof org.postgis.Geometry)))
            return null;
        Struct result = (Struct) new org.postgis.PGgeometry((org.postgis.Geometry)struct);
        return result;
    }
}
