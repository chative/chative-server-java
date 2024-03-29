// Copyright 2020 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.casbin.jcasbin.persist;

import org.casbin.jcasbin.model.Model;

import java.util.List;

public interface WatcherEx extends Watcher {
    void updateForAddPolicy(String s, String... params);

    void updateForRemovePolicy(String s, String... params);

    void updateForRemoveFilteredPolicy(int fieldIndex, String... fieldValues);

    void updateForSavePolicy(Model model);

    void updateForUpdatePolicy(List<String> var1, List<String> var2);
}
