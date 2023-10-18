package org.whispersystems.textsecuregcm.filter;

import io.dropwizard.auth.Auth;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.whispersystems.textsecuregcm.storage.Account;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.lang.annotation.Annotation;

public class UserAgentFilterFeature implements DynamicFeature {
    public UserAgentFilter userAgentFilter;
    public UserAgentFilterFeature(UserAgentFilter userAgentFilter) {
        this.userAgentFilter = userAgentFilter;
    }
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(resourceInfo.getResourceMethod());
        Annotation[][] parameterAnnotations = annotatedMethod.getParameterAnnotations();
        Class<?>[] parameterTypes = annotatedMethod.getParameterTypes();
        this.verifyAuthAnnotations(parameterAnnotations);

        for(int i = 0; i < parameterAnnotations.length; ++i) {
            Annotation[] var7 = parameterAnnotations[i];
            int var8 = var7.length;
            for(int var9 = 0; var9 < var8; ++var9) {
                Annotation annotation = var7[var9];
                if (annotation instanceof Auth) {
                    Class<?> aClass=parameterTypes[i];
                    if(aClass.equals(Account.class)) {
                        context.register(userAgentFilter);
                    }
                }
            }
        }
    }

    private void verifyAuthAnnotations(Annotation[][] parameterAnnotations) {
        int authCount = 0;
        Annotation[][] var3 = parameterAnnotations;
        int var4 = parameterAnnotations.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Annotation[] annotations = var3[var5];
            Annotation[] var7 = annotations;
            int var8 = annotations.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                Annotation annotation = var7[var9];
                if (annotation instanceof Auth) {
                    ++authCount;
                }
            }
        }

        if (authCount > 1) {
            throw new IllegalArgumentException("Only one @Auth tag supported per resource method!");
        }
    }
}