package ch.epfl.data
package legobase
package deep

import scala.language.implicitConversions
import pardis.ir._
import reflect.runtime.universe.{ TypeTag, Type }

trait IRToProgram extends TopDownTransformer[LoweringLegoBase, LoweringLegoBase] with Traverser[LoweringLegoBase] {
  import from._

  val from = IR
  val to = IR

  def createProgram(node: Block[_]): PardisProgram = {
    traverseBlock(node)
    val structsDef = structs.toList.map(x => PardisStructDef(x._1, x._2))
    PardisProgram(structsDef, node)
  }

  val structs = collection.mutable.HashMap.empty[StructTags.StructTag[_], Seq[(String, Type, Boolean)]]

  override def traverseDef(node: Def[_]): Unit = node match {
    case Struct(tag, elems) => {
      def getTypeTag(m: Manifest[Any]): TypeTag[Any] = reflect.runtime.universe.internal.manifestToTypeTag[Any](scala.reflect.runtime.currentMirror, m).asInstanceOf[TypeTag[Any]]
      def getType(m: Manifest[Any]): Type = getTypeTag(m).tpe
      structs += tag -> elems.map(x =>
        (x._1, getType(x._2.tp), isVar(x._2)))
    }
    case _ => super.traverseDef(node)
  }

  def isVar[T](exp: Rep[T]) = {
    exp match {
      case s @ Sym(_) => s.correspondingNode match {
        case ReadVar(Var(_)) => true
        case _               => false
      }
      case _ => false
    }
    // exp.correspondingNode
    // exp match {
    //   case null => false
    //   case to.Def(d) => d match {
    //     case null => {
    //       scala.Predef.println("null for " + exp)
    //       false
    //     }
    //     case ReadVar(Var(_)) => true
    //   }
    //   // case Def(ReadVar(Var(_))) => true
    //   case _ => false
    // }
  }
}
