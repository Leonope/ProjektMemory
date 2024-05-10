file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/util/Giga.worksheet.sc
### file%3A%2F%2F%2FC%3A%2FUsers%2Fleo11%2FOneDrive%2FDesktop%2FHTWG%2FAIN%2FSemester%25203%2FSoftware-Engineering%2FProjektMemory%2Fsrc%2Fmain%2Fscala%2Futil%2FGiga.worksheet.sc:2: error: `identifier` expected but `import` found
import scala.util.{Try, Success, Failure}
^

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 2.13.12
Classpath:
/modules [exists ], <WORKSPACE>\.bloop\projektmemory\bloop-bsp-clients-classes\classes-Metals-JtlebLLSTH6tGBYqBuDuqg== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.9.9\semanticdb-javac-0.9.9.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.12\scala-library-2.13.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalameta\mdoc-runtime_2.13\2.5.2\mdoc-runtime_2.13-2.5.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\fansi_2.13\0.4.0\fansi_2.13-0.4.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\pprint_2.13\0.8.1\pprint_2.13-0.8.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.12\scala-reflect-2.13.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalameta\mdoc-interfaces\2.5.2\mdoc-interfaces-2.5.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\geirsson\metaconfig-pprint_2.13\0.12.0\metaconfig-pprint_2.13-0.12.0.jar [exists ]
Options:
-Yrangepos -Xplugin-require:semanticdb


action parameters:
uri: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/util/Giga.worksheet.sc
text:
```scala
package 
import scala.util.{Try, Success, Failure}

val x: Int = 28796

def frageSpielerAnzahlMock(eingabe: String): Try[Int] = Try {
  val anzahl = eingabe.toInt
  if (anzahl < 1 || anzahl > 3) throw new IllegalArgumentException("Spieleranzahl muss zwischen 1 und 3 liegen.")
  anzahl
}

def pruefeSpielerAnzahlMock(anzahl: Int): Try[String] = anzahl match {
  case 1 => Success("Ein Spieler wurde ausgewählt.")
  case _ => Failure(new Exception("Es ist nur 1 Spieler zugelassen, da die Funktion für mehr Spieler noch nicht implementiert ist."))
}

def frageSpielerNameMock(eingabe: String): String = eingabe

val test1 = frageSpielerAnzahlMock("2")
val test2 = frageSpielerAnzahlMock("1") 

val test3 = pruefeSpielerAnzahlMock(1) 
val test4 = pruefeSpielerAnzahlMock(2)

val test5 = frageSpielerNameMock("Alice")

// Zusammenstellung der Testergebnisse
test1
test2
test3
test4
test5

```



#### Error stacktrace:

```
scala.meta.internal.parsers.Reporter.syntaxError(Reporter.scala:16)
	scala.meta.internal.parsers.Reporter.syntaxError$(Reporter.scala:16)
	scala.meta.internal.parsers.Reporter$$anon$1.syntaxError(Reporter.scala:22)
	scala.meta.internal.parsers.Reporter.syntaxError(Reporter.scala:17)
	scala.meta.internal.parsers.Reporter.syntaxError$(Reporter.scala:17)
	scala.meta.internal.parsers.Reporter$$anon$1.syntaxError(Reporter.scala:22)
	scala.meta.internal.parsers.ScalametaParser.syntaxErrorExpected(ScalametaParser.scala:392)
	scala.meta.internal.parsers.ScalametaParser.name(ScalametaParser.scala:1224)
	scala.meta.internal.parsers.ScalametaParser.termName(ScalametaParser.scala:1227)
	scala.meta.internal.parsers.ScalametaParser.qualId(ScalametaParser.scala:1304)
	scala.meta.internal.parsers.ScalametaParser.$anonfun$batchSource$10(ScalametaParser.scala:4764)
	scala.meta.internal.parsers.ScalametaParser.tryParse(ScalametaParser.scala:216)
	scala.meta.internal.parsers.ScalametaParser.$anonfun$batchSource$1(ScalametaParser.scala:4759)
	scala.meta.internal.parsers.ScalametaParser.atPos(ScalametaParser.scala:316)
	scala.meta.internal.parsers.ScalametaParser.autoPos(ScalametaParser.scala:365)
	scala.meta.internal.parsers.ScalametaParser.batchSource(ScalametaParser.scala:4727)
	scala.meta.internal.parsers.ScalametaParser.$anonfun$source$1(ScalametaParser.scala:4720)
	scala.meta.internal.parsers.ScalametaParser.atPos(ScalametaParser.scala:316)
	scala.meta.internal.parsers.ScalametaParser.autoPos(ScalametaParser.scala:365)
	scala.meta.internal.parsers.ScalametaParser.source(ScalametaParser.scala:4720)
	scala.meta.internal.parsers.ScalametaParser.entrypointSource(ScalametaParser.scala:4725)
	scala.meta.internal.parsers.ScalametaParser.parseSourceImpl(ScalametaParser.scala:135)
	scala.meta.internal.parsers.ScalametaParser.$anonfun$parseSource$1(ScalametaParser.scala:132)
	scala.meta.internal.parsers.ScalametaParser.parseRuleAfterBOF(ScalametaParser.scala:59)
	scala.meta.internal.parsers.ScalametaParser.parseRule(ScalametaParser.scala:54)
	scala.meta.internal.parsers.ScalametaParser.parseSource(ScalametaParser.scala:132)
	scala.meta.parsers.Parse$.$anonfun$parseSource$1(Parse.scala:29)
	scala.meta.parsers.Parse$$anon$1.apply(Parse.scala:36)
	scala.meta.parsers.Api$XtensionParseDialectInput.parse(Api.scala:25)
	scala.meta.internal.semanticdb.scalac.ParseOps$XtensionCompilationUnitSource.toSource(ParseOps.scala:17)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument.toTextDocument(TextDocumentOps.scala:206)
	scala.meta.internal.pc.SemanticdbTextDocumentProvider.textDocument(SemanticdbTextDocumentProvider.scala:54)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticdbTextDocument$1(ScalaPresentationCompiler.scala:400)
```
#### Short summary: 

file%3A%2F%2F%2FC%3A%2FUsers%2Fleo11%2FOneDrive%2FDesktop%2FHTWG%2FAIN%2FSemester%25203%2FSoftware-Engineering%2FProjektMemory%2Fsrc%2Fmain%2Fscala%2Futil%2FGiga.worksheet.sc:2: error: `identifier` expected but `import` found
import scala.util.{Try, Success, Failure}
^