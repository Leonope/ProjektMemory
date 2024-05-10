file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/util/ObserverPattern.worksheet.sc
### file%3A%2F%2F%2FC%3A%2FUsers%2Fleo11%2FOneDrive%2FDesktop%2FHTWG%2FAIN%2FSemester%25203%2FSoftware-Engineering%2FProjektMemory%2Fsrc%2Fmain%2Fscala%2Futil%2FObserverPattern.worksheet.sc:6: error: `end of file` expected but `}` found
}
^

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 2.13.12
Classpath:
/modules [exists ], <WORKSPACE>\.bloop\projektmemory\bloop-bsp-clients-classes\classes-Metals-JtlebLLSTH6tGBYqBuDuqg== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.9.9\semanticdb-javac-0.9.9.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.12\scala-library-2.13.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalameta\mdoc-runtime_2.13\2.5.2\mdoc-runtime_2.13-2.5.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\fansi_2.13\0.4.0\fansi_2.13-0.4.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\lihaoyi\pprint_2.13\0.8.1\pprint_2.13-0.8.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.12\scala-reflect-2.13.12.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalameta\mdoc-interfaces\2.5.2\mdoc-interfaces-2.5.2.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\com\geirsson\metaconfig-pprint_2.13\0.12.0\metaconfig-pprint_2.13-0.12.0.jar [exists ]
Options:
-Yrangepos -Xplugin-require:semanticdb


action parameters:
uri: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/util/ObserverPattern.worksheet.sc
text:
```scala
import util.{Observer, Observable}

class TestObject extends Observer {
    def update:Unit = println("Ping")
  }
}

object ObserverPattern {
  val observable = new Observable
  val observer1 = new TestObject
  val observer2 = new TestObject

  observable.add(observer1)
  observable.add(observer2)
  observable.notifyObservers                      //> Ping    
  observable.remove(observer1)
  observable.notifyObservers                      //> Ping
  observable.remove(observer2)
  observable.notifyObservers
}
```



#### Error stacktrace:

```
scala.meta.internal.parsers.Reporter.syntaxError(Reporter.scala:16)
	scala.meta.internal.parsers.Reporter.syntaxError$(Reporter.scala:16)
	scala.meta.internal.parsers.Reporter$$anon$1.syntaxError(Reporter.scala:22)
	scala.meta.internal.parsers.Reporter.syntaxError(Reporter.scala:17)
	scala.meta.internal.parsers.Reporter.syntaxError$(Reporter.scala:17)
	scala.meta.internal.parsers.Reporter$$anon$1.syntaxError(Reporter.scala:22)
	scala.meta.internal.parsers.ScalametaParser.scala$meta$internal$parsers$ScalametaParser$$expectAt(ScalametaParser.scala:394)
	scala.meta.internal.parsers.ScalametaParser.scala$meta$internal$parsers$ScalametaParser$$expectAt(ScalametaParser.scala:397)
	scala.meta.internal.parsers.ScalametaParser.expect(ScalametaParser.scala:398)
	scala.meta.internal.parsers.ScalametaParser.accept(ScalametaParser.scala:410)
	scala.meta.internal.parsers.ScalametaParser.parseRuleAfterBOF(ScalametaParser.scala:63)
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

file%3A%2F%2F%2FC%3A%2FUsers%2Fleo11%2FOneDrive%2FDesktop%2FHTWG%2FAIN%2FSemester%25203%2FSoftware-Engineering%2FProjektMemory%2Fsrc%2Fmain%2Fscala%2Futil%2FObserverPattern.worksheet.sc:6: error: `end of file` expected but `}` found
}
^