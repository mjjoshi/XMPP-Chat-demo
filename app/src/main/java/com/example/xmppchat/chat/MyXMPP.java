package com.example.xmppchat.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.io.IOException;
import java.util.Date;


public class MyXMPP  {

    public static boolean connected = false;
    public boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    private boolean chat_created = false;
    private String serverAddress;
    public static XMPPTCPConnection connection;
    public static String loginUser;
    public static String passwordUser;
    Gson gson;
    MyService context;
    public static MyXMPP instance = null;
    public static boolean instanceCreated = false;

    public MyXMPP(MyService context, String serverAdress, String logiUser,
                  String passwordser) {
        this.serverAddress = serverAdress;
        this.loginUser = logiUser;
        this.passwordUser = passwordser;
        this.context = context;
        init();

    }

    public static MyXMPP getInstance(MyService context, String server,
                                     String user, String pass) {

        if (instance == null) {
            instance = new MyXMPP(context, server, user, pass);
            instanceCreated = true;
        }
        return instance;

    }

    public org.jivesoftware.smack.chat.Chat Mychat;

    ChatManagerListenerImpl mChatManagerListener;
    MMessageListener mMessageListener;

    String text = "";
    String mMessage = "", mReceiver = "";

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
        }
    }

    public void init() {
        gson = new Gson();
        mMessageListener = new MMessageListener(context);
        mChatManagerListener = new ChatManagerListenerImpl();
        initialiseConnection();

    }

    private void initialiseConnection() {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setUsernameAndPassword("user1", "Test@123");
        config.setServiceName(serverAddress);
        config.setHost(serverAddress);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);




      //  connection.addSyncStanzaListener(this, null);

//        StanzaListener listener = packet -> {
//          Message message = (Message) packet;
//        };
//        StanzaFilter filter = new AndFilter();
//        connection.addAsyncStanzaListener(listener, filter);
//        connection.addStanzaAcknowledgedListener(new StanzaListener() {
//            @Override
//            public void processPacket(Stanza packet)
//                    throws SmackException.NotConnectedException {
//                if (packet instanceof Message) {
//                    final Message message = (Message) packet;
//                    if (packet.getError() != null) {
//                        // Mark  Message un sent acknowledgment here
//                    } else {
//                        // Mark Message sent acknowledgment here
//                    }
//                }
//            }
//        });


        //addStanzaListner();
    }


    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }


    public void connect(final String caller) {

        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (connection.isConnected())
                    return false;
                isconnecting = true;
                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(context,
                                    caller + "=>connecting....",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                Log.d("Connect() Function", caller + "=>connecting....");

                try {
                    connection.connect();
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid,
                                                      final String toid, final String msgid,
                                                      final Stanza packet) {


                            Message message = new Message();
                            message.setStanzaId(packet.getStanzaId());
                            Log.e("chjchchhc", message.getBody());

                        }
                    });
                    connected = true;

                } catch (IOException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                                .post(() -> Toast.makeText(
                                        context,
                                        "(" + caller + ")"
                                                + "IOException: ",
                                        Toast.LENGTH_SHORT).show());

                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "(" + caller + ")" + "SMACKException: ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("(" + caller + ")",
                            "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)

                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(
                                                context,
                                                "(" + caller + ")"
                                                        + "XMPPException: ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    Log.e("connect(" + caller + ")",
                            "XMPPException: " + e.getMessage());

                }
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    public void login() {

        try {
            // connection.login(loginUser, passwordUser);
            connection.login();
            Log.e("LOGIN", "Yey! We're connected to the Xmpp server!");

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

    }

//    @Override
//    public void processPacket(Stanza packet) throws NotConnectedException {
////        Log.e("dbhdhdd","Body - " + packet.getFrom());
////        Log.e("ddddddd","Body - " + connection.getUser());
//        Message message = (Message) packet;
//        Log.e("dddddddrr", "Body - " + message);
////        if (packet instanceof Message) {
////            Message message = (Message) packet;
////            String chatMessage = message.getBody();
////            Log.e("dddddddrr","Body - " + chatMessage);
////        }
////        if (!packet.getFrom().contains(connection.getUser())) {
////            if (packet instanceof Message) {
////                Log.e("chehchch", ((Message) packet).getBody());
////            }
////        }
//    }


    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(final org.jivesoftware.smack.chat.Chat chat,
                                final boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(mMessageListener);

        }

    }


//    private void addStanzaListner() {
//        connection.addAsyncStanzaListener(new StanzaListener() {
//            private ChatMessage superMessage;
//            private DelayInformation delayInformation;
//
//            @Override
//            public void processPacket(Stanza packet)
//                    throws SmackException.NotConnectedException {
//                if (!packet.getFrom().contains(connection.getUser())) {
//                    Log.i("goommmm", "processPacket: new packet received in service");
//                    //superMessage = ChatMessage(((Message) packet).getBody());
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
//        }, stanza -> {
//            if (stanza instanceof Message) {
//                if (stanza.hasExtension(ChatStateExtension.NAMESPACE)) {
//                    Intent intent = new Intent();
//                    intent.setAction("composing");
//                    intent.putExtra(stanza.getExtension(ChatStateExtension.NAMESPACE).getElementName(),
//                            stanza.getFrom());
//                    // sendBroadcast(intent);
//                }
//                if (((Message) stanza).getBody() != null) {
//                    return true;
//                }
//            }
//            return false;
//        });
//
//        connection.addStanzaAcknowledgedListener(new StanzaListener() {
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


    @SuppressLint("LongLogTag")
    public void sendMessage(ChatMessage chatMessage) {
        Log.e("yesssss", String.valueOf(chatMessage.receiver + " " + chatMessage.body + " " + chatMessage.sender));
        String body = gson.toJson(chatMessage.body);
        if (!chat_created) {
            Mychat = ChatManager.getInstanceFor(connection).createChat(
                    chatMessage.receiver + "@" + "mansi",
                    mMessageListener);
            chat_created = true;
        }
        final Message message = new Message();
        message.setBody(body);
        message.setStanzaId(chatMessage.msgid);
        message.setType(Message.Type.chat);
        try {
            if (connection.isAuthenticated()) {
                Mychat.sendMessage(message);
            } else {
                login();
            }
        } catch (NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.SendMessage()-Exception",
                    "msg Not sent!" + e.getMessage());
        }

    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
            Log.d("xmpp", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "ConnectionCLosed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "ConnectionClosedOn Error!!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {

            Log.d("xmpp", "Reconnectingin " + arg0);

            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(context, "ReconnectionFailed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "REConnected!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;

            ChatManager.getInstanceFor(connection).addChatListener(
                    mChatManagerListener);

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "Connected!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
        }
    }

    private class MMessageListener implements ChatMessageListener {

        public MMessageListener(Context contxt) {
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat,
                                   final Message message) {
            Log.e("message listners", "Xmpp message received: '"
                    + message);


            if (message.getType() == Message.Type.chat
                    && message.getBody() != null) {

                Log.e("messages", "Xmpp message received: '"
                        + message.getBody());
//                JsonReader reader = new JsonReader(new StringReader(message.getBody()));
//                reader.setLenient(true);
//                Log.e("messages", "Xmpp message received: '"
//                        + reader);


                ChatMessage chatMessage = new ChatMessage(
                        " ", " ", message.getBody(), " ", false
                );
                Log.e("messages", "Xmpp message received: '"
                        + chatMessage.body);
//                final ChatMessage chatMessage = gson.fromJson(
//                        message.getBody(), ChatMessage.class);
                processMessage(chatMessage);
            }
        }

        private void processMessage(final ChatMessage chatMessage) {
            // chatMessage.isMine = false;
            Chats.chatlist.add(chatMessage);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Chats.chatAdapter.notifyDataSetChanged();

                }
            });
        }

    }
}