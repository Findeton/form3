package controllers

import javax.inject._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.Logger
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

import models._
import utils._
import utils.JsonFormatters._

trait ErrorProcessing {
  /**
   * Get the message safely from a `Throwable`
   */
  def getMessageFromThrowable(t: Throwable): String = {
    if (null == t.getCause) {
        t.toString
     } else {
        t.getCause.getMessage
     }
  }
}

/**
 */
@Singleton
class RootController @Inject()(
  paymentDAO: PaymentDAOSpec,
  cc: ControllerComponents)
extends AbstractController(cc)
with ErrorProcessing {

  def getList() = Action(parse.json).async {
    implicit request: Request[JsValue] =>
      val promise = Promise[Result]()
      Future {
        Logger.info("/get-list/ request with body\n" + request.body)
        val fetchResult = request.body.validate[FetchRequest]
        fetchResult.asOpt match {
          case None => 
            Logger.error("/get-list/ error: Invalid format")
            promise.success(BadRequest("Invalid format"))

          case Some(fetchRequest) =>
            paymentDAO.filterByIdList(fetchRequest.payment_ids) andThen {
              case Success(id_list) =>
                val list = id_list.map {
                  el =>
                    val attributes: JsResult[Attributes] = 
                      Json.parse(el.attributes).validate[Attributes]
                    PaymentResponse(
                      el.id,
                      el.`type`,
                      el.version,
                      el.organisation_id,
                      attributes.get)
                }
                val str: String = Json.toJson(FetchResponse(list)).toString
                Logger.info("/get-list/ request SUCCESS")
                promise.success(Ok(str))
            } recover {
              case t: Throwable =>
                Logger.error("/get-list/ error: " + getMessageFromThrowable(t))
                promise.success(BadRequest(getMessageFromThrowable(t)))
            }
        }
      } recover {
        case t: Throwable =>
          Logger.error("/get-list/ error: " + getMessageFromThrowable(t))
          promise.success(BadRequest(getMessageFromThrowable(t)))
      }
      promise.future
  }

  def get(id: String) = Action.async {
    implicit request: Request[AnyContent] =>
      val promise = Promise[Result]()
      Future {
        Logger.info("/get/" + id + " request")
        paymentDAO.filterById(id) andThen {
          case Success(paymentOpt) =>
            paymentOpt match {
              case None =>
                Logger.error("/get/" + id + " error: Payment not found")
                promise.success(NotFound("Payment not found"))

              case Some(payment) =>
                val attributes: JsResult[Attributes] = 
                  Json.parse(payment.attributes).validate[Attributes]
                  
                attributes.asOpt match {
                  case None =>
                    Logger.error("/get/" + id + " error: Error reading attributes")
                    promise.success(BadRequest("Error reading attributes"))
                  case Some(attr) => 
                    val paymentResponse = 
                      PaymentResponse(
                        payment.id,
                        payment.`type`,
                        payment.version,
                        payment.organisation_id,
                        attr)
                    val str: String = Json.toJson(paymentResponse).toString
                    Logger.info("/get/" + id + " request SUCCESS")
                    promise.success(Ok(str))
                }
            }
        } recover {
          case t:Throwable =>
            Logger.error("/get/" + id + " error: " + getMessageFromThrowable(t))
            promise.success(BadRequest(getMessageFromThrowable(t)))
        }
      } recover {
        case t: Throwable =>
          Logger.error("/get/" + id + " error: " + getMessageFromThrowable(t))
          promise.success(BadRequest(getMessageFromThrowable(t)))
      }
      promise.future
  }

  def create() = Action(parse.json).async {
    implicit request: Request[JsValue] =>
      val promise = Promise[Result]()
      Future {
        Logger.info("/create/ request with body\n" + request.body)
        val paymentResult = request.body.validate[PaymentResponse]
        paymentResult.asOpt match {
          case None => {
            Logger.error("/create/ error: Invalid format")
            promise.success(BadRequest("Invalid format"))
          }
          case Some(paymentResponse) =>{
              val attributesStr: String = 
                Json.toJson(paymentResponse.attributes).toString
              val payment = 
                Payment(
                  paymentResponse.id,
                  paymentResponse.`type`,
                  paymentResponse.version,
                  paymentResponse.organisation_id,
                  attributesStr)
              paymentDAO.insert(payment) andThen {
                case Success(unit) =>
                  Logger.info("/create/ request SUCCESS")
                  promise.success(Ok(""))
              } recover {
                case t: Throwable =>
                  Logger.error("/create/ error: " + getMessageFromThrowable(t))
                  promise.success(BadRequest(getMessageFromThrowable(t)))
              }
          }
        }
      } recover {
        case t: Throwable =>
          Logger.error("/create/ error: " + getMessageFromThrowable(t))
          promise.success(BadRequest(getMessageFromThrowable(t)))
      }
      promise.future
  }

  def update() = Action(parse.json).async {
    implicit request: Request[JsValue] =>
      val promise = Promise[Result]()
      Future {
        Logger.info("/update/ request with body\n" + request.body)
        val paymentResult = request.body.validate[PaymentResponse]
        paymentResult.asOpt match {
          case None =>
            Logger.error("/update/ error: Invalid format")
            promise.success(BadRequest("Invalid format"))

          case Some(paymentResponse) => 
              val attributesStr: String = 
                Json.toJson(paymentResponse.attributes).toString
              val payment = 
                Payment(
                  paymentResponse.id,
                  paymentResponse.`type`,
                  paymentResponse.version,
                  paymentResponse.organisation_id,
                  attributesStr)
              paymentDAO.update(payment) andThen {
                case Success(unit) =>
                  Logger.info("/update/ request SUCCESS")
                  promise.success(Ok(""))
              } recover {
                case t: Throwable =>
                  Logger.error("/update/ error: " + getMessageFromThrowable(t))
                  promise.success(BadRequest(getMessageFromThrowable(t)))
              }
        }
      } recover {
        case t: Throwable =>
          Logger.error("/update/ error: " + getMessageFromThrowable(t))
          promise.success(BadRequest(getMessageFromThrowable(t)))
      }
      promise.future
  }
}
