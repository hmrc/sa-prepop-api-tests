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
import http.HttpGetRequest
import models.Response
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.StandaloneWSResponse

class GetWinterFuelPaymentAmount(val headers: Seq[(String, String)]) extends HttpGetRequest {

  def getWinterFuelPaymentAmountResponse(nino: String, taxYear: String): Response = {

    val url =
      s"${Configuration.settings.APP_BENEFITS_ROOT}/$nino/winter-fuel-payment-amount/annual-summary/$taxYear"

    val response: StandaloneWSResponse = executeRestCall(url)
    val data: JsValue                  = Json.parse(response.body)

    Response(response.status, data)
  }

  def getWinterFuelPaymentAmountResponseOTRSA(nino: String, taxYear: String): Response = {

    val url =
      s"${Configuration.settings.APP_BENEFITS_ROOT}/otrsa/$nino/winter-fuel-payment-amount/annual-summary/$taxYear"

    val response: StandaloneWSResponse = executeRestCall(url)
    val data: JsValue                  = Json.parse(response.body)

    Response(response.status, data)
  }

}
