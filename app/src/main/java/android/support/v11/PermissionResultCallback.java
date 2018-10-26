package android.support.v11;

public interface PermissionResultCallback {
    void allPermissionDenied(int requestCode);

    void partialPermissionGranted(int requestCode);

    void allPermissionGranted(int requestCode);
}
