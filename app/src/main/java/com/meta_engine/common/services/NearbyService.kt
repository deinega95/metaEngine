package com.meta_engine.common.services

import android.util.Log
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import com.meta_engine.CodenameGenerator
import com.meta_engine.common.Coordinator
import com.meta_engine.common.utils.MyLog
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class NearbyService(val connectionsClient: ConnectionsClient, val packageName: String) {
    @Inject
    lateinit var coordinator: Coordinator
    @Inject
    lateinit var gson: Gson

    private val codeName = CodenameGenerator.generate()
    private var opponentEndpointId = mutableListOf<String>()
    private val STRATEGY = Strategy.P2P_CLUSTER

    init {
        startAdvertising()
        startDiscovery()
    }


    val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.e("!!!received mes=", String(payload.asBytes()!!, StandardCharsets.UTF_8))
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.e("!!!onEndpointFound", "codeName =$codeName//endpointId=$endpointId")
            connectionsClient.requestConnection(codeName, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.e("!!!onConnectionInitiat", "acceptConnection endpointId =$endpointId")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.e("!!!onConnectionResult", "endpointId =$endpointId")
            Log.e("!!!onConnectionResult", "result.getStatus().isSuccess() =" + result.status.isSuccess)
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
            codeName
            , endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        )
        Log.e("!!!startDiscovery", "package =$packageName")
    }

    private fun startAdvertising() {
        connectionsClient.startAdvertising(
            codeName, packageName, connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        )
        Log.e("!!!startAdvertising", "nickName =$codeName")
    }

}