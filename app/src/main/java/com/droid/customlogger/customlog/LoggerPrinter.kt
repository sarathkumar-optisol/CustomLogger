package com.droid.customlogger.customlog

import com.droid.customlogger.customlog.Logger.ASSERT
import com.droid.customlogger.customlog.Logger.DEBUG
import com.droid.customlogger.customlog.Logger.ERROR
import com.droid.customlogger.customlog.Logger.INFO
import com.droid.customlogger.customlog.Logger.VERBOSE
import com.droid.customlogger.customlog.Logger.WARN
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.util.ArrayList
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * Created by SARATH on 29-04-2021
 */
internal class LoggerPrinter : Printer {
    private val localTag =
        ThreadLocal<String>()
    private val logAdapters: MutableList<LogAdapter> =
        ArrayList()

    override fun t(tag: String?): Printer {
        if (tag != null) {
            localTag.set(tag)
        }
        return this
    }

    override fun d(message: String, vararg args: Any?) {
        log(DEBUG, null, message, *args)
    }

    override fun d(`object`: Any?) {
        log(DEBUG, null, Utils.toString(`object`))
    }

    override fun e(message: String, vararg args: Any?) {
        e(null, message, *args)
    }

    override fun e(
        throwable: Throwable?,
        message: String,
        vararg args: Any?
    ) {
        log(ERROR, throwable, message, *args)
    }

    override fun w(message: String, vararg args: Any?) {
        log(WARN, null, message, *args)
    }

    override fun i(message: String, vararg args: Any?) {
        log(INFO, null, message, *args)
    }

    override fun v(message: String, vararg args: Any?) {
        log(VERBOSE, null, message, *args)
    }

    override fun wtf(message: String, vararg args: Any?) {
        log(ASSERT, null, message, *args)
    }

    override fun json(json: String?) {
        var mJSON = json
        if (Utils.isEmpty(mJSON)) {
            d("Empty/Null json content")
            return
        }
        try {
            mJSON = mJSON!!.trim { it <= ' ' }
            if (mJSON.startsWith("{")) {
                val jsonObject = JSONObject(mJSON)
                val message =
                    jsonObject.toString(JSON_INDENT)
                d(message)
                return
            }
            if (mJSON.startsWith("[")) {
                val jsonArray = JSONArray(mJSON)
                val message = jsonArray.toString(JSON_INDENT)
                d(message)
                return
            }
            e("Invalid Json")
        } catch (e: JSONException) {
            e("Invalid Json")
        }
    }

    override fun xml(xml: String?) {
        if (Utils.isEmpty(xml)) {
            d("Empty/Null xml content")
            return
        }
        try {
            val xmlInput: Source =
                StreamSource(StringReader(xml!!))
            val xmlOutput =
                StreamResult(StringWriter())
            val transformer =
                TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            d(xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n"))
        } catch (e: TransformerException) {
            e("Invalid xml")
        }
    }

    @Synchronized
    override fun log(
        priority: Int,
        tag: String?,
        message: String?,
        throwable: Throwable?
    ) {
        var sMessage = message
        if (throwable != null && sMessage != null) {
            sMessage += " : " + Utils.getStackTraceString(throwable)
        }
        if (throwable != null && sMessage == null) {
            sMessage = Utils.getStackTraceString(throwable)
        }
        if (Utils.isEmpty(sMessage)) {
            sMessage = "Empty/NULL log message"
        }
        for (adapter in logAdapters) {
            if (adapter.isLoggable(priority, tag)) {
                adapter.log(priority, tag, sMessage!!)
            }
        }
    }

    override fun clearLogAdapters() {
        logAdapters.clear()
    }

    override fun addAdapter(adapter: LogAdapter) {
        logAdapters.add(Utils.checkNotNull(adapter))
    }

    @Synchronized
    private fun log(
        priority: Int,
        throwable: Throwable?,
        msg: String,
        vararg args: Any?
    ) {
        Utils.checkNotNull(msg)
        val tag = tag
        val message = createMessage(msg, *args)
        log(priority, tag, message, throwable)
    }

    private val tag: String?
        get() {
            val tag = localTag.get()
            if (tag != null) {
                localTag.remove()
                return tag
            }
            return null
        }

    private fun createMessage(message: String, vararg args: Any?): String {
        return if (args.isEmpty()) message else String.format(message, *args)
    }

    companion object {
        private const val JSON_INDENT = 2
    }
}