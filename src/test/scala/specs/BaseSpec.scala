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

package specs

import models.*
import models.Scenario.{HAPPY_PATH_1, HAPPY_PATH_2}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

trait BaseSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  val specificTestCases: Seq[BaseSaPrePopTestInput] = Seq.empty
  val baseTestCases: Seq[BaseSaPrePopTestInput]     = Seq(
    SuccessSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2017-18",
      expectedStatusCode = 200,
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_1
    ),
    SuccessSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2018-19",
      expectedStatusCode = 200,
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_2
    ),
    ErrorSaPrePopTestInput(
      saUtr = "109717256-",
      taxYearRange = "2017-18",
      expectedStatusCode = 400,
      expectedResponseErrorCode = "SA_UTR_INVALID",
      expectedResponseErrorMessage = "The provided SA UTR is invalid",
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_1
    ),
    ErrorSaPrePopTestInput(
      saUtr = "",
      taxYearRange = "2017-18",
      expectedStatusCode = 404,
      expectedResponseErrorCode = "NOT_FOUND",
      expectedResponseErrorMessage = "Resource was not found",
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_1
    ),
    ErrorSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2017-19",
      expectedStatusCode = 400,
      expectedResponseErrorCode = "TAX_YEAR_INVALID",
      expectedResponseErrorMessage = "The provided Tax Year is invalid",
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_1
    ),
    ErrorSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2017-18",
      expectedStatusCode = 401,
      expectedResponseErrorCode = "UNAUTHORIZED",
      expectedResponseErrorMessage = "Bearer token is missing or not authorized",
      bearerToken = BearerTokenType.Missing,
      scenario = HAPPY_PATH_1
    ),
    ErrorSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2017-18",
      expectedStatusCode = 401,
      expectedResponseErrorCode = "UNAUTHORIZED",
      expectedResponseErrorMessage = "Bearer token is missing or not authorized",
      bearerToken = BearerTokenType.Invalid,
      scenario = HAPPY_PATH_1
    ),
    ErrorSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2017-18",
      expectedStatusCode = 406,
      expectedResponseErrorCode = "ACCEPT_HEADER_INVALID",
      expectedResponseErrorMessage = "The accept header is missing or invalid",
      bearerToken = BearerTokenType.Valid,
      scenario = HAPPY_PATH_1
    ),
    ErrorSaPrePopTestInput(
      saUtr = "1097172564",
      taxYearRange = "2017-18",
      expectedStatusCode = 401,
      expectedResponseErrorCode = "UNAUTHORIZED",
      expectedResponseErrorMessage = "Bearer token is missing or not authorized",
      bearerToken = BearerTokenType.WrongSaUtr,
      scenario = HAPPY_PATH_1
    )
  )

  def serviceUnderTest: String

  def allTestCases: Seq[BaseSaPrePopTestInput] = specificTestCases ++ baseTestCases

}
