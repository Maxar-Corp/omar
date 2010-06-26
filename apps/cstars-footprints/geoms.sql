
ALTER TABLE image_footprint DROP COLUMN ground_geom;
SELECT AddGeometryColumn( 'image_footprint', 'ground_geom', 4326, 'GEOMETRY', 2 );
CREATE INDEX image_footprint_ground_geom_idx ON image_footprint USING GIST ( ground_geom GIST_GEOMETRY_OPS );

VACUUM ANALYZE;

