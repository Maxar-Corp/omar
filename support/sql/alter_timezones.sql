alter table raster_entry alter COLUMN acquisition_date type timestamp with time zone;
alter table raster_entry alter COLUMN access_date type timestamp with time zone;
alter table raster_entry alter COLUMN ingest_date type timestamp with time zone;
alter table raster_entry alter COLUMN receive_date type timestamp with time zone;
alter table video_data_set alter COLUMN start_date type timestamp with time zone;
alter table video_data_set alter COLUMN end_date type timestamp with time zone;

