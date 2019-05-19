package matwes.zpi.domain;

import java.io.Serializable;

/**
 * Created by Mateusz Wesołowski
 */
public class Price implements Serializable {
    private String currency;
    private double min;
    private double max;

    public Price(String currency, int min, int max) {
        this.currency = currency;
        this.min = min;
        this.max = max;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
