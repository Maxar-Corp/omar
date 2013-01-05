package org.ossim.omar.federation

import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.packet.Packet

/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 1/3/13
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
class JabberParticipantListener implements PacketListener {
    def federatedServerService
    void processPacket(Packet packet)
    {
        try{

            def from = packet.from.split("/")[-1]
            switch("${packet}")
            {
                case "available":
                case "chat":
                    federatedServerService.makeAvailable(from)
                    break
                default:
                    federatedServerService.makeUnavailable(from)
                    break
            }
        }
        catch(def e)
        {
            println e
        }
    }
}
