import java.net.URL
import java.util
import java.util.{Map, HashMap}

import akka.actor.ActorSystem
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
import com.collective.celos.ui._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.collection.mutable
import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.{JsonParser, DefaultJsonProtocol}

import scala.language.postfixOps

case class IpPairSummaryRequest(ip1: String, ip2: String)

trait Protocols extends DefaultJsonProtocol {
  implicit val ipPairSummaryRequestFormat = jsonFormat2(IpPairSummaryRequest.apply)
}


object AkkaHttpMicroService extends App with Protocols {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  lazy val UICommandLineParser: UICommandLineParser = new UICommandLineParser
  lazy val commandLine: UICommandLine = UICommandLineParser.parse(args)

  private def getAttributes(commandLine: UICommandLine) = {
    val attrs = new java.util.HashMap[String, AnyRef]
    attrs.put(Main.CELOS_URL_ATTR, commandLine.getCelosUrl)
    attrs.put(Main.HUE_URL_ATTR, commandLine.getHueUrl)
    attrs.put(Main.CONFIG_FILE_ATTR, commandLine.getConfigFile)
    attrs
  }

  lazy val servletContext: util.HashMap[String, Object] = getAttributes(commandLine)

  lazy val routes = {
    logRequestResult("akka-http-microservice") {
      path("static" / ) {
        getFromResource("static/index.html")
      } ~
      pathPrefix("static") {
        getFromResourceDirectory("static")
      } ~
      path("version") {
        get {
          complete {
            val currentVersion = System.getenv("CELOS_VERSION")
            if (currentVersion == null) {
              "UNDEFINED"
            } else {
              currentVersion
            }
          }
        }
      } ~
      // REST request from backbone
      path("main") {
        get {
          parameters("time" ? "", "zoom" ? "") {
            (time: String, zoom: String) =>
              complete {
                val res = ReactMainServlet.processGet(servletContext, time, zoom)
                res
              }
          }
        }
      } ~
      path("group") {
        get {
          parameters("time" ? "", "zoom" ? "", "group") {
            (time: String, zoom: String, group: String) =>
              complete {
                val res = ReactWorkflowsServlet.processGet(servletContext, time, zoom, group)
                res
              }
          }
        }
      }
    }
  }

  logger.info("running server on port " + commandLine.getPort)
  Http().bindAndHandle(routes, interface = "localhost", port = commandLine.getPort)

}
