package matwes.zpi;

public interface callbackInterface {
    <T> void onDownloadFinished(T data, String error);
}
