package com.rgcase.bittorrent.torrentfile.encoding

import java.nio.file.Paths

import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext

object TorrentFileDecoder {

  def fileBytes(path: String)(implicit mat: ActorMaterializer, ec: ExecutionContext) =
    FileIO
      .fromPath(Paths.get(path))
      .toMat(
        Sink
          .fold(ByteString.empty) { case (acc, bytes) => acc ++ bytes }
      )(Keep.right)
      .run()

}
