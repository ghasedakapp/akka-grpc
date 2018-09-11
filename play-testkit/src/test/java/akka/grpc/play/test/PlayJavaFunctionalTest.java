package akka.grpc.play.test;

import example.myapp.helloworld.grpc.helloworld.*;

import org.junit.*;

import play.*;
import play.api.routing.*;
import play.inject.guice.*;
import play.libs.ws.*;
import play.test.*;

import static org.junit.Assert.*;
import static play.inject.Bindings.*;

public final class PlayJavaFunctionalTest extends WithServer {
  private static final GreeterService$ GreeterService = GreeterService$.MODULE$;

  @Override
  protected Application provideApplication() {
    return new GuiceApplicationBuilder()
        .overrides(bind(Router.class).to(GreeterServiceImpl.class))
        .build();
  }

  private WSRequest wsUrl(final String path) {
    return WSTestClient.newClient((int) testServer.runningHttpPort().get()).url(path);
  }

  @Test public void returns404OnNonGrpcRequest() throws Exception {
    final WSResponse rsp = wsUrl("/").get().toCompletableFuture().get();
    assertEquals(404, rsp.getStatus());
  }

  @Test public void returns200OnNonExistentGrpcMethod() throws Exception {
    final WSResponse rsp = wsUrl("/" + GreeterService.name() + "/FooBar").get().toCompletableFuture().get();
    assertEquals(200, rsp.getStatus());
  }

  @Test public void returns500OnEmptyRequestToAGrpcMethod() throws Exception {
    final WSResponse rsp = wsUrl("/" + GreeterService.name() + "/SayHello").get().toCompletableFuture().get();
    assertEquals(500, rsp.getStatus());
  }

}
