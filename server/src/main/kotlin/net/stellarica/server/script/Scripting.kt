package net.stellarica.server.script

import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.plugin
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.SandboxPolicy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.jvm.javaMethod

object Scripting {
	val enabled: Boolean
		get() = plugin.scriptingEnabled

	
	fun setup() {
		newContext().eval("python", """
			scripting.callMe()
		""".trimIndent())
	}

	@Suppress("unused")
	fun callMe() {
		klogger.warn { "Hey look it called me!" }
	}

	private fun newContext(): Context = Context.newBuilder("python")
		.sandbox(SandboxPolicy.UNTRUSTED)
		.`in`(ByteArrayInputStream(ByteArray(0)))
		.out(ByteArrayOutputStream())
		.err(ByteArrayOutputStream())
		.allowHostAccess(HostAccess.newBuilder()
			.allowMutableTargetMappings() // empty list
			.methodScoping(true)
			.allowAccess(::callMe.javaMethod)
			.build()
		)
		.options(mapOf(
			"engine.MaxIsolateMemory" to "8MB",
			"sandbox.MaxHeapMemory" to "64MB",
			"sandbox.MaxCPUTime" to "500ms",
			"sandbox.MaxCPUTimeCheckInterval" to "5ms",
			"sandbox.MaxStatements" to "1000",
			"sandbox.MaxStackFrames" to "32",
			"sandbox.MaxThreads" to "1",
			"sandbox.MaxASTDepth" to "32",
			"sandbox.MaxOutputStreamSize" to "1MB",
			"sandbox.MaxErrorOutputSize" to "1MB",
		))
		.build().also {
			it.getBindings("python").putMember("scripting", this)
		}
}
