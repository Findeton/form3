This is a simple scala service for dealing whit payments. It uses Scala, the Play framework and PostgreSQL (with a docker/slick connection). I planned to document the API calls with Swagger (https://github.com/swagger-api/swagger-play) but I had no more time.

Tested on ubuntu 16.04 64 bits with:

 * scala 2.12.4
 * sbt 1.0.2
 * docker 17.09.0-ce 
 * docker-compose 1.15.0

# tests

To run the tests, simply execute:
    sbt test

# run system

On one console, run docker-compose with:

    docker-compose up

On another console, run this scala service with:

    sbt run

You can make API requests with curl.

# store payment

In order to record/store this payment, run:

    curl -0 -v -X POST http://localhost:9000/create \
    -H 'Content-Type: text/json; charset=utf-8' \
    -d @- << EOF
    {
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
    EOF
    

# get info on a list of payments

    curl -0 -v -X POST http://localhost:9000/get-list \
    -H 'Content-Type: text/json; charset=utf-8' \
    -d @- << EOF
    {
        "payment_ids": [
            "4ee3a8d8-ca7b-4290-a52c-dd5b6165ec43"
        ]
    }
    EOF
    

# get info on one payment

    curl -i -H "Accept: application/json" "http://localhost:9000/get/4ee3a8d8-ca7b-4290-a52c-dd5b6165ec43"

# update a payment record

    curl -0 -v -X POST http://localhost:9000/update \
    -H 'Content-Type: text/json; charset=utf-8' \
    -d @- << EOF
    {
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
    EOF
