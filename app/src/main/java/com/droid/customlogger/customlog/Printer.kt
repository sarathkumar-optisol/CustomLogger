package com.droid.customlogger.customlog

/**
 * Created by SARATH on 29-04-2021
 */
interface Printer {

    fun addAdapter(adapter: LogAdapter)
    fun t(tag: String?): Printer?
    fun d(message: String, vararg args: Any?)
    fun d(`object`: Any?)
    fun e(message: String, vararg args: Any?)
    fun e(
        throwable: Throwable?,
        message: String,
        vararg args: Any?
    )

    fun w(message: String, vararg args: Any?)
    fun i(message: String, vararg args: Any?)
    fun v(message: String, vararg args: Any?)
    fun wtf(message: String, vararg args: Any?)
    fun json(json: String?)
    fun xml(xml: String?)
    fun log(
        priority: Int,
        tag: String?,
        message: String?,
        throwable: Throwable?
    )

    fun clearLogAdapters()
}