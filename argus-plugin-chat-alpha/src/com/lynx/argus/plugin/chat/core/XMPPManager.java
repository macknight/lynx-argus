package com.lynx.argus.plugin.chat.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

/**
 * @author: chris.liu
 * @addtime: 13-12-23 下午5:11
 */
public class XMPPManager {
    private Context context;

    private NotificationService.TaskSubmitter taskSubmitter;
    private NotificationService.TaskTracker taskTracker;
    private SharedPreferences sharedPreferences;

    private String xmppHost;
    private int xmppPort;
    private XMPPConnection connection;
    private String userName;
    private String pwd;
    private ConnectionListener connListener;
    private PacketListener packetListener;
    private Handler handler;
    private List<Runnable> tasks;
    private boolean running = false;
    private Future<?> futureTask;
    private Thread reconn;
    
    public XMPPManager(NotificationService notificatinService) {
        context = notificatinService;
        taskSubmitter = notificatinService.getTaskSubmitter();
        taskTracker = notificatinService.getTaskTracker();
        sharedPreferences = notificatinService.getSharedPreferences();

        xmppHost = sharedPreferences.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sharedPreferences.getInt(Constants.XMPP_PORT, 5222);
        userName = sharedPreferences.getString(Constants.XMPP_USERNAME, "");
        pwd  = sharedPreferences.getString(Constants.XMPP_PWD, "");

        connListener = new PersistentConnectionListener(this);
        packetListener = new NotificationPacketListener(this);
        
        handler = new Handler();
        tasks = new ArrayList<Runnable>();
        reconn = new ReconnectionThread(this);
    }
}
