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

package specs.nationalInsurance

import http.HttpHeaders
import models.*
import models.NationalInsuranceResponse.{firstNationalInsuranceResponse, secondNationalInsuranceResponse}
import models.Scenario.{HAPPY_PATH_1, HAPPY_PATH_2}
import requests.CreateTestUser.createNationalInsuranceTestUserData
import requests.GetNationalInsurance
import requests.LocalBearerGenerator.fetchBearerToken
import specs.BaseSpec

class NationalInsuranceSpec extends BaseSpec {

  override val serviceUnderTest: String = "insurance"

  def successTest(testCase: SuccessSaPrePopTestInput): Unit =
    s"return ${testCase.expectedStatusCode} when calling national-$serviceUnderTest with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken.toString} for scenario: ${testCase.scenario}" in {
      createNationalInsuranceTestUserData(testCase.saUtr, testCase.taxYearRange, testCase.scenario.toString)

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val allHeaders = HttpHeaders.allHeaders(bearerToken, "1.1")
      val response   =
        new GetNationalInsurance(allHeaders).getNationalInsuranceResponse(testCase.saUtr, testCase.taxYearRange)

      response.status shouldBe testCase.expectedStatusCode

      val jsonResponseData = response.data
      val responseData     = jsonResponseData.as[NationalInsuranceResponse]

      testCase.scenario match {
        case HAPPY_PATH_1 => responseData shouldBe firstNationalInsuranceResponse
        case HAPPY_PATH_2 => responseData shouldBe secondNationalInsuranceResponse
        case s            =>
          fail(s"[${this.getClass.getSimpleName}][successTest] scenario $s does not match or exist")
      }
    }

  def errorTest(testCase: ErrorSaPrePopTestInput): Unit =
    s"return ${testCase.expectedStatusCode}:[${testCase.expectedResponseErrorCode}|${testCase.expectedResponseErrorMessage}] when calling individual-$serviceUnderTest with UTR: ${testCase.saUtr} and tax year: ${testCase.taxYearRange} and bearer: ${testCase.bearerToken.toString} for scenario: ${testCase.scenario}" in {
      createNationalInsuranceTestUserData(testCase.saUtr, testCase.taxYearRange, testCase.scenario.toString)

      val bearerToken = fetchBearerToken(testCase.bearerToken, testCase.saUtr)

      val allHeaders =
        if (testCase.expectedStatusCode == 406) HttpHeaders.headersNoAccept(bearerToken)
        else HttpHeaders.allHeaders(bearerToken, "1.1")
      val response   =
        new GetNationalInsurance(allHeaders).getNationalInsuranceResponse(testCase.saUtr, testCase.taxYearRange)

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
