package org.neo4j.cyphermen

import org.junit.Test
import twitter4j.{User, Status}
import org.scalatest.mock.MockitoSugar
import org.scalatest.Assertions
import org.mockito.Mockito._

class ParserTest extends MockitoSugar with Assertions {
  @Test def simples() {
    val status = mock[Status]
    val user = mock[User]
    when(status.getText).thenReturn("@cyphermen Are you alive?")
    when(status.getUser).thenReturn(user)
    when(user.getScreenName).thenReturn("andres_taylor")

    val q = Parser.parse(status)

    assert(q.from === "andres_taylor")
    assert(q.query === "Are you alive?")
  }
}