package org.ossim.omar.core
class Repository
{
  String baseDir
  Date scanStartDate
  Date scanEndDate

  static constraints = {
    baseDir(unique: true, blank: false)
    scanStartDate(nullable: true)
    scanEndDate(nullable: true)
  }

  static mapping = {
    columns {
      baseDir column: 'repository_base_dir', index: 'repository_base_dir_idx'
    }
  }
}
