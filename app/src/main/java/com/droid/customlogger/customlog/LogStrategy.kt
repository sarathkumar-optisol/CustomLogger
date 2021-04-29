package com.droid.customlogger.customlog

/**
 * Created by SARATH on 29-04-2021
 */
interface LogStrategy {
    fun log(priority: Int, tag: String?, message: String)
}