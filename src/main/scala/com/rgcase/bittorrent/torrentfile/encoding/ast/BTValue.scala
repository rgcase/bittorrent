package com.rgcase.bittorrent.ast

import akka.util.ByteString

sealed trait BTValue

case class BTString(string: ByteString) extends BTValue { override def toString = "BTString(" + string.map(_.toChar).mkString("") + ")" }
case class BTNumber(number: BigInt) extends BTValue { override def toString = "BTNumber(" + number.toString + ")" }
case class BTDict(dict: Vector[(BTString, BTValue)]) extends BTValue { override def toString = "BTDict(Map(" + dict.toString.drop(7) + ")" }
case class BTList(list: List[BTValue]) extends BTValue { override def toString = "BTList(" + list.toString + ")" }
case object BTEmpty extends BTValue

object BTValue {
  implicit val ordering = new Ordering[BTString] {
    def compare(x: BTString, y: BTString) =
      implicitly[Ordering[String]].compare(x.string.mkString(""), y.string.mkString(""))
  }
}