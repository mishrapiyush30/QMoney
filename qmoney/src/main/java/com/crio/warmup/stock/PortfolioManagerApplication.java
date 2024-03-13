package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
//import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
//import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
//import org.mockito.internal.matchers.Null;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be available in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. Extract stock symbols from the json file with ObjectMapper provided by
  // #getObjectMapper.
  // 3. Return the list of all symbols in the same order as provided in json.

  // Note: 5842ef8fc751e3fc878afacb7bba7840df60922f
  // 1. There can be few unused imports, you will need to fix them to make the
  // build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    TradePojo[] trades = returnPojo(args);
    List<String> symbolList = new ArrayList<String>();
    for (TradePojo trade : trades) {
      symbolList.add(trade.symbol);
    }
    return symbolList;
  }

  public static Map<String, String> purchaseDateMap(String[] args)
      throws IOException, URISyntaxException {
    TradePojo[] trades = returnPojo(args);
    Map<String, String> symbolDate = new HashMap<String, String>();
    for (TradePojo trade : trades) {
      symbolDate.put(trade.symbol, trade.purchaseDate);
    }
    return symbolDate;
  }

  public static TradePojo[] returnPojo(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    Path path = Paths.get(file.getPath());
    final byte[] bytes = Files.readAllBytes(path);
    String fileContent = new String(bytes, Charset.defaultCharset());
    ObjectMapper mapper = getObjectMapper();
    TradePojo[] trades = mapper.readValue(fileContent, TradePojo[].class);
    return trades;
  }

  // and deserialize the results in List<Candle>

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader()
    .getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/crviswatmula-ME_QMONEY/"
        + "qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@47542153";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, 
      toStringOfObjectMapper, functionNameFromTestFileInStackTrace, 
      lineNumberFromTestFileInStackTrace });
  }

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.
  // and deserialize the results in List<Candle>

  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    String endDate = args[1];
    String token = "5842ef8fc751e3fc878afacb7bba7840df60922f";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    List<String> tickerlist = mainReadFile(args);
    List<String> closingPriceString = new ArrayList<String>();
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = getObjectMapper();
    Map<String, String> dateMap = purchaseDateMap(args);
    SortedMap<Float, String> closingPriceSorted = new TreeMap<Float, String>();
    for (String ticker : tickerlist) {
      String url = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?token=%s&startDate=%s&endDate=%s",
          ticker, token, endDate, endDate);
      try {
        String test3 = restTemplate.getForObject(url, String.class);
        TiingoPojo[] tings = mapper.readValue(test3, TiingoPojo[].class);
        closingPriceSorted.put(tings[0].getClose(),ticker);
        Date purchaseDate = formatter.parse(dateMap.get(ticker));
        Date endDateComp = formatter.parse(endDate);
        if (purchaseDate.compareTo(endDateComp) > 0) {
          throw new RuntimeException("dateException");
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    for (Map.Entry<Float, String> entry : closingPriceSorted.entrySet()) {
      closingPriceString.add(entry.getValue());
    }
    return closingPriceString;
  }


  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    Path path = Paths.get(file.getPath());
    final byte[] bytes = Files.readAllBytes(path);
    String fileContent = new String(bytes, Charset.defaultCharset());
    ObjectMapper mapper1 = getObjectMapper();
    TradePojo[] trades = mapper1.readValue(fileContent, TradePojo[].class);
    String endDate = args[1];
    String token = "5842ef8fc751e3fc878afacb7bba7840df60922f";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = getObjectMapper();
    List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();
    for (TradePojo trade : trades) {
      String purchaseUrl = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?token=%s&startDate=%s&endDate=%s",
          trade.symbol, token, trade.purchaseDate, trade.purchaseDate);
      String purchaseDateValues = restTemplate.getForObject(purchaseUrl, String.class);
      TiingoPojo[] purchaseTings = mapper.readValue(purchaseDateValues, TiingoPojo[].class);
      String url = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?token=%s&startDate=%s&endDate=%s",
          trade.symbol, token, endDate, endDate);
      String apiResult = restTemplate.getForObject(url, String.class);
      if (apiResult != null) {  
        while (apiResult.equals("[]"))  {
          try {
            Date endD = formatter.parse(endDate);
            Date oneDayBefore = new Date(endD.getTime() - 2);
            endDate = formatter.format(oneDayBefore);
            url = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?token=%s&startDate=%s&endDate=%s",
              trade.symbol, token, endDate, endDate);
            String apiPrevDateResult = restTemplate.getForObject(url, String.class);
            if (apiPrevDateResult != null) {
              apiResult = apiPrevDateResult;
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        } 
      }
      //System.out.println(apiResult);
      TiingoPojo[] tings = mapper.readValue(apiResult, TiingoPojo[].class);
      PortfolioTrade tradePort = new PortfolioTrade(trade.symbol, trade.quantity,
          LocalDate.parse(trade.purchaseDate));
      returns.add(PortfolioManagerApplication
          .calculateAnnualizedReturns(LocalDate.parse(endDate),
          tradePort, Double.valueOf(purchaseTings[0].getOpen() * trade.quantity),
          Double.valueOf(tings[0].getClose() * trade.quantity)));
    }
    //Collections.sort(returns, Collections.reverseOrder());
    Collections.sort(returns, Comparator.comparing((AnnualizedReturn::getAnnualizedReturn))
        .reversed());
    System.out.println(returns);
    return returns;
  }
  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    Double totalReturns = (sellPrice - buyPrice) / buyPrice;
    Double period = Double.valueOf(ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate));
    Double years = Double.valueOf(period / (double)365);
    //Double denom = new Double(endDate.getYear() - trade.getPurchaseDate().getYear());
    Double annualizedReturns = Math.pow((1 + totalReturns), 1 / years) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
  }


  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  private static String readFileAsString(String filename) throws URISyntaxException, IOException {
    return new String(Files.readAllBytes(resolveFileFromResources(filename).toPath()),
        "UTF-8");
  }
  
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
    String file = args[0];
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(
        new RestTemplate());
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades),
      endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());




  }
}

