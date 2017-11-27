package controllers

import org.scalatestplus.play.guice._
import play.api.test.FakeRequestFactory
import play.api.test._
import play.api.mvc._
import play.api.http._
import play.api.libs.json._
import play.api.test.Helpers._

import scala.collection.mutable
import scala.concurrent._

import models._
import utils._
import utils.JsonFormatters._

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._

import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.any
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec
  extends PlaySpec
  with GuiceOneAppPerTest
  with Injecting
  with MockitoSugar {

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "RootController CREATE" should {

    "create a payment" in {
      val mockedPaymentDAO = mock[PaymentDAOSpec]
      val promise =  Promise[Unit]()
      promise.success( () )
      when(mockedPaymentDAO.insert(any[Payment])) thenReturn promise.future

      val bodyJson = Json.parse(
"""{
   "type":"Payment",
   "id":"4ee3a8d8-ca7b-4290-a52c-dd5b6165ec43",
   "version":0,
   "organisation_id":"743d5b63-8e6f-432e-a8fa-c5d8d2ee5fcb",
   "attributes":{
      "amount":"100.21",
      "beneficiary_party":{
         "account_name":"W Owens",
         "account_number":"31926819",
         "account_number_code":"BBAN",
         "account_type":0,
         "address":"1 The Beneficiary Localtown SE2",
         "bank_id":"403000",
         "bank_id_code":"GBDSC",
         "name":"Wilfred Jeremiah Owens"
      },
      "charges_information":{
         "bearer_code":"SHAR",
         "sender_charges":[
            {
               "amount":"5.00",
               "currency":"GBP"
            },
            {
               "amount":"10.00",
               "currency":"USD"
            }
         ],
         "receiver_charges_amount":"1.00",
         "receiver_charges_currency":"USD"
      },
      "currency":"GBP",
      "debtor_party":{
         "account_name":"EJ Brown Black",
         "account_number":"GB29XABC10161234567801",
         "account_number_code":"IBAN",
         "address":"10 Debtor Crescent Sourcetown NE1",
         "bank_id":"203301",
         "bank_id_code":"GBDSC",
         "name":"Emelia Jane Brown"
      },
      "end_to_end_reference":"Wil piano Jan",
      "fx":{
         "contract_reference":"FX123",
         "exchange_rate":"2.00000",
         "original_amount":"200.42",
         "original_currency":"USD"
      },
      "numeric_reference":"1002001",
      "payment_id":"123456789012345678",
      "payment_purpose":"Paying for goods/services",
      "payment_scheme":"FPS",
      "payment_type":"Credit",
      "processing_date":"2017-01-18",
      "reference":"Payment for Em's piano lessons",
      "scheme_payment_sub_type":"InternetBanking",
      "scheme_payment_type":"ImmediatePayment",
      "sponsor_party":{
         "account_number":"56781234",
         "bank_id":"123123",
         "bank_id_code":"GBDSC"
      }
   }
}
""")
      val paymentResult = bodyJson.validate[PaymentResponse]

      val fakeRequest = FakeRequest(POST, "/create")
          .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
          .withBody(Json.toJson(paymentResult.get))
      val controller = new RootController(mockedPaymentDAO, stubControllerComponents())
      val listRequest = Await.ready(controller.create().apply(fakeRequest), 5000 milliseconds)

      paymentResult.asOpt.isEmpty mustBe false
      listRequest.isCompleted mustBe true
      listRequest.value.isEmpty mustBe false
      listRequest.value.get.isSuccess mustBe true
      val result: Result = listRequest.value.get.get
      result.header.status mustBe OK
    }
  }
}
