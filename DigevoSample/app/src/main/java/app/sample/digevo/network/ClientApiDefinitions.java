package app.sample.digevo.network;

import app.sample.digevo.network.responses.LastCallsResponse;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by romantolmachev on 6/12/14.
 */
public interface ClientApiDefinitions {

    @GET(Urls.GET_LAST_CALLS)
    void getLastCalls(
            Callback<LastCallsResponse> callback
    );

}
