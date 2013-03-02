package org.neo4j.cyphermen

import twitter4j.{StatusUpdate, Status, Paging, TwitterFactory}
import collection.JavaConverters._
import scala.io.Source.fromFile
import java.io.{PrintWriter, File, FileNotFoundException}
import org.eclipse.egit.github.core.{Gist, GistFile}
import org.eclipse.egit.github.core.service.GistService
import org.neo4j.test.ImpermanentGraphDatabase
import org.neo4j.cypher.ExecutionEngine

object Main extends App {
  val twitter = TwitterFactory.getSingleton
  val LAST_ID_FILENAME = "lastId.txt"

  {
    val statuses = twitter.getMentionsTimeline(new Paging(lastId)).asScala.toSeq

    statuses.map(status => (Parser.parse(status), status)).foreach {
      case (q: Query, s: Status) =>
        val result = runQuery(q.query)
        val url = createGist(result, q.query)

        val message = s"@${q.from} $url"
        println(message)

        val update = new StatusUpdate(message).inReplyToStatusId(s.getId)
        twitter.updateStatus(update)
    }

    if (statuses.nonEmpty) {
      saveId(statuses.head.getId)
    }
  }


  private def runQuery(query: String): String = {
    val db = new ImpermanentGraphDatabase()
    val engine = new ExecutionEngine(db)
    val result = engine.execute(query).dumpToString()
    result
  }

  def lastId: Long =
    try
      fromFile(LAST_ID_FILENAME).getLines().reduceLeft(_ + _).toLong
    catch {
      case _: FileNotFoundException => 1
    }

  def saveId(id: Long) {
    val writer = new PrintWriter(new File(LAST_ID_FILENAME))

    writer.write(id.toString)
    writer.close()
  }

  def createGist(text: String, query: String): String = {
    val file = new GistFile()
    file.setContent(text)
    val gist = new Gist()
    gist.setDescription("Response for query: " + query)
    gist.setFiles(Map("query_response.txt" -> file).asJava)
    val service = new GistService()
    service.getClient.setCredentials("user", "password")
    service.createGist(gist).getHtmlUrl
  }
}

