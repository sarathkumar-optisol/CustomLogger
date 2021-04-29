package com.droid.customlogger.customlog

import android.util.Log

/**
 * Created by SARATH on 29-04-2021
 */
class LogcatLogStrategy : LogStrategy {
    override fun log(
        priority: Int,
        tag: String?,
        message: String
    ) {
        var mTag = tag
        Utils.checkNotNull(message)
        if (mTag == null) {
            mTag = DEFAULT_TAG
        }
        Log.println(priority, mTag, message)
    }

    companion object {
        const val DEFAULT_TAG = "NO_TAG"
    }
}