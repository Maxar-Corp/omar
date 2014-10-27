package org.ossim.omar

class DiskCache {

  String directory
  /**
   * This can be of type SUB_DIRECTORY or of type DEDICATED.
   *
   * If the type is SUB_DIRECTORY then sizes are calculated by scanning the contents
   * and if it's DEDICATED then the entire device is used as the cache and calculating remaining size is much more efficient.
   *
   */
  String directoryType
  Integer maxSize
  Integer currentSize = 0

  /**
   * This is an ISO8601 Period format for defining lengths of time
   * any job can be cached.  If you want jobs to stay around for
   * 7 days then the format is P7D (7 days) or P1W ( one week).
   *
   * This can be used by other processes to expire data as it ages
   *
   */
  String  expirePeriod

  static mapping = {
    directory     index:"disk_cache_directory_idx"
    directoryType index:"disk_cache_directory_type_idx"
  }

  static constraints = {
    directory         nullable:false, blank:false, unique:true, validator: { value ->
      (new File(value)).exists() }
    directoryType     nullable:false, blank:false, unique:false
    maxSize           nullable:true, unique:false
    currentSize       nullable:false, unique:false
    expirePeriod      nullable:true, unique:false
  }
}
