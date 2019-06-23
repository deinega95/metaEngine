package com.meta_engine.common.services

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import com.meta_engine.CodenameGenerator
import com.meta_engine.common.Coordinator
import com.meta_engine.common.utils.MyLog
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class NearbyService(val appContext: Context) {
    @Inject
    lateinit var coordinator: Coordinator
    @Inject
    lateinit var gson: Gson

    private lateinit var connectionsClient : ConnectionsClient
    private lateinit var packageName : String
    private val codeName = CodenameGenerator.generate()
    private var opponentEndpointId = mutableListOf<String>()
    private val STRATEGY = Strategy.P2P_CLUSTER

    fun connect() {
        connectionsClient = Nearby.getConnectionsClient(appContext)
        packageName = appContext.packageName

        startAdvertising()
        startDiscovery()
    }


    val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            MyLog.show("!! !!received mes= " + String(payload.asBytes()!!, StandardCharsets.UTF_8))
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            MyLog.show("!!!onEndpointFound codeName =$codeName//endpointId=$endpointId")
            connectionsClient.requestConnection(codeName, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            MyLog.show("!! onConnectionInitiat acceptConnection endpointId =$endpointId")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            MyLog.show("!! !onConnectionResult endpointId =$endpointId")
            MyLog.show("!! !!onConnectionResult result.getStatus().isSuccess() =" + result.status.isSuccess)
            if (result.status.isSuccess) {
                MyLog.show("!!! onConnectionResult: connection successful")
                opponentEndpointId.add(endpointId)
            } else {
                MyLog.show("!!!onConnectionResult: connection failed")
            }
        }

        override fun onDisconnected(endpointId: String) {
            MyLog.show("!!!onDisconnected: disconnected from the opponent")

        }
    }


    private fun sendData(data: Any) {
        val jsonData = gson.toJson(data).toByteArray(StandardCharsets.UTF_8)

        MyLog.show("!!!sendPayload to =$opponentEndpointId//data = $jsonData")
        opponentEndpointId.forEach {
            connectionsClient.sendPayload(
                it, Payload.fromBytes(jsonData)
            )
        }
    }

    private fun startDiscovery() {
        connectionsClient.startDiscovery(
            codeName,
            endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        ).addOnSuccessListener {
            MyLog.show("!! discovery successListener")
        }.addOnFailureListener {
            MyLog.show("!! discovery failure ${it.localizedMessage} //${it.printStackTrace()}")
        }
        MyLog.show("!! !!startDiscovery package =$packageName")
    }

    private fun startAdvertising() {
        connectionsClient.startAdvertising(
            codeName, packageName, connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        ).addOnSuccessListener {
            MyLog.show("!! startAdvertising successListener")
        }.addOnFailureListener {
            MyLog.show("!! startAdvertising failure ${it.localizedMessage} //${it.printStackTrace()}")
        }
        MyLog.show("!! !!!startAdvertising nickName =$codeName")
    }

    fun stopAllEndpoints() {
        connectionsClient.stopAllEndpoints()
    }
}