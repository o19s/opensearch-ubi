package org.opensearch.ubi.action;

import org.junit.Test;
import org.mockito.Mockito;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.common.bytes.BytesReference;
import org.opensearch.rest.RestRequest;
import org.opensearch.ubi.backends.Backend;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static org.mockito.Mockito.when;

public class UserBehaviorInsightsRestHandlerTest {

    @Test
    public void handle() {

        final Backend backend = Mockito.mock(Backend.class);
        final NodeClient nodeClient = Mockito.mock(NodeClient.class);
        final RestRequest restRequest = Mockito.mock(RestRequest.class);

        when(restRequest.method()).thenReturn(RestRequest.Method.PUT);
        when(restRequest.param("store")).thenReturn("store");

        final BytesReference bytesReference = BytesReference.fromByteBuffer(ByteBuffer.wrap("test".getBytes(Charset.defaultCharset())));
        when(restRequest.content()).thenReturn(bytesReference);

        final UserBehaviorInsightsRestHandler handler = new UserBehaviorInsightsRestHandler(backend);
        handler.prepareRequest(restRequest, nodeClient);

        System.out.println("ok");

    }

}
