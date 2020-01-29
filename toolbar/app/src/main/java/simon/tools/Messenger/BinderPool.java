package simon.tools.Messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Jarry Leo
 * @date : 2018/9/13 9:31
 */
public class BinderPool extends Service {
    private static ConcurrentHashMap<Integer, Messenger> mMessageMap = new ConcurrentHashMap<>();
    private static Messenger messenger;
    private static Handler handler;

    static {
        handler = new ServiceHandler();
        messenger = new Messenger(handler);
    }

    static class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            int key = msg.arg1;
            switch (what) {
                case Constant.SUBSCRIBE:
                    mMessageMap.put(key, msg.replyTo);
                    break;
                case Constant.SEND_MSG_TO_TARGET:
                    //要发送的消息
                    sendMsg(msg);
                    break;
                case Constant.UNSUBSCRIBE:
                    mMessageMap.remove(key);
                    break;
                default:
            }
        }
    }

    private static void sendMsg(Message msg) {
        try {
            Message msgToClient = Message.obtain(msg);
            for (Messenger messenger : mMessageMap.values()) {
                //发送消息
                if (messenger != null) {
                    Message m = new Message();
                    m.copyFrom(msgToClient);
                    messenger.send(m);
                }
            }
            Message m = new Message();
            m.copyFrom(msgToClient);
            ProcessMsgCenter.onMsgReceive(m.getData());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Message message = Message.obtain(handler, Constant.SEND_MSG_TO_TARGET);
                message.replyTo = messenger;
                message.setData(extras);
                sendMsg(message);
            }
        }
        return START_NOT_STICKY;
    }

}
