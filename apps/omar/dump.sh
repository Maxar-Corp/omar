#!/bin/sh
export INPUTDB=$1
OUTPUTFILE="${INPUTDB}-data.sql"
TABLES="auth_user auth_user_id_seq geometry_columns raster_data_set raster_data_set_id_seq raster_entry raster_entry_file raster_entry_file_id_seq raster_entry_id_seq raster_entry_search_tag raster_entry_search_tag_id_seq raster_file raster_file_id_seq repository repository_id_seq requestmap requestmap_id_seq role role_auth_user role_id_seq stager_queue_item stager_queue_item_id_seq video_data_set video_data_set_id_seq video_data_set_search_tag video_data_set_search_tag_id_seq video_file video_file_id_seq"


rm -f $OUTPUTFILE

for table in $TABLES;  do
	echo $table;
	pg_dump \
		-U postgres \
		--data-only \
		--column-inserts \
		--disable-triggers \
		--encoding=UNICODE \
		--table=$table \
		$INPUTDB >> $OUTPUTFILE;
done
echo "geometry_columns"
pg_dump \
	-U postgres \
	--data-only \
	--column-inserts \
	--disable-triggers \
	--encoding=UNICODE \
	--table=geometry_columns \
	$INPUTDB >> $OUTPUTFILE;
