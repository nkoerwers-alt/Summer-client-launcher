package com.summerlauncher;

import com.microsoft.aad.msal4j.*;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public final class MicrosoftAuth {

    private static IAuthenticationResult currentAccount;

    private MicrosoftAuth() {}

    public static CompletableFuture<IAuthenticationResult> login() {

        if (AuthConfig.CLIENT_ID.equals("YOUR_CLIENT_ID_HERE")) {
            CompletableFuture<IAuthenticationResult> failed =
                    new CompletableFuture<>();

            failed.completeExceptionally(
                    new IllegalStateException(
                            "Microsoft Client ID is not configured."
                    )
            );

            return failed;
        }

        try {
            PublicClientApplication app =
                    PublicClientApplication
                            .builder(AuthConfig.CLIENT_ID)
                            .authority(AuthConfig.AUTHORITY)
                            .build();

            InteractiveRequestParameters parameters =
                    InteractiveRequestParameters
                            .builder(
                                    new URI(AuthConfig.REDIRECT_URI)
                            )
                            .scopes(
                                    Collections.singleton(
                                            "XboxLive.signin"
                                    )
                            )
                            .build();

            return app.acquireToken(parameters)
                    .thenApply(result -> {
                        currentAccount = result;
                        return result;
                    });

        } catch (Exception e) {
            CompletableFuture<IAuthenticationResult> failed =
                    new CompletableFuture<>();

            failed.completeExceptionally(e);
            return failed;
        }
    }

    public static boolean isLoggedIn() {
        return currentAccount != null;
    }

    public static String getAccessToken() {
        return currentAccount == null
                ? null
                : currentAccount.accessToken();
    }

    public static String getUsername() {
        if (currentAccount == null ||
                currentAccount.account() == null) {
            return null;
        }

        return currentAccount.account().username();
    }
}
