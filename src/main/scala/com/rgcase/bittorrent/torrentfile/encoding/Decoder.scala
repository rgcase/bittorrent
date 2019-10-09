package com.rgcase.bittorrent.torrentfile.encoding

import akka.util.ByteString
import com.rgcase.bittorrent.torrentfile.TorrentFile
import com.rgcase.bittorrent.torrentfile.encoding.ast._

object Decoder {

  def decode(bytes: ByteString): TorrentFile =
    TorrentFile(decodeDictionary(bytes,0)._1)

  private val dividerByte = ':'.toByte
  private val intByte     = 'i'.toByte
  private val dictByte    = 'd'.toByte
  private val listByte    = 'l'.toByte
  private val endByte     = 'e'.toByte

  private def decodeString(bytes: ByteString, start: Int): (BTString, Int) = {
    var i = start

    val sizeBuffer = new StringBuffer()
    while(bytes(i) != dividerByte) {
      sizeBuffer.append(bytes(i).toChar)
      i += 1
    }

    val size = sizeBuffer.toString.toInt

    i += 1

    val end = size + i

    val buffer = ByteString.newBuilder
    while (i < end) {
      buffer += bytes(i)
      i += 1
    }

    val bts = BTString(buffer.result())
    (bts, i)
  }

  private def decodeNumber(bytes: ByteString, start: Int): (BTNumber, Int) = {
    val buffer = new StringBuffer()
    var i = start + 1
    while (bytes(i) != endByte) {
      buffer.append(bytes(i).toChar)
      i += 1
    }

    val num = BTNumber(BigInt(buffer.toString))
    (num, i + 1)
  }

  private def decodeDictionary(bytes: ByteString, start: Int): (BTDict, Int) = {
    var dict = Vector.empty[(BTString, BTValue)]
    var index = start + 1
    var inside = true
    while (inside) {
      if (bytes(index) == endByte) {
        inside = false
      } else {
        val (key, next1) = decodeString(bytes, index)
        val (value, next2) = nextType(bytes, next1)
        dict :+= (key -> value)
        index = next2
      }
    }

    val btdict = BTDict(dict)
    (btdict, index + 1)
  }

  private def decodeList(bytes: ByteString, start: Int): (BTList, Int) = {
    var list = List.empty[BTValue]
    var index = start + 1
    var inside = true
    while(inside) {
      if (bytes(index) == endByte) {
        inside = false
      } else {
        val (obj, next) = nextType(bytes, index)
        list ::= obj
        index = next
      }
    }

    val btlist = BTList(list.reverse)
    (btlist, index + 1)

  }

  private def nextType(bytes: ByteString, start: Int): (BTValue, Int) = bytes(start).toChar match {
    case `intByte`  => decodeNumber(bytes, start)
    case `dictByte` => decodeDictionary(bytes, start)
    case `listByte` => decodeList(bytes, start)
    case _          => decodeString(bytes, start)
  }

}