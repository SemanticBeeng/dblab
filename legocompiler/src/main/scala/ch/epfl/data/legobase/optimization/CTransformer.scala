package ch.epfl.data
package legobase
package optimization

import scala.language.existentials
import pardis.shallow.OptimalString
import pardis.ir._
import pardis.types._
import pardis.types.PardisTypeImplicits._
import pardis.deep.scalalib._
import pardis.deep.scalalib.collection._
import legobase.deep._
import pardis.optimization._
import scala.language.implicitConversions
import pardis.utils.Utils._
import scala.reflect.runtime.universe
import pardis.ir.StructTags._
import cscala.CLangTypesDeep._
import cscala.GLibTypes._

trait CTransformer extends TopDownTransformerTraverser[LoweringLegoBase] {
  val IR: LoweringLegoBase
  import IR._
  import CNodes._

  implicit class PointerTypeOps[T](tp: TypeRep[T]) {
    def isPointerType: Boolean = tp match {
      case x: CTypes.PointerType[_] => true
      case _                        => false
    }
  }

  override def transformExp[T: TypeRep, S: TypeRep](exp: Rep[T]): Rep[S] = exp match {
    case t: typeOf[_] => typeOf()(apply(t.tp)).asInstanceOf[Rep[S]]
    case _            => super.transformExp[T, S](exp)
  }
}

class CTransformersPipeline(val settings: compiler.Settings) extends TransformerHandler {
  def apply[Lang <: Base, T: PardisType](context: Lang)(block: context.Block[T]): context.Block[T] = {
    apply[T](context.asInstanceOf[LoweringLegoBase], block)
  }
  def apply[A: PardisType](context: LoweringLegoBase, b: PardisBlock[A]) = {
    val pipeline = new TransformerPipeline()
    pipeline += new GenericEngineToCTransformer(context)
    pipeline += new ScalaScannerToCmmapTransformer(context)
    // pipeline += new ScalaScannerToCFScanfTransformer(context)
    pipeline += new ScalaArrayToCStructTransformer(context)
    // pipeline += compiler.TreeDumper(false)
    pipeline += new cscala.deep.ManualGLibMultiMapTransformation(context)
    pipeline += new ScalaCollectionsToGLibTransfomer(context)
    pipeline += new Tuple2ToCTransformer(context)
    pipeline += new OptionToCTransformer(context)
    pipeline += new HashEqualsFuncsToCTraansformer(context)
    pipeline += new OptimalStringToCTransformer(context)
    pipeline += new RangeToCTransformer(context)
    pipeline += new ScalaConstructsToCTranformer(context, settings.ifAggressive)
    pipeline += new BlockFlattening(context)
    pipeline(context)(b)
  }
}

class GenericEngineToCTransformer(override val IR: LoweringLegoBase) extends RuleBasedTransformer[LoweringLegoBase](IR) {
  import IR._
  import CNodes._
  import CTypes._

  rewrite += rule {
    case GenericEngineRunQueryObject(b) =>
      val init = infix_asInstanceOf[TimeVal](unit(0))
      val diff = readVar(__newVar(init))
      val start = readVar(__newVar(init))
      val end = readVar(__newVar(init))
      gettimeofday(&(start))
      inlineBlock(apply(b))
      gettimeofday(&(end))
      val tm = timeval_subtract(&(diff), &(end), &(start))
      printf(unit("Generated code run in %ld milliseconds."), tm)
  }
  rewrite += rule { case GenericEngineParseStringObject(s) => s }
  rewrite += rule {
    case GenericEngineDateToStringObject(d) => NameAlias[String](None, "ltoa", List(List(d)))
  }
  rewrite += rule {
    case GenericEngineDateToYearObject(d) => d / unit(10000)
  }
  rewrite += rule {
    case GenericEngineParseDateObject(Constant(d)) =>
      val data = d.split("-").map(x => x.toInt)
      unit((data(0) * 10000) + (data(1) * 100) + data(2))
  }
}

// Mapping Scala Scanner operations to C FILE operations
class ScalaScannerToCFScanfTransformer(override val IR: LoweringLegoBase) extends RuleBasedTransformer[LoweringLegoBase](IR) {
  import IR._
  import CNodes._
  import CTypes._

  // TODO: Brainstorm about rewrite += tp abstraction for transforming types
  override def transformType[T: PardisType]: PardisType[Any] = ({
    val tp = typeRep[T]
    if (tp == typeK2DBScanner) typePointer(typeFILE)
    else super.transformType[T]
  }).asInstanceOf[PardisType[Any]]

  rewrite += rule {
    case K2DBScannerNew(f) => CStdIO.fopen(f.asInstanceOf[Rep[LPointer[Char]]], unit("r"))
  }
  rewrite += rule {
    case K2DBScannerNext_int(s) =>
      val v = readVar(__newVar[Int](0))
      __ifThenElse(fscanf(apply(s), unit("%d|"), &(v)) __== eof, break, unit(()))
      v
  }
  rewrite += rule {
    case K2DBScannerNext_double(s) =>
      val v = readVar(__newVar(unit(0.0)))
      __ifThenElse(fscanf(apply(s), unit("%lf|"), &(v)) __== eof, break, unit)
      v
  }
  rewrite += rule {
    case K2DBScannerNext_char(s) =>
      val v = readVar(__newVar(unit('a')))
      __ifThenElse(fscanf(apply(s), unit("%c|"), &(v)) __== eof, break, unit)
      v
  }
  rewrite += rule {
    case nn @ K2DBScannerNext1(s, buf) =>
      var i = __newVar[Int](0)
      __whileDo(unit(true), {
        val v = readVar(__newVar[Byte](unit('a')))
        __ifThenElse(fscanf(apply(s), unit("%c"), &(v)) __== eof, break, unit)
        // have we found the end of line or end of string?
        __ifThenElse((v __== unit('|')) || (v __== unit('\n')), break, unit)
        buf(i) = v
        __assign(i, readVar(i) + unit(1))
      })
      buf(i) = unit('\u0000')
      readVar(i)
  }
  rewrite += rule {
    case K2DBScannerNext_date(s) =>
      val x = readVar(__newVar[Int](unit(0)))
      val y = readVar(__newVar[Int](unit(0)))
      val z = readVar(__newVar[Int](unit(0)))
      __ifThenElse(fscanf(apply(s), unit("%d-%d-%d|"), &(x), &(y), &(z)) __== eof, break, unit)
      (x * unit(10000)) + (y * unit(100)) + z
  }
  rewrite += rule { case K2DBScannerHasNext(s) => unit(true) }
  rewrite += rule {
    case LoaderFileLineCountObject(Constant(x: String)) =>
      val p = CStdIO.popen(unit("wc -l " + x), unit("r"))
      val cnt = readVar(__newVar[Int](0))
      CStdIO.fscanf(p, unit("%d"), CLang.&(cnt))
      CStdIO.pclose(p)
      cnt
  }
}

// Mapping Scala Scanner operations to C mmap operations
class ScalaScannerToCmmapTransformer(override val IR: LoweringLegoBase) extends RuleBasedTransformer[LoweringLegoBase](IR) {
  import IR._
  import CNodes._
  import CTypes._

  implicit def toArrayChar(s: Expression[K2DBScanner]): Expression[Pointer[Char]] =
    s.asInstanceOf[Expression[Pointer[Char]]]

  override def transformType[T: PardisType]: PardisType[Any] = ({
    val tp = typeRep[T]
    if (tp == typeK2DBScanner) typePointer(typeChar)
    else super.transformType[T]
  }).asInstanceOf[PardisType[Any]]

  /* Helper functions */
  def readInt(s: Expression[Pointer[Char]]): Expression[Int] = {
    val num = readVar(__newVar[Int](0))
    pointer_assign(s,
      toAtom(NameAlias[Pointer[Char]](None, "strntoi_unchecked", List(List(s, &(num))))))
    num
  }

  // rewritings
  rewrite += rule {
    case K2DBScannerNew(f) => {
      val fd: Expression[Int] = open(f, O_RDONLY)
      val st = &(__newVar[StructStat](infix_asInstanceOf(unit(0))(typeStat)))
      stat(f, st)
      val size = field(st, "st_size")(typeSize_T)
      NameAlias[Pointer[Char]](None, "mmap", List(List(Constant(null), size, PROT_READ, MAP_PRIVATE, fd, Constant(0))))
    }
  }

  rewrite += rule { case K2DBScannerNext_int(s) => readInt(s) }

  rewrite += rule {
    case K2DBScannerNext_double(s) =>
      val num = readVar(__newVar[Double](unit(0.0)))
      pointer_assign(s,
        toAtom(NameAlias[Pointer[Char]](None, "strntod_unchecked", List(List(s, &(num))))))
      num
  }

  rewrite += rule {
    case K2DBScannerNext_char(s) =>
      val tmp = __newVar[Char](*(s));
      pointer_increase(s) // move to next char, which is the delimiter
      pointer_increase(s) // skip '|'
      readVar(tmp)
  }

  rewrite += rule {
    case K2DBScannerNext_date(s) => {
      val year = readInt(s)
      val month = readInt(s)
      val day = readInt(s)
      (year * 10000) + (month * 100) + day
    }
  }

  rewrite += rule {
    case nn @ K2DBScannerNext1(s, buf) =>
      val array = field(buf, "array")(typePointer(typeChar))
      val begin = __newVar[Pointer[Char]](s)
      __whileDo((*(s) __!= unit('|')) && (*(s) __!= unit('\n')), {
        pointer_increase(s)
      })
      val size = pointer_minus(s, readVar(begin))
      strncpy(array, readVar(begin), size)
      pointer_assign_content(array, size, unit('\u0000'))
      pointer_increase(s) // skip '|'
      size
  }

  rewrite += rule {
    case K2DBScannerHasNext(s) => infix_!=(*(s), unit('\u0000'))

  }
  rewrite += rule {
    case LoaderFileLineCountObject(Constant(x: String)) =>
      val p = popen(unit("wc -l " + x), unit("r"))
      val cnt = readVar(__newVar[Int](0))
      fscanf(p, unit("%d"), &(cnt))
      pclose(p)
      cnt
  }
}

class ScalaArrayToCStructTransformer(override val IR: LoweringLegoBase) extends RuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._

  override def transformType[T: PardisType]: PardisType[Any] = ({
    val tp = typeRep[T]
    tp match {
      case c if c.isPrimitive    => super.transformType[T]
      case ArrayBufferType(args) => typePointer(typeGArray)
      // case ArrayType(x) if x == ByteType => typePointer(ByteType)
      case ArrayType(args) => typePointer(typeCArray({
        if (args.isArray) typeCArray(args)
        else args
      }))
      case c if c.isRecord => tp.typeArguments match {
        case Nil     => typePointer(tp)
        case List(t) => typePointer(transformType(t))
      }
      case TreeSetType(args) => typePointer(typeGTree)
      case SetType(args)     => typePointer(typeLPointer(typeGList))
      case OptionType(args)  => typePointer(transformType(args))
      case _                 => super.transformType[T]
    }
  }).asInstanceOf[PardisType[Any]]

  rewrite += rule {
    case pc @ PardisCast(x) => PardisCast(apply(x))(apply(pc.castFrom), apply(pc.castTp))
  }

  rewrite += rule {
    case a @ ArrayNew(x) =>
      if (a.tp.typeArguments(0).isArray) {
        // Get type of elements stored in array
        val elemType = typeCArray(a.tp.typeArguments(0).typeArguments(0))
        // Allocate original array
        val array = malloc(x)(elemType)
        // Create wrapper with length
        val am = typeCArray(typeCArray(a.tp.typeArguments(0).typeArguments(0))).asInstanceOf[PardisType[CArray[CArray[Any]]]] //transformType(a.tp)
        val tagName = structName(am)
        if (tagName == "ArrayOfArrayOfChar") {
          System.out.println(s"HERE!!!! for $am")
          System.out.println(s"HERE2!!!! for ${am.typeArguments(0).typeArguments(0).getClass}=${structName(am.typeArguments(0).typeArguments(0))}!=${structName(CharType)}")
        }
        val s = toAtom(
          PardisStruct(StructTags.ClassTag(tagName),
            List(PardisStructArg("array", false, array), PardisStructArg("length", false, x)),
            List())(am))(am)
        val m = malloc(unit(1))(am)
        structCopy(m.asInstanceOf[Expression[Pointer[Any]]], s)
        m.asInstanceOf[Expression[Any]]
      } else {
        // Get type of elements stored in array
        val elemType = if (a.tp.typeArguments(0).isRecord) a.tp.typeArguments(0) else transformType(a.tp.typeArguments(0))
        // Allocate original array
        val array = malloc(x)(elemType)
        // Create wrapper with length
        val am = transformType(a.tp)
        val s = toAtom(
          PardisStruct(StructTags.ClassTag(structName(am)),
            List(PardisStructArg("array", false, array), PardisStructArg("length", false, x)),
            List())(am))(am)
        val m = malloc(unit(1))(am.typeArguments(0))
        structCopy(m.asInstanceOf[Expression[Pointer[Any]]], s)
        m.asInstanceOf[Expression[Any]]
      }
  }
  rewrite += rule {
    case au @ ArrayUpdate(a, i, v) =>
      val s = apply(a)
      // Get type of elements stored in array
      val elemType = a.tp.typeArguments(0)
      // Get type of internal array
      val newTp = ({
        if (elemType.isArray) typePointer(typeCArray(elemType.typeArguments(0)))
        else typeArray(typePointer(elemType))
      }).asInstanceOf[PardisType[Any]] //if (elemType.isPrimitive) elemType else typePointer(elemType)
      // Read array and perform update
      val arr = field(s, "array")(newTp)
      if (elemType.isPrimitive) arrayUpdate(arr.asInstanceOf[Expression[Array[Any]]], i, apply(v))
      else if (elemType.name == "OptimalString")
        pointer_assign_content(arr.asInstanceOf[Expression[Pointer[Any]]], i, apply(v))
      else if (v match {
        case Def(Cast(Constant(null))) => true
        case Constant(null)            => true
        case _                         => false
      }) {
        class T
        // implicit val typeT = apply(v).tp.typeArguments(0).asInstanceOf[TypeRep[T]]
        implicit val typeT = newTp.typeArguments(0).typeArguments(0).asInstanceOf[TypeRep[T]]
        val tArr = arr.asInstanceOf[Expression[Pointer[T]]]
        pointer_assign_content(tArr, i, unit(null))
      } // else if (elemType.isRecord) {
      //   class T
      //   implicit val typeT = apply(v).tp.typeArguments(0).asInstanceOf[TypeRep[T]]
      //   val newV = apply(v).asInstanceOf[Expression[Pointer[T]]]
      //   val vContent = *(newV)
      //   val tArr = arr.asInstanceOf[Expression[Pointer[T]]]
      //   pointer_assign(tArr, i, vContent)
      //   val pointerVar = newV match {
      //     case Def(ReadVar(v)) => v.asInstanceOf[Var[Pointer[T]]]
      //     case x               => Var(x.asInstanceOf[Rep[Var[Pointer[T]]]])
      //   }
      //   __assign(pointerVar, (&(tArr, i)).asInstanceOf[Rep[Pointer[T]]])(PointerType(typeT))
      else if (elemType.isRecord) {
        class T
        implicit val typeT = apply(v).tp.typeArguments(0).asInstanceOf[TypeRep[T]]
        val newV = apply(v).asInstanceOf[Expression[Pointer[T]]]
        __ifThenElse(apply(v) __== unit(null), {
          class T1
          implicit val typeT1 = newTp.typeArguments(0).typeArguments(0).asInstanceOf[TypeRep[T1]]
          val tArr = arr.asInstanceOf[Expression[Pointer[T1]]]
          pointer_assign_content(tArr, i, unit(null))
        }, {
          val vContent = *(newV)
          val tArr = arr.asInstanceOf[Expression[Pointer[T]]]
          pointer_assign_content(tArr, i, vContent)
          val pointerVar = newV match {
            case Def(ReadVar(v)) => v.asInstanceOf[Var[Pointer[T]]]
            case x               => Var(x.asInstanceOf[Rep[Var[Pointer[T]]]])
          }
          __assign(pointerVar, (&(tArr, i)).asInstanceOf[Rep[Pointer[T]]])(PointerType(typeT))
        })
      } else if (elemType.isInstanceOf[SetType[_]]) {
        pointer_assign_content(arr.asInstanceOf[Expression[Pointer[Any]]], i, apply(v).asInstanceOf[Expression[Pointer[Any]]])
      } else
        pointer_assign_content(arr.asInstanceOf[Expression[Pointer[Any]]], i, *(apply(v).asInstanceOf[Expression[Pointer[Any]]])(v.tp.name match {
          case x if v.tp.isArray            => transformType(v.tp).typeArguments(0).asInstanceOf[PardisType[Any]]
          case x if x.startsWith("Pointer") => v.tp.typeArguments(0).asInstanceOf[PardisType[Any]]
          case _                            => v.tp
        }))
  }
  rewrite += rule {
    case ArrayFilter(a, op) => field(apply(a), "array")(transformType(a.tp))
  }
  rewrite += rule {
    case ArrayApply(a, i) =>
      val s = apply(a)
      // Get type of elements stored in array
      val elemType = a.tp.typeArguments(0)
      // Get type of internal array
      val newTp = ({
        if (elemType.isArray) typePointer(typeCArray(elemType.typeArguments(0)))
        else typeArray(typePointer(elemType))
      }).asInstanceOf[PardisType[Any]]
      val arr = field(s, "array")(newTp)
      // <<<<<<< HEAD
      // if (elemType.isRecord) {
      // =======
      if (elemType.isPrimitive || elemType == OptimalStringType || elemType.isInstanceOf[SetType[_]]) ArrayApply(arr.asInstanceOf[Expression[Array[Any]]], apply(i))(newTp.asInstanceOf[PardisType[Any]])
      else {
        // >>>>>>> merged-ir-str
        i match {
          case Constant(0) => Cast(arr)(arr.tp, typePointer(newTp))
          case _           => PTRADDRESS(arr.asInstanceOf[Expression[Pointer[Any]]], apply(i))(typePointer(newTp).asInstanceOf[PardisType[Pointer[Any]]])
        }
      }
    // } else ArrayApply(arr.asInstanceOf[Expression[Array[Any]]], apply(i))(newTp.asInstanceOf[PardisType[Any]])
    // class T
    // implicit val typeT = a.tp.typeArguments(0).asInstanceOf[PardisType[T]]
    // val newTp = ({
    //   if (elemType.isArray) typePointer(typeCArray(elemType.typeArguments(0)))
    //   else typeArray(typePointer(elemType))
    // }).asInstanceOf[PardisType[Any]]
    // val arr = field[Array[T]](s, "array")(newTp.asInstanceOf[PardisType[Array[T]]])
    // if (elemType.isPrimitive) arrayApply(arr, i)(newTp.asInstanceOf[PardisType[T]])
    // else &(arr, i)(newTp.typeArguments(0).typeArguments(0).asInstanceOf[PardisType[Array[T]]])
  }

  def __arrayLength[T: TypeRep](arr: Rep[Array[T]]): Rep[Int] = {
    val s = apply(arr)
    field[Int](s, "length")
  }

  rewrite += rule {
    case ArrayLength(a) =>
      __arrayLength(a)
  }
  rewrite += rule {
    case ArrayForeach(a, f) =>
      // val length = toAtom(apply(ArrayLength(a)))(IntType)
      val length = __arrayLength(a)
      // System.out.println(s"symbol for length $length")
      // TODO if we use recursive rule based, the next line will be cleaner
      Range(unit(0), length).foreach {
        __lambda { index =>
          System.out.println(s"index: $index, f: ${f.correspondingNode}")
          val elemNode = apply(ArrayApply(a, index))
          val elem = toAtom(elemNode)(elemNode.tp.typeArguments(0).typeArguments(0).asInstanceOf[TypeRep[Any]])
          inlineFunction(f, elem)
        }
      }
  }
  rewrite += rule {
    case s @ PardisStruct(tag, elems, methods) =>
      // TODO if needed method generation should be added
      val x = toAtom(Malloc(unit(1))(s.tp))(typePointer(s.tp))
      val newElems = elems.map(el => PardisStructArg(el.name, el.mutable, transformExp(el.init)(el.init.tp, apply(el.init.tp))))
      structCopy(x, PardisStruct(tag, newElems, methods.map(m => m.copy(body =
        transformDef(m.body.asInstanceOf[Def[Any]]).asInstanceOf[PardisLambdaDef])))(s.tp))
      x
  }
}

class ScalaCollectionsToGLibTransfomer(override val IR: LoweringLegoBase) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._
  import GHashTableHeader._
  import GListHeader._
  import GTreeHeader._
  import GArrayHeader._

  override def transformType[T: PardisType]: PardisType[Any] = ({
    val tp = typeRep[T]
    tp match {
      case ArrayBufferType(t)  => typePointer(typeGArray)
      case SeqType(t)          => typePointer(typeLPointer(typeGList))
      case TreeSetType(t)      => typePointer(typeGTree)
      case SetType(t)          => typePointer(typeLPointer(typeGList))
      case OptionType(t)       => typePointer(transformType(t))
      case HashMapType(t1, t2) => typePointer(typeGHashTable)
      case CArrayType(t1)      => tp
      case _                   => super.transformType[T]
    }
  }).asInstanceOf[PardisType[Any]]

  def allocDoubleKey(key: Rep[_]): Rep[LPointer[Double]] = {
    val ptr = CStdLib.malloc[Double](unit(8))
    CLang.pointer_assign(ptr, key.asInstanceOf[Rep[Double]])
    ptr
  }

  /* HashMap Operations */
  rewrite += rule {
    case nm @ HashMapNew3(_, _) =>
      apply(HashMapNew()(nm.typeA, ArrayBufferType(nm.typeB)))
    case nm @ HashMapNew4(_, _) =>
      apply(HashMapNew()(nm.typeA, nm.typeB))
  }
  rewrite += rule {
    case nm @ HashMapNew() =>
      if (nm.typeA == DoubleType || nm.typeA == PointerType(DoubleType)) {
        def hashFunc = toAtom(transformDef(doLambdaDef((s: Rep[gconstpointer]) => g_double_hash(s)))).asInstanceOf[Rep[GHashFunc]]
        def equalsFunc = toAtom(transformDef(doLambda2Def((s1: Rep[gconstpointer], s2: Rep[gconstpointer]) => g_double_equal(s1, s2)))).asInstanceOf[Rep[GEqualFunc]]
        g_hash_table_new(hashFunc, equalsFunc)
      } else if (nm.typeA.isPrimitive || nm.typeA == StringType || nm.typeA == OptimalStringType) {
        val nA = nm.typeA
        val nB = nm.typeB
        def hashFunc[T: TypeRep] = toAtom(transformDef(doLambdaDef((s: Rep[T]) => infix_hashCode(s)))).asInstanceOf[Rep[GHashFunc]]
        def equalsFunc[T: TypeRep] = toAtom(transformDef(doLambda2Def((s1: Rep[T], s2: Rep[T]) => infix_==(s1, s2)))).asInstanceOf[Rep[GEqualFunc]]
        g_hash_table_new(hashFunc(nA), equalsFunc(nA))
      } else {
        val nA = typePointer(transformType(nm.typeA))
        val nB = typePointer(transformType(nm.typeB))
        def hashFunc[T: TypeRep] = toAtom(transformDef(doLambdaDef((s: Rep[T]) => infix_hashCode(s)))).asInstanceOf[Rep[GHashFunc]]
        def equalsFunc[T: TypeRep] = toAtom(transformDef(doLambda2Def((s1: Rep[T], s2: Rep[T]) => infix_==(s1, s2)))).asInstanceOf[Rep[GEqualFunc]]
        g_hash_table_new(hashFunc(nA), equalsFunc(nA))
      }
  }
  rewrite += rule {
    case HashMapSize(map) => g_hash_table_size(map.asInstanceOf[Rep[LPointer[GHashTable]]])
  }
  rewrite += rule {
    case hmks @ HashMapKeySet(map) =>
      &(g_hash_table_get_keys(map.asInstanceOf[Rep[LPointer[GHashTable]]]))
  }
  rewrite += rule {
    case hmc @ HashMapContains(map, key) =>
      val z = toAtom(transformDef(HashMapApply(map, key)))(typePointer(transformType(hmc.typeB)).asInstanceOf[TypeRep[Any]])
      infix_!=(z, unit(null))
  }
  rewrite += rule {
    case ma @ HashMapApply(map, k) =>
      val key = if (ma.typeA == DoubleType || ma.typeA == PointerType(DoubleType)) CLang.&(apply(k)) else apply(k)
      g_hash_table_lookup(map.asInstanceOf[Rep[LPointer[GHashTable]]], key.asInstanceOf[Rep[gconstpointer]])
  }
  rewrite += rule {
    case mu @ HashMapUpdate(map, k, value) =>
      val key = if (mu.typeA == DoubleType || mu.typeA == PointerType(DoubleType)) allocDoubleKey(apply(k)) else apply(k)
      g_hash_table_insert(map.asInstanceOf[Rep[LPointer[GHashTable]]],
        key.asInstanceOf[Rep[gconstpointer]],
        value.asInstanceOf[Rep[gpointer]])
  }
  rewrite += rule {
    case hmgu @ HashMapGetOrElseUpdate(map, key, value) =>
      val ktp = typePointer(transformType(hmgu.typeA)).asInstanceOf[TypeRep[Any]]
      val vtp = typePointer(transformType(hmgu.typeB)).asInstanceOf[TypeRep[Any]]
      val v = toAtom(transformDef(HashMapApply(map, key)(ktp, vtp))(vtp))(vtp)
      __ifThenElse(infix_==(v, unit(null)), {
        val res = inlineBlock(apply(value))
        toAtom(HashMapUpdate(map, key, res)(ktp, vtp))
        res
      }, v)(v.tp)
  }
  rewrite += rule {
    case mr @ HashMapRemove(map, key) =>
      val x = toAtom(transformDef(HashMapApply(map, key)))(typePointer(transformType(mr.typeB)).asInstanceOf[TypeRep[Any]])
      val keyptr = if (mr.typeA == DoubleType || mr.typeA == PointerType(DoubleType)) allocDoubleKey(key) else key
      g_hash_table_remove(map.asInstanceOf[Rep[LPointer[GHashTable]]], keyptr.asInstanceOf[Rep[gconstpointer]])
      x
  }
  rewrite += rule {
    case hmfe @ HashMapForeach(map, f) =>
      val ktp = typePointer(transformType(hmfe.typeA))
      //val vtp = typePointer(transformType(hmfe.typeB))
      val vtp = f.tp.typeArguments(0).typeArguments(1).asInstanceOf[TypeRep[Any]]
      val keys = __newVar(g_hash_table_get_keys(map.asInstanceOf[Rep[LPointer[GHashTable]]]))
      val nKeys = g_hash_table_size(map.asInstanceOf[Rep[LPointer[GHashTable]]])
      Range(unit(0), nKeys).foreach {
        __lambda { i =>
          val keysRest = __readVar(keys)
          val key = g_list_nth_data(keysRest, unit(0))
          __assign(keys, g_list_next(keysRest))
          val value = g_hash_table_lookup(map.asInstanceOf[Rep[LPointer[GHashTable]]], key)
          inlineFunction(f, __newTuple2(infix_asInstanceOf(key)(ktp), infix_asInstanceOf(value)(vtp)))
        }
      }
  }

  /* Set Operations */
  rewrite += rule { case SetApplyObject1(s) => s }
  rewrite += rule {
    case nm @ SetApplyObject2() =>
      val s = CStdLib.malloc[LPointer[GList]](unit(8))
      CLang.pointer_assign(s, CLang.NULL[GList])
      s
  }
  rewrite += rule {
    case sh @ SetHead(s) =>
      val elemType = if (sh.typeA.isRecord) typeLPointer(sh.typeA) else sh.typeA
      val glist = CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]])
      infix_asInstanceOf(g_list_nth_data(glist, unit(0)))(elemType)
  }
  rewrite += rule {
    case SetRemove(s, e) =>
      val glist = CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]])
      val newHead = g_list_remove(glist, apply(e).asInstanceOf[Rep[LPointer[Any]]])
      CLang.pointer_assign(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]], newHead)
  }
  rewrite += rule {
    case SetToSeq(set) =>
      set
  }
  rewrite += rule {
    case Set$plus$eq(s, e) =>
      val glist = CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]])
      val newHead = g_list_prepend(glist, apply(e).asInstanceOf[Rep[LPointer[Any]]])
      CLang.pointer_assign(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]], newHead)
  }
  rewrite += rule {
    case sfe @ SetForeach(s, f) =>
      val elemType = if (sfe.typeA.isRecord) typeLPointer(sfe.typeA) else sfe.typeA
      val l = __newVar(CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]]))
      __whileDo(__readVar(l) __!= CLang.NULL[GList], {
        val elem = infix_asInstanceOf(g_list_nth_data(readVar(l), unit(0)))(elemType)
        __assign(l, g_list_next(readVar(l)))
        inlineFunction(f, elem)
        unit(())
      })
  }
  rewrite += rule {
    case sf @ SetFind(s, f) =>
      val elemType = if (sf.typeA.isRecord) typeLPointer(sf.typeA) else sf.typeA
      val result = __newVar(unit(null).asInstanceOf[Rep[Any]])(elemType.asInstanceOf[TypeRep[Any]])
      val l = __newVar(CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]]))
      __whileDo(__readVar(l) __!= CLang.NULL[GList], {
        val elem = infix_asInstanceOf(g_list_nth_data(readVar(l), unit(0)))(elemType)
        __assign(l, g_list_next(readVar(l)))
        val found = inlineFunction(f, elem)
        __ifThenElse(found, {
          __assign(result, elem)
          break()
        }, unit(()))
      })
      optionApplyObject(readVar(result)(elemType.asInstanceOf[TypeRep[Any]]))
  }
  rewrite += rule {
    case se @ SetExists(s, f) =>
      class X
      class Y
      implicit val typeX = se.typeA.asInstanceOf[TypeRep[X]]
      val set = s.asInstanceOf[Rep[Set[X]]]
      val fun = f.asInstanceOf[Rep[((X) => Boolean)]]
      val found = set.find(fun)
      found.nonEmpty
  }
  rewrite += rule {
    case sfl @ SetFoldLeft(s, z, f) =>
      val elemType = if (sfl.typeA.isRecord) typeLPointer(sfl.typeA) else sfl.typeA
      val state = __newVar(z)(sfl.typeB)
      val l = __newVar(CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]]))
      __whileDo(__readVar(l) __!= CLang.NULL[GList], {
        val elem = infix_asInstanceOf(g_list_nth_data(readVar(l), unit(0)))(elemType)
        val newState = inlineFunction(f, __readVar(state)(sfl.typeB), elem)
        __assign(state, newState)(sfl.typeB)
        __assign(l, g_list_next(readVar(l)))
      })
      __readVar(state)(sfl.typeB)
  }
  rewrite += rule {
    case SetSize(s) =>
      val l = CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]])
      g_list_length(l)
  }
  rewrite += rule {
    case sr @ SetRetain(s, f) =>
      val elemType = if (sr.typeA.isRecord) typeLPointer(sr.typeA) else sr.typeA
      val l = __newVar(CLang.*(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]]))
      val prevPtr = __newVar(s.asInstanceOf[Rep[LPointer[LPointer[GList]]]])

      __whileDo(__readVar(l) __!= CLang.NULL[GList], {
        val elem = infix_asInstanceOf(g_list_nth_data(readVar(l), unit(0)))(elemType)
        val keep = inlineFunction(f, elem)
        __ifThenElse(keep, {
          //__assign(prevPtr, CLang.&(CLang.->[GList, LPointer[GList]](__readVar(l), unit("next"))))
          __assign(prevPtr, CLang.&(field[LPointer[GList]](__readVar[LPointer[GList]](l), "next")))
        }, {
          CLang.pointer_assign(readVar(prevPtr), g_list_next(readVar(l)))
        })
        __assign(l, g_list_next(readVar(l)))
      })
  }

  rewrite += rule {
    case smb @ SetMinBy(s, f) =>
      class X
      class Y
      implicit val typeX = (if (smb.typeA.isRecord) typeLPointer(smb.typeA) else smb.typeA).asInstanceOf[TypeRep[X]]
      implicit val typeY = smb.typeB.asInstanceOf[TypeRep[Y]]
      val set = s.asInstanceOf[Rep[Set[X]]]
      val fun = f.asInstanceOf[Rep[((X) => Y)]]

      val l = __newVar(CLang.*(set.asInstanceOf[Rep[LPointer[LPointer[GList]]]]))
      val first = infix_asInstanceOf[X](g_list_nth_data(__readVar(l), unit(0)))
      val result = __newVar(first)
      val min = __newVar(inlineFunction(fun, __readVar(result)))

      val cmp = OrderingRep(smb.typeB)

      __whileDo(__readVar(l) __!= CLang.NULL[GList], {
        val elem = infix_asInstanceOf[X](g_list_nth_data(__readVar(l), unit(0)))
        __assign(l, g_list_next(readVar(l)))
        val newMin = inlineFunction(fun, elem)
        __ifThenElse(cmp.lt(newMin, __readVar[Y](min)), {
          __assign(min, newMin)
          __assign(result, elem)
        }, unit(()))
      })
      __readVar(result)
  }

  /* TreeSet Operations */
  rewrite += remove { case OrderingNew(o) => () }

  rewrite += rule {
    case ts @ TreeSetNew2(Def(OrderingNew(Def(Lambda2(f, i1, i2, o))))) =>
      val compare = Lambda2(f, i2.asInstanceOf[Rep[LPointer[Any]]], i1.asInstanceOf[Rep[LPointer[Any]]], transformBlock(o))
      g_tree_new(CLang.&(compare))
  }
  rewrite += rule {
    case TreeSet$plus$eq(t, s) => g_tree_insert(t.asInstanceOf[Rep[LPointer[GTree]]], s.asInstanceOf[Rep[gpointer]], s.asInstanceOf[Rep[gpointer]])
  }
  rewrite += rule {
    case TreeSet$minus$eq(self, t) =>
      g_tree_remove(self.asInstanceOf[Rep[LPointer[GTree]]], t.asInstanceOf[Rep[gconstpointer]])
  }
  rewrite += rule {
    case TreeSetSize(t) => g_tree_nnodes(t.asInstanceOf[Rep[LPointer[GTree]]])
  }
  rewrite += rule {
    case op @ TreeSetHead(t) =>
      // def treeHead[A: PardisType, B: PardisType] = doLambda3((s1: Rep[A], s2: Rep[A], s3: Rep[Pointer[B]]) => {
      //   pointer_assign_content(s3.asInstanceOf[Expression[Pointer[Any]]], s2)
      def treeHead[T: TypeRep] = doLambda3((s1: Rep[gpointer], s2: Rep[gpointer], s3: Rep[gpointer]) => {
        CLang.pointer_assign(infix_asInstanceOf[LPointer[T]](s3), infix_asInstanceOf[T](s2))
        unit(0)
      })
      class X
      implicit val elemType = transformType(if (op.typeA.isRecord) typeLPointer(op.typeA) else op.typeA).asInstanceOf[TypeRep[X]]
      val init = CLang.NULL[Any]
      g_tree_foreach(t.asInstanceOf[Rep[LPointer[GTree]]], (treeHead(elemType)).asInstanceOf[Rep[LPointer[(gpointer, gpointer, gpointer) => Int]]], CLang.&(init).asInstanceOf[Rep[gpointer]])
      init.asInstanceOf[Rep[LPointer[Any]]]
      infix_asInstanceOf[X](init)
  }

  /* ArrayBuffer Operations */
  rewrite += rule {
    case abn @ (ArrayBufferNew2() | ArrayBufferNew3()) =>
      class X
      implicit val tpX = abn.tp.typeArguments(0).asInstanceOf[TypeRep[X]]
      g_array_new(0, 1, sizeof[X])
  }
  rewrite += rule {
    case aba @ ArrayBufferApply(a, i) =>
      class X
      implicit val tp = (if (aba.tp.isPrimitive || aba.tp.isPointerType || aba.tp == OptimalStringType) aba.tp else typePointer(aba.tp)).asInstanceOf[TypeRep[X]]
      System.out.println(s"tp X: $tp")
      g_array_index[X](a.asInstanceOf[Rep[LPointer[GArray]]], i)
  }
  rewrite += rule {
    case ArrayBufferAppend(a, e) =>
      g_array_append_vals(apply(a).asInstanceOf[Rep[LPointer[GArray]]], CLang.&(e.asInstanceOf[Rep[gconstpointer]]), 1)
  }
  rewrite += rule {
    case ArrayBufferSize(a) =>
      CLang.->[GArray, Int](a.asInstanceOf[Rep[LPointer[GArray]]], unit("len"))
  }
  rewrite += rule {
    case ArrayBufferRemove(a, e) =>
      g_array_remove_index(a.asInstanceOf[Rep[LPointer[GArray]]], e)
  }
  rewrite += rule {
    case ArrayBufferMinBy(a, f @ Def(Lambda(fun, input, o))) =>
      val len = transformDef(ArrayBufferSize(a))
      val i = __newVar[Int](1)
      val tp = a.tp.typeArguments(0).typeArguments(0).asInstanceOf[PardisType[Any]]
      val varInit = toAtom(transformDef(ArrayBufferApply(apply(a), unit(0))(tp))(tp))(tp)
      val min = __newVar(varInit)(tp)
      val minBy = __newVar(fun(readVar(min)(tp)).asInstanceOf[Expression[Int]])
      __whileDo(readVar(i) < len, {
        val e = toAtom(transformDef(ArrayBufferApply(apply(a), readVar(i))(tp))(tp))(tp)
        val eminBy = fun(e).asInstanceOf[Expression[Int]]
        __ifThenElse(eminBy < readVar(minBy), {
          __assign(min, e)
          __assign(minBy, eminBy)
        }, unit())
        __assign(i, readVar(i) + unit(1))
      })
      ReadVar(min)(tp)
  }
  rewrite += rule {
    case ArrayBufferFoldLeft(a, cnt, Def(Lambda2(fun, input1, input2, o))) =>
      var idx = __newVar[Int](0)
      val len = transformDef(ArrayBufferSize(a))
      var agg = __newVar(cnt)(cnt.tp)
      __whileDo(readVar(idx) < len, {
        val tp = a.tp.typeArguments(0).typeArguments(0).asInstanceOf[PardisType[Any]]
        val e = toAtom(apply(ArrayBufferApply(apply(a), readVar(idx))(tp)))(tp)
        __assign(agg, fun(readVar(agg)(cnt.tp), e))
        __assign(idx, readVar(idx) + unit(1))
      })
      ReadVar(agg)
  }

  def arrayBufferIndexOf[T: PardisType](a: Expression[IR.ArrayBuffer[T]], elem: Expression[T]): Expression[Int] = {
    val idx = __newVar[Int](unit(-1))
    Range(unit(0), fieldGetter[Int](apply(a), "len")).foreach {
      __lambda { i =>
        val elemNode = apply(ArrayBufferApply(apply(a), i))
        val elem2 = toAtom(elemNode)(elemNode.tp)
        __ifThenElse(elem2 __== elem, {
          __assign(idx, i)
        }, unit())
      }
    }
    readVar(idx)
  }

  rewrite += rule {
    case ArrayBufferIndexOf(a, elem) =>
      // val tp = a.tp.typeArguments(0).typeArguments(0).asInstanceOf[PardisType[Any]]
      val tp = elem.tp.asInstanceOf[PardisType[Any]]
      arrayBufferIndexOf(a, elem)(tp)
  }
  rewrite += rule {
    case ArrayBufferContains(a, elem) => {
      // System.out.println(s"tp: ${a.tp}, ${elem.tp}")
      // val tp = a.tp.typeArguments(0).typeArguments(0).asInstanceOf[PardisType[Any]]
      val tp = elem.tp.asInstanceOf[PardisType[Any]]
      val idx = arrayBufferIndexOf(a, elem)(tp)
      idx __!= unit(-1)
    }
  }

  def __arrayBufferIndexWhere[T: PardisType](a: Rep[IR.ArrayBuffer[T]], pred: Rep[T => Boolean], lastIndex: Boolean): Rep[Int] = {
    // printf(unit(s"indexWhere started with $a and ${apply(a)}"))
    val idx = __newVarNamed[Int](unit(-1), "indexWhere")
    Range(unit(0), fieldGetter[Int](a, "len")).foreach {
      __lambda { i =>
        val elemNode = apply(ArrayBufferApply(a, i))
        val elem = toAtom(elemNode)(elemNode.tp).asInstanceOf[Rep[T]]
        __ifThenElse(if (lastIndex) inlineFunction[T, Boolean](pred, elem) else { inlineFunction[T, Boolean](pred, elem) && (readVar(idx) __== unit(-1)) }, {
          __assign(idx, i)
        }, unit())
      }
    }
    readVar(idx)
  }

  rewrite += rule {
    case ArrayBufferIndexWhere(a, pred) => {
      val tp = pred.tp.typeArguments(0).asInstanceOf[PardisType[Any]]
      __arrayBufferIndexWhere(a, pred, false)(tp)
    }
  }

  rewrite += rule {
    case ArrayBufferLastIndexWhere(a, pred) => {
      val tp = pred.tp.typeArguments(0).asInstanceOf[PardisType[Any]]
      __arrayBufferIndexWhere(a, pred, true)(tp)
    }
  }

  rewrite += rule {
    case ArrayBufferSortWith(a, sortFunc @ Def(Lambda2(f, i1, i2, o))) => {
      // val tp = a.tp.typeArguments(0).typeArguments(0).asInstanceOf[PardisType[Any]]
      class T
      implicit val tp = i1.tp.asInstanceOf[PardisType[T]]
      val (newI1, newI2) = (fresh(typePointer(tp)), fresh(typePointer(tp)))
      nameAlias[Unit](None, "g_array_sort", List(List(apply(a), Lambda2(f, newI1, newI2, reifyBlock { inlineFunction[T, T, Boolean](sortFunc, *(newI2), *(newI1)) }))))
      apply(a)
    }
  }
}

class HashEqualsFuncsToCTraansformer(override val IR: LoweringLegoBase) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer with StructCollector[LoweringLegoBase] {
  import IR._
  import CNodes._
  import CTypes._

  // Handling proper hascode and equals
  def __isRecord(e: Expression[Any]) = e.tp.isRecord || (e.tp.isPointerType && e.tp.typeArguments(0).isRecord)
  object Equals {
    def unapply(node: Def[Any]): Option[(Rep[Any], Rep[Any], Boolean)] = node match {
      case Equal(a, b)    => Some((a, b, true))
      case NotEqual(a, b) => Some((a, b, false))
      case _              => None
    }
  }

  val alreadyEquals = scala.collection.mutable.ArrayBuffer[Rep[Any]]()

  rewrite += rule {
    case Equals(e1, Constant(null), isEqual) if (e1.tp == OptimalStringType || e1.tp == StringType) && !alreadyEquals.contains(e1) =>
      alreadyEquals += e1
      if (isEqual) (e1 __== unit(null)) || !strcmp(e1, unit("")) else (e1 __!= unit(null)) && strcmp(e1, unit(""))
    case Equals(e1, e2, isEqual) if (e1.tp == OptimalStringType || e1.tp == StringType) && !alreadyEquals.contains(e1) =>
      if (isEqual) !strcmp(e1, e2) else strcmp(e1, e2)
    case Equals(e1, Constant(null), isEqual) if __isRecord(e1) && !alreadyEquals.contains(e1) =>
      val structDef = if (e1.tp.isRecord)
        getStructDef(e1.tp).get
      else {
        getStructDef(e1.tp.typeArguments(0)).get
      }
      // System.out.println(structDef.fields)
      alreadyEquals += e1
      structDef.fields.filter(_.name != "next").find(f => f.tpe.isPointerType || f.tpe == OptimalStringType || f.tpe == StringType) match {
        case Some(firstField) =>
          def fieldExp = field(e1, firstField.name)(firstField.tpe)
          if (isEqual)
            (e1 __== unit(null)) || (fieldExp __== unit(null))
          else
            (e1 __!= unit(null)) && (fieldExp __!= unit(null))
        case None => {
          if (isEqual)
            (e1 __== unit(null))
          else
            (e1 __!= unit(null))
        }
      }
    // case Equals(e1, e2, isEqual) if __isRecord(e1) && __isRecord(e2) =>
    //   class T
    //   implicit val ttp = (if (e1.tp.isRecord) e1.tp else e1.tp.typeArguments(0)).asInstanceOf[TypeRep[T]]
    //   val eq = getStructEqualsFunc[T]()
    //   val res = inlineFunction(eq, e1.asInstanceOf[Rep[T]], e2.asInstanceOf[Rep[T]])
    //   if (isEqual) res else !res
    case Equals(e1, e2, isEqual) if __isRecord(e1) && __isRecord(e2) =>
      val ttp = (if (e1.tp.isRecord) e1.tp else e1.tp.typeArguments(0))
      val structDef = getStructDef(ttp).get
      val res = structDef.fields.filter(_.name != "next").map(f => field(e1, f.name)(f.tpe) __== field(e2, f.name)(f.tpe)).reduce(_ && _)
      // val eq = getStructEqualsFunc[T]()
      // val res = inlineFunction(eq, e1.asInstanceOf[Rep[T]], e2.asInstanceOf[Rep[T]])
      if (isEqual) res else !res
  }
  rewrite += rule {
    case HashCode(t) if t.tp == StringType => unit(0) // KEY is constant. No need to hash anything
    // case HashCode(t) if __isRecord(t) =>
    //   val tp = t.tp.asInstanceOf[PardisType[Any]]
    //   val hashFunc = {
    //     if (t.tp.isRecord) getStructHashFunc[Any]()(tp)
    //     else getStructHashFunc[Any]()(tp.typeArguments(0).asInstanceOf[TypeRep[Any]])
    //   }
    //   val hf = toAtom(hashFunc)(hashFunc.tp)
    //   inlineFunction(hf.asInstanceOf[Rep[Any => Int]], apply(t))
    case HashCode(e) if __isRecord(e) =>
      val ttp = (if (e.tp.isRecord) e.tp else e.tp.typeArguments(0))
      val structDef = getStructDef(ttp).get
      structDef.fields.map(f => infix_hashCode(field(e, f.name)(f.tpe))).reduce(_ + _)
    case HashCode(t) if t.tp.isPrimitive      => infix_asInstanceOf[Int](t)
    case HashCode(t) if t.tp == CharacterType => infix_asInstanceOf[Int](t)
    case HashCode(t) if t.tp == OptimalStringType =>
      val len = t.asInstanceOf[Expression[OptimalString]].length
      val idx = __newVar[Int](0)
      val h = __newVar[Int](0)
      __whileDo(readVar(idx) < len, {
        __assign(h, readVar(h) + OptimalStringApply(t.asInstanceOf[Expression[OptimalString]], readVar(idx)))
        __assign(idx, readVar(idx) + unit(1));
      })
      readVar(h)
    case HashCode(t) if t.tp.isArray => field[Int](t.asInstanceOf[Rep[Array[Any]]], "length")
    //case HashCode(t)                 => throw new Exception("Unhandled type " + t.tp.toString + " passed to HashCode")
  }
  rewrite += rule {
    case ToString(obj) => obj.tp.asInstanceOf[PardisType[_]] match {
      case PointerType(tpe) if tpe.isRecord => {
        val structFields = {
          val structDef = getStructDef(tpe).get
          structDef.fields
        }
        def getDescriptor(field: StructElemInformation): String = field.tpe.asInstanceOf[PardisType[_]] match {
          case IntType | ShortType            => "%d"
          case DoubleType | FloatType         => "%f"
          case LongType                       => "%lf"
          case StringType | OptimalStringType => "%s"
          case ArrayType(elemTpe)             => s"Array[$elemTpe]"
          case tp                             => tp.toString
        }
        val fieldsWithDescriptor = structFields.map(f => f -> getDescriptor(f))
        val descriptor = tpe.name + "(" + fieldsWithDescriptor.map(f => f._2).mkString(", ") + ")"
        val fields = fieldsWithDescriptor.collect {
          case f if f._2.startsWith("%") => {
            val tp = f._1.tpe
            field(obj, f._1.name)(tp)
          }
        }
        val str = malloc(4096)(CharType)
        sprintf(str.asInstanceOf[Rep[String]], unit(descriptor), fields: _*)
        str.asInstanceOf[Rep[String]]
      }
      case tp => throw new Exception(s"toString conversion for non-record type $tp is not handled for the moment")
    }
  }
}

class OptimalStringToCTransformer(override val IR: LoweringLegoBase) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._
  import cscala.CLangTypes

  private[OptimalStringToCTransformer] implicit def optimalStringToCharPointer(x: Rep[OptimalString]) = x.asInstanceOf[Rep[LPointer[Char]]]

  implicit class OptimalStringRep(self: Rep[OptimalString]) {
    def getBaseValue(s: Rep[OptimalString]): Rep[LPointer[Char]] = apply(s).asInstanceOf[Rep[LPointer[Char]]]
  }

  rewrite += rule { case OptimalStringNew(self) => self }
  rewrite += rule {
    case OptimalStringString(self) =>
      new OptimalStringRep(self).getBaseValue(self)
  }
  rewrite += rule {
    case OptimalStringDiff(self, y) =>
      CString.strcmp(new OptimalStringRep(self).getBaseValue(self), new OptimalStringRep(self).getBaseValue(y))
  }
  rewrite += rule {
    case OptimalStringEndsWith(self, y) =>
      {
        val lenx: Rep[ch.epfl.data.cscala.CLangTypes.CSize] = CString.strlen(new OptimalStringRep(self).getBaseValue(self));
        val leny: Rep[ch.epfl.data.cscala.CLangTypes.CSize] = CString.strlen(new OptimalStringRep(self).getBaseValue(y));
        val len: Rep[Int] = lenx.$minus(leny);
        infix_$eq$eq(CString.strncmp(CLang.pointer_add[Char](new OptimalStringRep(self).getBaseValue(self), len)(typeRep[Char], CLangTypes.charType), new OptimalStringRep(self).getBaseValue(y), len), unit(0))
      }
  }
  rewrite += rule {
    case OptimalStringStartsWith(self, y) =>
      infix_$eq$eq(CString.strncmp(new OptimalStringRep(self).getBaseValue(self), new OptimalStringRep(self).getBaseValue(y), CString.strlen(new OptimalStringRep(self).getBaseValue(y))), unit(0))
  }
  rewrite += rule {
    case OptimalStringCompare(self, y) =>
      CString.strcmp(new OptimalStringRep(self).getBaseValue(self), new OptimalStringRep(self).getBaseValue(y))
  }
  rewrite += rule { case OptimalStringCompare(x, y) => strcmp(x, y) }
  rewrite += rule { case OptimalStringCompare(x, y) => strcmp(x, y) }
  rewrite += rule { case OptimalStringLength(x) => strlen(x) }
  rewrite += rule { case OptimalString$eq$eq$eq(x, y) => strcmp(x, y) __== unit(0) }
  rewrite += rule { case OptimalString$eq$bang$eq(x, y) => infix_!=(strcmp(x, y), unit(0)) }
  rewrite += rule { case OptimalStringContainsSlice(x, y) => infix_!=(strstr(x, y), unit(null)) }
  rewrite += rule {
    case OptimalStringLength(self) =>
      CString.strlen(new OptimalStringRep(self).getBaseValue(self))
  }
  rewrite += rule {
    case OptimalString$eq$eq$eq(self, y) =>
      infix_$eq$eq(CString.strcmp(new OptimalStringRep(self).getBaseValue(self), new OptimalStringRep(self).getBaseValue(y)), unit(0))
  }
  rewrite += rule {
    case OptimalString$eq$bang$eq(self, y) =>
      infix_$bang$eq(CString.strcmp(new OptimalStringRep(self).getBaseValue(self), new OptimalStringRep(self).getBaseValue(y)), unit(0))
  }
  rewrite += rule {
    case OptimalStringContainsSlice(self, y) =>
      infix_$bang$eq(CString.strstr(new OptimalStringRep(self).getBaseValue(self), new OptimalStringRep(self).getBaseValue(y)), CLang.NULL[Char])
  }
  rewrite += rule {
    case OptimalStringIndexOfSlice(self, y, idx) =>
      {
        val substr: Rep[ch.epfl.data.cscala.CLangTypes.LPointer[Char]] = CString.strstr(CLang.pointer_add[Char](new OptimalStringRep(self).getBaseValue(self), idx)(typeRep[Char], CLangTypes.charType), new OptimalStringRep(self).getBaseValue(y));
        __ifThenElse(infix_$eq$eq(substr, CLang.NULL[Char]), unit(-1), CString.str_subtract(substr, new OptimalStringRep(self).getBaseValue(self)))
      }
  }
  rewrite += rule {
    case OptimalStringApply(self, idx) =>
      CLang.*(CLang.pointer_add[Char](new OptimalStringRep(self).getBaseValue(self), idx)(typeRep[Char], CLangTypes.charType))
  }
  rewrite += rule {
    case OptimalStringSlice(self, start, end) =>
      {
        val len: Rep[Int] = end.$minus(start).$plus(unit(1));
        val newbuf: Rep[ch.epfl.data.cscala.CLangTypes.LPointer[Char]] = CStdLib.malloc[Char](len);
        CString.strncpy(newbuf, CLang.pointer_add[Char](new OptimalStringRep(self).getBaseValue(self), start)(typeRep[Char], CLangTypes.charType), len.$minus(unit(1)));
        newbuf
      }
  }
  rewrite += rule {
    case OptimalStringReverse(x) =>
      val str = infix_asInstanceOf(apply(x))(typeArray(typePointer(CharType))).asInstanceOf[Rep[Array[Pointer[Char]]]]
      val i = __newVar[Int](unit(0))
      val j = __newVar[Int](strlen(str))

      __whileDo((i: Rep[Int]) < (j: Rep[Int]), {
        val _i = (i: Rep[Int])
        val _j = (j: Rep[Int])
        val temp = str(_i)
        str(_i) = str(_j)
        str(_j) = temp
        __assign(i, _i + unit(1))
        __assign(j, _j - unit(1))
      })
      str
  }
}

class RangeToCTransformer(override val IR: LoweringLegoBase) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._

  rewrite += remove { case RangeApplyObject(_, _) => () }
  rewrite += remove { case RangeNew(start, end, step) => () }
  rewrite += rule {
    case RangeForeach(self @ Def(RangeApplyObject(start, end)), f) =>
      val i = fresh[Int]
      val body = reifyBlock {
        inlineFunction(f.asInstanceOf[Rep[Int => Unit]], i)
      }
      For(start, end, unit(1), i, body)
  }
  rewrite += rule {
    case RangeForeach(Def(RangeNew(start, end, step)), Def(Lambda(f, i1, o))) =>
      PardisFor(start, end, step, i1.asInstanceOf[Expression[Int]], reifyBlock({ o }).asInstanceOf[PardisBlock[Unit]])
  }
}

class OptionToCTransformer(override val IR: LoweringLegoBase) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._

  rewrite += rule { case OptionApplyObject(x) => x }

  rewrite += rule { case OptionGet(x) => x }

  rewrite += rule { case OptionNonEmpty(x) => infix_!=(x, unit(null)) }

}

class Tuple2ToCTransformer(override val IR: LoweringLegoBase) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._

  // rewrite += rule { case Tuple2ApplyObject(_1, _2) => __new(("_1", false, _1), ("_2", false, _2))(Tuple2Type(_1.tp, _2.tp)) }

  // rewrite += rule { case n @ Tuple2_Field__1(self) => field(apply(self), "_1")((n.tp)) }

  // rewrite += rule { case n @ Tuple2_Field__2(self) => field(apply(self), "_2")((n.tp)) }

  object Tuple2Create {
    def unapply[T](d: Def[T]): Option[(Rep[Any], Rep[Any])] = d match {
      case Tuple2ApplyObject(_1, _2) => Some(_1 -> _2)
      case Tuple2New(_1, _2)         => Some(_1 -> _2)
      case _                         => None
    }
  }

  rewrite += remove { case Tuple2Create(_1, _2) => () }

  rewrite += rule { case n @ Tuple2_Field__1(Def(Tuple2Create(_1, _2))) => _1 }

  rewrite += rule { case n @ Tuple2_Field__2(Def(Tuple2Create(_1, _2))) => _2 }

}

class ScalaConstructsToCTranformer(override val IR: LoweringLegoBase, val ifAgg: Boolean) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._

  def blockIsPure[T](block: Block[T]): Boolean = {
    for (stm <- block.stmts) {
      val isPure = stm.rhs match {
        case b @ Block(_, _) => blockIsPure(b)
        case d               => d.isPure
      }
      if (!isPure)
        return false
    }
    true
  }

  def isNullCheck[T](exp: Rep[T]): Boolean = exp match {
    case Def(NotEqual(_, Constant(null))) => true
    case Def(Equal(_, Constant(null)))    => true
    case _                                => false
  }

  rewrite += rule {
    case IfThenElse(cond, thenp, elsep) if thenp.tp != UnitType =>
      // val res = __newVar(unit(0))(thenp.tp.asInstanceOf[TypeRep[Int]])
      val res = __newVarNamed[Int](unit(0), "ite")(thenp.tp.asInstanceOf[TypeRep[Int]])
      // if (res.e.asInstanceOf[Sym[_]].id == 17398) {
      //   System.out.println(s"tp is ${thenp.tp}, ${elsep.tp}, ${elsep.correspondingNode.asInstanceOf[Block[Any]].res.tp}, ${elsep.correspondingNode.asInstanceOf[Block[Any]].res}")
      // }
      __ifThenElse(cond, {
        __assign(res, inlineBlock(thenp))
      }, {
        __assign(res, inlineBlock(elsep))
      })
      ReadVar(res)(res.tp)
  }

  rewrite += rule {
    case and @ Boolean$bar$bar(case1, b) if ifAgg && blockIsPure(b) && !isNullCheck(case1) => {
      val resB = inlineBlock[Boolean](b)
      // NameAlias[Boolean](Some(case1), " or ", List(List(resB)))
      case1 | resB
    }
  }

  rewrite += rule {
    case and @ Boolean$amp$amp(case1, b) if ifAgg && blockIsPure(b) && !isNullCheck(case1) => {
      val resB = inlineBlock[Boolean](b)
      // NameAlias[Boolean](Some(case1), " and ", List(List(resB)))
      case1 & resB
    }
  }

  rewrite += rule {
    case or @ Boolean$bar$bar(case1, b) => {
      __ifThenElse(case1, unit(true), b)
    }
  }

  rewrite += rule {
    case and @ Boolean$amp$amp(case1, b) => {
      __ifThenElse(case1, b, unit(false))
    }
  }
  // rewrite += rule {
  //   case and @ Boolean$amp$amp(case1, b) if b.stmts.forall(stm => stm.rhs.isPure) && b.stmts.nonEmpty => {
  //     val rb = inlineBlock(b)
  //     case1 && rb
  //   }
  // }
  // rewrite += rule {
  //   case and @ Boolean$amp$amp(case1, b) if b.stmts.nonEmpty => {
  //     __ifThenElse(case1, b, unit(false))
  //   }
  // }
  rewrite += rule { case IntUnary_$minus(self) => unit(-1) * self }
  rewrite += rule { case IntToLong(x) => x }
  rewrite += rule { case ByteToInt(x) => x }
  rewrite += rule { case IntToDouble(x) => x }
  rewrite += rule { case DoubleToInt(x) => infix_asInstanceOf[Double](x) }
  rewrite += rule { case BooleanUnary_$bang(b) => NameAlias[Boolean](None, "!", List(List(b))) }
}

class BlockFlattening(override val IR: LoweringLegoBase) extends RecursiveRuleBasedTransformer[LoweringLegoBase](IR) with CTransformer {
  import IR._
  import CNodes._
  import CTypes._

  rewrite += statement {
    case sym -> (blk @ Block(stmts, res)) =>
      inlineBlock(blk)
  }
}