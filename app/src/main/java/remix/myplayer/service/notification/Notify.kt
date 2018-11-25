package remix.myplayer.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.TaskStackBuilder

import remix.myplayer.R
import remix.myplayer.service.Command
import remix.myplayer.service.MusicService
import remix.myplayer.service.MusicService.EXTRA_CONTROL
import remix.myplayer.ui.activity.PlayerActivity
import remix.myplayer.ui.activity.PlayerActivity.EXTRA_FROM_NOTIFY
import remix.myplayer.util.LogUtil

/**
 * Created by Remix on 2017/11/22.
 */

abstract class Notify internal constructor(internal var service: MusicService) {
    private var notificationManager: NotificationManager? = null

    private var notifyMode = NOTIFY_MODE_BACKGROUND

    //    Notification mNotification;
    internal var isStop: Boolean = false


    internal val contentIntent: PendingIntent
        get() {
            val result = Intent(service, PlayerActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(service)
            stackBuilder.addParentStack(PlayerActivity::class.java)
            stackBuilder.addNextIntent(result)

            stackBuilder.editIntentAt(1).putExtra(EXTRA_FROM_NOTIFY, true)
            stackBuilder.editIntentAt(0).putExtra(EXTRA_FROM_NOTIFY, true)
            return stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    init {
        init()
    }

    private fun init() {
        notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val playingNotificationChannel = NotificationChannel(PLAYING_NOTIFICATION_CHANNEL_ID, service.getString(R.string.playing_notification), NotificationManager.IMPORTANCE_LOW)
        playingNotificationChannel.setShowBadge(false)
        playingNotificationChannel.enableLights(false)
        playingNotificationChannel.enableVibration(false)
        playingNotificationChannel.description = service.getString(R.string.playing_notification_description)
        notificationManager?.createNotificationChannel(playingNotificationChannel)
    }

    abstract fun updateForPlaying()

    internal fun pushNotify(notification: Notification) {
        val newNotifyMode: Int
        if (service.isPlaying) {
            newNotifyMode = NOTIFY_MODE_FOREGROUND
        } else {
            newNotifyMode = NOTIFY_MODE_BACKGROUND
        }

        if (notifyMode != newNotifyMode && newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            //            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            service.stopForeground(false)
        }
        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            LogUtil.d("ServiceLifeCycle", "启动前台服务")
            service.startForeground(PLAYING_NOTIFICATION_ID, notification)
        } else {
            notificationManager?.notify(PLAYING_NOTIFICATION_ID, notification)
        }

        notifyMode = newNotifyMode
        isNotifyShowing = true
    }

    /**
     * 取消通知栏
     */
    fun cancelPlayingNotify() {
        service.stopForeground(true)
        notificationManager?.cancel(PLAYING_NOTIFICATION_ID)
        isStop = true
        isNotifyShowing = false
        //        notifyMode = NOTIFY_MODE_NONE;
    }

    internal fun buildPendingIntent(context: Context, operation: Int): PendingIntent {
        val intent = Intent(MusicService.ACTION_CMD)
        intent.putExtra(EXTRA_CONTROL, operation)
        intent.component = ComponentName(context, MusicService::class.java)
        intent.putExtra(EXTRA_FROM_NOTIFY, true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return PendingIntent.getService(context, operation, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            if (operation != Command.TOGGLE_FLOAT_LRC && operation != Command.CLOSE_NOTIFY) {
                return PendingIntent.getForegroundService(context, operation, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getService(context, operation, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        return PendingIntent.getService(context, operation, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        /**
         * 通知栏是否显示
         */
        @JvmStatic
        var isNotifyShowing = false

        private const val NOTIFY_MODE_FOREGROUND = 1
        private const val NOTIFY_MODE_BACKGROUND = 2

        internal const val PLAYING_NOTIFICATION_CHANNEL_ID = "playing_notification"
        private const val PLAYING_NOTIFICATION_ID = 1
    }
}
