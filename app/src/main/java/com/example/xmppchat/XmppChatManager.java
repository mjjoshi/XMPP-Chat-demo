//package com.example.xmppchat;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.gson.Gson;
//
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.ConnectionListener;
//import org.jivesoftware.smack.ReconnectionManager;
//import org.jivesoftware.smack.SASLAuthentication;
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.StanzaListener;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.chat.ChatManager;
//import org.jivesoftware.smack.filter.StanzaFilter;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.packet.Stanza;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
//import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
//import org.jivesoftware.smackx.delay.packet.DelayInformation;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//import org.jivesoftware.smackx.ping.PingFailedListener;
//import org.jivesoftware.smackx.ping.PingManager;
//import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;
//import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
//import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
//import org.jxmpp.jid.DomainBareJid;
//import org.jxmpp.jid.Jid;
//import org.jxmpp.jid.impl.JidCreate;
//
//import java.io.IOException;
//import java.util.Date;
//
//public class XmppChatManager {
//    public static boolean connected = false;
//    public boolean loggedin = false;
//    public static boolean isconnecting = false;
//    private boolean chat_created = false;
//    private String serverAddress;
//    public static XMPPTCPConnection mConnection;
//    public static String loginUser;
//    public static String passwordUser;
//    Gson gson;
//    SmackService context;
//
//    public static XmppChatManager instance = null;
//    public static boolean instanceCreated = false;
//    public static final String TAG = "Smack Service";
//
//    public XmppChatManager(SmackService context, String serverAdress, String logiUser,
//                           String passwordser) {
//        this.serverAddress = serverAdress;
//        this.loginUser = logiUser;
//        this.passwordUser = passwordser;
//        this.context = context;
//        init();
//    }
//
//    public static XmppChatManager getInstance(SmackService context, String server, String user,
//                                              String pass) {
//
//        if (instance == null) {
//            instance = new XmppChatManager(context, server, user, pass);
//            instanceCreated = true;
//        }
//        return instance;
//    }
//
//    public static org.jivesoftware.smack.chat.Chat oneToOneChat;
//    public static MultiUserChat groupChat;
//
//    static {
//        try {
//            Class.forName("org.jivesoftware.smack.ReconnectionManager");
//        } catch (ClassNotFoundException ex) {
//            // problem loading reconnection manager
//        }
//    }
//
//    public void init() {
//        gson = new Gson();
//
//        initialiseConnection();
//    }
//
//    public static XMPPTCPConnection getConnection() {
//        return mConnection;
//    }
//
//    private void initialiseConnection() {
//        // Add SSL certificate
//        DomainBareJid serviceName = null;
//        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
//        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
//        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
//        try {
//            serviceName = JidCreate.domainBareFrom("0.tcp.ngrok.io");
//        } catch (Exception e) {
//
//        }
//        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
//        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//        config.setServiceName(serviceName);
//        config.setHost(serverAddress);
//        config.setPort(5222);
//        config.setDebuggerEnabled(true);
//        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
//        XMPPTCPConnection.setUseStreamManagementDefault(true);
//        mConnection = new XMPPTCPConnection(config.build());
//        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
//        mConnection.addConnectionListener(connectionListener);
//        mConnection.setUseStreamManagement(true);
//
//        // set packet reply timeout time
//        mConnection.setPacketReplyTimeout(10000);
//
//        // Add reconnect manager
//        ReconnectionManager.getInstanceFor(mConnection).enableAutomaticReconnection();
//        ServerPingWithAlarmManager.onCreate(context);
//        ServerPingWithAlarmManager.getInstanceFor(mConnection).setEnabled(true);
//        ReconnectionManager.setEnabledPerDefault(true);
//
//        // Add ping manager here
//        PingManager.getInstanceFor(mConnection).registerPingFailedListener(new PingFailedListener() {
//            @Override
//            public void pingFailed() {
//                disconnect();
//                initialiseConnection();
//            }
//        });
//        //addStanzaListner();
//    }
//
//    public void disconnect() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mConnection.disconnect();
//            }
//        }).start();
//    }
//
//    public void connect(final String caller) {
//
//        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected synchronized Boolean doInBackground(Void... arg0) {
//                if (mConnection.isConnected()) return false;
//                isconnecting = true;
//                Log.d("Connect() Function", caller + "=>connecting....");
//
//                try {
//                    mConnection.connect();
//                    DeliveryReceiptManager deliveryReceiptManager =
//                            DeliveryReceiptManager.getInstanceFor(mConnection);
//                    deliveryReceiptManager.autoAddDeliveryReceiptRequests();
//                    deliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
//                    deliveryReceiptManager.addReceiptReceivedListener(new ReceiptReceivedListener() {
//                        @Override
//                        public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
//
//                        }
//
////                        @Override
////                        public void onReceiptReceived(String fromJid, String toJid, String receiptId,
////                                                      Stanza receipt) {
////
////                            // Mark delivered here
////
////                            Message message = new Message();
////                            message.setStanzaId(receipt.getStanzaId());
////                            //                message.setType(Message.Type.headline);
////                            //                                ChatManager.getInstanceFor(mConnection)
//////                                        .createChat("admin" + MyCommunityChatService.JID_SUFFIX)
//////                                        .sendMessage(message);
////                        }
//                    });
//                    connected = true;
//                } catch (IOException e) {
//
//                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());
//                } catch (SmackException e) {
//
//                    Log.e("(" + caller + ")", "SMACKException: " + e.getMessage());
//                } catch (XMPPException e) {
//                    Log.e("connect(" + caller + ")", "XMPPException: " + e.getMessage());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return isconnecting = false;
//            }
//        };
//        connectionThread.execute();
//    }
//
//    public void login() {
//
//        try {
//            mConnection.login(loginUser, passwordUser);
//            // Set the status to available
//            Presence presence = new Presence(Presence.Type.available);
//            mConnection.sendPacket(presence);
//            Log.i("LOGIN", "Yey! We're connected to the Xmpp server!" + loginUser);
//        } catch (XMPPException | SmackException | IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//        }
//    }
//
////    public void sendOneToOneMessage(ChatMessage message) {
////        if (!chat_created) {
////            oneToOneChat = ChatManager.getInstanceFor(mConnection).createChat(message.getReceiver());
////            chat_created = true;
////        }
////        final Message chatMessage = new Message();
////        chatMessage.setBody(message.toString());
////        chatMessage.setStanzaId(message.getMsgid());
////        chatMessage.setType(Message.Type.chat);
////        try {
////            oneToOneChat.sendMessage(chatMessage);
////        } catch (SmackException.NotConnectedException e) {
////            e.printStackTrace();
////            disconnect();
////            init();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////    }
//
//    public class XMPPConnectionListener implements ConnectionListener {
//        @Override
//        public void connected(final XMPPConnection mConnection) {
//
//            Log.d("xmpp", "Connected!");
//            connected = true;
//            if (!mConnection.isAuthenticated()) {
//                login();
//            }
//        }
//
//        @Override
//        public void connectionClosed() {
//
//            Log.d("xmpp", "ConnectionCLosed!");
//            connected = false;
//            chat_created = false;
//            loggedin = false;
//        }
//
//        @Override
//        public void connectionClosedOnError(Exception arg0) {
//
//            Log.d("xmpp", "ConnectionClosedOn Error!");
//            connected = false;
//            chat_created = false;
//            loggedin = false;
//        }
//
//        @Override
//        public void reconnectingIn(int arg0) {
//
//            Log.d("xmpp", "Reconnectingin " + arg0);
//            loggedin = false;
//            disconnect();
//            initialiseConnection();
//        }
//
//        @Override
//        public void reconnectionFailed(Exception arg0) {
//
//            Log.d("xmpp", "ReconnectionFailed!");
//            connected = false;
//            chat_created = false;
//            loggedin = false;
//        }
//
//        @Override
//        public void reconnectionSuccessful() {
//            Log.d("xmpp", "ReconnectionSuccessful");
//            connected = true;
//            chat_created = false;
//            loggedin = false;
//        }
//
//        @Override
//        public void authenticated(XMPPConnection arg0, boolean arg1) {
//            Log.d("xmpp", "Authenticated!");
//            loggedin = true;
//
//            //      ChatManager.getInstanceFor(mConnection).addChatListener(mChatManagerListener);
//
//            chat_created = false;
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
//    }
//
//    private void addStanzaListner() {
//        mConnection.addAsyncStanzaListener(new StanzaListener() {
//            private ChatMessage superMessage;
//            private DelayInformation delayInformation;
//
//            @Override
//            public void processPacket(Stanza packet)
//                    throws SmackException.NotConnectedException {
//                if (!packet.getFrom().contains(mConnection.getUser())) {
//                    Log.i("TAG", "processPacket: new packet received in service");
//                    superMessage = ChatMessage.instanceOf(((Message) packet).getBody());
//
//                    delayInformation = null;
//                    try {
//                        delayInformation = packet.getExtension("delay", "urn:xmpp:delay");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (delayInformation != null) {
//                        Date date = delayInformation.getStamp();
//                        // set up message delay information here
//                    }
//                    // Message receive here
//
//                }
//            }
//        }, new StanzaFilter() {
//            @Override
//            public boolean accept(Stanza stanza) {
//                if (stanza instanceof Message) {
//                    if (stanza.hasExtension(ChatStateExtension.NAMESPACE)) {
//                        Intent intent = new Intent();
//                        intent.setAction("composing");
//                        intent.putExtra(stanza.getExtension(ChatStateExtension.NAMESPACE).getElementName(),
//                                stanza.getFrom());
//                        // sendBroadcast(intent);
//                    }
//                    if (((Message) stanza).getBody() != null) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//        mConnection.addStanzaAcknowledgedListener(new StanzaListener() {
//            @Override
//            public void processPacket(Stanza packet)
//                    throws SmackException.NotConnectedException {
//                if (packet instanceof Message) {
//                    if (packet.getError() != null) {
//                        // Mark  Message un sent acknowledgment here
//                    } else {
//                        // Mark Message sent acknowledgment here
//                    }
//                }
//            }
//        });
//    }
//}
