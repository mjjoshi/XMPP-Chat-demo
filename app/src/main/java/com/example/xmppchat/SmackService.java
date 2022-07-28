//package com.example.xmppchat;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//
//import androidx.annotation.Nullable;
//
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//
//public class SmackService extends Service {
//
//  private boolean mActive;
//  private Thread mThread;
//  private Handler mHandler;
//
//  String userName="username52",password="Password123";
//  private XmppChatManager xmppConnection;
//  private static XMPPTCPConnection mConnection;
//
//  public SmackService() {
//  }
//
//  public static void start(Context context) {
//    Intent intent = new Intent(context, SmackService.class);
//    context.startService(intent);
//  }
//
//  @Override public void onCreate() {
//    super.onCreate();
//
//
//  }
//
//  @Override public int onStartCommand(Intent intent, int flags, int startId) {
//    mConnection = XmppChatManager.getConnection();
//    if (isConnectingToInternet()) {
//      start();
//    }
//
//    return Service.START_STICKY;
//  }
//
//  @Override public void onDestroy() {
//    super.onDestroy();
//    stop();
//  }
//
//  @Nullable
//  @Override public IBinder onBind(Intent intent) {
//    return null;
//  }
//
//  public void start() {
//    if (!mActive) {
//      mActive = true;
//
//      // Create ConnectionThread Loop
//      if (mThread == null || !mThread.isAlive()) {
//        mThread = new Thread(new Runnable() {
//          @Override public void run() {
//            Looper.prepare();
//            mHandler = new Handler();
//            initConnection();
//            Looper.loop();
//          }
//        });
//        mThread.start();
//      }
//    }
//  }
//
//  public void stop() {
//    mActive = false;
//  }
//
//  private void initConnection() {
//
//    if (mConnection == null) {
//      xmppConnection = XmppChatManager.getInstance(this, "0.tcp.ngrok.io", userName, userName);
//      xmppConnection.connect(userName);
//    } else {
//
//    }
//  }
//
//  public boolean isConnectingToInternet() {
//    ConnectivityManager connectivity =
//        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//    if (connectivity != null) {
//      NetworkInfo[] info = connectivity.getAllNetworkInfo();
//      if (info != null) {
//        for (int i = 0; i < info.length; i++)
//          if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//            return true;
//          }
//      }
//    }
//    return false;
//  }
//}
