package utils

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._
import scala.math.BigInt


import play.api.libs._
import json._

object JsonFormatters {
  implicit val beneficiaryPartyF = Json.format[BeneficiaryParty]
  implicit val sponsorPartyF = Json.format[SponsorParty]
  implicit val fXF = Json.format[FX]
  implicit val debtorPartyF = Json.format[DebtorParty]
  implicit val senderChargeF = Json.format[SenderCharge]
  implicit val chargesInformationF = Json.format[ChargesInformation]
  implicit val attributesF = Json.format[Attributes]
  implicit val paymentF = Json.format[Payment]
  implicit val paymentResponseF = Json.format[PaymentResponse]
  implicit val fetchRequestF = Json.format[FetchRequest]
  implicit val fetchResponseF = Json.format[FetchResponse]
  implicit val createRequestF = Json.format[CreateRequest]
}