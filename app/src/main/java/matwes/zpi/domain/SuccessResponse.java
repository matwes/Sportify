package matwes.zpi.domain;

/**
 * Created by Mateusz Wesołowski
 */
public class SuccessResponse {
    boolean success;

    public SuccessResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "SuccessResponse{" +
                "success=" + success +
                '}';
    }
}
