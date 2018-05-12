package com.example.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import videorawtest.example.com.videorawtest.MainActivity;

/**
 * 自定义广播类 语言改变后重启Activity
 * 
 * @author asus
 *
 */
public class ChangeReceiver extends BroadcastReceiver {
	private Intent mIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		mIntent = new Intent(context, MainActivity.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mIntent);
	}
}
