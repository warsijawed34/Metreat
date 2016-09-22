/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metreat.gcmPushNotisfication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.metreat.R;
import com.metreat.activity.HomeActivity;
import com.metreat.activity.NotificationActivity;
import com.metreat.model.syncContactsModel;

import java.util.ArrayList;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService  {

    private static final String TAG = "GcmListenerService";
    String messagePush, senderId,messageType;
    Intent intent=null;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        messagePush = data.getString("message");
        senderId = data.getString("senderID");
        messageType = data.getString("messageType");

        sendNotification(messagePush,senderId,messageType);
    }

    private void sendNotification(String message, String senderId, String messageType) {
        if(messageType.equalsIgnoreCase("friendship-request")){
            intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("senderId", senderId);
            intent.putExtra("messageType", messageType);

        }else if(messageType.equalsIgnoreCase("upcoming-events")){
            intent = new Intent(this, NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.appicon)
                .setContentTitle("MeTreat")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}