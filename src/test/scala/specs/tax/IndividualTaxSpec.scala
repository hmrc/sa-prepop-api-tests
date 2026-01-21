/*
 * Copyright 2025 HM Revenue & Customs
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

package specs.tax

import http.HttpHeaders
import models.*
import models.Scenario.{HAPPY_PATH_1, HAPPY_PATH_2}
import requests.CreateTestUser.createTestUserData
import requests.GetIndividualTax
import requests.LocalBearerGenerator.fetchBearerToken
import specs.BaseSpec

class IndividualTaxSpec extends BaseSpec {

  override val serviceUnderTest: String = "tax"

  val maximumModel: IndividualTaxResponse = IndividualTaxResponse(
    StateBenefits(None, None),
    Refund(None),
    List(IndividualTaxEmployment(employerPayeReference = "111/AAA", taxTakenOffPay = 3000))
  )

  val minimumModel: IndividualTaxResponse = IndividualTaxResponse(
    StateBenefits(otherPensionsAndRetirementAnnuities = Option(36.50), incapacityBenefit = Option(980.45)),
    Refund(taxRefundedOrSetOff = Option(325.00)),
    List(
      IndividualTaxEmployment(employerPayeReference = "123/AB456", taxTakenOffPay = 890.35),
      IndividualTaxEmployment(employerPayeReference = "456/AB456", taxTakenOffPay = 224.99)
    )
  )

  def successTest(testCase: SuccessSaPrePopTestInput): Unit =
    s"return ${testCase.expectedStatusCode} when calling individual-$serviceUnderTest with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken.toString} for scenario: ${testCase.scenario}" in {
      createTestUserData(testCase.saUtr, testCase.taxYearRange, testCase.scenario.toString, serviceUnderTest)

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val allHeaders = HttpHeaders.allHeaders(bearerToken, "1.1")
      val response   =
        new GetIndividualTax(allHeaders).getIndividualTaxResponse(testCase.saUtr, testCase.taxYearRange)

      response.status shouldBe testCase.expectedStatusCode

      val jsonResponseData = response.data
      val responseData     = jsonResponseData.as[IndividualTaxResponse]

      testCase.scenario match {
        case HAPPY_PATH_1 => responseData shouldBe minimumModel
        case HAPPY_PATH_2 => responseData shouldBe maximumModel
        case s            =>
          fail(s"[${this.getClass.getSimpleName}][successTest] scenario $s does not match or exist")
      }
    }

  def errorTest(testCase: ErrorSaPrePopTestInput): Unit =
    s"return ${testCase.expectedStatusCode}:[${testCase.expectedResponseErrorCode}|${testCase.expectedResponseErrorMessage}] when calling individual-$serviceUnderTest with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken.toString} for scenario: ${testCase.scenario}" in {
      createTestUserData(testCase.saUtr, testCase.taxYearRange, testCase.scenario.toString, serviceUnderTest)

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val allHeaders =
        if (testCase.expectedStatusCode == 406) HttpHeaders.headersNoAccept(bearerToken)
        else HttpHeaders.allHeaders(bearerToken, "1.1")
      val response   =
        new GetIndividualTax(allHeaders).getIndividualTaxResponse(testCase.saUtr, testCase.taxYearRange)

      testCase.expectedStatusCode shouldBe response.status

      val error = response.data.as[JsonErrorResponse]

      testCase.expectedResponseErrorCode    shouldBe error.code
      testCase.expectedResponseErrorMessage shouldBe error.message
    }

  s"${this.getClass.getSimpleName}" when
    allTestCases.foreach {
      case successCase: SuccessSaPrePopTestInput =>
        "making successful requests" should
          successTest(successCase)
      case errorCase: ErrorSaPrePopTestInput     =>
        "making error requests" should
          errorTest(errorCase)
    }
}
