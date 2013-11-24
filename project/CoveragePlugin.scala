import sbt._
import Keys._
import scala.Console
import scala.xml.NodeSeq
import scala.xml.XML

object CoveragePlugin extends Plugin {
  val printCoverage = taskKey[Unit]("prints coverage report overview")

  lazy val coverageSettings = Seq[Def.Setting[_]](
    printCoverage := {
      val reportDir: File = ScctPlugin.scctReportDir.value
      val report = XML.loadFile(new File(reportDir, "cobertura.xml"))

      printFormatedCoverage(report)
    }
  )

  private def printFormatedCoverage(report: NodeSeq): Unit = {
    for (pkg <- report \ "packages" \ "package") {
      val pkgName = (pkg \ "@name").text
      val pkgCoverage = (pkg \ "@line-rate").text.toDouble

      println(s"[${formatLineRate(pkgCoverage)}] Package $pkgName")

      for (cl <- pkg \ "classes" \ "class") {
        val clName = (cl \ "@name").text
        val clCoverage = (cl \ "@line-rate").text.toDouble

        println(s"[${formatLineRate(clCoverage)}] Class $clName ")
      }
    }

    val coverage = (report \ "@line-rate").text.toDouble
    println(s"[${formatLineRate(coverage)}] Summary")
  }

  private def formatLineRate(coverage: Double): String = {
    def colorize(text: String, color: String) = s"$color$text%${Console.RESET}"

    val percentText = "%5.1f" format (coverage * 100)

    coverage match {
      case c if c > 0.9 => colorize(percentText, Console.GREEN)
      case c if c > 0.6 => colorize(percentText, Console.YELLOW)
      case c => colorize(percentText, Console.RED)
    }
  }
}
