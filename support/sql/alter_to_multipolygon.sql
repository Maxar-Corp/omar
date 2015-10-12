ALTER TABLE raster_entry ALTER COLUMN ground_geom SET DATA TYPE geometry(MultiPolygon,4326) USING ST_Multi(ground_geom);
