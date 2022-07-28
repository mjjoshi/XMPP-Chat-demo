package com.example.xmppchat

import android.os.AsyncTask
import android.os.Build.HOST
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration



//http://shubhank101.github.io/iOSAndroidChaosOverFlow/2016/10/Chat-Application-Using-XMPP-Smack-API-Android-Tutorial
class MainActivity1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //  setConnection()
        val task = MyLoginTask()
        task.execute("")
    }

    private class MyLoginTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String {
           // val serviceName: DomainBareJid = JidCreate.domainBareFrom(HOST)
            val config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword("user1", "Test@123")
                .setHost("mansi")
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setPort(5222)
                .setServiceName("mansi")
                .setDebuggerEnabled(true) // to view what's happening in detail
                .build()
            val conn1: AbstractXMPPConnection = XMPPTCPConnection(config)
            try {
                conn1.connect()
                if (conn1.isConnected) {
                    Log.w("app11", "conn done")
                }
                conn1.login()
                if (conn1.isAuthenticated) {
                    Log.w("app11", "Auth done")
                }
            } catch (e: Exception) {
                Log.w("appissuees", e.toString())
            }
            return ""
        }
    }

//    fun setConnection() {
//        val serviceName: DomainBareJid = JidCreate.domainBareFrom("0.tcp.ngrok.io")
//        val config = XMPPTCPConnectionConfiguration.builder()
//            .setUsernameAndPassword("user1", "Test@123")
//            .setHost("0.tcp.ngrok.io")
//            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//            .setPort(15417)
//            .setServiceName(serviceName)
//            .setDebuggerEnabled(true) // to view what's happening in detail
//            .build()
//        val conn1: XMPPConnection = XMPPTCPConnection(config)
//        Log.e("chechch",conn1.host)
////        try {
////            conn1.connect()
////            if (conn1.isConnected) {
////                Log.w("app11", "conn done")
////            }
////            conn1.login()
////            if (conn1.isAuthenticated) {
////                Log.w("app11", "Auth done")
////            }
////        } catch (e: Exception) {
////            Log.w("appissuees", e.toString())
////        }
//    }

}