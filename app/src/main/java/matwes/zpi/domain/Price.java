package matwes.zpi.domain;

/**
 * Created by Mateusz Wesołowski
 */
public class Price {
    private String type;
    private String currency;
    private int min;
    private int max;

    public Price(String type, String currency, int min, int max) {
        this.type = type;
        this.currency = currency;
        this.min = min;
        this.max = max;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
