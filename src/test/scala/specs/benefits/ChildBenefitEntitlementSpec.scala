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
import requests.CreateTestUser.createTestUserData
import requests.LocalBearerGenerator.fetchBearerToken
import requests.GetChildBenefitEntitlement
import specs.BaseSpec

class ChildBenefitEntitlementSpec extends BaseSpec {

  override val serviceUnderTest: String = "child-benefit-entitlement"

  override val specificTestCases: Seq[BaseSaPrePopTestInput] = Seq(
    ErrorSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2018-19",
      expectedStatusCode = 404,
      expectedResponseErrorCode = "NOT_FOUND",
      expectedResponseErrorMessage = "Resource was not found",
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_2
    )
  )

  override def allTestCases: Seq[BaseSaPrePopTestInput] =
    specificTestCases ++ baseTestCases.filterNot {
      case s: SuccessSaPrePopTestInput =>
        s.taxYearRange == "2018-19" && s.scenario == HAPPY_PATH_2

      case e: ErrorSaPrePopTestInput =>
        e.expectedStatusCode == 406

      case _ =>
        false
    }

  val fullAmountModel: ChildBenefitEntitlementResponse =
    ChildBenefitEntitlementResponse(childBenefitEntitlement = 450.99)

  def successTest(testCase: SuccessSaPrePopTestInput): Unit =
    s"should return ${testCase.expectedStatusCode} when calling child-benefit-entitlement with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken} for scenario: ${testCase.scenario}" in {

      createTestUserData(
        testCase.saUtr,
        testCase.taxYearRange,
        testCase.scenario.toString,
        serviceUnderTest
      )

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)
      val headers     = HttpHeaders.allHeaders(bearerToken, "2.0")

      val response = new GetChildBenefitEntitlement(headers)
        .getChildBenefitEntitlementResponse(testCase.saUtr, testCase.taxYearRange)

      response.status shouldBe testCase.expectedStatusCode

      val responseData = response.data.as[ChildBenefitEntitlementResponse]

      testCase.scenario match {
        case HAPPY_PATH_1 => responseData shouldBe fullAmountModel
        case s            => fail(s"[ChildBenefitEntitlementSpec] Unexpected success scenario $s")
      }
    }

  def errorTest(testCase: ErrorSaPrePopTestInput): Unit =
    s"should return ${testCase.expectedStatusCode} when calling child-benefit-entitlement with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken} for scenario: ${testCase.scenario}" in {

      val isValidTaxYear = testCase.taxYearRange.matches("\\d{4}-\\d{2}")

      if (isValidTaxYear) {
        createTestUserData(
          testCase.saUtr,
          testCase.taxYearRange,
          testCase.scenario.toString,
          serviceUnderTest
        )
      }

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val headers =
        if (testCase.expectedStatusCode A== 406) HttpHeaders.headersNoAccept(bearerToken)
        else HttpHeaders.allHeaders(bearerToken, "1.1")

      val response = new GetChildBenefitEntitlement(headers)
        .getChildBenefitEntitlementResponse(testCase.saUtr, testCase.taxYearRange)

      response.status shouldBe testCase.expectedStatusCode

      val error = response.data.as[JsonErrorResponse]

      error.code shouldBe testCase.expectedResponseErrorCode
      error.message shouldBe testCase.expectedResponseErrorMessage
    }

  s"${this.getClass.getSimpleName}" when
    allTestCases.foreach {
      case successCase: SuccessSaPrePopTestInput =>
        "making successful requests" should
          successTest(successCase)

      case errorCase: ErrorSaPrePopTestInput =>
        "making error requests" should
          errorTest(errorCase)
    }
}