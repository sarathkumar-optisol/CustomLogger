package com.droid.customlogger.customlog

/**
 * Created by SARATH on 29-04-2021
 */
open class AndroidLogAdapter : LogAdapter {

    private val formatStrategy : FormatStrategy

    constructor() {
        formatStrategy = PrettyFormatStrategy.newBuilder().build()
    }

    constructor(formatStrategy: FormatStrategy) {
        this.formatStrategy = Utils.checkNotNull(formatStrategy)
    }


    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return true
    }

    override fun log(priority: Int, tag: String?, message: String) {
        formatStrategy.log(priority, tag, message)
    }
}