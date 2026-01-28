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

package models

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, Reads}

case class NationalInsuranceResponse(class1: Class1, class2: Class2, maxNICsReached: Boolean)

case class Class1(totalNICableEarnings: Double)

case class Class2(totalDue: Double)

object NationalInsuranceResponse {

  val firstNationalInsuranceResponse: NationalInsuranceResponse = NationalInsuranceResponse(
    Class1(10.0),
    Class2(20.0),
    maxNICsReached = false
  )

  val secondNationalInsuranceResponse: NationalInsuranceResponse = NationalInsuranceResponse(
    Class1(17.0),
    Class2(319.57),
    maxNICsReached = true
  )

  implicit val class1Reads: Reads[Class1] = Json.format[Class1]

  implicit val class2Reads: Reads[Class2] = Json.format[Class2]

  implicit val nationalInsuranceResponseReads: Reads[NationalInsuranceResponse] = (
    (JsPath \ "class1").read[Class1] and
      (JsPath \ "class2").read[Class2] and
      (JsPath \ "maxNICsReached").read[Boolean]
  )(NationalInsuranceResponse.apply)

}
