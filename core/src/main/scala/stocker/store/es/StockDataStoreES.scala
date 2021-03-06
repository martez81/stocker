package stocker.store.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{HitReader, Hit}
import org.elasticsearch.search.sort.SortOrder
import org.joda.time.LocalDate
import stocker.model.StockDay
import stocker.store.StockDataStore
import stocker.util.DateUtil

/**
  * Created by marcin on 9/26/16.
  */
class StockDataStoreES extends StockDataStore {

    // Convert ES hit into model object
    // https://github.com/sksamuel/elastic4s#search-conversion
    implicit object StockDataHitAs extends HitReader[StockDay] {
        override def read(hit: Hit): Either[Throwable, StockDay] = {
            Right(
                StockDay(
                  hit.sourceAsMap("symbol").toString,
                  hit.sourceAsMap("exchange").toString,
                  hit.sourceAsMap("date").toString,
                  hit.sourceAsMap("open").toString.toDouble,
                  hit.sourceAsMap("close").toString.toDouble,
                  hit.sourceAsMap("low").toString.toDouble,
                  hit.sourceAsMap("high").toString.toDouble,
                  hit.sourceAsMap("volume").toString.toInt
              )
            )
        }
    }

    def add(data: StockDay) = {

        ES.client.execute {
            index into "stock_data" / "day" id s"${data.exchange}:${data.symbol}:${data.date}" fields (
                "symbol" -> data.symbol,
                "exchange" -> data.exchange,
                "date" -> data.date,
                "open" -> data.open,
                "close" -> data.close,
                "low" -> data.low,
                "high" -> data.high,
                "volume" -> data.volume
                    )
        } await
    }

    def findBetween(symbol: String, exchange: String, startDate: LocalDate, endDate: LocalDate, start: Int, offset: Int) = {
        val res = ES.client.execute {
            search in "stock_data" -> "day" query {
                bool {
                    must (
                        termQuery("symbol", symbol),
                        termQuery("exchange", exchange),
                        rangeQuery("date") from DateUtil.ISODateFormat.print(startDate) to DateUtil.ISODateFormat.print(endDate)
                    )
                }
            } sort ( field sort "date" order SortOrder.ASC) start start limit offset
        } await

        if (res.hits.size > 0) {
            res.to[StockDay].toList
        } else {
            List[StockDay]()
        }
    }

    def latest(symbol: String, exchange: String) = {
        val res = ES.client.execute {
            search in "stock_data" / "day" query {
                bool {
                    must (
                        termQuery("symbol", symbol),
                        termQuery("exchange", exchange)
                    )
                }
            } sort ( field sort "date" order SortOrder.DESC ) start 0 limit 1
        } await

        if (res.hits.size == 1) {
            Some(res.to[StockDay].head)
        } else {
            None
        }
    }

    def deleteAll(symbol: String, exchange: String) = ???
}
