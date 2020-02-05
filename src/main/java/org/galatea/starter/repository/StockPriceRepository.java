package org.galatea.starter.repository;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.NonNull;
import org.galatea.starter.object.StockDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface StockPriceRepository extends JpaRepository<StockDay, Integer> {

  /**
   * Given a stock id and a starting date, retrieve all stock data for this stock on days after the
   * given date. does what it says on the tin.
   */
  @Query(value = "select * from price where stock_id = ?1 and event_date >= ?2", nativeQuery = true)
  List<StockDay> findStockHistoryById(final int stockId,
      @NonNull final OffsetDateTime date);

  /**
   * get the id of a given stock symbol, if it exists. null otherwise.
   */
  @Query(value = "select id from stock where symbol = ?1", nativeQuery = true)
  Integer findStockIdBySymbol(final String symbol);

  /**
   * insert an entry for the given stock symbol into the stock table.
   */
  @Modifying
  @Transactional
  @Query(value = "insert into stock(symbol) values (?1)", nativeQuery = true)
  void insertStockRecord(@NonNull final String symbol);

  /**
   * insert an entry for the given stock symbol and name into the stock table.
   */
  @Modifying
  @Query(value = "insert into stock(symbol, name) values (?1, ?2)", nativeQuery = true)
  void insertStockRecord(@NonNull final String symbol, @NonNull final String name);


}
