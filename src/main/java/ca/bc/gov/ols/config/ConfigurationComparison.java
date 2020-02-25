/**
 * Copyright © 2008-2019, Province of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.bc.gov.ols.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationComparison {

	protected int curConfigParamCount = 0;
	protected int otherConfigParamCount = 0;
	protected List<ConfigDifference<ConfigurationParameter>> configParamDiffs;

	public ConfigurationComparison(ConfigurationStore curConfig, ConfigurationStore otherConfig) {
		List<ConfigurationParameter> curConfigParams = curConfig.getConfigParams().collect(Collectors.toList());
		curConfigParamCount = curConfigParams.size();
		List<ConfigurationParameter> otherConfigParams = otherConfig.getConfigParams().collect(Collectors.toList());
		otherConfigParamCount = otherConfigParams.size();
		configParamDiffs = diffLists(curConfigParams, otherConfigParams);
	}

	protected <T extends Comparable<T>> List<ConfigDifference<T>> diffLists(List<T> a, List<T> b) {
		List<ConfigDifference<T>> diffs = new ArrayList<ConfigDifference<T>>();
		Collections.sort(a);
		Collections.sort(b);
		int aIndex = 0;
		int bIndex = 0;
		while(aIndex < a.size() && bIndex < b.size()) {
			T nextA = a.get(aIndex);
			T nextB = b.get(bIndex);
			int comp = nextA.compareTo(nextB);
			if(comp == 0) {
				if(!nextA.equals(nextB)) {
					// same key but different value, save diff
					diffs.add(new ConfigDifference<T>(nextA, nextB));
				}
				aIndex++;
				bIndex++;
			} else if(comp < 0) {
				// different keys, nextA comes first, so it is unmatched
				diffs.add(new ConfigDifference<T>(nextA, null));
				aIndex++;
			} else if(comp > 0) {
				diffs.add(new ConfigDifference<T>(null, nextB));
				bIndex++;
			}
		}
		// if there are more items in either list, add them as diffs
		while(aIndex < a.size()) {
			diffs.add(new ConfigDifference<T>(a.get(aIndex), null));
			aIndex++;			
		}
		while(bIndex < b.size()) {
			diffs.add(new ConfigDifference<T>(null, b.get(bIndex)));
			bIndex++;			
		}
		return diffs;
	}

	public int getCurConfigParamCount() {
		return curConfigParamCount;
	}

	public int getOtherConfigParamCount() {
		return otherConfigParamCount;
	}

	public List<ConfigDifference<ConfigurationParameter>> getConfigParamDiffs() {
		return configParamDiffs;
	}

}