
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
      throws JsonProcessingException, StockQuoteServiceException {
    ObjectMapper objectMapper = getObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String uri = buildUri(symbol, from, to);
    String result = (restTemplate.getForObject(uri, String.class));
    List<Candle> candleList = new ArrayList<>();
    if (result != null) {
      List<TiingoCandle> candles;
      try {
        candles = objectMapper.readValue(result, 
        new TypeReference<List<TiingoCandle>>() {});
      } catch (Exception e) {
        throw new JsonProcessingException("Exception in Tiingo Service Json") {};
      }
      for (int i = 0; i < candles.size();i++) {
        candleList.add(candles.get(i));
      } 
    } else {
      throw new StockQuoteServiceException(
        "Invalid Stock Quote");
    }
    return candleList;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
          + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    String token = "5842ef8fc751e3fc878afacb7bba7840df60922f";
    String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
          .replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());
    return url;
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }



}
