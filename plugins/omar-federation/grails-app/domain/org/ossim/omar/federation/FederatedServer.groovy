package org.ossim.omar.federation

class FederatedServer {
    String  serverId
    Boolean available
    String  vcard // xml form of vcard

    static mapping = {
        columns{
            serverId index: 'federated_server_server_id_idx'
            vcard type:     'text'
        }
    }

    static constraints = {
        serverId(nullable: false, unique: true)
        available()
        vcard(nullable: false)
    }

}
