package com.krithi.lyric

import kotlinx.serialization.Serializable

@Serializable
data class Word(
  val time: Long, // in ms
  val content: String
)