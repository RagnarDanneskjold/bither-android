/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.runnable;

import java.util.List;

import net.bither.BitherSetting.KlineTimeType;
import net.bither.BitherSetting.MarketType;
import net.bither.api.GetKlineApi;
import net.bither.charts.entity.IStickEntity;
import net.bither.model.KLine;
import net.bither.util.KLineUtil;

public class GetKLineRunnable extends BaseRunnable {

	private MarketType marketType;
	private KlineTimeType mKlineTimeType;

	public GetKLineRunnable(MarketType marketType, KlineTimeType klineTimeType) {
		this.marketType = marketType;
		this.mKlineTimeType = klineTimeType;
	}

	@Override
	public void run() {
		boolean hasCache = false;
		obtainMessage(HandlerMessage.MSG_PREPARE);
		try {
			KLine kLine = KLineUtil.getKLine(this.marketType,
					this.mKlineTimeType);
			hasCache = kLine != null;
			obtainMessage(HandlerMessage.MSG_SUCCESS_FROM_CACHE, kLine);
			GetKlineApi getKlineApi = new GetKlineApi(this.marketType,
					this.mKlineTimeType);
			getKlineApi.handleHttpGet();
			List<IStickEntity> entityList = getKlineApi.getResult();
			kLine = new KLine(this.marketType, this.mKlineTimeType, entityList);
			obtainMessage(HandlerMessage.MSG_SUCCESS, kLine);
			KLineUtil.addKline(kLine);
		} catch (Exception e) {
			if (!hasCache) {
				obtainMessage(HandlerMessage.MSG_FAILURE);
			}
			e.printStackTrace();
		}

	}
}
