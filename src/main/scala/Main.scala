import scala.io.StdIn.readLine
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}
import com.twitter.io.Buf

enum Choice:
  case Rock, Paper, Scissors

enum Outcome:
  case Win, Lose, Tie

def getCpuChoice(): Choice =
  val rand = new scala.util.Random
  val ordinal = rand.between(0, 2)
  return Choice.fromOrdinal(ordinal)

def parseUserChoice(remoteChoice: String): Choice =
  val choices = Map(
    "r" -> Choice.Rock,
    "rock" -> Choice.Rock,
    "p" -> Choice.Paper,
    "paper" -> Choice.Paper,
    "s" -> Choice.Scissors,
    "scissors" -> Choice.Scissors
  )
  return choices.getOrElse(remoteChoice, Choice.Rock)

def getOutcome(cpuChoice: Choice, userChoice: Choice): Outcome =
  if (cpuChoice == userChoice) return Outcome.Tie

  val games = Map(
    Choice.Rock -> Map(
      Choice.Paper -> Outcome.Win,
      Choice.Scissors -> Outcome.Lose
    ),
    Choice.Paper -> Map(
      Choice.Rock -> Outcome.Lose,
      Choice.Scissors -> Outcome.Win
    ),
    Choice.Scissors -> Map(
      Choice.Rock -> Outcome.Win,
      Choice.Paper -> Outcome.Lose
    )
  )

  return games.get(cpuChoice).get(userChoice)

object Server extends App {
  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] = {

      val remoteUserChoice = req.getParam("choice")

      val userChoice = parseUserChoice(remoteUserChoice)
      val cpuChoice = getCpuChoice()
      val outcome = getOutcome(cpuChoice, userChoice)

      val response = http.Response(req.version, http.Status.Ok)
      response.setContentString(s"""
        {
          "userChoice": "$userChoice",
          "cpuChoice": "$cpuChoice",
          "outcome": "$outcome"
        }
        """)

      Future.value(
        response
      )
    }
  }
  val server = Http.serve(":8080", service)
  Await.ready(server)
}
