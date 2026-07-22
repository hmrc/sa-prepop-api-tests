/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package specs.benefits

import http.HttpHeaders
import models.*
import models.Scenario.{HAPPY_PATH_1, HAPPY_PATH_2}
import requests.CreateTestUserWinterFuelPaymentAmount.createTestUserData
import requests.LocalBearerGenerator.fetchBearerToken
import requests.GetWinterFuelPaymentAmount
import specs.BaseSpec

class WinterFuelPaymentAmountSpec extends BaseSpec {

  override val serviceUnderTest: String = "winter-fuel-payment-amount"

  override val specificTestCasesWithNino: Seq[BaseSaPrePopTestInputWithNino] = Seq(
    ErrorSaPrePopTestInputWithNino(
      nino = "AA123456A",
      saUtr = "1097172564",
      taxYearRange = "2018-19",
      expectedStatusCode = 404,
      expectedResponseErrorCode = "NOT_FOUND",
      expectedResponseErrorMessage = "Resource was not found",
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_2
    )
  )

  override def allTestCasesWithNino: Seq[BaseSaPrePopTestInputWithNino] =
    specificTestCasesWithNino ++ baseTestCasesWithNino.filterNot {
      case s: SuccessSaPrePopTestInputWithNino =>
        s.taxYearRange == "2018-19" && s.scenario == HAPPY_PATH_2

      case e: ErrorSaPrePopTestInputWithNino =>
        e.expectedStatusCode == 406
    }

  val fullAmountModel: WinterFuelPaymentAmountResponse =
    WinterFuelPaymentAmountResponse(winterFuelPaymentAmount = 215.67)

  def successTest(testCase: SuccessSaPrePopTestInputWithNino): Unit =
    s"should return ${testCase.expectedStatusCode} when calling winter-fuel-payment-amount with nino: ${testCase.nino} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken} for scenario: ${testCase.scenario}" in {

      createTestUserData(
        testCase.nino,
        testCase.taxYearRange,
        testCase.scenario.toString,
        serviceUnderTest
      ) shouldBe 201

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)
      val headers     = HttpHeaders.allHeaders(bearerToken, "2.1")
      val client      = new GetWinterFuelPaymentAmount(headers)

      val response3rdParties = client.getWinterFuelPaymentAmountResponse(testCase.nino, testCase.taxYearRange)
      response3rdParties.status shouldBe testCase.expectedStatusCode

      val responseData3rdParties = response3rdParties.data.as[WinterFuelPaymentAmountResponse]

      val responseOTRSA = client.getWinterFuelPaymentAmountResponseOTRSA(testCase.nino, testCase.taxYearRange)
      responseOTRSA.status shouldBe testCase.expectedStatusCode

      val responseDataOTRSA = responseOTRSA.data.as[WinterFuelPaymentAmountResponse]

      testCase.scenario match {
        case HAPPY_PATH_1 => responseData3rdParties shouldBe fullAmountModel
        case s            => fail(s"[WinterFuelPaymentAmountSpec] Unexpected success scenario $s")
      }
      testCase.scenario match {
        case HAPPY_PATH_1 => responseDataOTRSA shouldBe fullAmountModel
        case s            => fail(s"[WinterFuelPaymentAmountSpec] Unexpected success scenario $s")
      }
    }
  def errorTest(testCase: ErrorSaPrePopTestInputWithNino): Unit     =
    s"should return ${testCase.expectedStatusCode} when calling winter-fuel-payment-amount with nino: ${testCase.nino} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken} for scenario: ${testCase.scenario}" in {

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)
      val headers     =
        if (testCase.expectedStatusCode == 406) HttpHeaders.headersNoAccept(bearerToken)
        else HttpHeaders.allHeaders(bearerToken, "2.1")

      val client = new GetWinterFuelPaymentAmount(headers)

      val response3rdParties = client.getWinterFuelPaymentAmountResponse(testCase.nino, testCase.taxYearRange)
      response3rdParties.status shouldBe testCase.expectedStatusCode

      val error3rdParties = response3rdParties.data.as[JsonErrorResponse]

      error3rdParties.code    shouldBe testCase.expectedResponseErrorCode
      error3rdParties.message shouldBe testCase.expectedResponseErrorMessage

      val responseOTRSA = client.getWinterFuelPaymentAmountResponseOTRSA(testCase.nino, testCase.taxYearRange)
      responseOTRSA.status shouldBe testCase.expectedStatusCode

      val errorOTRSA = responseOTRSA.data.as[JsonErrorResponse]

      errorOTRSA.code    shouldBe testCase.expectedResponseErrorCode
      errorOTRSA.message shouldBe testCase.expectedResponseErrorMessage
    }

  s"${this.getClass.getSimpleName}" when
    allTestCasesWithNino.foreach {
      case successCase: SuccessSaPrePopTestInputWithNino =>
        "making successful requests" should
          successTest(successCase)

      case errorCase: ErrorSaPrePopTestInputWithNino =>
        "making error requests" should
          errorTest(errorCase)
    }
}
