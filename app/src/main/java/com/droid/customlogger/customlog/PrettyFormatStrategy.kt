package com.droid.customlogger.customlog

import kotlin.math.min

/**
 * Created by SARATH on 29-04-2021
 */
class PrettyFormatStrategy private constructor(builder: Builder) :
    FormatStrategy {
    private val methodCount: Int
    private val methodOffset: Int
    private val showThreadInfo: Boolean
    private val logStrategy: LogStrategy
    private val tag: String?
    override fun log(
        priority: Int,
        tag: String?,
        message: String
    ) {
        Utils.checkNotNull(message)
        val mTag = formatTag(tag)
        logTopBorder(priority, mTag)
        logHeaderContent(priority, mTag, methodCount)
        val bytes = message.toByteArray()
        val length = bytes.size
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(priority, mTag)
            }
            logContent(priority, mTag, message)
            logBottomBorder(priority, mTag)
            return
        }
        if (methodCount > 0) {
            logDivider(priority, mTag)
        }
        var i = 0
        while (i < length) {
            val count =
                min(length - i, CHUNK_SIZE)
            logContent(priority, mTag, String(bytes, i, count))
            i += CHUNK_SIZE
        }
        logBottomBorder(priority, mTag)
    }

    private fun logTopBorder(logType: Int, tag: String?) {
        logChunk(logType, tag, TOP_BORDER)
    }

    private fun logHeaderContent(logType: Int, tag: String?, methodCount: Int) {
        var sMethodCount = methodCount
        val trace =
            Thread.currentThread().stackTrace
        if (showThreadInfo) {
            logChunk(
                logType,
                tag,
                HORIZONTAL_LINE.toString() + " Thread: " + Thread.currentThread().name
            )
            logDivider(logType, tag)
        }
        var level = ""
        val stackOffset = getStackOffset(trace) + methodOffset
        if (sMethodCount + stackOffset > trace.size) {
            sMethodCount = trace.size - stackOffset - 1
        }
        for (i in sMethodCount downTo 1) {
            val stackIndex = i + stackOffset
            if (stackIndex >= trace.size) {
                continue
            }
            val builder = StringBuilder()
            builder.append(HORIZONTAL_LINE)
                .append(' ')
                .append(level)
                .append(getSimpleClassName(trace[stackIndex].className))
                .append(".")
                .append(trace[stackIndex].methodName)
                .append(" ")
                .append(" (")
                .append(trace[stackIndex].fileName)
                .append(":")
                .append(trace[stackIndex].lineNumber)
                .append(")")
            level += "   "
            logChunk(logType, tag, builder.toString())
        }
    }

    private fun logBottomBorder(logType: Int, tag: String?) {
        logChunk(logType, tag, BOTTOM_BORDER)
    }

    private fun logDivider(logType: Int, tag: String?) {
        logChunk(logType, tag, MIDDLE_BORDER)
    }

    private fun logContent(logType: Int, tag: String?, chunk: String) {
        Utils.checkNotNull(chunk)
        val lines =
            chunk.split(System.getProperty("line.separator")!!).toTypedArray()
        for (line in lines) {
            logChunk(
                logType,
                tag,
                "$HORIZONTAL_LINE $line"
            )
        }
    }

    private fun logChunk(priority: Int, tag: String?, chunk: String) {
        Utils.checkNotNull(chunk)
        logStrategy.log(priority, tag, chunk)
    }

    private fun getSimpleClassName(name: String): String {
        Utils.checkNotNull(name)
        val lastIndex = name.lastIndexOf(".")
        return name.substring(lastIndex + 1)
    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        Utils.checkNotNull(trace)
        var i = MIN_STACK_OFFSET
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != LoggerPrinter::class.java.name && name != Logger::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    private fun formatTag(tag: String?): String? {
        return if (!Utils.isEmpty(tag) && !Utils.equals(
                this.tag,
                tag
            )
        ) {
            this.tag + "-" + tag
        } else this.tag
    }

    class Builder {
        var methodCount = 2
        var methodOffset = 0
        var showThreadInfo = true
        var logStrategy: LogStrategy? = null
        var tag: String? = "CustomLogger : "
        fun methodCount(`val`: Int): Builder {
            methodCount = `val`
            return this
        }

        fun methodOffset(`val`: Int): Builder {
            methodOffset = `val`
            return this
        }

        fun showThreadInfo(`val`: Boolean): Builder {
            showThreadInfo = `val`
            return this
        }

        fun logStrategy(`val`: LogStrategy?): Builder {
            logStrategy = `val`
            return this
        }

        fun tag(tag: String?): Builder {
            this.tag = tag
            return this
        }

        fun build(): PrettyFormatStrategy {
            if (logStrategy == null) {
                logStrategy = LogcatLogStrategy()
            }
            return PrettyFormatStrategy(this)
        }
    }

    companion object {
        private const val CHUNK_SIZE = 4000
        private const val MIN_STACK_OFFSET = 5
        private const val TOP_LEFT_CORNER = '┌'
        private const val BOTTOM_LEFT_CORNER = '└'
        private const val MIDDLE_CORNER = '├'
        private const val HORIZONTAL_LINE = '│'
        private const val DOUBLE_DIVIDER =
            "────────────────────────────────────────────────────────"
        private const val SINGLE_DIVIDER =
            "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
        private const val TOP_BORDER =
            TOP_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
        private const val BOTTOM_BORDER =
            BOTTOM_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
        private const val MIDDLE_BORDER =
            MIDDLE_CORNER.toString() + SINGLE_DIVIDER + SINGLE_DIVIDER

        fun newBuilder(): Builder {
            return Builder()
        }
    }

    init {
        Utils.checkNotNull(
            builder
        )
        methodCount = builder.methodCount
        methodOffset = builder.methodOffset
        showThreadInfo = builder.showThreadInfo
        logStrategy = builder.logStrategy!!
        tag = builder.tag
    }
}