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

class GetNationalInsurance(val headers: Seq[(String, String)]) extends HttpGetRequest {

  def getNationalInsuranceResponse(utr: String, taxYear: String): Response = {
    val url                  = s"${Configuration.settings. APP_NATIONAL_INSURANCE_ROOT}/sa/$utr/annual-summary/$taxYear"
    val response             = executeRestCall(url)
    val jsonResponse: String = response.body
    val data: JsValue        = Json.parse(jsonResponse)

    Response(response.status, data)
  }

}
