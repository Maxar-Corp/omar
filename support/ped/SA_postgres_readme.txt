This archive provides an SQL script which will add "value-added" data to fields provided in the base OMAR server.
When scheduled via crontab, it provides a "no" maintenance method of adding metadata to assist in searching the datasets
visible within OMAR.

It contains:
/opt/radiantblue/ossim/omar/scripts/update_raster_SATURN_ARCH.sql	- The SQL statement to populate additional fields in the database
/opt/radiantblue/bin/omar-postgres-update				- The script to run the SQL statements
/opt/radiantblue/crontab/SA_crontab.txt					- a sample of the crontab entry to schedule the script


INSTALLATION:
run as omar

This archive should be unzipped from / on the OMAR server.

change the permissions on the script:
chmod 755 /opt/radiantblue/bin/omar-postgres-update

Update the crontab - to make sure anything that is already in the crontab remains, add it to the SA_crontab.txt file:
crontab -l >>/opt/radiantblue/crontab/SA_crontab.txt

to install the new crontab:
crontab < /opt/radiantblue/crontab/SA_crontab.txt
