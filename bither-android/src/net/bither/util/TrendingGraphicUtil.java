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

package net.bither.util;

import net.bither.BitherSetting.MarketType;
import net.bither.api.GetExchangeTrendApi;
import net.bither.model.TrendingGraphicData;
import net.bither.runnable.BaseRunnable;
import android.os.Handler;
import android.os.Looper;

public class TrendingGraphicUtil {

	public static int TRENDING_GRAPIC_COUNT = 25;

	public interface TrendingGraphicListener {

		void error();

		void success(TrendingGraphicData trendingGraphicData);
	}

	private static TrendingGraphicData[] trendingDatas = new TrendingGraphicData[MarketType
			.values().length + 1];

	public static TrendingGraphicData getTrendingGraphicData(
			final MarketType marketType,
			final TrendingGraphicListener trendingGraphicListener) {

		TrendingGraphicData trendingGraphicData = trendingDatas[marketType
				.getValue()];
		if (trendingGraphicData != null && !trendingGraphicData.isExpired()) {
			return trendingGraphicData;
		}
		BaseRunnable baseRunnable = new BaseRunnable() {

			@Override
			public void run() {

				try {
					GetExchangeTrendApi getExchangeTrendApi = new GetExchangeTrendApi(
							marketType);
					getExchangeTrendApi.handleHttpGet();
					final TrendingGraphicData trendingGraphicData = getExchangeTrendApi
							.getResult();
					trendingDatas[marketType.getValue()] = trendingGraphicData;
					if (trendingGraphicListener != null) {
						new Handler(Looper.getMainLooper())
								.post(new Runnable() {

									@Override
									public void run() {
										trendingGraphicListener
												.success(trendingGraphicData);
									}
								});
					}
				} catch (Exception e) {
					if (trendingGraphicListener != null) {
						new Handler(Looper.getMainLooper())
								.post(new Runnable() {

									@Override
									public void run() {
										trendingGraphicListener.error();
									}
								});
					}
					e.printStackTrace();
				}

			}
		};
		new Thread(baseRunnable).start();

		return null;
	}

}
