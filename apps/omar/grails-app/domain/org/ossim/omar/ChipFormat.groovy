package org.ossim.omar

class ChipFormat {
    String label
    Integer width
    Integer height
    String comment

    // Return for AOI drop-down menu in imageSpace.gsp
    //  (delimiters used by function genAOI)
    String toString() {
        def selString = "${label}: ${width}x${height}"
        return selString
    }

    static constraints = {
        label(unique: true, blank: false)
        width(min: 0)
        height(min: 0)
        comment(nullable: true)
    }
}
