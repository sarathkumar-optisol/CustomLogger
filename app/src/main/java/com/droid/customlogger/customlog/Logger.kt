package com.droid.customlogger.customlog

/**
 * Created by SARATH on 29-04-2021
 */
object Logger {

    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6
    const val ASSERT = 7
    private var printer: Printer = LoggerPrinter()
    fun printer(printer: Printer) {
        Logger.printer =
            Utils.checkNotNull(printer)
    }



    fun addLogAdapter(adapter: LogAdapter) {
        printer.addAdapter(
            Utils.checkNotNull(
                adapter
            )
        )
    }

    fun clearLogAdapters() {
        printer.clearLogAdapters()
    }

    fun t(tag: String?): Printer {
        return printer.t(tag)!!
    }

    fun log(
        priority: Int,
        tag: String?,
        message: String?,
        throwable: Throwable?
    ) {
        printer.log(priority, tag, message, throwable)
    }

    fun d(message: String, vararg args: Any?) {
        printer.d(message, *args)
    }

    fun d(`object`: Any?) {
        printer.d(`object`)
    }

    fun e(message: String, vararg args: Any?) {
        printer.e(null, message, *args)
    }

    fun e(
        throwable: Throwable?,
        message: String,
        vararg args: Any?
    ) {
        printer.e(throwable, message, *args)
    }

    fun i(message: String, vararg args: Any?) {
        printer.i(message, *args)
    }

    fun v(message: String, vararg args: Any?) {
        printer.v(message, *args)
    }

    fun w(message: String, vararg args: Any?) {
        printer.w(message, *args)
    }

    fun wtf(message: String, vararg args: Any?) {
        printer.wtf(message, *args)
    }

    fun json(json: String?) {
        printer.json(json)
    }

    fun xml(xml: String?) {
        printer.xml(xml)
    }
}