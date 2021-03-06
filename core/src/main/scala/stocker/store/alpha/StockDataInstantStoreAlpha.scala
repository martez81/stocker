package stocker.store.alpha

/**
  * Created by marcin1 on 9/1/17.
  */
import stocker.store.StockDataInstantStore
import org.joda.time.LocalDate
import stocker.model.StockDataInstant
import scalaj.http._

class StockDataInstantStoreAlphaIntraDay extends StockDataInstantStore {
    def findBetween(symbol: String, exchange: String, startDate: LocalDate, endDate: LocalDate): Seq[StockDataInstant] = List()

    def latest(symbol: String, exchange: String): Option[StockDataInstant] = {
        val request: HttpRequest = Http(Config.urlIntraDay + symbol)
    }
}

object AlphaUtils {
    def buildUrl(apiFunc: String): String = {
        AlphaConfig
    }
    def parseJson = ???
}
