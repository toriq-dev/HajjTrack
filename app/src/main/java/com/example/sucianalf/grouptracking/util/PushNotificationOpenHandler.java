package com.example.sucianalf.grouptracking.util;

import android.content.Intent;

import com.example.sucianalf.grouptracking.activity.ListGroupActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by PERSONAL on 6/13/2018.
 */

public class PushNotificationOpenHandler implements OneSignal.NotificationOpenedHandler {

    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String activityToBeOpened;


        Intent intent = new Intent(
                AppController.getContext(),
                ListGroupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        AppController.getContext().startActivity(intent);

    }

}
