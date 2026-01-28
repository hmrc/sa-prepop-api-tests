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

package requests

import config.Configuration
import http.HttpPostRequest
import models.BearerTokenType

object LocalBearerGenerator extends HttpPostRequest {

  override def headers: Seq[(String, String)] = Seq(
    "Content-Type" -> "application/json"
  )

  private def putBodyLocal(utr: String): String = {
    lazy val enrolments: String =
      s""" [
         |   {
         |     "key": "IR-SA",
         |     "identifiers": [
         |       {
         |         "key": "UTR",
         |         "value": "$utr"
         |       }
         |     ],
         |     "state": "Activated"
         |   }
         |  ] """.stripMargin

    val authPayload =
      s"""
         | {
         |  "credentials": {
         |    "providerId": "81448533810644543",
         |    "providerType": "GovernmentGateway"
         |  },
         |  "confidenceLevel": 200,
         |  "nino": "CK562300D",
         |  "usersName": "test",
         |  "credentialRole": "User",
         |  "affinityGroup": "Individual",
         |  "credentialStrength": "strong",
         |  "credId": "453234543adr54hy9",
         |  "enrolments": $enrolments
         | }
       """.stripMargin

    authPayload
  }

  private def getBearerLocal(utr: String): String = {
    val url: String  = s"${Configuration.settings.AUTH_ROOT}/government-gateway/session/login"
    val body: String = putBodyLocal(utr)
    val response     = executeRestWithBodyCall(url, body)
    val bearer       = response.headers("Authorization")

    bearer.head
  }

  def fetchBearerToken(tokenType: BearerTokenType, utr: String): String =
    tokenType match {
      case BearerTokenType.Valid      => getBearerLocal(utr)
      case BearerTokenType.WrongSaUtr => getBearerLocal(utr + "99")
      case BearerTokenType.Missing    => ""
      case BearerTokenType.Invalid    => "Bearer DUMMY"
    }

}
