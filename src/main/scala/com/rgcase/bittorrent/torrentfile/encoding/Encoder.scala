package com.rgcase.bittorrent.encoding

import akka.util.ByteString
import com.rgcase.bittorrent.ast._

object Encoder {

  def encode(btvalue: BTValue): ByteString = btvalue match {
    case BTEmpty      => encodeEmpty
    case bt: BTString => encodeBTString(bt)
    case bt: BTNumber => encodeBTNumber(bt)
    case bt: BTDict   => encodeBTDict(bt)
    case bt: BTList   => encodeBTList(bt)
  }

  private val dividerByte = ':'.toByte
  private val intByte     = 'i'.toByte
  private val dictByte    = 'd'.toByte
  private val listByte    = 'l'.toByte
  private val endByte     = 'e'.toByte

  private val encodeEmpty = ByteString.empty

  private def encodeBTString(btstring: BTString) = {
    ByteString(btstring.string.size.toString + dividerByte) ++ btstring.string
  }

  private def encodeBTNumber(btnum: BTNumber) = intByte +: ByteString(btnum.number.toString) :+ endByte

  private def encodeBTDict(btdict: BTDict) =
    dictByte +:
      btdict
        .dict
        .map { case (k ,v) => encodeBTString(k) ++ encode(v) }
        .foldLeft(ByteString.empty) { case (acc, v) => acc ++ v } :+ 'e'.toByte

  private def encodeBTList(btlist: BTList) =
    listByte +: btlist.list.foldLeft(ByteString.empty) { case (acc, v) => acc ++ encode(v) } :+ endByte

}