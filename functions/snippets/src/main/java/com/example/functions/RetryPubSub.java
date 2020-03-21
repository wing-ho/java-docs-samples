/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.functions;

// [START functions_tips_retry]
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Base64;
import java.util.logging.Logger;

public class RetryPubSub implements BackgroundFunction<PubSubMessage> {
  private static final Logger LOGGER = Logger.getLogger(RetryPubSub.class.getName());

  // Use Gson (https://github.com/google/gson) to parse JSON content.
  private Gson gsonParser = new Gson();

  @Override
  public void accept(PubSubMessage message, Context context) {
    String bodyJson = new String(Base64.getDecoder().decode(message.data));
    JsonElement bodyElement = gsonParser.fromJson(bodyJson, JsonElement.class);

    boolean retry = false;
    if (bodyElement != null && bodyElement.isJsonObject()) {
      JsonObject body = bodyElement.getAsJsonObject();

      if (body.has("retry") && body.get("retry").getAsBoolean()) {
        retry = true;
      }
    }

    if (retry) {
      throw new RuntimeException("Retrying...");
    } else {
      LOGGER.info("Not retrying...");
    }
  }
}
// [END functions_tips_retry]