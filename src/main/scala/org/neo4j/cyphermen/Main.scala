package org.neo4j.cyphermen

import twitter4j.TwitterFactory
import collection.JavaConverters._

object Main extends App {
  //  Twitter twitter = TwitterFactory.getSingleton();
  val twitter = TwitterFactory.getSingleton
  val scala = twitter.getHomeTimeline.asScala.toSeq.map(status => status.getText).foreach(println)
  println(scala)
}
