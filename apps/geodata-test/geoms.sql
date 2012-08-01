ALTER TABLE location DROP COLUMN geometry_object;
SELECT AddGeometryColumn( 'location', 'geometry_object', 4326, 'POINT', 2 );
CREATE INDEX location_geometry_object_idx ON location USING GIST ( geometry_object GIST_GEOMETRY_OPS );

ALTER TABLE city DROP COLUMN ground_geom;
SELECT AddGeometryColumn( 'city', 'ground_geom', 4326, 'POINT', 2 );
CREATE INDEX city_ground_geom_idx ON city USING GIST ( ground_geom GIST_GEOMETRY_OPS );

VACUUM ANALYZE;

