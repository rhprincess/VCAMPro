package io.twinkle.unreal.data

import kotlinx.serialization.Serializable

@Serializable
data class EnableMap(val map: LinkedHashMap<String, Boolean> = linkedMapOf())
