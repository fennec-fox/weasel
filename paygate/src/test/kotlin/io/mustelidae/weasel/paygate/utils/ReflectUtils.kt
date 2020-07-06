package io.mustelidae.weasel.paygate.utils

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun invokeId(clazz: Any, id: Long, invoke: Boolean = true): Any {
    if (invoke.not())
        return clazz

    val props = clazz::class.memberProperties.find { it.name == "id" }
    props!!.isAccessible = true
    if (props is KMutableProperty<*>) {
        props.setter.call(clazz, id)
    }
    return clazz
}
