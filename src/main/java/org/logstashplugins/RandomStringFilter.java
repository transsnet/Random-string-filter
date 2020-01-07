package org.logstashplugins;

import co.elastic.logstash.api.*;

import java.util.Collection;
import java.util.Collections;

// class name must match plugin name
@LogstashPlugin(name = "random_string_filter")
public class RandomStringFilter implements Filter {

    public static final PluginConfigSpec<String> SOURCE_CONFIG =
            PluginConfigSpec.stringSetting("source", "msg");

    private String id;
    private String sourceField;

    public RandomStringFilter(String id, Configuration config, Context context) {
        // constructors should validate configuration options
        this.id = id;
        this.sourceField = config.get(SOURCE_CONFIG);
    }

    @Override
    public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
        for (Event e : events) {
            Object f = e.getField(sourceField);
            if (f instanceof String) {
                e.setField(sourceField, trim((String) f));
                matchListener.filterMatched(e);
            }
        }
        return events;
    }

    @Override
    public Collection<PluginConfigSpec<?>> configSchema() {
        // should return a list of all configuration options for this plugin
        return Collections.singletonList(SOURCE_CONFIG);
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     * 去掉影响聚合操作的无用部分
     */
    private String trim(String msg) {
        MessageTrimChain chain = new MessageTrimChain(msg);
        return chain.trim();
    }
}
