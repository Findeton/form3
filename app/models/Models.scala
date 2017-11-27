package models

import play.api.Play
import slick.lifted.Tag
import play.api.libs.json._
import play.api.Logger

import java.sql.Timestamp
import java.util.Date


import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import slick.jdbc.{GetResult => Q}

import com.google.inject.ImplementedBy

case class BeneficiaryParty(
  account_name: String,
  account_number: String,
  account_number_code: String,
  account_type: Long,
  address: String,
  bank_id: String,
  bank_id_code: String,
  name: String)

case class SponsorParty(
  account_number: String,
  bank_id: String,
  bank_id_code: String)

case class FX(
  contract_reference: String,
  exchange_rate: String,
  original_amount: String,
  original_currency: String)

case class DebtorParty(
  account_name: String,
  account_number: String,
  account_number_code: String,
  address: String,
  bank_id: String,
  bank_id_code: String,
  name: String)

case class SenderCharge(
  amount: String,
  currency: String)

case class ChargesInformation(
  bearer_code: String,
  sender_charges: Seq[SenderCharge],
  receiver_charges_amount: String,
  receiver_charges_currency: String)

case class Attributes(
  amount: String,
  beneficiary_party: BeneficiaryParty,
  charges_information: ChargesInformation,
  currency: String,
  debtor_party: DebtorParty,
  end_to_end_reference: String,
  fx: FX,
  numeric_reference: String,
  payment_id: String,
  payment_purpose: String,
  payment_scheme: String,
  payment_type: String,
  processing_date: String,
  reference: String,
  scheme_payment_sub_type: String,
  scheme_payment_type: String,
  sponsor_party: SponsorParty)
  
case class Payment(
  id: String, 
  `type`: String,
  version: Long,
  organisation_id: String,
  attributes: String)

case class PaymentResponse(
  id: String, 
  `type`: String,
  version: Long,
  organisation_id: String,
  attributes: Attributes)

case class FetchRequest(payment_ids: Seq[String])
case class FetchResponse(data: Seq[PaymentResponse])
case class CreateRequest(data: Seq[PaymentResponse])


@ImplementedBy(classOf[PaymentDAO])
trait PaymentDAOSpec {
  def filterByIdList(idList: Seq[String]): Future[Seq[Payment]]
  def filterById(id: String): Future[Option[Payment]]
  def all(): Future[Seq[Payment]]
  def insert(payment: Payment): Future[Unit]
  def update(payment: Payment): Future[Unit]
}

class PaymentDAO @Inject()
  (protected val dbConfigProvider: DatabaseConfigProvider)
  (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
  with PaymentDAOSpec {

  import profile.api._

  private val Payments = TableQuery[PaymentTable]

  def filterByIdList(idList: Seq[String]): Future[Seq[Payment]] = 
    db.run(Payments.filter(_.id inSetBind idList).result)

  def filterById(id: String): Future[Option[Payment]] = 
    db.run(Payments.filter(_.id === id).result).map {
      x =>
        if (1 == x.length) {
          Some(x(0))
        } else {
          None
        }
    }

  def all(): Future[Seq[Payment]] = db.run(Payments.result)

  def insert(payment: Payment): Future[Unit] = 
    db.run(Payments += payment).map { _ => () }
  
  def update(payment: Payment): Future[Unit] = db.run(
    Payments.filter(_.id === payment.id).delete
  ) map {
    _ =>
      db.run(Payments += payment) 
  } map { _ => () }

  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {

    def id = column[String]("id", O.PrimaryKey)
    def `type` = column[String]("type")
    def version = column[Long]("version")
    def organisation_id = column[String]("organisation_id")
    def attributes = column[String]("attributes")

    def * = (id, `type`, version, organisation_id, attributes) <> (Payment.tupled, Payment.unapply)
  }
}