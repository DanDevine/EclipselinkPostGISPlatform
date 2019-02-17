/*
 * Copyright (c) 2015 AdSpore Incorporated
 */

package org.eclipse.persistence.platform.database.postgis.converter;

import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.postgis.PGgeometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

public class JTSPointStructConverter  implements StructConverter {
	private static final Logger LOG = LoggerFactory.getLogger(JTSPointStructConverter.class);
	public static final String POSTGIS_GEOMETRY_DB_TYPE = "POSTGIS_POING_GEOMETRY_DB_TYPE";
	public static final Class POSTGIS_GEOMETRY_CLASS = org.postgis.PGgeometry.class;



	public String getStructName() {
		return POSTGIS_GEOMETRY_DB_TYPE;
	}


	public Class getJavaType() {
		return POSTGIS_GEOMETRY_CLASS;
	}


	public org.locationtech.jts.geom.Point convertToObject(Struct struct) throws SQLException {
		if ((null == struct) || (!(struct instanceof PGgeometry)))
			return null;

		org.postgis.Geometry pgGeometry = ((PGgeometry)struct).getGeometry();
		org.locationtech.jts.geom.Point jtsPoint = null;
		
		if (pgGeometry instanceof org.postgis.Point) {
			//	Determined that the incoming object is actually a proper PostGIS point
			//	Convert by using WKT a the intermediate
			
			StringBuffer sb = new StringBuffer();
			pgGeometry.outerWKT(sb);
			org.locationtech.jts.io.WKTReader reader = new org.locationtech.jts.io.WKTReader();
			
			try {
				jtsPoint = (Point) reader.read(sb.toString());
				jtsPoint.setSRID(pgGeometry.getSrid());
			} catch (ParseException e) {
				LOG.error("Caught parse exception while converting pgPoint to jtsPoint exception:{}", e.toString());
			}
		}
		return jtsPoint;
	}

	

	public Struct convertToStruct(Object struct, Connection connection) throws SQLException {
		if ((null == struct) || (null == connection) || (!(struct instanceof org.locationtech.jts.geom.Geometry)))
			return null;
		
		org.locationtech.jts.geom.Geometry jtsGeometry = (org.locationtech.jts.geom.Geometry) struct;
		org.postgis.Point pgPoint = null;
		if (jtsGeometry instanceof org.locationtech.jts.geom.Point) {
			//	Determined that the incoming object is actually a proper JTS Point
			//	Convert by using WKT as the intermediary
			
			org.locationtech.jts.io.WKTWriter writer = new org.locationtech.jts.io.WKTWriter();
			String wktPoint = writer.write(jtsGeometry);
			
			pgPoint = new org.postgis.Point(wktPoint); 
		}
		return (Struct) pgPoint;
	}
}
