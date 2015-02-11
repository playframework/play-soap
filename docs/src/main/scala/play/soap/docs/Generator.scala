/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
package play.soap.docs

import java.io.File
import java.util.Collections

import org.apache.commons.io.FileUtils
import org.pegdown.ast.WikiLinkNode
import org.pegdown.{VerbatimSerializer, LinkRenderer, Extensions, PegDownProcessor}
import play.doc.PrettifyVerbatimSerializer
import play.twirl.api.Html

object Generator extends App {

  val outDir = new File(args(0))
  val inDir = new File(args(1))
  val inPages = args.drop(2)

  val parser = new PegDownProcessor(Extensions.ALL)
  val linkRenderer = new LinkRenderer {
    import LinkRenderer.Rendering
    override def render(node: WikiLinkNode) = {
      node.getText.split("\\|", 2) match {
        case Array(name) => new Rendering(name + ".html", name)
        case Array(title, name) => new Rendering(name + ".html", title)
        case _ => new Rendering(node.getText + ".html", node.getText)
      }
    }
  }
  val verbatimSerializer = Collections.singletonMap[String, VerbatimSerializer](VerbatimSerializer.DEFAULT,
    PrettifyVerbatimSerializer)

  val nav = Seq(
    "Home" -> "Home",
    "Using sbt WSDL" -> "SbtWsdl",
    "Using the Play SOAP client" -> "PlaySoapClient",
    "Using JAX WS Handlers" -> "Handlers",
    "Security" -> "Security"
  )
  val titleMap = nav.map(t => t._2 -> t._1).toMap

  inPages.foreach { name =>

    val inFile = new File(inDir, name + ".md")
    val markdown = FileUtils.readFileToString(inFile)
    val htmlSnippet = parser.markdownToHtml(markdown, linkRenderer, verbatimSerializer)
    val title = titleMap.get(name)
    val htmlPage = html.template(title, nav)(Html(htmlSnippet))
    FileUtils.writeStringToFile(new File(outDir, name + ".html"), htmlPage.body)

  }
}
