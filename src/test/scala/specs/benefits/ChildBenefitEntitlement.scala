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
import models.Scenario.HAPPY_PATH_1
import requests.CreateTestUser.createTestUserData
import requests.LocalBearerGenerator.fetchBearerToken
import requests.{GetChildBenifitEntitlement, LocalBearerGenerator}
import specs.BaseSpec

class ChildBenifitEntitlement extends BaseSpec {

  override val serviceUnderTest: String = "child-benefit-entitlement"

  val fullAmountModel: ChildBenifitEntitlementResponse =
    ChildBenifitEntitlementResponse(childBenefitEntitlement = 450.99)

  // val zeroAmountModel: ChildBenifitEntitlementResponse =
  //  ChildBenifitEntitlementResponse(childBenefitEntitlement = 0.00)

  def successTest(testCase: SuccessSaPrePopTestInput): Unit =
    s"return ${testCase.expectedStatusCode} when calling ChildBenifitEntitlement with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange}" in {

      createTestUserData(
        testCase.saUtr,
        testCase.taxYearRange,
        testCase.scenario.toString,
        serviceUnderTest
      )

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val headers = HttpHeaders.allHeaders(bearerToken, "1.1")

      val response = new GetChildBenifitEntitlement(headers)
        .getChildBenifitEntitlementResponse(testCase.saUtr, testCase.taxYearRange)

      response.status shouldBe testCase.expectedStatusCode

      val responseData = response.data.as[ChildBenifitEntitlementResponse]

      testCase.scenario match {
        case HAPPY_PATH_1 => responseData shouldBe fullAmountModel
        //  case HAPPY_PATH_2 => responseData shouldBe zeroAmountModel
        case s            =>
          fail(s"[${this.getClass.getSimpleName}][successTest] scenario $s does not match or exist")
      }
    }

  def errorTest(testCase: ErrorSaPrePopTestInput): Unit =
    s"return ${testCase.expectedStatusCode} when calling ChildBenifitEntitlement with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken} for scenario: ${testCase.scenario}" in {

      // Only prime stub for VALID inputs
      if (testCase.expectedStatusCode != 400 || !testCase.taxYearRange.contains("19")) {
        createTestUserData(
          testCase.saUtr,
          testCase.taxYearRange,
          testCase.scenario.toString,
          serviceUnderTest
        )
      }
      )

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val headers =
        if (testCase.expectedStatusCode == 406)
          HttpHeaders.headersNoAccept(bearerToken)
        else
          HttpHeaders.allHeaders(bearerToken, "1.1")

      val response = new GetChildBenifitEntitlement(headers)
        .getChildBenifitEntitlementResponse(testCase.saUtr, testCase.taxYearRange)

      response.status shouldBe testCase.expectedStatusCode

      val error = response.data.as[JsonErrorResponse]

      error.code    shouldBe testCase.expectedResponseErrorCode
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
