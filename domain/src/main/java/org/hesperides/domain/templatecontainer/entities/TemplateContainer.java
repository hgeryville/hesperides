package org.hesperides.domain.templatecontainer.entities;

import lombok.Value;
import lombok.experimental.NonFinal;

import java.net.URI;
import java.util.List;

@Value
@NonFinal
public abstract class TemplateContainer {
    Key key;
    List<Template> templates;

    public enum Type {
        workingcopy("wc"),
        release("release");

        private final String minimizedForm;

        Type(String minimizedForm) {
            this.minimizedForm = minimizedForm;
        }

        public String getMinimizedForm() {
            return minimizedForm;
        }

        public static String toString(boolean isWorkingCopy) {
            return isWorkingCopy ? workingcopy.toString() : release.toString();
        }
    }

    @Value
    public static class Key {
        String name;
        String version;
        Type versionType;

        public URI getURI(String prefix) {
            return URI.create("/rest/" + prefix + "/" + name + "/" + version + "/" + versionType.name().toLowerCase());
        }

        public String getNamespace(String prefix) {
            return prefix + "#" + name + "#" + version + "#" + versionType.name().toUpperCase();
        }

        public String toString(String prefix) {
            return prefix + "-" + name + "-" + version + "-" + versionType.getMinimizedForm();
        }

        public boolean isWorkingCopy() {
            return versionType == Type.workingcopy;
        }
    }
}
