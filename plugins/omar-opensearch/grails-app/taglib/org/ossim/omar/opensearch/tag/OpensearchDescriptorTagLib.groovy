package org.ossim.omar.opensearch.tag

class OpensearchDescriptorTagLib {
    static namespace = "opensearch"
    static encodeAsForTags = [descriptors: [taglib:'none']]
    def descriptors = {attrs ->
        grailsApplication.config.opensearch.descriptorList.each{k,v->
            String tagString = "<link rel='search' type='application/opensearchdescription+xml' href='${v.href}' title='${v.title}' />"

            out<<tagString

        }
    }
}
