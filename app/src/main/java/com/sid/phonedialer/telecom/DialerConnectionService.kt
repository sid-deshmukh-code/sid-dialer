package com.sid.phonedialer.telecom

import android.net.Uri
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager

/**
 * Handles the platform side of placing/receiving calls once this app is
 * set as the default dialer. Actual call UI + recording lives in
 * DialerInCallService / InCallActivity — this class just tells Android
 * "here is a live Connection object for this call".
 */
class DialerConnectionService : ConnectionService() {

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val number = request?.address ?: Uri.EMPTY
        return buildConnection(number, isIncoming = false)
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val number = request?.address ?: Uri.EMPTY
        return buildConnection(number, isIncoming = true)
    }

    private fun buildConnection(number: Uri, isIncoming: Boolean): Connection {
        val connection = object : Connection() {
            override fun onAnswer() {
                setActive()
            }

            override fun onReject() {
                setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
                destroy()
            }

            override fun onDisconnect() {
                setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
                destroy()
            }

            override fun onAbort() {
                setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
                destroy()
            }
        }
        connection.setAddress(number, TelecomManager.PRESENTATION_ALLOWED)
        connection.setConnectionCapabilities(
            Connection.CAPABILITY_MUTE or Connection.CAPABILITY_SUPPORT_HOLD
        )
        if (isIncoming) {
            connection.setRinging()
        } else {
            connection.setDialing()
            // For a demo/basic dialer we mark outgoing calls active once dialed;
            // a production dialer would wait for real network call state events.
            connection.setActive()
        }
        return connection
    }
}
