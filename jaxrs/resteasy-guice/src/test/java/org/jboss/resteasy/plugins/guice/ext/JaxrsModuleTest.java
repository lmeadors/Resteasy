package org.jboss.resteasy.plugins.guice.ext;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Binder;

public class JaxrsModuleTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testInjection() {
      final Module module = new Module()
      {
         public void configure(final Binder binder)
         {
            binder.bind(TestResource.class).to(JaxrsTestResource.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.process(module, new JaxrsModule());
      final TestResource resource = ProxyFactory.create(TestResource.class, generateBaseUrl());
      Assert.assertEquals("ok", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      public String getName();
   }

   public static class JaxrsTestResource implements TestResource
   {
      private final RuntimeDelegate runtimeDelegate;
      private final Response.ResponseBuilder responseBuilder;
      private final UriBuilder uriBuilder;
      private final Variant.VariantListBuilder variantListBuilder;

      @Inject
      public JaxrsTestResource(final RuntimeDelegate runtimeDelegate, final Response.ResponseBuilder responseBuilder, final UriBuilder uriBuilder, final Variant.VariantListBuilder variantListBuilder)
      {
         this.runtimeDelegate = runtimeDelegate;
         this.responseBuilder = responseBuilder;
         this.uriBuilder = uriBuilder;
         this.variantListBuilder = variantListBuilder;
      }

      public String getName()
      {
         Assert.assertNotNull(runtimeDelegate);
         Assert.assertNotNull(responseBuilder);
         Assert.assertNotNull(uriBuilder);
         Assert.assertNotNull(variantListBuilder);
         return "ok";
      }
   }
}