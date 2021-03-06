/*
 * Copyright 2020 StreamSets Inc.
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
package com.streamsets.pipeline.stage.origin.datalake.gen2;

import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.StageUpgrader;
import com.streamsets.pipeline.api.impl.Utils;
import com.streamsets.pipeline.lib.AzureUtils;

import java.util.ArrayList;
import java.util.List;

public class DataLakeGen2SourceUpgrader implements StageUpgrader {

  private final String prefix = "dataLakeGen2SourceConfigBean.";

  @Override
  public List<Config> upgrade(List<Config> configs, Context context) throws StageException {
    int fromVersion = context.getFromVersion();

    switch(fromVersion) {
      case 1:
        upgradeV1toV2(configs);
        if(context.getToVersion() == 2) {
          break;
        }
      case 2:
        // Call helper util function for upgrading when adding connections.
        AzureUtils.updateConfigsForConnections(configs, prefix);
        break;
      default:
        throw new IllegalStateException(Utils.format("Unexpected fromVersion {}", fromVersion));
    }
    return configs;
  }

  private static void upgradeV1toV2(List<Config> configs) {
    List<Config> configsToRemove = new ArrayList<>();
    for (Config config : configs) {
      if (config.getName().equals("conf.dataFormatConfig.preserveRootElement")) {
        configsToRemove.add(config);
      }
    }
    configs.removeAll(configsToRemove);
    configs.add(new Config("conf.dataFormatConfig.preserveRootElement", false));
  }

}
