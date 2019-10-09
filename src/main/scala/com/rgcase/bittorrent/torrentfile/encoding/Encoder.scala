package com.rgcase.bittorrent.torrentfile.encoding

import akka.util.ByteString
import com.rgcase.bittorrent.torrentfile.TorrentFile
import com.rgcase.bittorrent.torrentfile.encoding.ast._

object Encoder {

  def encode(torrent: TorrentFile): ByteString =
    encodeBTDict(torrent.dict)

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
        .map { case (k ,v) => encodeBTString(k) ++ encodeBTValue(v) }
        .foldLeft(ByteString.empty) { case (acc, v) => acc ++ v } :+ 'e'.toByte

  private def encodeBTList(btlist: BTList) =
    listByte +: btlist.list.foldLeft(ByteString.empty) { case (acc, v) => acc ++ encodeBTValue(v) } :+ endByte

  private def encodeBTValue(btvalue: BTValue): ByteString = btvalue match {
    case BTEmpty => encodeEmpty
    case s: BTString => encodeBTString(s)
    case n: BTNumber => encodeBTNumber(n)
    case dict: BTDict => encodeBTDict(dict)
    case list: BTList => encodeBTList(list)
  }

}