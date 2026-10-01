package com.krithi.data.model.lastfm

import kotlinx.serialization.Serializable

@Serializable
class LastFmAlbum {
  var album: Album? = null

  @Serializable
  class Album {
    var image: List<Image> = ArrayList()

  }
}
