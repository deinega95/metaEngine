package com.meta_engine

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.meta_engine.common.utils.MyLog
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*


class MainActivity : AppCompatActivity() {

    val nickname = "andorid${Random().nextInt(100)}"
    lateinit var client: ConnectionsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
    }

    private fun initNearby() {
        MyLog.show("!!Nearby init")

        client = Nearby.getConnectionsClient(this)

        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        val appName = packageName
        val callbackConnections = object : ConnectionLifecycleCallback() {
            override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                MyLog.show("!!nearby onConnectionResult $p0 //p1=${p1.status}")
            }

            override fun onDisconnected(p0: String) {
                MyLog.show("!!nearby onDisconnected $p0}")
            }

            override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                MyLog.show("!!nearby onConnectionInitiated $p0 //p1=${p1.authenticationToken}")

            }

        }

        Log.e("!!!startAdvertising", "nickName =$nickname")

        client.startAdvertising(nickname, appName, callbackConnections, options)
            .addOnSuccessListener {
                MyLog.show("!! suucess start advertising")
            }
            .addOnFailureListener {
                MyLog.show("!! failure start advertising")
            }

        val discoveredCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(p0: String, p1: DiscoveredEndpointInfo) {

                Log.e("!!!onEndpointFound", "codeName =$nickname//endpointId=$p0")
                MyLog.show("discovery onEndpointFound $p0 //" + "p1.endPoint=${p1.endpointName}// " + "p1.serviceId=${p1.serviceId}")
                requestForConnect(p0)
            }

            override fun onEndpointLost(p0: String) {
                MyLog.show("discovery onEndpointLost $p0")
            }
        }

        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

        Log.e("!!!startDiscovery", "package =$packageName")
        Log.e("!!!startDiscovery", "appname =$appName")

        client.startDiscovery(appName, discoveredCallback, discoveryOptions)
            .addOnSuccessListener {
                MyLog.show("!!Discovery suucess start advertising")
            }
            .addOnFailureListener {
                MyLog.show("!!Discovery failure start advertising")
            }
    }

    private fun checkPermissions() {
        if (permissionsGranted()) {
            initNearby()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE),
                123
            )
        }
    }


    private fun permissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initNearby()
            } else {
                Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * SEND from discover devices
     */

    fun requestForConnect(endpointName: String) {
        val mPayloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                // A new payload is being sent over.
                MyLog.show("onPayloadReceived from $endpointId payload = ${String(payload.asBytes()!!, UTF_8)}")
                Toast.makeText(
                    this@MainActivity,
                    "Mess received = ${String(payload.asBytes()!!, UTF_8)}",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                // Payload progress has updated.
                MyLog.show("onPayloadTransferUpdate $endpointId")
            }
        }

        MyLog.show("!client$client")

        val mConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.

                Log.e("!!!onConnectionInitiat", "acceptConnection endpointId =$endpointId")
                MyLog.show("onConnectionInitiated client$client")
                MyLog.show("onConnectionInitiated $endpointId")
                client.acceptConnection(endpointId, mPayloadCallback)
                    .addOnSuccessListener {
                        MyLog.show("acceptConnection suucess with $endpointId")
                    }
                    .addOnFailureListener {
                        MyLog.show("acceptConnection failure with $endpointId// ${it.localizedMessage}//${it.printStackTrace()}")
                    }

            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {

                Log.e("!!!onConnectionResult", "endpointId =$endpointId")
                Log.e("!!!onConnectionResult", "result.getStatus().isSuccess() =" + result.status.isSuccess)

                MyLog.show("onConnectionResult   result.status.statusCode = ${result.status.statusCode}")
                MyLog.show("onConnectionResult   result.status.statusMessage = ${result.status.statusMessage}")
                MyLog.show("onConnectionResult   result.status.statusMessage = ${result.status.status.statusMessage}")

                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        sendMessage(endpointId)
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    }
                    else -> {
                    }
                }// We're connected! Can now start sending and receiving data.
                // The connection was rejected by one or both sides.
                // The connection was broken before it was accepted.

            }

            override fun onDisconnected(endpointId: String) {

                MyLog.show("onDisconnected $endpointId")
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
            }
        }

        MyLog.show("! requestConnection from $nickname to $endpointName")
        client.requestConnection(
            nickname,
            endpointName,
            mConnectionLifecycleCallback
        ).addOnSuccessListener {
            MyLog.show("!!requestConnection suucess")
            sendMessage(endpointName)
        }.addOnFailureListener {

            MyLog.show("!!requestConnection failure ${it.localizedMessage}//${it.printStackTrace()}")

        }
    }

    fun sendMessage(endPoint: String) {
        MyLog.show("sendMessage = $endPoint")

        Log.e("!!!sendPayload", "to =$endPoint")
        val bytesPayload = Payload.fromBytes(nickname.toByteArray(UTF_8))
        client.sendPayload(endPoint, bytesPayload)
    }
}
