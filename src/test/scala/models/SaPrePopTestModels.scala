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

package models

sealed trait BaseSaPrePopTestInput {
  def saUtr: String
  def taxYearRange: String
  def bearerToken: BearerTokenType
  def scenario: Scenario
  def expectedStatusCode: Int
}

case class SuccessSaPrePopTestInput(
  saUtr: String,
  taxYearRange: String,
  bearerToken: BearerTokenType,
  scenario: Scenario,
  expectedStatusCode: Int
) extends BaseSaPrePopTestInput

case class ErrorSaPrePopTestInput(
  saUtr: String,
  taxYearRange: String,
  bearerToken: BearerTokenType,
  scenario: Scenario,
  expectedStatusCode: Int,
  expectedResponseErrorCode: String,
  expectedResponseErrorMessage: String
) extends BaseSaPrePopTestInput
