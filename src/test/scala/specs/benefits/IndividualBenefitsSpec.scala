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

package specs.benefits

import http.HttpHeaders
import models.*
import models.Scenario.{HAPPY_PATH_1, HAPPY_PATH_2}
import requests.CreateTestUser.createTestUserData
import requests.LocalBearerGenerator.fetchBearerToken
import requests.{GetIndividualBenefits, LocalBearerGenerator}
import specs.BaseSpec

class IndividualBenefitsSpec extends BaseSpec {

  override val serviceUnderTest: String = "benefits"

  val maximumModel: IndividualBenefitsResponse = IndividualBenefitsResponse(
    employments = List(
      IndividualBenefitsEmployment(
        employerPayeReference = "123/AB456",
        companyCarsAndVansBenefit = Some(44.0),
        fuelForCompanyCarsAndVansBenefit = Some(99.0),
        privateMedicalDentalInsurance = Some(64.0),
        vouchersCreditCardsExcessMileageAllowance = Some(80.0),
        goodsEtcProvidedByEmployer = Some(125.0),
        accommodationProvidedByEmployer = Some(375.0),
        otherBenefits = Some(16.0),
        expensesPaymentsReceived = Some(415.0)
      ),
      IndividualBenefitsEmployment(
        employerPayeReference = "456/AB456",
        companyCarsAndVansBenefit = Some(50.0),
        fuelForCompanyCarsAndVansBenefit = Some(249.0),
        privateMedicalDentalInsurance = Some(75.0),
        vouchersCreditCardsExcessMileageAllowance = Some(23.0),
        goodsEtcProvidedByEmployer = Some(250.0),
        accommodationProvidedByEmployer = Some(275.0),
        otherBenefits = Some(87.0),
        expensesPaymentsReceived = Some(265.0)
      )
    )
  )

  val minimumModel: IndividualBenefitsResponse =
    IndividualBenefitsResponse(
      employments = List(
        IndividualBenefitsEmployment(
          employerPayeReference = "123/AB456"
        )
      )
    )

  def successTest(testCase: SuccessSaPrePopTestInput): Unit =
    s"return ${testCase.expectedStatusCode} when calling individual-$serviceUnderTest with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken.toString} for scenario: ${testCase.scenario}" in {
      createTestUserData(testCase.saUtr, testCase.taxYearRange, testCase.scenario.toString, serviceUnderTest)

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val allHeaders = HttpHeaders.allHeaders(bearerToken, "1.1")
      val response   = new GetIndividualBenefits(allHeaders)
        .getIndividualBenefitsResponse(testCase.saUtr, testCase.taxYearRange)

      response.status shouldBe testCase.expectedStatusCode

      val jsonResponseData = response.data
      val responseData     = jsonResponseData.as[IndividualBenefitsResponse]

      testCase.scenario match {
        case HAPPY_PATH_1 => responseData shouldBe maximumModel
        case HAPPY_PATH_2 => responseData shouldBe minimumModel
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
        new GetIndividualBenefits(allHeaders)
          .getIndividualBenefitsResponse(testCase.saUtr, testCase.taxYearRange)

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
