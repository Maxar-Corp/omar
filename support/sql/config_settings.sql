SELECT name, current_setting(name), source
  FROM pg_settings
--  WHERE source NOT IN ('default', 'override');

