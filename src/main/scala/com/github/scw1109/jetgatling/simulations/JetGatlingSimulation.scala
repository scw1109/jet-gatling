package com.github.scw1109.jetgatling.simulations

import com.github.scw1109.jetgatling.{JetGatling, JetGatlingOptions}
import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

class JetGatlingSimulation extends Simulation {

  val logger: Logger = LoggerFactory.getLogger(getClass.getName)
  val options: JetGatlingOptions = JetGatling.OPTIONS

  val rps: Double = options.getRps
  val concurrency: Int = options.getConcurrency
  val duration: Int = options.getDurationInSecond
  val rampDuration: Int = options.getRampDurationInSecond
  val baseUrl: String = options.getBaseUrl match {
    case httpUrl if httpUrl.startsWith("http://") => httpUrl
    case httpsUrl if httpsUrl.startsWith("https://") => httpsUrl
    case url => "http://" + url
  }
  val pathFile: String = options.getPathFile
  val timeout: Int = options.getTimeout

  val httpMethod: String = options.getHttpMethod.toUpperCase
  val keepAlive: Boolean = options.isKeepAlive
  val userAgent: String = options.getUserAgent
  val httpHeaders: Map[String, String] = options.getHeaders.asScala.map(h => {
    val i = h.indexOf(":")
    h.substring(0, i) -> h.substring(i + 1)
  }).toMap
  val httpBodyFile: String = options.getBodyFile

  logger.info("Running with the following parameters")
  logger.info("RPS: {}", rps)
  logger.info("Concurrency: {}", concurrency)
  logger.info("Duration: {} seconds", duration)
  logger.info("Ramp up duration: {} seconds", rampDuration)
  logger.info("Base url: {}", baseUrl)
  logger.info("Path file: {}", pathFile)
  logger.info("Timeout: {} milliseconds", timeout)
  logger.info("-----")
  logger.info("HTTP method: {}", httpMethod)
  logger.info("Keep alive: {}", keepAlive)
  logger.info("User agent: {}", userAgent)
  logger.info("HTTP headers: {}", httpHeaders)
  logger.info("HTTP body file: {}", options.getBodyFile)
  logger.info("-----")

  val httpConf: HttpProtocolBuilder = http
    .baseURL(baseUrl)
    .acceptEncodingHeader("gzip, deflate")
    .connectionHeader(if (keepAlive) "keep-alive" else "close")
    .userAgentHeader(userAgent)
    .headers(httpHeaders)
    .disableWarmUp
    .disableCaching
    .map(conf =>
      // Share connections across simulation users for Fix RPS
      concurrency match {
        case 0 => conf.shareConnections
        case _ => conf
      }
    ).get
  
  val path: String = "${path}"
  val feeder: RecordSeqFeederBuilder[String] = (pathFile match {
    case "" => Array(Map("path" -> ""))
    case _ => Source.fromFile(pathFile).getLines()
      .map(line => Map("path" -> line))
      .toArray
  }).circular

  logger.info("HTTP conf: {}", httpConf)
  logger.info("Path: {}", path)
  logger.info("Feeder size: {}", feeder.records.size)

  http("http").map(
    httpMethod match {
      case "GET" => _.get(path)
      case "POST" => _.post(path)
      case "PUT" => _.put(path)
      case "DELETE" => _.delete(path)
      case "HEAD" => _.head(path)
      case "PATCH" => _.patch(path)
      case "OPTIONS" => _.options(path)
      case _ => _.httpRequest(httpMethod, path)
    }
  ).map(http =>
    httpBodyFile match {
      case "" => http
      case _ => http.body(RawFileBody(httpBodyFile))
    }
  ).map(http =>
    timeout match {
      case t if timeout > 0 => http.check(responseTimeInMillis.lessThanOrEqual(t))
      case _ => http
    }
  ).onSuccess(http => {
    val scn: ScenarioBuilder = concurrency match {
      case 0 => scenario("Fix RPS simulation")
        .feed(feeder)
        .exec(http)
      case _ => scenario("Fix Concurrent simulation")
        .during(duration seconds) {
          feed(feeder).exec(http)
        }
    }

    val steps = concurrency match {
      case 0 => Array(
        rampDuration match {
          case 0 => nothingFor(0)
          case _ => rampUsersPerSec(1) to (rps * 0.5) during (rampDuration seconds)
        },
        constantUsersPerSec(rps) during (duration seconds)
      )
      case _ => Array(
        rampDuration match {
          case 0 => nothingFor(0)
          case _ => rampUsers(concurrency) over (rampDuration seconds)
        },
        atOnceUsers(concurrency)
      )
    }

    logger.info("Scenario: {}", scn)
    logger.info("Steps: {}", steps)
    logger.info("-----")

    setUp(scn.inject(steps).protocols(httpConf))

  }).onFailure(logger.error)
}
