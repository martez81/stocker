package stocker.store.memory

import org.joda.time.LocalDate
import stocker.model.StockDay
import stocker.store.StockDataStore

/**
  * Created by marcin on 9/25/16.
  */
class StockDataStoreInMemory extends StockDataStore {
    private var stockDetails: List[StockDay] = List(
        StockDay("GPRO", "NYSE", "2016-09-22", 10.6, 11.0, 9.0, 12.0, 140),
        StockDay("GPRO", "NYSE", "2016-09-22", 10.4, 10.5, 9.0, 12.0, 140),
        StockDay("GPRO", "NYSE", "2016-09-22", 10.2, 10.3, 9.0, 12.0, 140),
        StockDay("GPRO", "NYSE", "2016-09-22", 10.1, 10.2, 9.0, 12.0, 140),
        StockDay("GPRO", "NYSE", "2016-09-22", 10.0, 11.0, 9.0, 12.0, 140)
    )

    def findBetween(symbol: String, exchange: String, startDate: LocalDate, endDate: LocalDate, start: Int, end: Int) = ???

    def add(detail: StockDay) = stockDetails = stockDetails :+ detail

    def updateChecked(name: String, exchange: String, date: LocalDate) = ???

    def latest(symbol: String, exchange: String): Option[StockDay] = {
        var latest = stockDetails.head

        for (detail <- stockDetails) {
            if (detail > latest) latest = detail
        }
        Some(latest)
    }

    def deleteAll(symbol: String, exchange: String) = ???
}
