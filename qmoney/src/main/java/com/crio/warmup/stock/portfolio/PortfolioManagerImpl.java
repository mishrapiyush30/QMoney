
package com.crio.warmup.stock.portfolio;

//import static java.time.temporal.ChronoUnit.DAYS;
//import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.cglib.core.Local;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws StockQuoteServiceException {
    List<AnnualizedReturn> annualizedReturn = new ArrayList<>();

    for (PortfolioTrade obj : portfolioTrades) {
      List<Candle> candleList = Collections.emptyList();
      try {
        candleList = stockQuotesService
            .getStockQuote(obj.getSymbol(), obj.getPurchaseDate(), endDate);
      } catch (StockQuoteServiceException | JsonProcessingException exception) {
        throw new StockQuoteServiceException("Exception") {};
      }
      if (candleList != null) {
        Double buyPrice = candleList.get(0).getOpen();
        Double sellPrice = candleList.get(candleList.size() - 1).getClose();
        Double totalReturn = (sellPrice - buyPrice) / buyPrice;
        System.out.println(sellPrice + " " + buyPrice);
        LocalDate startDate = obj.getPurchaseDate();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        Double year = Double.valueOf(days);
        Double annualizedReturns = Math.pow((1 + totalReturn), (365 / year)) - 1;
        annualizedReturn.add(new AnnualizedReturn(obj.getSymbol(), annualizedReturns, totalReturn));
      }
    }
    annualizedReturn.sort(getComparator());
    return annualizedReturn;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades,LocalDate endDate, int numThreads)
      throws InterruptedException, StockQuoteServiceException {
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    List<Callable<List<AnnualizedReturn>>> callableTasks = new ArrayList<>();
    List<AnnualizedReturn> annualizedReturnsResult = new ArrayList<AnnualizedReturn>();
    if (portfolioTrades == null || endDate == null) {
      return annualizedReturnsResult;
    }
    for (PortfolioTrade portfolioTrade: portfolioTrades) {
      List<PortfolioTrade> portfolioList = new ArrayList<PortfolioTrade>();
      portfolioList.add(portfolioTrade);
      Call object = new Call(portfolioList, endDate);
      callableTasks.add(object);
    }
    List<Future<List<AnnualizedReturn>>> futures = executorService.invokeAll(callableTasks);
    for (Future future: futures) {
      try {
        annualizedReturnsResult.addAll((List<AnnualizedReturn>) future.get());
      } catch (ExecutionException e) {
        throw new StockQuoteServiceException(e.getMessage());
      }
    }
    annualizedReturnsResult.sort(getComparator());
    return annualizedReturnsResult;
  }

  private List<AnnualizedReturn> sort(List<AnnualizedReturn> annualReturnArray) {
    if (annualReturnArray == null || annualReturnArray.size() <= 1) {
      return annualReturnArray;
    }
    for (int i = 0; i < annualReturnArray.size(); i++) {
      for (int j = 0; j < annualReturnArray.size() - i - 1; j++) {
        if (annualReturnArray.get(j).getAnnualizedReturn() < annualReturnArray.get(j + 1)
            .getAnnualizedReturn()) {
          AnnualizedReturn tmp = annualReturnArray.get(j);
          annualReturnArray.set(j, annualReturnArray.get(j + 1));
          annualReturnArray.set(j + 1, tmp);
        }
      }
    }
    return annualReturnArray;
  }

  class Call implements Callable<List<AnnualizedReturn>> {
    List<PortfolioTrade> portfolioTrades;
    LocalDate endDate;

    public Call(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
      this.portfolioTrades = portfolioTrades;
      this.endDate = endDate;
    }

    @Override
    public List<AnnualizedReturn> call() throws StockQuoteServiceException {
      List<AnnualizedReturn> listAnnRet = calculateAnnualizedReturn(portfolioTrades, endDate);
      return listAnnRet;
    }
  }
 


  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.
  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.


  public List<TiingoCandle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    String uri = buildUri(symbol, from, to);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String res = restTemplate.getForObject(uri, String.class);
    List<TiingoCandle> collection = mapper.readValue(res, 
        new TypeReference<ArrayList<TiingoCandle>>() {});
    return collection;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
          + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    String token = "5842ef8fc751e3fc878afacb7bba7840df60922f";
    String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
          .replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());
    return url;
  }

}
