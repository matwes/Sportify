package matwes.zpi.events;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Mateusz Wesołowski
 */

class MyItem implements ClusterItem {
    private final LatLng position;
    private final long id;
    private final int localId;

    MyItem(LatLng position, long id, int localId) {
        this.position = position;
        this.id = id;
        this.localId = localId;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public long getId() {
        return id;
    }

    int getLocalId() {
        return localId;
    }
}
