package apollo.spring.annotation;


import framework.apollo.core.ConfigConsts;

import java.lang.annotation.*;

/**
 * Use this annotation to register Apollo ConfigChangeListener.
 *
 * <p>Usage example:</p>
 * <pre class="code">
 * //Listener on namespaces of "someNamespace" and "anotherNamespace", will be notified when any key is changed
 * &#064;ApolloConfigChangeListener({"someNamespace","anotherNamespace"})
 * private void onChange(ConfigChangeEvent changeEvent) {
 *     //handle change event
 * }
 * <br />
 * //Listener on namespaces of "someNamespace" and "anotherNamespace", will only be notified when "someKey" or "anotherKey" is changed
 * &#064;ApolloConfigChangeListener(value = {"someNamespace","anotherNamespace"}, interestedKeys = {"someKey", "anotherKey"})
 * private void onChange(ConfigChangeEvent changeEvent) {
 *     //handle change event
 * }
 * </pre>
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ApolloConfigChangeListener {
  /**
   * Apollo appNamespace for the config, if not specified then default to application
   */
  String[] value() default {ConfigConsts.NAMESPACE_APPLICATION};

  /**
   * The keys interested by the listener, will only be notified if any of the interested keys is changed.
   * <br />
   * If not specified then will be notified when any key is changed.
   */
  String[] interestedKeys() default {};
}
