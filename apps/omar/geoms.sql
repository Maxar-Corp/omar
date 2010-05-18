ALTER TABLE raster_entry_metadata DROP COLUMN ground_geom;
SELECT AddGeometryColumn( 'raster_entry_metadata', 'ground_geom', 4326, 'POLYGON', 2 );
CREATE INDEX raster_entry_metadata_ground_geom_idx ON raster_entry_metadata USING GIST ( ground_geom GIST_GEOMETRY_OPS );

ALTER TABLE video_data_set_metadata DROP COLUMN ground_geom;
SELECT AddGeometryColumn( 'video_data_set_metadata', 'ground_geom', 4326, 'GEOMETRY', 2 );
CREATE INDEX video_data_set_metadata_ground_geom_idx ON video_data_set_metadata USING GIST ( ground_geom GIST_GEOMETRY_OPS );

VACUUM ANALYZE;

