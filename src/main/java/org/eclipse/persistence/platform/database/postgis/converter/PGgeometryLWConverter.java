/*
 * Copyright (c) 2015 AdSpore Incorporated
 */

package org.eclipse.persistence.platform.database.postgis.converter;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.locationtech.jts.io.ParseException;
import org.postgis.PGgeometryLW;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;


/**
 * Simple Converter class that permits the usage of PostGIS spatial types with
 * Eclipselink.  This is much simpler than the other conversions that I've seen,
 * it appears that the trick is to specify that the returned type is of PGobject-->PGgeometry,
 * which is the base class upon which all PostgreSQL types are formed.
 * 
 * @author ddevine
 *
 */
public class PGgeometryLWConverter implements Converter {
    private static final long serialVersionUID = -5145526665751136803L;
    private static final Logger LOG = LoggerFactory.getLogger(PGgeometryLWConverter.class);
    public static final String POSTGIS_GEOMETRY_DB_TYPE = "geometry";
    public static final Class<? extends PGobject> POSTGIS_GEOMETRY_CLASS = PGgeometryLW.class;

    

    public PGgeometryLW convertObjectValueToDataValue(Object objectValue, Session session) {
        if ((null == objectValue) || (null == session) || (!(objectValue instanceof org.locationtech.jts.geom.Geometry)))
            return null;
        
        org.postgis.Geometry pgGeometry = null;
        org.locationtech.jts.io.WKTWriter writer = new org.locationtech.jts.io.WKTWriter();
        
        if (objectValue instanceof org.locationtech.jts.geom.Point) {
        	org.locationtech.jts.geom.Point jtsPoint = (org.locationtech.jts.geom.Point) objectValue;
        	String wktPoint = writer.write(jtsPoint);
        	try {
				pgGeometry = new org.postgis.Point(wktPoint);
			} catch (SQLException e) {
				LOG.error("Parse Error while converting JTS POINT", e);
			}
        	pgGeometry.setSrid(jtsPoint.getSRID());
        } else if (objectValue instanceof org.locationtech.jts.geom.Polygon) {
        	org.locationtech.jts.geom.Polygon jtsPolygon = (org.locationtech.jts.geom.Polygon) objectValue;
        	String wktPolygon = writer.write(jtsPolygon);
        	try {
        		pgGeometry = new org.postgis.Polygon(wktPolygon);
        	} catch (SQLException e) {
        		LOG.error("Parse Error while converting JTS POLYGON", e);
        	}
        	pgGeometry.setSrid(jtsPolygon.getSRID());
        }
        return new org.postgis.PGgeometryLW(pgGeometry);
    }
    
    

    public org.locationtech.jts.geom.Geometry convertDataValueToObjectValue(Object dataValue, Session session) {
        if ((null == dataValue) || (null == session) || (!(dataValue instanceof PGgeometryLW)))
            return null;
        
        org.postgis.Geometry pgGeometry = ((PGgeometryLW)dataValue).getGeometry();
        org.locationtech.jts.geom.Geometry jtsGeometry = null;
        org.locationtech.jts.io.WKTReader reader = new org.locationtech.jts.io.WKTReader();
        StringBuffer sb = new StringBuffer();
        
        if (pgGeometry instanceof org.postgis.Point) {
        	org.postgis.Point pgPoint = (org.postgis.Point) pgGeometry;
        	pgPoint.outerWKT(sb);
        	try {
				jtsGeometry = reader.read(sb.toString());
			} catch (ParseException e) {
				LOG.error("Parse Error while converting Postgis POINT", e);
			}
        	jtsGeometry.setSRID(pgPoint.getSrid());
        } else if (pgGeometry instanceof org.postgis.Polygon) {
        	org.postgis.Polygon pgPolygon = (org.postgis.Polygon) pgGeometry;
        	pgPolygon.outerWKT(sb);
        	try {
				jtsGeometry = reader.read(sb.toString());
			} catch (ParseException e) {
				LOG.error("Parse Error while converting Postgis POLYGON", e);
			}
        	jtsGeometry.setSRID(pgPolygon.getSrid());
        }
        return jtsGeometry;
    }



    public boolean isMutable() {
        return false;
    }


    public void initialize(DatabaseMapping mapping, Session session) {
        mapping.getField().setSqlType(java.sql.Types.OTHER); 
    }
}

