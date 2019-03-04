package matwes.zpi;

public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T result);
}
