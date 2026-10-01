package com.krithi.data.model.smb

class SmbException(
  message: String? = null,
  cause: Throwable? = null,
  val isNotFound: Boolean = false
) : Exception(message, cause)