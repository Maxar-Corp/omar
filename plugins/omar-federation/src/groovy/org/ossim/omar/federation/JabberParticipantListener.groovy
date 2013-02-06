package org.ossim.omar.federation

import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.packet.Packet

import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener

/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 1/3/13
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
class JabberParticipantListener extends DefaultParticipantStatusListener {
    def federatedServerService

    def getUser(def participant)
    {
       return participant.split("/")[-1]
    }
    void joined(String participant)
    {
        //println "JOINED: ${getUser(participant)}"

        federatedServerService.makeAvailable(getUser(participant));
    }
    void membershipGranted(String participant)
    {
        //println "membershipGranted: ${getUser(participant)}"
        federatedServerService.makeAvailable(getUser(participant));
    }
    void membershipRevoked(String participant)
    {
        //println "membershipRevoked: ${getUser(participant)}"
        federatedServerService.makeUnavailable(getUser(participant));
    }
    void kicked(String participant, String actor, String reason)
    {
        //println "KICKED: ${getUser(participant)}"
        federatedServerService.makeUnavailable(getUser(participant));
    }
    void left(String participant)
    {
        //println "LEFT: ${getUser(participant)}"
        federatedServerService.makeUnavailable(getUser(participant));
    }
    void banned(String participant, String actor, String reason)
    {
        //println "BANNED: ${getUser(participant)}"
        federatedServerService.makeUnavailable(getUser(participant));
    }
}
