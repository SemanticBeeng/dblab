/* Generated by AutoLifter © 2014 */

package ch.epfl.data
package legobase
package deep

import scalalib._
import pardis.ir._

trait AGGRecordOps extends Base { this: DeepDSL =>
  implicit class AGGRecordRep[B](self: Rep[AGGRecord[B]])(implicit manifestB: Manifest[B]) {
    def aggs: Rep[Array[Double]] = aGGRecord_Field_Aggs[B](self)(manifestB)
    def key: Rep[B] = aGGRecord_Field_Key[B](self)(manifestB)
  }
  // constructors
  def __newAGGRecord[B](key: Rep[B], aggs: Rep[Array[Double]])(implicit manifestB: Manifest[B]): Rep[AGGRecord[B]] = aGGRecordNew[B](key, aggs)(manifestB)
  // case classes
  case class AGGRecordNew[B](key: Rep[B], aggs: Rep[Array[Double]])(implicit val manifestB: Manifest[B]) extends FunctionDef[AGGRecord[B]](None, "new AGGRecord", List(List(key, aggs))) {
    override def curriedConstructor = (copy[B] _).curried
  }

  case class AGGRecord_Field_Aggs[B](self: Rep[AGGRecord[B]])(implicit val manifestB: Manifest[B]) extends FieldDef[Array[Double]](self, "aggs") {
    override def curriedConstructor = (copy[B] _)
    override def isPure = true

  }

  case class AGGRecord_Field_Key[B](self: Rep[AGGRecord[B]])(implicit val manifestB: Manifest[B]) extends FieldDef[B](self, "key") {
    override def curriedConstructor = (copy[B] _)
    override def isPure = true

  }

  // method definitions
  def aGGRecordNew[B](key: Rep[B], aggs: Rep[Array[Double]])(implicit manifestB: Manifest[B]): Rep[AGGRecord[B]] = AGGRecordNew[B](key, aggs)
  def aGGRecord_Field_Aggs[B](self: Rep[AGGRecord[B]])(implicit manifestB: Manifest[B]): Rep[Array[Double]] = AGGRecord_Field_Aggs[B](self)
  def aGGRecord_Field_Key[B](self: Rep[AGGRecord[B]])(implicit manifestB: Manifest[B]): Rep[B] = AGGRecord_Field_Key[B](self)
  type AGGRecord[B] = ch.epfl.data.legobase.queryengine.AGGRecord[B]
}
trait AGGRecordImplicits { this: AGGRecordComponent =>
  // Add implicit conversions here!
}
trait AGGRecordComponent extends AGGRecordOps with AGGRecordImplicits { self: DeepDSL => }

trait LINEITEMRecordOps extends Base { this: DeepDSL =>
  implicit class LINEITEMRecordRep(self: Rep[LINEITEMRecord]) {
    def getField(key: Rep[String]): Rep[Option[Any]] = lINEITEMRecordGetField(self, key)
    def L_COMMENT: Rep[Array[Byte]] = lINEITEMRecord_Field_L_COMMENT(self)
    def L_SHIPMODE: Rep[Array[Byte]] = lINEITEMRecord_Field_L_SHIPMODE(self)
    def L_SHIPINSTRUCT: Rep[Array[Byte]] = lINEITEMRecord_Field_L_SHIPINSTRUCT(self)
    def L_RECEIPTDATE: Rep[Long] = lINEITEMRecord_Field_L_RECEIPTDATE(self)
    def L_COMMITDATE: Rep[Long] = lINEITEMRecord_Field_L_COMMITDATE(self)
    def L_SHIPDATE: Rep[Long] = lINEITEMRecord_Field_L_SHIPDATE(self)
    def L_LINESTATUS: Rep[Character] = lINEITEMRecord_Field_L_LINESTATUS(self)
    def L_RETURNFLAG: Rep[Character] = lINEITEMRecord_Field_L_RETURNFLAG(self)
    def L_TAX: Rep[Double] = lINEITEMRecord_Field_L_TAX(self)
    def L_DISCOUNT: Rep[Double] = lINEITEMRecord_Field_L_DISCOUNT(self)
    def L_EXTENDEDPRICE: Rep[Double] = lINEITEMRecord_Field_L_EXTENDEDPRICE(self)
    def L_QUANTITY: Rep[Double] = lINEITEMRecord_Field_L_QUANTITY(self)
    def L_LINENUMBER: Rep[Int] = lINEITEMRecord_Field_L_LINENUMBER(self)
    def L_SUPPKEY: Rep[Int] = lINEITEMRecord_Field_L_SUPPKEY(self)
    def L_PARTKEY: Rep[Int] = lINEITEMRecord_Field_L_PARTKEY(self)
    def L_ORDERKEY: Rep[Int] = lINEITEMRecord_Field_L_ORDERKEY(self)
  }
  // constructors
  def __newLINEITEMRecord(L_ORDERKEY: Rep[Int], L_PARTKEY: Rep[Int], L_SUPPKEY: Rep[Int], L_LINENUMBER: Rep[Int], L_QUANTITY: Rep[Double], L_EXTENDEDPRICE: Rep[Double], L_DISCOUNT: Rep[Double], L_TAX: Rep[Double], L_RETURNFLAG: Rep[Character], L_LINESTATUS: Rep[Character], L_SHIPDATE: Rep[Long], L_COMMITDATE: Rep[Long], L_RECEIPTDATE: Rep[Long], L_SHIPINSTRUCT: Rep[Array[Byte]], L_SHIPMODE: Rep[Array[Byte]], L_COMMENT: Rep[Array[Byte]]): Rep[LINEITEMRecord] = lINEITEMRecordNew(L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)
  // case classes
  case class LINEITEMRecordNew(L_ORDERKEY: Rep[Int], L_PARTKEY: Rep[Int], L_SUPPKEY: Rep[Int], L_LINENUMBER: Rep[Int], L_QUANTITY: Rep[Double], L_EXTENDEDPRICE: Rep[Double], L_DISCOUNT: Rep[Double], L_TAX: Rep[Double], L_RETURNFLAG: Rep[Character], L_LINESTATUS: Rep[Character], L_SHIPDATE: Rep[Long], L_COMMITDATE: Rep[Long], L_RECEIPTDATE: Rep[Long], L_SHIPINSTRUCT: Rep[Array[Byte]], L_SHIPMODE: Rep[Array[Byte]], L_COMMENT: Rep[Array[Byte]]) extends FunctionDef[LINEITEMRecord](None, "new LINEITEMRecord", List(List(L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT))) {
    override def curriedConstructor = (copy _).curried
  }

  case class LINEITEMRecordGetField(self: Rep[LINEITEMRecord], key: Rep[String]) extends FunctionDef[Option[Any]](Some(self), "getField", List(List(key))) {
    override def curriedConstructor = (copy _).curried
  }

  case class LINEITEMRecord_Field_L_COMMENT(self: Rep[LINEITEMRecord]) extends FieldDef[Array[Byte]](self, "L_COMMENT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_SHIPMODE(self: Rep[LINEITEMRecord]) extends FieldDef[Array[Byte]](self, "L_SHIPMODE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_SHIPINSTRUCT(self: Rep[LINEITEMRecord]) extends FieldDef[Array[Byte]](self, "L_SHIPINSTRUCT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_RECEIPTDATE(self: Rep[LINEITEMRecord]) extends FieldDef[Long](self, "L_RECEIPTDATE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_COMMITDATE(self: Rep[LINEITEMRecord]) extends FieldDef[Long](self, "L_COMMITDATE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_SHIPDATE(self: Rep[LINEITEMRecord]) extends FieldDef[Long](self, "L_SHIPDATE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_LINESTATUS(self: Rep[LINEITEMRecord]) extends FieldDef[Character](self, "L_LINESTATUS") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_RETURNFLAG(self: Rep[LINEITEMRecord]) extends FieldDef[Character](self, "L_RETURNFLAG") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_TAX(self: Rep[LINEITEMRecord]) extends FieldDef[Double](self, "L_TAX") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_DISCOUNT(self: Rep[LINEITEMRecord]) extends FieldDef[Double](self, "L_DISCOUNT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_EXTENDEDPRICE(self: Rep[LINEITEMRecord]) extends FieldDef[Double](self, "L_EXTENDEDPRICE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_QUANTITY(self: Rep[LINEITEMRecord]) extends FieldDef[Double](self, "L_QUANTITY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_LINENUMBER(self: Rep[LINEITEMRecord]) extends FieldDef[Int](self, "L_LINENUMBER") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_SUPPKEY(self: Rep[LINEITEMRecord]) extends FieldDef[Int](self, "L_SUPPKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_PARTKEY(self: Rep[LINEITEMRecord]) extends FieldDef[Int](self, "L_PARTKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class LINEITEMRecord_Field_L_ORDERKEY(self: Rep[LINEITEMRecord]) extends FieldDef[Int](self, "L_ORDERKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  // method definitions
  def lINEITEMRecordNew(L_ORDERKEY: Rep[Int], L_PARTKEY: Rep[Int], L_SUPPKEY: Rep[Int], L_LINENUMBER: Rep[Int], L_QUANTITY: Rep[Double], L_EXTENDEDPRICE: Rep[Double], L_DISCOUNT: Rep[Double], L_TAX: Rep[Double], L_RETURNFLAG: Rep[Character], L_LINESTATUS: Rep[Character], L_SHIPDATE: Rep[Long], L_COMMITDATE: Rep[Long], L_RECEIPTDATE: Rep[Long], L_SHIPINSTRUCT: Rep[Array[Byte]], L_SHIPMODE: Rep[Array[Byte]], L_COMMENT: Rep[Array[Byte]]): Rep[LINEITEMRecord] = LINEITEMRecordNew(L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE, L_DISCOUNT, L_TAX, L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)
  def lINEITEMRecordGetField(self: Rep[LINEITEMRecord], key: Rep[String]): Rep[Option[Any]] = LINEITEMRecordGetField(self, key)
  def lINEITEMRecord_Field_L_COMMENT(self: Rep[LINEITEMRecord]): Rep[Array[Byte]] = LINEITEMRecord_Field_L_COMMENT(self)
  def lINEITEMRecord_Field_L_SHIPMODE(self: Rep[LINEITEMRecord]): Rep[Array[Byte]] = LINEITEMRecord_Field_L_SHIPMODE(self)
  def lINEITEMRecord_Field_L_SHIPINSTRUCT(self: Rep[LINEITEMRecord]): Rep[Array[Byte]] = LINEITEMRecord_Field_L_SHIPINSTRUCT(self)
  def lINEITEMRecord_Field_L_RECEIPTDATE(self: Rep[LINEITEMRecord]): Rep[Long] = LINEITEMRecord_Field_L_RECEIPTDATE(self)
  def lINEITEMRecord_Field_L_COMMITDATE(self: Rep[LINEITEMRecord]): Rep[Long] = LINEITEMRecord_Field_L_COMMITDATE(self)
  def lINEITEMRecord_Field_L_SHIPDATE(self: Rep[LINEITEMRecord]): Rep[Long] = LINEITEMRecord_Field_L_SHIPDATE(self)
  def lINEITEMRecord_Field_L_LINESTATUS(self: Rep[LINEITEMRecord]): Rep[Character] = LINEITEMRecord_Field_L_LINESTATUS(self)
  def lINEITEMRecord_Field_L_RETURNFLAG(self: Rep[LINEITEMRecord]): Rep[Character] = LINEITEMRecord_Field_L_RETURNFLAG(self)
  def lINEITEMRecord_Field_L_TAX(self: Rep[LINEITEMRecord]): Rep[Double] = LINEITEMRecord_Field_L_TAX(self)
  def lINEITEMRecord_Field_L_DISCOUNT(self: Rep[LINEITEMRecord]): Rep[Double] = LINEITEMRecord_Field_L_DISCOUNT(self)
  def lINEITEMRecord_Field_L_EXTENDEDPRICE(self: Rep[LINEITEMRecord]): Rep[Double] = LINEITEMRecord_Field_L_EXTENDEDPRICE(self)
  def lINEITEMRecord_Field_L_QUANTITY(self: Rep[LINEITEMRecord]): Rep[Double] = LINEITEMRecord_Field_L_QUANTITY(self)
  def lINEITEMRecord_Field_L_LINENUMBER(self: Rep[LINEITEMRecord]): Rep[Int] = LINEITEMRecord_Field_L_LINENUMBER(self)
  def lINEITEMRecord_Field_L_SUPPKEY(self: Rep[LINEITEMRecord]): Rep[Int] = LINEITEMRecord_Field_L_SUPPKEY(self)
  def lINEITEMRecord_Field_L_PARTKEY(self: Rep[LINEITEMRecord]): Rep[Int] = LINEITEMRecord_Field_L_PARTKEY(self)
  def lINEITEMRecord_Field_L_ORDERKEY(self: Rep[LINEITEMRecord]): Rep[Int] = LINEITEMRecord_Field_L_ORDERKEY(self)
  type LINEITEMRecord = ch.epfl.data.legobase.storagemanager.TPCHRelations.LINEITEMRecord
}
trait LINEITEMRecordImplicits { this: LINEITEMRecordComponent =>
  // Add implicit conversions here!
}
trait LINEITEMRecordComponent extends LINEITEMRecordOps with LINEITEMRecordImplicits { self: DeepDSL => }

trait SUPPLIERRecordOps extends Base { this: DeepDSL =>
  implicit class SUPPLIERRecordRep(self: Rep[SUPPLIERRecord]) {
    def getField(key: Rep[String]): Rep[Option[Any]] = sUPPLIERRecordGetField(self, key)
    def S_COMMENT: Rep[Array[Byte]] = sUPPLIERRecord_Field_S_COMMENT(self)
    def S_ACCTBAL: Rep[Double] = sUPPLIERRecord_Field_S_ACCTBAL(self)
    def S_PHONE: Rep[Array[Byte]] = sUPPLIERRecord_Field_S_PHONE(self)
    def S_NATIONKEY: Rep[Int] = sUPPLIERRecord_Field_S_NATIONKEY(self)
    def S_ADDRESS: Rep[Array[Byte]] = sUPPLIERRecord_Field_S_ADDRESS(self)
    def S_NAME: Rep[Array[Byte]] = sUPPLIERRecord_Field_S_NAME(self)
    def S_SUPPKEY: Rep[Int] = sUPPLIERRecord_Field_S_SUPPKEY(self)
  }
  // constructors
  def __newSUPPLIERRecord(S_SUPPKEY: Rep[Int], S_NAME: Rep[Array[Byte]], S_ADDRESS: Rep[Array[Byte]], S_NATIONKEY: Rep[Int], S_PHONE: Rep[Array[Byte]], S_ACCTBAL: Rep[Double], S_COMMENT: Rep[Array[Byte]]): Rep[SUPPLIERRecord] = sUPPLIERRecordNew(S_SUPPKEY, S_NAME, S_ADDRESS, S_NATIONKEY, S_PHONE, S_ACCTBAL, S_COMMENT)
  // case classes
  case class SUPPLIERRecordNew(S_SUPPKEY: Rep[Int], S_NAME: Rep[Array[Byte]], S_ADDRESS: Rep[Array[Byte]], S_NATIONKEY: Rep[Int], S_PHONE: Rep[Array[Byte]], S_ACCTBAL: Rep[Double], S_COMMENT: Rep[Array[Byte]]) extends FunctionDef[SUPPLIERRecord](None, "new SUPPLIERRecord", List(List(S_SUPPKEY, S_NAME, S_ADDRESS, S_NATIONKEY, S_PHONE, S_ACCTBAL, S_COMMENT))) {
    override def curriedConstructor = (copy _).curried
  }

  case class SUPPLIERRecordGetField(self: Rep[SUPPLIERRecord], key: Rep[String]) extends FunctionDef[Option[Any]](Some(self), "getField", List(List(key))) {
    override def curriedConstructor = (copy _).curried
  }

  case class SUPPLIERRecord_Field_S_COMMENT(self: Rep[SUPPLIERRecord]) extends FieldDef[Array[Byte]](self, "S_COMMENT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class SUPPLIERRecord_Field_S_ACCTBAL(self: Rep[SUPPLIERRecord]) extends FieldDef[Double](self, "S_ACCTBAL") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class SUPPLIERRecord_Field_S_PHONE(self: Rep[SUPPLIERRecord]) extends FieldDef[Array[Byte]](self, "S_PHONE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class SUPPLIERRecord_Field_S_NATIONKEY(self: Rep[SUPPLIERRecord]) extends FieldDef[Int](self, "S_NATIONKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class SUPPLIERRecord_Field_S_ADDRESS(self: Rep[SUPPLIERRecord]) extends FieldDef[Array[Byte]](self, "S_ADDRESS") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class SUPPLIERRecord_Field_S_NAME(self: Rep[SUPPLIERRecord]) extends FieldDef[Array[Byte]](self, "S_NAME") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class SUPPLIERRecord_Field_S_SUPPKEY(self: Rep[SUPPLIERRecord]) extends FieldDef[Int](self, "S_SUPPKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  // method definitions
  def sUPPLIERRecordNew(S_SUPPKEY: Rep[Int], S_NAME: Rep[Array[Byte]], S_ADDRESS: Rep[Array[Byte]], S_NATIONKEY: Rep[Int], S_PHONE: Rep[Array[Byte]], S_ACCTBAL: Rep[Double], S_COMMENT: Rep[Array[Byte]]): Rep[SUPPLIERRecord] = SUPPLIERRecordNew(S_SUPPKEY, S_NAME, S_ADDRESS, S_NATIONKEY, S_PHONE, S_ACCTBAL, S_COMMENT)
  def sUPPLIERRecordGetField(self: Rep[SUPPLIERRecord], key: Rep[String]): Rep[Option[Any]] = SUPPLIERRecordGetField(self, key)
  def sUPPLIERRecord_Field_S_COMMENT(self: Rep[SUPPLIERRecord]): Rep[Array[Byte]] = SUPPLIERRecord_Field_S_COMMENT(self)
  def sUPPLIERRecord_Field_S_ACCTBAL(self: Rep[SUPPLIERRecord]): Rep[Double] = SUPPLIERRecord_Field_S_ACCTBAL(self)
  def sUPPLIERRecord_Field_S_PHONE(self: Rep[SUPPLIERRecord]): Rep[Array[Byte]] = SUPPLIERRecord_Field_S_PHONE(self)
  def sUPPLIERRecord_Field_S_NATIONKEY(self: Rep[SUPPLIERRecord]): Rep[Int] = SUPPLIERRecord_Field_S_NATIONKEY(self)
  def sUPPLIERRecord_Field_S_ADDRESS(self: Rep[SUPPLIERRecord]): Rep[Array[Byte]] = SUPPLIERRecord_Field_S_ADDRESS(self)
  def sUPPLIERRecord_Field_S_NAME(self: Rep[SUPPLIERRecord]): Rep[Array[Byte]] = SUPPLIERRecord_Field_S_NAME(self)
  def sUPPLIERRecord_Field_S_SUPPKEY(self: Rep[SUPPLIERRecord]): Rep[Int] = SUPPLIERRecord_Field_S_SUPPKEY(self)
  type SUPPLIERRecord = ch.epfl.data.legobase.storagemanager.TPCHRelations.SUPPLIERRecord
}
trait SUPPLIERRecordImplicits { this: SUPPLIERRecordComponent =>
  // Add implicit conversions here!
}
trait SUPPLIERRecordComponent extends SUPPLIERRecordOps with SUPPLIERRecordImplicits { self: DeepDSL => }

trait PARTSUPPRecordOps extends Base { this: DeepDSL =>
  implicit class PARTSUPPRecordRep(self: Rep[PARTSUPPRecord]) {
    def getField(key: Rep[String]): Rep[Option[Any]] = pARTSUPPRecordGetField(self, key)
    def PS_COMMENT: Rep[Array[Byte]] = pARTSUPPRecord_Field_PS_COMMENT(self)
    def PS_SUPPLYCOST: Rep[Double] = pARTSUPPRecord_Field_PS_SUPPLYCOST(self)
    def PS_AVAILQTY: Rep[Int] = pARTSUPPRecord_Field_PS_AVAILQTY(self)
    def PS_SUPPKEY: Rep[Int] = pARTSUPPRecord_Field_PS_SUPPKEY(self)
    def PS_PARTKEY: Rep[Int] = pARTSUPPRecord_Field_PS_PARTKEY(self)
  }
  // constructors
  def __newPARTSUPPRecord(PS_PARTKEY: Rep[Int], PS_SUPPKEY: Rep[Int], PS_AVAILQTY: Rep[Int], PS_SUPPLYCOST: Rep[Double], PS_COMMENT: Rep[Array[Byte]]): Rep[PARTSUPPRecord] = pARTSUPPRecordNew(PS_PARTKEY, PS_SUPPKEY, PS_AVAILQTY, PS_SUPPLYCOST, PS_COMMENT)
  // case classes
  case class PARTSUPPRecordNew(PS_PARTKEY: Rep[Int], PS_SUPPKEY: Rep[Int], PS_AVAILQTY: Rep[Int], PS_SUPPLYCOST: Rep[Double], PS_COMMENT: Rep[Array[Byte]]) extends FunctionDef[PARTSUPPRecord](None, "new PARTSUPPRecord", List(List(PS_PARTKEY, PS_SUPPKEY, PS_AVAILQTY, PS_SUPPLYCOST, PS_COMMENT))) {
    override def curriedConstructor = (copy _).curried
  }

  case class PARTSUPPRecordGetField(self: Rep[PARTSUPPRecord], key: Rep[String]) extends FunctionDef[Option[Any]](Some(self), "getField", List(List(key))) {
    override def curriedConstructor = (copy _).curried
  }

  case class PARTSUPPRecord_Field_PS_COMMENT(self: Rep[PARTSUPPRecord]) extends FieldDef[Array[Byte]](self, "PS_COMMENT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTSUPPRecord_Field_PS_SUPPLYCOST(self: Rep[PARTSUPPRecord]) extends FieldDef[Double](self, "PS_SUPPLYCOST") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTSUPPRecord_Field_PS_AVAILQTY(self: Rep[PARTSUPPRecord]) extends FieldDef[Int](self, "PS_AVAILQTY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTSUPPRecord_Field_PS_SUPPKEY(self: Rep[PARTSUPPRecord]) extends FieldDef[Int](self, "PS_SUPPKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTSUPPRecord_Field_PS_PARTKEY(self: Rep[PARTSUPPRecord]) extends FieldDef[Int](self, "PS_PARTKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  // method definitions
  def pARTSUPPRecordNew(PS_PARTKEY: Rep[Int], PS_SUPPKEY: Rep[Int], PS_AVAILQTY: Rep[Int], PS_SUPPLYCOST: Rep[Double], PS_COMMENT: Rep[Array[Byte]]): Rep[PARTSUPPRecord] = PARTSUPPRecordNew(PS_PARTKEY, PS_SUPPKEY, PS_AVAILQTY, PS_SUPPLYCOST, PS_COMMENT)
  def pARTSUPPRecordGetField(self: Rep[PARTSUPPRecord], key: Rep[String]): Rep[Option[Any]] = PARTSUPPRecordGetField(self, key)
  def pARTSUPPRecord_Field_PS_COMMENT(self: Rep[PARTSUPPRecord]): Rep[Array[Byte]] = PARTSUPPRecord_Field_PS_COMMENT(self)
  def pARTSUPPRecord_Field_PS_SUPPLYCOST(self: Rep[PARTSUPPRecord]): Rep[Double] = PARTSUPPRecord_Field_PS_SUPPLYCOST(self)
  def pARTSUPPRecord_Field_PS_AVAILQTY(self: Rep[PARTSUPPRecord]): Rep[Int] = PARTSUPPRecord_Field_PS_AVAILQTY(self)
  def pARTSUPPRecord_Field_PS_SUPPKEY(self: Rep[PARTSUPPRecord]): Rep[Int] = PARTSUPPRecord_Field_PS_SUPPKEY(self)
  def pARTSUPPRecord_Field_PS_PARTKEY(self: Rep[PARTSUPPRecord]): Rep[Int] = PARTSUPPRecord_Field_PS_PARTKEY(self)
  type PARTSUPPRecord = ch.epfl.data.legobase.storagemanager.TPCHRelations.PARTSUPPRecord
}
trait PARTSUPPRecordImplicits { this: PARTSUPPRecordComponent =>
  // Add implicit conversions here!
}
trait PARTSUPPRecordComponent extends PARTSUPPRecordOps with PARTSUPPRecordImplicits { self: DeepDSL => }

trait REGIONRecordOps extends Base { this: DeepDSL =>
  implicit class REGIONRecordRep(self: Rep[REGIONRecord]) {
    def getField(key: Rep[String]): Rep[Option[Any]] = rEGIONRecordGetField(self, key)
    def R_COMMENT: Rep[Array[Byte]] = rEGIONRecord_Field_R_COMMENT(self)
    def R_NAME: Rep[Array[Byte]] = rEGIONRecord_Field_R_NAME(self)
    def R_REGIONKEY: Rep[Int] = rEGIONRecord_Field_R_REGIONKEY(self)
  }
  // constructors
  def __newREGIONRecord(R_REGIONKEY: Rep[Int], R_NAME: Rep[Array[Byte]], R_COMMENT: Rep[Array[Byte]]): Rep[REGIONRecord] = rEGIONRecordNew(R_REGIONKEY, R_NAME, R_COMMENT)
  // case classes
  case class REGIONRecordNew(R_REGIONKEY: Rep[Int], R_NAME: Rep[Array[Byte]], R_COMMENT: Rep[Array[Byte]]) extends FunctionDef[REGIONRecord](None, "new REGIONRecord", List(List(R_REGIONKEY, R_NAME, R_COMMENT))) {
    override def curriedConstructor = (copy _).curried
  }

  case class REGIONRecordGetField(self: Rep[REGIONRecord], key: Rep[String]) extends FunctionDef[Option[Any]](Some(self), "getField", List(List(key))) {
    override def curriedConstructor = (copy _).curried
  }

  case class REGIONRecord_Field_R_COMMENT(self: Rep[REGIONRecord]) extends FieldDef[Array[Byte]](self, "R_COMMENT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class REGIONRecord_Field_R_NAME(self: Rep[REGIONRecord]) extends FieldDef[Array[Byte]](self, "R_NAME") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class REGIONRecord_Field_R_REGIONKEY(self: Rep[REGIONRecord]) extends FieldDef[Int](self, "R_REGIONKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  // method definitions
  def rEGIONRecordNew(R_REGIONKEY: Rep[Int], R_NAME: Rep[Array[Byte]], R_COMMENT: Rep[Array[Byte]]): Rep[REGIONRecord] = REGIONRecordNew(R_REGIONKEY, R_NAME, R_COMMENT)
  def rEGIONRecordGetField(self: Rep[REGIONRecord], key: Rep[String]): Rep[Option[Any]] = REGIONRecordGetField(self, key)
  def rEGIONRecord_Field_R_COMMENT(self: Rep[REGIONRecord]): Rep[Array[Byte]] = REGIONRecord_Field_R_COMMENT(self)
  def rEGIONRecord_Field_R_NAME(self: Rep[REGIONRecord]): Rep[Array[Byte]] = REGIONRecord_Field_R_NAME(self)
  def rEGIONRecord_Field_R_REGIONKEY(self: Rep[REGIONRecord]): Rep[Int] = REGIONRecord_Field_R_REGIONKEY(self)
  type REGIONRecord = ch.epfl.data.legobase.storagemanager.TPCHRelations.REGIONRecord
}
trait REGIONRecordImplicits { this: REGIONRecordComponent =>
  // Add implicit conversions here!
}
trait REGIONRecordComponent extends REGIONRecordOps with REGIONRecordImplicits { self: DeepDSL => }

trait NATIONRecordOps extends Base { this: DeepDSL =>
  implicit class NATIONRecordRep(self: Rep[NATIONRecord]) {
    def getField(key: Rep[String]): Rep[Option[Any]] = nATIONRecordGetField(self, key)
    def N_COMMENT: Rep[Array[Byte]] = nATIONRecord_Field_N_COMMENT(self)
    def N_REGIONKEY: Rep[Int] = nATIONRecord_Field_N_REGIONKEY(self)
    def N_NAME: Rep[Array[Byte]] = nATIONRecord_Field_N_NAME(self)
    def N_NATIONKEY: Rep[Int] = nATIONRecord_Field_N_NATIONKEY(self)
  }
  // constructors
  def __newNATIONRecord(N_NATIONKEY: Rep[Int], N_NAME: Rep[Array[Byte]], N_REGIONKEY: Rep[Int], N_COMMENT: Rep[Array[Byte]]): Rep[NATIONRecord] = nATIONRecordNew(N_NATIONKEY, N_NAME, N_REGIONKEY, N_COMMENT)
  // case classes
  case class NATIONRecordNew(N_NATIONKEY: Rep[Int], N_NAME: Rep[Array[Byte]], N_REGIONKEY: Rep[Int], N_COMMENT: Rep[Array[Byte]]) extends FunctionDef[NATIONRecord](None, "new NATIONRecord", List(List(N_NATIONKEY, N_NAME, N_REGIONKEY, N_COMMENT))) {
    override def curriedConstructor = (copy _).curried
  }

  case class NATIONRecordGetField(self: Rep[NATIONRecord], key: Rep[String]) extends FunctionDef[Option[Any]](Some(self), "getField", List(List(key))) {
    override def curriedConstructor = (copy _).curried
  }

  case class NATIONRecord_Field_N_COMMENT(self: Rep[NATIONRecord]) extends FieldDef[Array[Byte]](self, "N_COMMENT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class NATIONRecord_Field_N_REGIONKEY(self: Rep[NATIONRecord]) extends FieldDef[Int](self, "N_REGIONKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class NATIONRecord_Field_N_NAME(self: Rep[NATIONRecord]) extends FieldDef[Array[Byte]](self, "N_NAME") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class NATIONRecord_Field_N_NATIONKEY(self: Rep[NATIONRecord]) extends FieldDef[Int](self, "N_NATIONKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  // method definitions
  def nATIONRecordNew(N_NATIONKEY: Rep[Int], N_NAME: Rep[Array[Byte]], N_REGIONKEY: Rep[Int], N_COMMENT: Rep[Array[Byte]]): Rep[NATIONRecord] = NATIONRecordNew(N_NATIONKEY, N_NAME, N_REGIONKEY, N_COMMENT)
  def nATIONRecordGetField(self: Rep[NATIONRecord], key: Rep[String]): Rep[Option[Any]] = NATIONRecordGetField(self, key)
  def nATIONRecord_Field_N_COMMENT(self: Rep[NATIONRecord]): Rep[Array[Byte]] = NATIONRecord_Field_N_COMMENT(self)
  def nATIONRecord_Field_N_REGIONKEY(self: Rep[NATIONRecord]): Rep[Int] = NATIONRecord_Field_N_REGIONKEY(self)
  def nATIONRecord_Field_N_NAME(self: Rep[NATIONRecord]): Rep[Array[Byte]] = NATIONRecord_Field_N_NAME(self)
  def nATIONRecord_Field_N_NATIONKEY(self: Rep[NATIONRecord]): Rep[Int] = NATIONRecord_Field_N_NATIONKEY(self)
  type NATIONRecord = ch.epfl.data.legobase.storagemanager.TPCHRelations.NATIONRecord
}
trait NATIONRecordImplicits { this: NATIONRecordComponent =>
  // Add implicit conversions here!
}
trait NATIONRecordComponent extends NATIONRecordOps with NATIONRecordImplicits { self: DeepDSL => }

trait PARTRecordOps extends Base { this: DeepDSL =>
  implicit class PARTRecordRep(self: Rep[PARTRecord]) {
    def getField(key: Rep[String]): Rep[Option[Any]] = pARTRecordGetField(self, key)
    def P_COMMENT: Rep[Array[Byte]] = pARTRecord_Field_P_COMMENT(self)
    def P_RETAILPRICE: Rep[Double] = pARTRecord_Field_P_RETAILPRICE(self)
    def P_CONTAINER: Rep[Array[Byte]] = pARTRecord_Field_P_CONTAINER(self)
    def P_SIZE: Rep[Int] = pARTRecord_Field_P_SIZE(self)
    def P_TYPE: Rep[Array[Byte]] = pARTRecord_Field_P_TYPE(self)
    def P_BRAND: Rep[Array[Byte]] = pARTRecord_Field_P_BRAND(self)
    def P_MFGR: Rep[Array[Byte]] = pARTRecord_Field_P_MFGR(self)
    def P_NAME: Rep[Array[Byte]] = pARTRecord_Field_P_NAME(self)
    def P_PARTKEY: Rep[Int] = pARTRecord_Field_P_PARTKEY(self)
  }
  // constructors
  def __newPARTRecord(P_PARTKEY: Rep[Int], P_NAME: Rep[Array[Byte]], P_MFGR: Rep[Array[Byte]], P_BRAND: Rep[Array[Byte]], P_TYPE: Rep[Array[Byte]], P_SIZE: Rep[Int], P_CONTAINER: Rep[Array[Byte]], P_RETAILPRICE: Rep[Double], P_COMMENT: Rep[Array[Byte]]): Rep[PARTRecord] = pARTRecordNew(P_PARTKEY, P_NAME, P_MFGR, P_BRAND, P_TYPE, P_SIZE, P_CONTAINER, P_RETAILPRICE, P_COMMENT)
  // case classes
  case class PARTRecordNew(P_PARTKEY: Rep[Int], P_NAME: Rep[Array[Byte]], P_MFGR: Rep[Array[Byte]], P_BRAND: Rep[Array[Byte]], P_TYPE: Rep[Array[Byte]], P_SIZE: Rep[Int], P_CONTAINER: Rep[Array[Byte]], P_RETAILPRICE: Rep[Double], P_COMMENT: Rep[Array[Byte]]) extends FunctionDef[PARTRecord](None, "new PARTRecord", List(List(P_PARTKEY, P_NAME, P_MFGR, P_BRAND, P_TYPE, P_SIZE, P_CONTAINER, P_RETAILPRICE, P_COMMENT))) {
    override def curriedConstructor = (copy _).curried
  }

  case class PARTRecordGetField(self: Rep[PARTRecord], key: Rep[String]) extends FunctionDef[Option[Any]](Some(self), "getField", List(List(key))) {
    override def curriedConstructor = (copy _).curried
  }

  case class PARTRecord_Field_P_COMMENT(self: Rep[PARTRecord]) extends FieldDef[Array[Byte]](self, "P_COMMENT") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_RETAILPRICE(self: Rep[PARTRecord]) extends FieldDef[Double](self, "P_RETAILPRICE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_CONTAINER(self: Rep[PARTRecord]) extends FieldDef[Array[Byte]](self, "P_CONTAINER") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_SIZE(self: Rep[PARTRecord]) extends FieldDef[Int](self, "P_SIZE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_TYPE(self: Rep[PARTRecord]) extends FieldDef[Array[Byte]](self, "P_TYPE") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_BRAND(self: Rep[PARTRecord]) extends FieldDef[Array[Byte]](self, "P_BRAND") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_MFGR(self: Rep[PARTRecord]) extends FieldDef[Array[Byte]](self, "P_MFGR") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_NAME(self: Rep[PARTRecord]) extends FieldDef[Array[Byte]](self, "P_NAME") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  case class PARTRecord_Field_P_PARTKEY(self: Rep[PARTRecord]) extends FieldDef[Int](self, "P_PARTKEY") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  // method definitions
  def pARTRecordNew(P_PARTKEY: Rep[Int], P_NAME: Rep[Array[Byte]], P_MFGR: Rep[Array[Byte]], P_BRAND: Rep[Array[Byte]], P_TYPE: Rep[Array[Byte]], P_SIZE: Rep[Int], P_CONTAINER: Rep[Array[Byte]], P_RETAILPRICE: Rep[Double], P_COMMENT: Rep[Array[Byte]]): Rep[PARTRecord] = PARTRecordNew(P_PARTKEY, P_NAME, P_MFGR, P_BRAND, P_TYPE, P_SIZE, P_CONTAINER, P_RETAILPRICE, P_COMMENT)
  def pARTRecordGetField(self: Rep[PARTRecord], key: Rep[String]): Rep[Option[Any]] = PARTRecordGetField(self, key)
  def pARTRecord_Field_P_COMMENT(self: Rep[PARTRecord]): Rep[Array[Byte]] = PARTRecord_Field_P_COMMENT(self)
  def pARTRecord_Field_P_RETAILPRICE(self: Rep[PARTRecord]): Rep[Double] = PARTRecord_Field_P_RETAILPRICE(self)
  def pARTRecord_Field_P_CONTAINER(self: Rep[PARTRecord]): Rep[Array[Byte]] = PARTRecord_Field_P_CONTAINER(self)
  def pARTRecord_Field_P_SIZE(self: Rep[PARTRecord]): Rep[Int] = PARTRecord_Field_P_SIZE(self)
  def pARTRecord_Field_P_TYPE(self: Rep[PARTRecord]): Rep[Array[Byte]] = PARTRecord_Field_P_TYPE(self)
  def pARTRecord_Field_P_BRAND(self: Rep[PARTRecord]): Rep[Array[Byte]] = PARTRecord_Field_P_BRAND(self)
  def pARTRecord_Field_P_MFGR(self: Rep[PARTRecord]): Rep[Array[Byte]] = PARTRecord_Field_P_MFGR(self)
  def pARTRecord_Field_P_NAME(self: Rep[PARTRecord]): Rep[Array[Byte]] = PARTRecord_Field_P_NAME(self)
  def pARTRecord_Field_P_PARTKEY(self: Rep[PARTRecord]): Rep[Int] = PARTRecord_Field_P_PARTKEY(self)
  type PARTRecord = ch.epfl.data.legobase.storagemanager.TPCHRelations.PARTRecord
}
trait PARTRecordImplicits { this: PARTRecordComponent =>
  // Add implicit conversions here!
}
trait PARTRecordComponent extends PARTRecordOps with PARTRecordImplicits { self: DeepDSL => }

trait K2DBScannerOps extends Base { this: DeepDSL =>
  implicit class K2DBScannerRep(self: Rep[K2DBScanner]) {
    def next_int(): Rep[Int] = k2DBScannerNext_int(self)
    def next_double(): Rep[Double] = k2DBScannerNext_double(self)
    def next_char(): Rep[Char] = k2DBScannerNext_char(self)
    def next(buf: Rep[Array[Byte]])(implicit overload1: Overloaded1): Rep[Int] = k2DBScannerNext1(self, buf)
    def next(buf: Rep[Array[Byte]], offset: Rep[Int])(implicit overload2: Overloaded2): Rep[Int] = k2DBScannerNext2(self, buf, offset)
    def next_date(): Rep[Long] = k2DBScannerNext_date(self)
    def hasNext(): Rep[Boolean] = k2DBScannerHasNext(self)
    def delimiter_=(x$1: Rep[Char]): Rep[Unit] = k2DBScanner_Field_Delimiter_$eq(self, x$1)
    def delimiter: Rep[Char] = k2DBScanner_Field_Delimiter(self)
    def intDigits_=(x$1: Rep[Int]): Rep[Unit] = k2DBScanner_Field_IntDigits_$eq(self, x$1)
    def intDigits: Rep[Int] = k2DBScanner_Field_IntDigits(self)
    def byteRead_=(x$1: Rep[Int]): Rep[Unit] = k2DBScanner_Field_ByteRead_$eq(self, x$1)
    def byteRead: Rep[Int] = k2DBScanner_Field_ByteRead(self)
    def filename(): Rep[String] = k2DBScanner_Field_Filename(self)
  }
  // constructors
  def __newK2DBScanner(filename: Rep[String]): Rep[K2DBScanner] = k2DBScannerNew(filename)
  // case classes
  case class K2DBScannerNew(filename: Rep[String]) extends FunctionDef[K2DBScanner](None, "new K2DBScanner", List(List(filename))) {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScannerNext_int(self: Rep[K2DBScanner]) extends FunctionDef[Int](Some(self), "next_int", List()) {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScannerNext_double(self: Rep[K2DBScanner]) extends FunctionDef[Double](Some(self), "next_double", List()) {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScannerNext_char(self: Rep[K2DBScanner]) extends FunctionDef[Char](Some(self), "next_char", List()) {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScannerNext1(self: Rep[K2DBScanner], buf: Rep[Array[Byte]]) extends FunctionDef[Int](Some(self), "next", List(List(buf))) {
    override def curriedConstructor = (copy _).curried
  }

  case class K2DBScannerNext2(self: Rep[K2DBScanner], buf: Rep[Array[Byte]], offset: Rep[Int]) extends FunctionDef[Int](Some(self), "next", List(List(buf, offset))) {
    override def curriedConstructor = (copy _).curried
  }

  case class K2DBScannerNext_date(self: Rep[K2DBScanner]) extends FunctionDef[Long](Some(self), "next_date", List()) {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScannerHasNext(self: Rep[K2DBScanner]) extends FunctionDef[Boolean](Some(self), "hasNext", List()) {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScanner_Field_Delimiter_$eq(self: Rep[K2DBScanner], x$1: Rep[Char]) extends FieldSetter[Char](self, "delimiter", x$1) {
    override def curriedConstructor = (copy _).curried
  }

  case class K2DBScanner_Field_Delimiter(self: Rep[K2DBScanner]) extends FieldGetter[Char](self, "delimiter") {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScanner_Field_IntDigits_$eq(self: Rep[K2DBScanner], x$1: Rep[Int]) extends FieldSetter[Int](self, "intDigits", x$1) {
    override def curriedConstructor = (copy _).curried
  }

  case class K2DBScanner_Field_IntDigits(self: Rep[K2DBScanner]) extends FieldGetter[Int](self, "intDigits") {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScanner_Field_ByteRead_$eq(self: Rep[K2DBScanner], x$1: Rep[Int]) extends FieldSetter[Int](self, "byteRead", x$1) {
    override def curriedConstructor = (copy _).curried
  }

  case class K2DBScanner_Field_ByteRead(self: Rep[K2DBScanner]) extends FieldGetter[Int](self, "byteRead") {
    override def curriedConstructor = (copy _)
  }

  case class K2DBScanner_Field_Filename(self: Rep[K2DBScanner]) extends FieldDef[String](self, "filename") {
    override def curriedConstructor = (copy _)
    override def isPure = true

  }

  // method definitions
  def k2DBScannerNew(filename: Rep[String]): Rep[K2DBScanner] = K2DBScannerNew(filename)
  def k2DBScannerNext_int(self: Rep[K2DBScanner]): Rep[Int] = K2DBScannerNext_int(self)
  def k2DBScannerNext_double(self: Rep[K2DBScanner]): Rep[Double] = K2DBScannerNext_double(self)
  def k2DBScannerNext_char(self: Rep[K2DBScanner]): Rep[Char] = K2DBScannerNext_char(self)
  def k2DBScannerNext1(self: Rep[K2DBScanner], buf: Rep[Array[Byte]]): Rep[Int] = K2DBScannerNext1(self, buf)
  def k2DBScannerNext2(self: Rep[K2DBScanner], buf: Rep[Array[Byte]], offset: Rep[Int]): Rep[Int] = K2DBScannerNext2(self, buf, offset)
  def k2DBScannerNext_date(self: Rep[K2DBScanner]): Rep[Long] = K2DBScannerNext_date(self)
  def k2DBScannerHasNext(self: Rep[K2DBScanner]): Rep[Boolean] = K2DBScannerHasNext(self)
  def k2DBScanner_Field_Delimiter_$eq(self: Rep[K2DBScanner], x$1: Rep[Char]): Rep[Unit] = K2DBScanner_Field_Delimiter_$eq(self, x$1)
  def k2DBScanner_Field_Delimiter(self: Rep[K2DBScanner]): Rep[Char] = K2DBScanner_Field_Delimiter(self)
  def k2DBScanner_Field_IntDigits_$eq(self: Rep[K2DBScanner], x$1: Rep[Int]): Rep[Unit] = K2DBScanner_Field_IntDigits_$eq(self, x$1)
  def k2DBScanner_Field_IntDigits(self: Rep[K2DBScanner]): Rep[Int] = K2DBScanner_Field_IntDigits(self)
  def k2DBScanner_Field_ByteRead_$eq(self: Rep[K2DBScanner], x$1: Rep[Int]): Rep[Unit] = K2DBScanner_Field_ByteRead_$eq(self, x$1)
  def k2DBScanner_Field_ByteRead(self: Rep[K2DBScanner]): Rep[Int] = K2DBScanner_Field_ByteRead(self)
  def k2DBScanner_Field_Filename(self: Rep[K2DBScanner]): Rep[String] = K2DBScanner_Field_Filename(self)
  type K2DBScanner = ch.epfl.data.legobase.storagemanager.K2DBScanner
}
trait K2DBScannerImplicits { this: K2DBScannerComponent =>
  // Add implicit conversions here!
}
trait K2DBScannerComponent extends K2DBScannerOps with K2DBScannerImplicits { self: DeepDSL => }

trait WindowRecordOps extends Base { this: DeepDSL =>
  implicit class WindowRecordRep[B, C](self: Rep[WindowRecord[B, C]])(implicit manifestB: Manifest[B], manifestC: Manifest[C]) {
    def wnd: Rep[C] = windowRecord_Field_Wnd[B, C](self)(manifestB, manifestC)
    def key: Rep[B] = windowRecord_Field_Key[B, C](self)(manifestB, manifestC)
  }
  // constructors
  def __newWindowRecord[B, C](key: Rep[B], wnd: Rep[C])(implicit manifestB: Manifest[B], manifestC: Manifest[C]): Rep[WindowRecord[B, C]] = windowRecordNew[B, C](key, wnd)(manifestB, manifestC)
  // case classes
  case class WindowRecordNew[B, C](key: Rep[B], wnd: Rep[C])(implicit val manifestB: Manifest[B], val manifestC: Manifest[C]) extends FunctionDef[WindowRecord[B, C]](None, "new WindowRecord", List(List(key, wnd))) {
    override def curriedConstructor = (copy[B, C] _).curried
  }

  case class WindowRecord_Field_Wnd[B, C](self: Rep[WindowRecord[B, C]])(implicit val manifestB: Manifest[B], val manifestC: Manifest[C]) extends FieldDef[C](self, "wnd") {
    override def curriedConstructor = (copy[B, C] _)
    override def isPure = true

  }

  case class WindowRecord_Field_Key[B, C](self: Rep[WindowRecord[B, C]])(implicit val manifestB: Manifest[B], val manifestC: Manifest[C]) extends FieldDef[B](self, "key") {
    override def curriedConstructor = (copy[B, C] _)
    override def isPure = true

  }

  // method definitions
  def windowRecordNew[B, C](key: Rep[B], wnd: Rep[C])(implicit manifestB: Manifest[B], manifestC: Manifest[C]): Rep[WindowRecord[B, C]] = WindowRecordNew[B, C](key, wnd)
  def windowRecord_Field_Wnd[B, C](self: Rep[WindowRecord[B, C]])(implicit manifestB: Manifest[B], manifestC: Manifest[C]): Rep[C] = WindowRecord_Field_Wnd[B, C](self)
  def windowRecord_Field_Key[B, C](self: Rep[WindowRecord[B, C]])(implicit manifestB: Manifest[B], manifestC: Manifest[C]): Rep[B] = WindowRecord_Field_Key[B, C](self)
  type WindowRecord[B, C] = ch.epfl.data.legobase.queryengine.WindowRecord[B, C]
}
trait WindowRecordImplicits { this: WindowRecordComponent =>
  // Add implicit conversions here!
}
trait WindowRecordComponent extends WindowRecordOps with WindowRecordImplicits { self: DeepDSL => }

trait DeepDSL extends OperatorsComponent with AGGRecordComponent with WindowRecordComponent with CharacterComponent
  with DoubleComponent with IntComponent with LongComponent with ArrayComponent
  with LINEITEMRecordComponent
  with SUPPLIERRecordComponent
  with PARTSUPPRecordComponent
  with REGIONRecordComponent
  with NATIONRecordComponent
  with PARTRecordComponent
  with K2DBScannerComponent with IntegerComponent
  with BooleanComponent with HashMapComponent with SetComponent with TreeSetComponent
  with DefaultEntryComponent with ArrayBufferComponent with ManualLiftedLegoBase

