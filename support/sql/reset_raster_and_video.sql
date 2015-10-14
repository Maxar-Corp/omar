ALTER SEQUENCE raster_file_id_seq RESTART WITH 1;
ALTER SEQUENCE raster_entry_file_id_seq RESTART WITH 1;
ALTER SEQUENCE raster_entry_id_seq RESTART WITH 1;
ALTER SEQUENCE raster_data_set_id_seq RESTART WITH 1;
ALTER SEQUENCE video_file_id_seq RESTART WITH 1;
ALTER SEQUENCE video_data_set_id_seq RESTART WITH 1;
delete from raster_file;
delete from raster_entry_file;
delete from raster_entry;
delete from raster_data_set;
delete from video_file;
delete from video_data_set;

