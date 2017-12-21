UPDATE raster_entry SET country_code ='AF' where country_code IS NULL;
UPDATE raster_entry SET mission_id='Y1' where filename LIKE '%Y1%' and mission_id IS NULL;
UPDATE raster_entry SET mission_id='YL' where filename LIKE '%YL%' and mission_id IS NULL;
UPDATE raster_entry SET mission_id='SAMI' where filename LIKE '%SAMI%' and mission_id IS NULL;
UPDATE raster_entry SET mission_id='INUKA' where filename LIKE '%INUKA%' and mission_id IS NULL;
UPDATE raster_entry SET mission_id='KODIAK' where filename LIKE '%KODIAK%' and mission_id IS NULL;

UPDATE raster_entry SET sensor_id='ALPHA' where filename LIKE '%ALPHA%' and sensor_id IS NULL;
UPDATE raster_entry SET sensor_id='BRAVO' where filename LIKE '%BRAVO%' and sensor_id IS NULL;
UPDATE raster_entry SET sensor_id='CHARLIE' where filename LIKE '%CHARLIE%' and sensor_id IS NULL;
UPDATE raster_entry SET sensor_id='DELTA' where filename LIKE '%DELTA%' and sensor_id IS NULL;
