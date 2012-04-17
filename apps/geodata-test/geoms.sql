ALTER TABLE location DROP COLUMN ais_geom;
SELECT AddGeometryColumn( 'location', 'ais_geom', 4326, 'POINT', 2 );
CREATE INDEX location_ais_geom_idx ON location USING GIST ( ais_geom GIST_GEOMETRY_OPS );

ALTER TABLE city DROP COLUMN ground_geom;
SELECT AddGeometryColumn( 'city', 'ground_geom', 4326, 'POINT', 2 );
CREATE INDEX city_ground_geom_idx ON city USING GIST ( ground_geom GIST_GEOMETRY_OPS );

VACUUM ANALYZE;

