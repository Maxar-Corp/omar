package org.ossim.omar

public enum JobStatus {
        READY('READY'),
        RUNNING('RUNNING'),
        PAUSED('PAUSED'),
        CANCELED('CANCELED'),
        FINISHED('FINISHED'),
        FAILED('FAILED')

        String name

        JobStatus(String name) {
          this.name = name
        }
 
}