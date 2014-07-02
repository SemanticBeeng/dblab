package ch.epfl.data
package legobase

import utils.Utilities._
import java.io.PrintStream

trait ScalaImpl {
  val sdf = new java.text.SimpleDateFormat("yyyy-MM-dd")
  def parseDate(x: String): Long = {
    sdf.parse(x).getTime
  }
  def parseString(x: String): Array[Byte] = x.getBytes
}

object MiniDB extends storagemanager.Loader with Queries {
  val numRuns: scala.Int = 1
  var currQuery: java.lang.String = ""

  def getOutputName = currQuery + "Output.txt"

  def getResultFileName = "results/" + currQuery + ".result"

  def main(args: Array[String]) {
    Config.datapath = args(0)
    Config.checkResults = true

    val queries: scala.collection.immutable.List[String] =
      if (args.length == 2 && args(1) == "testsuite") (for (i <- 1 to 22) yield "Q" + i).toList
      else args.tail.toList
    for (q <- queries) {
      currQuery = q
      Console.withOut(new PrintStream(getOutputName)) {
        currQuery match {
          case "Q1" => Q1(numRuns)
          //        case "Q2" => Q2(numRuns)
          case _    => throw new Exception("Query not supported!")
        }
        // Check results
        if (Config.checkResults) {
          if (new java.io.File(getResultFileName).exists) {
            val resq = scala.io.Source.fromFile(getOutputName).mkString
            val resc = {
              val str = scala.io.Source.fromFile(getResultFileName).mkString
              str * numRuns
            }
            if (resq != resc) {
              System.out.println("-----------------------------------------")
              System.out.println("QUERY" + q + " DID NOT RETURN CORRECT RESULT!!!")
              System.out.println("Correct result:")
              System.out.println(resc)
              System.out.println("Result obtained from execution:")
              System.out.println(resq)
              System.out.println("-----------------------------------------")
              System.exit(0)
            } else System.out.println("CHECK RESULT FOR QUERY " + q + ": [OK]")
          } else System.out.println("Reference result file not found. Skipping checking of result")
        }
      }
    }
  }
}
