The serverside code is split into two gradle subprojects:
 - Mixin, which contains the code for the mod loaded by Ignite
 - Paper, which contains the paper plugin with most of the functionality

It's a gradle nightmare, if you know what you're doing when it comes to Gradle, either spare yourself by ignoring it, or fix it :)

The Paper one has a compileOnly dependency on mixin.

The whole situation is kind of dumb, but it works:tm:
Trying to have Ignite and Paper load the same jar causes what can best be described as "funny little classloader issues"