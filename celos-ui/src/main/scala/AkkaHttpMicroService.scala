import java.net.URL
import java.util
import java.util.{Map, HashMap}

import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.collective.celos.{Util, CelosClient}
import com.collective.celos.ui.{UICommandLine, UICommandLineParser}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.collection.mutable
import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.{JsonParser, DefaultJsonProtocol}

import com.collective.celos.ui.Main

case class IpPairSummaryRequest(ip1: String, ip2: String)

trait Protocols extends DefaultJsonProtocol {
  implicit val ipPairSummaryRequestFormat = jsonFormat2(IpPairSummaryRequest.apply)
}

trait Service extends Protocols {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter


  lazy val routes = {
    logRequestResult("akka-http-microservice") {
      pathPrefix("skillen") {
        getFromResource("index.html")
      } ~
      // REST request from backbone
      path("ololo") {
        get {
          parameters("size", "color") {
            (orderItem: String, xx: String) =>
              complete {
                IpPairSummaryRequest("data.values", "tmp")
              }
          }
        }
      } ~
      path("main") {
        get {
          parameters("size", "color") {
            (orderItem: String, xx: String) =>
              complete {
                "data.values"
              }
          }
        }
      }
    }
  }
}

object AkkaHttpMicroService extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  val UICommandLineParser: UICommandLineParser = new UICommandLineParser
  val commandLine: UICommandLine = UICommandLineParser.parse(args)

  val celosURL: URL = new URL("http://celos002.ewr004.collective-media.net:9091")
  val client: CelosClient = new CelosClient(celosURL.toURI)


  val datax = getAttributes(commandLine)
  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))

  private def getAttributes(commandLine: UICommandLine) = {
    val attrs = new java.util.HashMap[String, AnyRef]
    attrs.put(Main.CELOS_URL_ATTR, commandLine.getCelosUrl)
    attrs.put(Main.HUE_URL_ATTR, commandLine.getHueUrl)
    attrs.put(Main.CONFIG_FILE_ATTR, commandLine.getConfigFile)
    attrs
  }

}
