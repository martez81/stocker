package stocker.store.es

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{HitReader, Hit}
import org.joda.time.LocalDate
import stocker.model.StockDetails
import stocker.store.StockStore
import stocker.util.DateUtil

/**
  * Created by marcin on 9/26/16.
  */
class StockStoreES extends StockStore {

    // Convert ES hit into model object
    // https://github.com/sksamuel/elastic4s#search-conversion
    implicit object StockHitAs extends HitReader[StockDetails] {
        override def read(hit: Hit): Either[Throwable, StockDetails] = {
            Right(
                StockDetails(
                  hit.sourceAsMap("symbol").toString,
                  hit.sourceAsMap("companyName").toString,
                  hit.sourceAsMap("exchange").toString,
                  hit.sourceAsMap("sector").toString,
                  hit.sourceAsMap("industry").toString,
                  hit.sourceAsMap("lastChecked").toString,
                  hit.sourceAsMap("active").toString.toBoolean
              )
            )
        }
    }

    def getMany(start: Int, offset: Int): List[StockDetails] = {
        val res = ES.client.execute { search in "stocks" / "stock" query {
            bool {
                must (
                    termQuery("active", true)
                )
            }
        } start start limit offset } await

        if (res.hits.size > 0) {
            res.to[StockDetails].toList
        } else {
            List[StockDetails]()
        }
    }

    // FIX: this is broken
    def getManyBeforeDate(start: Int, offset:Int, checkedDate: LocalDate) = {
        val res = ES.client.execute { search in "stocks" / "stock" query {
            bool {
                must (
                    termQuery("active", true)
                )
                not (
                    rangeQuery("lastChecked") from DateUtil.ISODateFormat.print(new LocalDate) to "now"
                )
            }
        } start start limit offset } await

        if (res.hits.size > 0) {
            res.to[StockDetails].toList
        } else {
            List[StockDetails]()
        }

    }

    def find(symbol: String, exchange: String): Option[StockDetails] = {
        val res = ES.client.execute {
            search in "stocks" / "stock" query {
                bool {
                    must(
                        termQuery("symbol", symbol),
                        termQuery("exchange", exchange)
                    )
                }
            }
        } await

        if (res.hits.size == 1) {
            val hit = res.hits(0)
            Some(res.to[StockDetails].head)
        } else if (res.hits.size > 1) {
            throw new Exception("Expected none or only 1 result, found more.")

        } else {
            None
        }
    }

    def updateChecked(symbol: String, exchange: String, date: LocalDate) = {
        ES.client.execute {
            update id s"$exchange:$symbol" in "stocks" / "stock" doc (
                "lastChecked" -> DateUtil.ISODateFormat.print(date)
            )
        } await
    }

    def add(stock: StockDetails): Unit = {
        ES.client.execute {
            index into "stocks" / "stock" id s"${stock.exchange}:${stock.symbol}" fields (
                    "symbol" -> stock.symbol,
                    "companyName" -> stock.companyName,
                    "exchange" -> stock.exchange,
                    "sector" -> stock.sector,
                    "industry" -> stock.industry,
                    "lastChecked" -> stock.lastChecked,
                    "active" -> stock.active
                    )
        } await
    }
}
