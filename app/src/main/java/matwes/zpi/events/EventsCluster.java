package matwes.zpi.events;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Mateusz Wesołowski
 */

class EventsCluster implements ClusterItem {
    private final LatLng position;
    private final int localId;

    EventsCluster(LatLng position, int localId) {
        this.position = position;
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

    int getLocalId() {
        return localId;
    }
}
