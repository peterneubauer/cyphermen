package org.neo4j.cyphermen

import twitter4j.Status

object Parser {
  def parse(in: Status): Query = Query(in.getText.replace("@cyphermen ", ""), in.getUser.getScreenName)
}

case class Query(query: String, from: String)