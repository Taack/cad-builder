package org.taack.cad.step

import groovy.transform.CompileStatic

@CompileStatic
class StepFile {
    final String fileName
    final String software
    final Date creationDate

    StepFile(String fileName, String software, Date creationDate) {
        this.fileName = fileName
        this.software = software
        this.creationDate = creationDate
    }


    @Override
    String toString() {
        return "StepFile{" +
                "fileName='" + fileName + '\'' +
                ", software='" + software + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
