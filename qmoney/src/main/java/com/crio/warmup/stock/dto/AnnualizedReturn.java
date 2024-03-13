
package com.crio.warmup.stock.dto;

public class AnnualizedReturn implements Comparable<AnnualizedReturn> {

  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }

  @Override
  public boolean equals(Object o) { 
   
    if (o == this) { 
      return true; 
    } 

    /* Check if o is an instance of Complex or not 
      "null instanceof [type]" also returns false */
    if (!(o instanceof AnnualizedReturn)) { 
      return false; 
    } 
      
    // typecast o to Complex so that we can compare data members  
    AnnualizedReturn c = (AnnualizedReturn) o; 
      
    // Compare the data members and return accordingly  
    return Double.compare(this.getAnnualizedReturn(),c.getAnnualizedReturn()) == 0;
  }

  @Override
  public int hashCode() {  
    return this.annualizedReturn.intValue(); 
  }

  @Override
  public int compareTo(AnnualizedReturn arObj) {
    //return (int) (this.getAnnualizedReturn() - arObj.getAnnualizedReturn());
    return (this.getAnnualizedReturn() < arObj.getAnnualizedReturn() ? -1 :
            (this.getAnnualizedReturn().equals(arObj.getAnnualizedReturn()) ? 0 : 1));
  }
}
