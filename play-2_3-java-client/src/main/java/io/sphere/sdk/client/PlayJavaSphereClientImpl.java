package io.sphere.sdk.client;

import play.libs.F;

import java.util.concurrent.CompletableFuture;

final class PlayJavaSphereClientImpl implements PlayJavaSphereClient {
    private final SphereClient sphereClient;

    private PlayJavaSphereClientImpl(final SphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public <T> F.Promise<T> execute(final SphereRequest<T> sphereRequest) {
        return convert(sphereClient.execute(sphereRequest));
    }

    @Override
    public void close() {
        sphereClient.close();
    }

    private static <T> F.Promise<T> convert(final CompletableFuture<T> future) {
        F.RedeemablePromise<T> promise = F.RedeemablePromise.empty();
        future.whenComplete((value, throwable) -> {
            if (throwable == null) {
                promise.success(value);
            } else {
                promise.failure(throwable);
            }
        });
        return promise;
    }

    public static PlayJavaSphereClient of(final SphereClient sphereClient) {
        return new PlayJavaSphereClientImpl(sphereClient);
    }
}
