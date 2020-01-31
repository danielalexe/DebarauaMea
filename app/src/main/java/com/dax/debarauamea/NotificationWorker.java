package com.dax.debarauamea;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dax.debarauamea.Objects.DTOProducts;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class NotificationWorker extends Worker {
    private Context mContext;

    public NotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        mContext=context;
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.

        Realm realm = Realm.getDefaultInstance();
        if (realm== null)
        {
            Realm.init(mContext);
            realm = Realm.getDefaultInstance();
        }
        DTOProducts Product = realm.where(DTOProducts.class).greaterThan("QUANTITY",0.0f).equalTo("HAS_EXPIRATION_DATE",true).greaterThan("EXPIRATION_DATE", Calendar.getInstance().getTime()).sort("EXPIRATION_DATE", Sort.ASCENDING).findFirst();
        if (getDaysDifference(Calendar.getInstance().getTime(),Product.EXPIRATION_DATE)<=4)
        {
            showNotification(mContext,"Debaraua Mea","A product is about to expire in the next 3 days.",null);
        }

        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }

    private void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addNextIntent(intent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                0,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    public int getDaysDifference(Date fromDate, Date toDate)
    {
        if(fromDate==null||toDate==null)
            return 0;

        return (int)( (toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

}
