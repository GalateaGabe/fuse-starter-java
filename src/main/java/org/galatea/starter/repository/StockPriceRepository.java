package org.galatea.starter.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.NonNull;
import org.galatea.starter.object.StockSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StockPriceRepository extends JpaRepository<StockSymbol, Integer> {

  List<StockSymbol> findByStockIdAndTradeDateAfterOrderByTradeDateDesc(final int stockId,
      @NonNull final OffsetDateTime date);

  @Modifying
  @Query(value = "update price set open_price = ?1, high_price = ?2, low_price = ?3, "
      + "close_price = ?4, volume = ?5, update_time = ?6 where stock_id = ?7 and trade_date = ?8",
      nativeQuery = true)
  void updateStockRecord(BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close,
      BigDecimal volume);
}
